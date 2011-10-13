package it.drwolf.slot.session;

import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.entity.EmbeddedProperty;
import it.drwolf.slot.entity.PropertyDef;
import it.drwolf.slot.entity.Rule;
import it.drwolf.slot.entity.RuleParameterInst;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.entity.SlotInst;
import it.drwolf.slot.entitymanager.SlotDefCloner;
import it.drwolf.slot.ruleverifier.RuleParametersResolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("slotDefHome")
public class SlotDefHome extends EntityHome<SlotDef> {

	private static final long serialVersionUID = -8721399617784074986L;

	private SlotDef model;

	@In(create = true)
	private RuleParametersResolver ruleParametersResolver;

	@In(create = true)
	private RuleHome ruleHome;

	// @In(create = true)
	private SlotDefCloner slotDefCloner = new SlotDefCloner();

	// private Map<Long, SlotDefCloner> dependentSlotDefCloners = new
	// HashMap<Long, SlotDefCloner>();

	// private DocDefCollection cloneDocDefCollection(DocDefCollection
	// collection,
	// SlotDef clonedSlotDef) {
	// DocDefCollection clonedCollection = new DocDefCollection();
	// clonedCollection.setName(collection.getName());
	// clonedCollection.setDocDef(collection.getDocDef());
	// clonedCollection.setMin(collection.getMin());
	// clonedCollection.setMax(collection.getMax());
	//
	// //
	// clonedCollection.setQuantifier(collection.getQuantifier());
	//
	// if (collection.getConditionalPropertyDef() != null) {
	// // SlotDef slotDef = collection.getSlotDef();
	// PropertyDef clonedConditionalPropertyDef = clonedSlotDef
	// .retrievePropertyDefByName(collection
	// .getConditionalPropertyDef().getName());
	// clonedCollection
	// .setConditionalPropertyDef(clonedConditionalPropertyDef);
	// PropertyInst conditionalPropertyInst = collection
	// .getConditionalPropertyInst();
	// PropertyInst clonedConditionalPropertyInst = new PropertyInst(
	// clonedConditionalPropertyDef);
	// clonedCollection
	// .setConditionalPropertyInst(clonedConditionalPropertyInst);
	// //
	// clonedConditionalPropertyInst
	// .setStringValue(conditionalPropertyInst.getStringValue());
	// clonedConditionalPropertyInst
	// .setIntegerValue(conditionalPropertyInst.getIntegerValue());
	// clonedConditionalPropertyInst
	// .setBooleanValue(conditionalPropertyInst.getBooleanValue());
	// clonedConditionalPropertyInst.setDateValue(conditionalPropertyInst
	// .getDateValue());
	// clonedConditionalPropertyInst
	// .setMultiplicity(conditionalPropertyInst.getMultiplicity());
	// clonedConditionalPropertyInst.setValues(new HashSet<String>(
	// conditionalPropertyInst.getValues()));
	// //
	//
	// }
	// return clonedCollection;
	// }
	//
	// private EmbeddedProperty cloneEmbeddedProperty(
	// EmbeddedProperty embeddedProperty) {
	// EmbeddedProperty clonedEmbeddedProperty = new EmbeddedProperty();
	// clonedEmbeddedProperty.setName(embeddedProperty.getName());
	// clonedEmbeddedProperty.setDataType(embeddedProperty.getDataType());
	// clonedEmbeddedProperty.setMultiple(embeddedProperty.isMultiple());
	//
	// clonedEmbeddedProperty.setDictionary(embeddedProperty.getDictionary());
	// clonedEmbeddedProperty.setValues(new HashSet<String>());
	// return clonedEmbeddedProperty;
	// }
	//
	// private PropertyDef clonePropertyDef(PropertyDef propertyDef) {
	// PropertyDef clonedPropertyDef = new PropertyDef();
	// clonedPropertyDef.setName(propertyDef.getName());
	// clonedPropertyDef.setDataType(propertyDef.getDataType());
	// clonedPropertyDef.setMultiple(propertyDef.isMultiple());
	// clonedPropertyDef.setRequired(propertyDef.isRequired());
	// clonedPropertyDef.setDictionary(propertyDef.getDictionary());
	// return clonedPropertyDef;
	// }

	private Rule cloneRule(Rule rule) {
		Rule clonedRule = new Rule();
		Map<String, String> parametersMap = rule.getParametersMap();
		Set<String> keySet = parametersMap.keySet();

		Set<String> deletedCollection = new HashSet<String>();
		boolean missingParams = false;

		for (String key : keySet) {
			String encodedParams = parametersMap.get(key);
			String[] splitted = encodedParams.split("\\|");
			String source = splitted[0];
			String field = splitted[1];

			String clonedParam = "";
			Object sourceDef = this.ruleParametersResolver
					.resolveSourceDef(source);
			if (sourceDef instanceof SlotDef) {
				clonedParam = clonedParam.concat(SlotDef.class.getName()) + ":"
						+ this.getInstance().getId().toString();
			} else if (sourceDef instanceof DocDefCollection) {
				clonedParam = clonedParam.concat(DocDefCollection.class
						.getName()) + ":";
				DocDefCollection docDefCollection = (DocDefCollection) sourceDef;
				DocDefCollection clonedDocDefCollection = this.getInstance()
						.retrieveDocDefCollectionByName(
								docDefCollection.getName());
				//
				if (clonedDocDefCollection != null) {
					clonedParam = clonedParam.concat(clonedDocDefCollection
							.getId().toString());
				} else {
					deletedCollection.add(docDefCollection.getName());
					missingParams = true;
				}
				//
			}

			clonedParam = clonedParam.concat("|");
			Object fieldDef = this.ruleParametersResolver
					.resolveFieldDef(field);
			if (fieldDef instanceof Property) {
				DocDefCollection docDefCollection = (DocDefCollection) sourceDef;

				if (!deletedCollection.contains(docDefCollection.getName())) {
					clonedParam = clonedParam.concat(Property.class.getName()
							+ ":" + ((Property) fieldDef).getName());
				} else {
					missingParams = true;
				}
			} else if (fieldDef instanceof PropertyDef) {
				PropertyDef clonedPropertyDef = this.getInstance()
						.retrievePropertyDefByName(
								((PropertyDef) fieldDef).getName());
				//
				if (clonedPropertyDef != null) {
					clonedParam = clonedParam.concat(PropertyDef.class
							.getName()
							+ ":"
							+ clonedPropertyDef.getId().toString());
				} else {
					missingParams = true;
				}
				//
			} else if (fieldDef instanceof EmbeddedProperty) {
				EmbeddedProperty clonedEmbeddedProperty = this.getInstance()
						.retrieveEmbeddedPropertyByName(
								((EmbeddedProperty) fieldDef).getName());
				//
				if (clonedEmbeddedProperty != null) {
					clonedParam = clonedParam.concat(EmbeddedProperty.class
							.getName())
							+ ":"
							+ clonedEmbeddedProperty.getId().toString();
				} else {
					missingParams = true;
				}
				//
			}

			clonedRule.getParametersMap().put(key, clonedParam);
		}

		Map<String, RuleParameterInst> embeddedParametersMap = rule
				.getEmbeddedParametersMap();
		Set<String> keySet2 = embeddedParametersMap.keySet();
		for (String key : keySet2) {
			RuleParameterInst ruleParameterInst = embeddedParametersMap
					.get(key);
			RuleParameterInst clonedRuleParameterInst = new RuleParameterInst();
			clonedRuleParameterInst.setStringValue(ruleParameterInst
					.getStringValue());
			clonedRuleParameterInst.setIntegerValue(ruleParameterInst
					.getIntegerValue());
			clonedRuleParameterInst.setBooleanValue(ruleParameterInst
					.getBooleanValue());
			clonedRuleParameterInst.setDateValue(ruleParameterInst
					.getDateValue());

			clonedRuleParameterInst.setRule(clonedRule);
			clonedRuleParameterInst.setVerifierParameterDef(ruleParameterInst
					.getVerifierParameterDef());
			clonedRule.getEmbeddedParametersMap().put(key,
					clonedRuleParameterInst);
		}

		clonedRule.setMandatory(rule.isMandatory());
		clonedRule.setSlotDef(this.getInstance());
		clonedRule.setType(rule.getType());
		clonedRule.setErrorMessage(rule.getErrorMessage());
		clonedRule.setWarningMessage(rule.getWarningMessage());

		if (!missingParams) {
			return clonedRule;
		} else {
			// FacesMessages
			// .instance()
			// .add(Severity.WARN,
			// "ATTENZIONE! Non è stato possibile clonare una regola perchè alcune fonti necessarie a valorizzare i parametri sono state eliminate o modificate in modo non adeguato ad essere utilizzate");
			return null;
		}
	}

	@Override
	protected SlotDef createInstance() {
		SlotDef slotDef = new SlotDef();
		return slotDef;
	}

	public SlotDef getDefinedInstance() {
		return this.isIdDefined() ? this.getInstance() : null;
	}

	public List<DocDefCollection> getDocDefCollections() {
		return this.getInstance() == null ? null
				: new ArrayList<DocDefCollection>(this.getInstance()
						.getDocDefCollections());
	}

	public SlotDef getModel() {
		return this.model;
	}

	public List<PropertyDef> getPropertyDefs() {
		return this.getInstance() == null ? null : new ArrayList<PropertyDef>(
				this.getInstance().getPropertyDefs());
	}

	public List<Rule> getRules() {
		return this.getInstance() == null ? null : new ArrayList<Rule>(this
				.getInstance().getRules());
	}

	public SlotDefCloner getSlotDefCloner() {
		return this.slotDefCloner;
	}

	public Long getSlotDefId() {
		return (Long) this.getId();
	}

	@SuppressWarnings("unchecked")
	public List<SlotInst> getSlotInstsReferenced() {
		if (this.getInstance().getId() != null) {
			List<SlotInst> resultList = this.getEntityManager()
					.createQuery("from SlotInst s where s.slotDef=:slotDef")
					.setParameter("slotDef", this.getInstance())
					.getResultList();
			if (resultList != null) {
				return resultList;
			}
		}
		return new ArrayList<SlotInst>();
	}

	@SuppressWarnings("unchecked")
	public boolean isReferenced() {
		if (this.getInstance().getId() != null) {
			List<SlotInst> resultList = this
					.getEntityManager()
					.createQuery(
							"select id from SlotInst s where s.slotDef=:slotDef")
					.setParameter("slotDef", this.getInstance())
					.setMaxResults(1).getResultList();
			if (resultList != null && !resultList.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public boolean isWired() {
		return true;
	}

	public void load() {
		if (this.isIdDefined()) {
			this.wire();
		}
	}

	@Override
	public String persist() {
		//
		Set<Long> keySet = this.slotDefCloner.getDependentSlotDefCloners()
				.keySet();
		EntityManager entityManager = this.getEntityManager();
		for (Long key : keySet) {
			SlotDefCloner slotDefCloner = this.slotDefCloner
					.getDependentSlotDefCloners().get(key);
			SlotDef cloned = slotDefCloner.getCloned();
			cloned.setName(this.getInstance().getName() + " | "
					+ cloned.getName());
			entityManager.persist(cloned);
			slotDefCloner.cloneRules();
			entityManager.persist(cloned);
		}
		//

		super.persist();
		// if (result.equals("persisted") && this.model != null) {
		// for (Rule rule : this.model.getRules()) {
		// //
		// if (rule.isActive()) {
		// Rule clonedRule = this.cloneRule(rule);
		// if (clonedRule != null) {
		// this.getInstance().getRules().add(clonedRule);
		// this.ruleHome.setInstance(clonedRule);
		// this.ruleHome.persist();
		// }
		// }
		// //
		// }
		// }

		this.slotDefCloner.cloneRules();
		// SlotDef cloned = this.slotDefCloner.getCloned();
		// entityManager.persist(cloned);
		return super.persist();
	}

	// private boolean checkEmbeddedPropertyValues() {
	// boolean passed = true;
	// for (EmbeddedProperty embeddedProperty : this.getInstance()
	// .getEmbeddedProperties()) {
	// if (embeddedProperty.getValue() == null
	// || embeddedProperty.getValue().equals("")
	// || ((Set<String>) embeddedProperty.getValue()).isEmpty()) {
	// FacesMessages.instance().add(
	// Severity.ERROR,
	// "L'informazione \"" + embeddedProperty.getLabel()
	// + "\" non è stata valorizzata");
	// passed = false;
	// }
	// }
	// if (!passed) {
	// FacesMessages
	// .instance()
	// .add(Severity.ERROR,
	// "Non possono esserci informazioni sulla gara non valorizzate");
	// }
	// return passed;
	// }

	public void setSlotDefId(Long id) {
		this.setId(id);
	}

	public void slotDefClone(SlotDef slotDef) {
		// this.model = slotDef;
		// SlotDef clonedSlotDef = null;
		// if (slotDef instanceof DependentSlotDef) {
		// clonedSlotDef = new DependentSlotDef();
		// } else {
		// clonedSlotDef = new SlotDef();
		// }
		// clonedSlotDef.setName("Copia di " + slotDef.getName());
		// clonedSlotDef.setType(slotDef.getType());
		//
		// for (EmbeddedProperty embeddedProperty : slotDef
		// .getEmbeddedProperties()) {
		// if (embeddedProperty.isActive()) {
		// EmbeddedProperty clonedEmbeddedProperty = this
		// .cloneEmbeddedProperty(embeddedProperty);
		// clonedSlotDef.getEmbeddedProperties().add(
		// clonedEmbeddedProperty);
		// }
		// }
		//
		// for (PropertyDef propertyDef : slotDef.getPropertyDefs()) {
		// if (propertyDef.isActive()) {
		// PropertyDef clonedPropertyDef = this
		// .clonePropertyDef(propertyDef);
		// clonedSlotDef.getPropertyDefs().add(clonedPropertyDef);
		// }
		// }
		//
		// for (DocDefCollection collection : slotDef.getDocDefCollections()) {
		// if (collection.isActive()) {
		// DocDefCollection clonedDocDefCollection = this
		// .cloneDocDefCollection(collection, clonedSlotDef);
		// clonedDocDefCollection.setSlotDef(clonedSlotDef);
		// clonedSlotDef.getDocDefCollections()
		// .add(clonedDocDefCollection);
		// }
		// }

		this.model = slotDef;

		this.slotDefCloner.setModel(slotDef);
		this.slotDefCloner.cloneModel();

		this.setInstance(this.slotDefCloner.getCloned());
	}

	public void wire() {
		this.getInstance();
	}

}
