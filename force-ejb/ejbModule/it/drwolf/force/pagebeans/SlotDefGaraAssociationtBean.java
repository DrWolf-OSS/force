package it.drwolf.force.pagebeans;

import it.drwolf.force.entity.Gara;
import it.drwolf.force.exceptions.DuplicateCoupleGaraSlotDefOwner;
import it.drwolf.force.session.homes.GaraHome;
import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.application.CustomModelController;
import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.entity.EmbeddedProperty;
import it.drwolf.slot.entity.Rule;
import it.drwolf.slot.entity.RuleParameterInst;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.entity.SlotInst;
import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.enums.RuleType;
import it.drwolf.slot.pagebeans.SlotDefEditBean;
import it.drwolf.slot.pagebeans.support.PropertiesSourceContainer;
import it.drwolf.slot.pagebeans.support.PropertyContainer;
import it.drwolf.slot.ruleverifier.DateComparison;
import it.drwolf.slot.ruleverifier.RuleParametersEncoder;
import it.drwolf.slot.ruleverifier.VerifierParameterDef;
import it.drwolf.slot.session.SlotDefHome;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("slotDefGaraAssociationtBean")
@Scope(ScopeType.CONVERSATION)
public class SlotDefGaraAssociationtBean {

	@In(create = true)
	private SlotDefHome slotDefHome;

	@In(create = true)
	private SlotDefEditBean slotDefEditBean;

	@In(create = true)
	private GaraHome garaHome;

	@In(create = true)
	private RuleParametersEncoder ruleParametersEncoder;

	@In(create = true)
	private CustomModelController customModelController;

	public final static String EP_DATA_SCANDENZA_GARA = "Data scadenza gara";

	private final String EXPIRABLE_ASPECT = "P:slot:expirable";
	private final String EXPIRATION_DATE_PROPERTY = "slot:expirationDate";

	private EmbeddedProperty embeddedDataScadenza = null;

	private void addDateComparisonRulesToExpirable() {
		if (this.embeddedDataScadenza != null
				&& this.embeddedDataScadenza.getDataType() == DataType.DATE
				&& this.embeddedDataScadenza.getDateValue() != null) {

			for (DocDefCollection docDefCollection : this.slotDefHome
					.getInstance().getDocDefCollections()) {
				if (docDefCollection.getDocDef().hasAspect(
						this.EXPIRABLE_ASPECT)) {
					Rule rule = this.buildDateComparisonRule(docDefCollection);
					this.slotDefHome.getInstance().getRules().add(rule);
					rule.setSlotDef(this.slotDefHome.getInstance());
				}
			}

			this.slotDefHome.update();
		}
	}

	private Rule buildDateComparisonRule(DocDefCollection docDefCollection) {
		Rule rule = new Rule();
		rule.setType(RuleType.DATE_COMPARISON);
		rule.setVerifier(new DateComparison());
		List<VerifierParameterDef> inParams = rule.getVerifier().getInParams();

		for (VerifierParameterDef verifierParameterDef : inParams) {
			if (verifierParameterDef.getName().equals(
					DateComparison.EARLIER_DATE)) {
				PropertiesSourceContainer sourceContainer = new PropertiesSourceContainer(
						this.slotDefHome.getInstance());
				PropertyContainer propertyContainer = new PropertyContainer(
						this.embeddedDataScadenza);
				String parameterEncoded = this.ruleParametersEncoder.encode(
						sourceContainer, propertyContainer);
				rule.getParametersMap().put(verifierParameterDef.getName(),
						parameterEncoded);
			}

			else if (verifierParameterDef.getName().equals(
					DateComparison.FOLLOWING_DATE)) {
				PropertiesSourceContainer sourceContainer = new PropertiesSourceContainer(
						docDefCollection);
				Property property = this.customModelController
						.getProperty(this.EXPIRATION_DATE_PROPERTY);
				PropertyContainer propertyContainer = new PropertyContainer(
						property);
				String encodedParam = this.ruleParametersEncoder.encode(
						sourceContainer, propertyContainer);
				rule.getParametersMap().put(verifierParameterDef.getName(),
						encodedParam);
			}

			else if (verifierParameterDef.getName().equals(
					DateComparison.WARNING_THRESHOLD)) {
				RuleParameterInst embeddedParameterInst = new RuleParameterInst();
				embeddedParameterInst
						.setVerifierParameterDef(verifierParameterDef);
				embeddedParameterInst.setIntegerValue(new Integer(10));
				embeddedParameterInst.setRule(rule);
				rule.getEmbeddedParametersMap().put(
						verifierParameterDef.getName(), embeddedParameterInst);
			}
		}

		rule.setErrorMessage("Il documento scade prima della scadenza della gara!");
		rule.setWarningMessage("Il documento scade solo 10 giorni dopo la scadenza della gara");

		return rule;
	}

	public void initSlotDefValues() {
		Gara gara = this.garaHome.getInstance();
		SlotDef slotDef = this.slotDefHome.getInstance();
		if (this.garaHome.getGaraId() != null
				&& this.slotDefHome.getSlotDefId() == null && slotDef != null) {
			String oggetto = gara.getOggetto().trim();
			slotDef.setName(oggetto);

			if (gara.getDataScadenza() != null) {
				this.embeddedDataScadenza = new EmbeddedProperty();
				this.embeddedDataScadenza
						.setName(SlotDefGaraAssociationtBean.EP_DATA_SCANDENZA_GARA);
				this.embeddedDataScadenza.setDataType(DataType.DATE);
				this.embeddedDataScadenza.setDateValue(gara.getDataScadenza());
				slotDef.getEmbeddedProperties().add(this.embeddedDataScadenza);
			}

			if (gara.getLink() != null && !gara.getLink().equals("")) {
				EmbeddedProperty embeddedLink = new EmbeddedProperty();
				embeddedLink.setName("Link alla gara");
				embeddedLink.setDataType(DataType.LINK);
				embeddedLink.setStringValue(gara.getLink());
				slotDef.getEmbeddedProperties().add(embeddedLink);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public boolean isSlotDefReferencedBySomeGara() {
		if (this.slotDefHome.getInstance() != null
				&& this.slotDefHome.getInstance().getId() != null) {
			List<SlotInst> resultList = this.slotDefHome
					.getEntityManager()
					.createQuery(
							"select id from Gara g where :slotDef in elements(g.slotDefs)")
					.setParameter("slotDef", this.slotDefHome.getInstance())
					.setMaxResults(1).getResultList();
			if (resultList != null && !resultList.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	@Transactional
	public String persistAssociation() {
		try {
			Gara gara = this.garaHome.getInstance();
			if (gara != null && this.slotDefHome.getInstance() != null) {
				this.slotDefEditBean.save();
				//
				this.addDateComparisonRulesToExpirable();
				//
				SlotDef slotDef = this.slotDefHome.getInstance();
				gara.addSlotDef(slotDef);
				String garaResult = this.garaHome.update();
				if (garaResult.equals("updated")) {
					return "associated";
				}
			}
		} catch (DuplicateCoupleGaraSlotDefOwner e) {
			FacesMessages.instance().add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
		}
		return "failed";
	}

	public void useSlotDef(SlotDef slotDefToClone) {
		this.slotDefHome.slotDefClone(slotDefToClone);
		this.initSlotDefValues();
	}

}
