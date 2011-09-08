package it.drwolf.slot.pagebeans;

import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.entity.DocInstCollection;
import it.drwolf.slot.entity.EmbeddedProperty;
import it.drwolf.slot.entity.PropertyDef;
import it.drwolf.slot.entity.PropertyInst;
import it.drwolf.slot.entity.Rule;
import it.drwolf.slot.entity.RuleParameterInst;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.entity.SlotInst;
import it.drwolf.slot.enums.CollectionQuantifier;
import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.enums.SlotDefType;
import it.drwolf.slot.interfaces.DataDefinition;
import it.drwolf.slot.pagebeans.support.PropertiesSourceContainer;
import it.drwolf.slot.session.RuleHome;
import it.drwolf.slot.session.SlotDefHome;
import it.drwolf.slot.session.SlotInstHome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ActionEvent;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("slotDefEditBean")
@Scope(ScopeType.CONVERSATION)
public class SlotDefEditBean {

	@In(create = true)
	private SlotDefHome slotDefHome;

	private DocDefCollection collection = new DocDefCollection();
	private PropertyDef propertyDef = new PropertyDef();
	private EmbeddedProperty embeddedProperty = new EmbeddedProperty();

	private Map<String, PropertyDef> converterPropertyMap = new HashMap<String, PropertyDef>();

	@In(create = true)
	private SlotInstHome slotInstHome;

	// private boolean model = Boolean.FALSE;
	// private String mode = SlotDefType.GENERAL.value();
	private String from = "";
	// private boolean wizard = Boolean.FALSE;

	private boolean conditional = Boolean.FALSE;
	private boolean edit = Boolean.FALSE;

	private boolean dirty = Boolean.FALSE;

	@In(create = true)
	private RuleEditBean ruleEditBean;

	@In(create = true)
	private RuleHome ruleHome;

	@In(create = true)
	private SlotDefParameters slotDefParameters;

	@Create
	public void init() {
		// checkReference();
		// slotDefHome.getInstance().setTemplate(model);
		// slotDefHome.getInstance().setType(SlotDefType.fromValue(mode));
		setKnownParameters();
	}

	// private void persist() {
	// if (slotDefHome.getInstance().getId() == null) {
	// this.save();
	// } else {
	// this.update();
	// }
	// }

	public void newProperty() {
		this.propertyDef = new PropertyDef();
	}

	public void newConditionaProperty() {
		this.newProperty();
		this.conditional = true;
	}

	public void addProperty() {
		SlotDef instance = slotDefHome.getInstance();
		if (!instance.getPropertyDefsAsList().contains(this.propertyDef)) {
			instance.getPropertyDefs().add(this.propertyDef);
		}
		if (converterPropertyMap.get(propertyDef.getUuid()) == null) {
			converterPropertyMap.put(propertyDef.getUuid(), propertyDef);
		}
		this.edit = false;

		//
		// this.persist();
	}

	public void addConditionalProperty() {
		this.newCollection();
		this.addProperty();
		this.collection.setConditionalPropertyDef(this.propertyDef);
		this.conditionalPropertyListener(null);
	}

	public void newCollection() {
		this.collection = new DocDefCollection();
	}

	public void addCollection() {
		if (!slotDefHome.getInstance().getDocDefCollectionsAsList()
				.contains(this.collection)) {
			collection.setSlotDef(slotDefHome.getInstance());
			slotDefHome.getInstance().getDocDefCollections()
					.add(this.collection);
		}
	}

	public void addConditionedCollection() {
		this.addCollection();
		this.conditional = false;

		//
		// this.persist();
	}

	public void newEmbeddedProperty() {
		this.embeddedProperty = new EmbeddedProperty();
	}

	public void addEmbeddedProperty() {
		if (!slotDefHome.getInstance().getEmbeddedPropertiesAsList()
				.contains(this.embeddedProperty)) {
			slotDefHome.getInstance().getEmbeddedProperties()
					.add(this.embeddedProperty);
		}

		//
		// this.persist();
	}

	@Factory("dataTypes")
	public List<DataType> getPropertyTypes() {
		return Arrays.asList(DataType.values());
	}

	@Factory("slotDefTypes")
	public List<SlotDefType> getSlotDefTypes() {
		return Arrays.asList(SlotDefType.values());
	}

	@Factory("quantifierTypes")
	public List<CollectionQuantifier> getQuantifierfTypes() {
		return Arrays.asList(CollectionQuantifier.values());
	}

	private boolean checkNames() {
		Set<String> differentNames = new HashSet<String>();
		boolean result = true;

		List<DataDefinition> allProperties = new ArrayList<DataDefinition>();
		allProperties.addAll(slotDefHome.getInstance().getPropertyDefsAsList());
		allProperties.addAll(slotDefHome.getInstance()
				.getEmbeddedPropertiesAsList());

		for (DataDefinition p : allProperties) {
			differentNames.add(p.getLabel());
		}
		if (differentNames.size() < allProperties.size()) {
			result = false;
			FacesMessages.instance().add(Severity.ERROR,
					"Non possono esserci property con lo stesso nome");
		}

		differentNames.clear();
		for (DocDefCollection d : slotDefHome.getInstance()
				.getDocDefCollectionsAsList()) {
			differentNames.add(d.getName());
		}
		if (differentNames.size() < this.slotDefHome.getInstance()
				.getDocDefCollectionsAsList().size()) {
			result = false;
			FacesMessages.instance().add(Severity.ERROR,
					"Non possono esserci collections con lo stesso nome");
		}

		return result;
	}

	private boolean checkEmbeddedPropertyValues() {
		boolean passed = true;
		for (EmbeddedProperty embeddedProperty : this.slotDefHome.getInstance()
				.getEmbeddedProperties()) {
			if ((!embeddedProperty.isMultiple() && (embeddedProperty.getValue() == null || embeddedProperty
					.getValue().equals("")))
					|| (embeddedProperty.isMultiple() && ((Set) embeddedProperty
							.getValue()).isEmpty())) {
				FacesMessages.instance().add(
						Severity.ERROR,
						"L'informazione \"" + embeddedProperty.getLabel()
								+ "\" non è stata valorizzata");
				passed = false;
			}
		}
		if (!passed) {
			FacesMessages
					.instance()
					.add(Severity.ERROR,
							"Non possono esserci informazioni sulla gara non valorizzate!");
		}
		return passed;
	}

	private boolean checkCollectionReferences() {
		boolean result = true;

		for (DocDefCollection collection : slotDefHome.getInstance()
				.getDocDefCollections()) {
			PropertyDef conditionalPropertyDef = collection
					.getConditionalPropertyDef();
			if (conditionalPropertyDef != null
					&& collection.getConditionalPropertyInst().getValue() == null) {
				result = false;
				FacesMessages
						.instance()
						.add(Severity.ERROR,
								"La richiesta \""
										+ collection.getName()
										+ "\" vincolata dall'informazione \""
										+ conditionalPropertyDef.getLabel()
										+ "\" non ha il valore del vincolo valorizzato");
			}
		}
		if (!result) {
			FacesMessages
					.instance()
					.add(Severity.ERROR,
							"Non possono esserci richieste vincolate con il loro vincolo non valorizzato!");
		}

		return result;
	}

	public String save() {
		boolean names = checkNames();
		boolean references = checkCollectionReferences();
		boolean embeddedValues = checkEmbeddedPropertyValues();
		if (names && references && embeddedValues) {
			return slotDefHome.persist();
		} else {
			return "failed";
		}
	}

	public String update() {
		boolean names = checkNames();
		boolean references = checkCollectionReferences();
		boolean embeddedValues = checkEmbeddedPropertyValues();
		if (names && references && embeddedValues) {
			Set<PropertyDef> newPropertyDefs = new HashSet<PropertyDef>();
			Set<DocDefCollection> newDocDefCollections = new HashSet<DocDefCollection>();

			for (PropertyDef propertyDef : slotDefHome.getInstance()
					.getPropertyDefsAsList()) {
				if (propertyDef.getId() == null) {
					newPropertyDefs.add(propertyDef);
				}
			}
			for (DocDefCollection collection : slotDefHome.getInstance()
					.getDocDefCollectionsAsList()) {
				if (collection.getId() == null) {
					newDocDefCollections.add(collection);
				}
			}
			String updateResult = slotDefHome.update();
			if ((!newDocDefCollections.isEmpty() || !newPropertyDefs.isEmpty())
					&& slotDefHome.isReferenced()) {
				modifyReferencedSlotInsts(newPropertyDefs, newDocDefCollections);
			}
			return updateResult;
		} else {
			return "failed";
		}
	}

	public void conditionalPropertyListener(ActionEvent event) {
		if (this.collection.getConditionalPropertyDef() != null) {
			this.collection.setConditionalPropertyInst(new PropertyInst(
					this.collection.getConditionalPropertyDef()));
		} else {
			this.collection.setConditionalPropertyInst(null);
		}
	}

	public void modifyReferencedSlotInsts(Set<PropertyDef> newPropertyDefs,
			Set<DocDefCollection> newDocDefCollections) {
		List<SlotInst> slotInstsReferenced = slotDefHome
				.getSlotInstsReferenced();
		for (SlotInst slotInst : slotInstsReferenced) {
			for (PropertyDef propertyDef : newPropertyDefs) {
				PropertyInst propertyInst = new PropertyInst(propertyDef,
						slotInst);
				slotInst.getPropertyInsts().add(propertyInst);
			}

			for (DocDefCollection docDefCollection : newDocDefCollections) {
				DocInstCollection docInstCollection = new DocInstCollection(
						slotInst, docDefCollection);
				slotInst.getDocInstCollections().add(docInstCollection);
			}
			slotInstHome.setInstance(slotInst);
			slotInstHome.update();
		}
	}

	public void removeEmbeddedProp(EmbeddedProperty embeddedProp) {
		slotDefHome.getInstance().getEmbeddedProperties().remove(embeddedProp);
		//
		// this.persist();
	}

	public void editEmbeddedProp(EmbeddedProperty embeddedProp) {
		this.embeddedProperty = embeddedProp;
	}

	public void removeProp(PropertyDef prop) {
		Set<DocDefCollection> referencedCollections = this
				.getReferencedCollections(prop);
		for (DocDefCollection collection : referencedCollections) {
			collection.getConditionalPropertyInst().setPropertyDef(null);
			collection.setConditionalPropertyInst(null);
			collection.setConditionalPropertyDef(null);
		}
		slotDefHome.getInstance().getPropertyDefs().remove(prop);

		//
		// this.persist();
	}

	public void editProp(PropertyDef prop) {
		this.propertyDef = prop;
		if (getReferencedCollections(prop).isEmpty()) {
			this.conditional = false;
		} else {
			this.conditional = true;
		}
		//
		this.edit = true;
	}

	public void removeColl(DocDefCollection coll) {
		slotDefHome.getInstance().getDocDefCollections().remove(coll);
		coll.setSlotDef(null);
		//
		// this.persist();
	}

	public void editColl(DocDefCollection coll) {
		this.collection = coll;
	}

	public DocDefCollection getCollection() {
		return collection;
	}

	public void setCollection(DocDefCollection collection) {
		this.collection = collection;
	}

	public PropertyDef getPropertyDef() {
		return propertyDef;
	}

	public void setPropertyDef(PropertyDef propertyDef) {
		this.propertyDef = propertyDef;
	}

	public EmbeddedProperty getEmbeddedProperty() {
		return embeddedProperty;
	}

	public void setEmbeddedProperty(EmbeddedProperty embeddedProperty) {
		this.embeddedProperty = embeddedProperty;
	}

	public Map<String, PropertyDef> getConverterPropertyMap() {
		return converterPropertyMap;
	}

	public void setConverterPropertyMap(
			Map<String, PropertyDef> converterPropertyMap) {
		this.converterPropertyMap = converterPropertyMap;
	}

	public void embeddedPropertyListener() {
		this.embeddedProperty.setDictionary(null);
		this.embeddedProperty.clean();
	}

	public String getReferencedCollectionsNames(PropertyDef propertyDef) {
		String collectionsReferenced = "";
		Iterator<DocDefCollection> iterator = this.getReferencedCollections(
				propertyDef).iterator();
		int count = 0;
		while (iterator.hasNext()) {
			DocDefCollection docDefCollection = iterator.next();
			if (count == 0) {
				collectionsReferenced = collectionsReferenced
						.concat(docDefCollection.getName());
				count++;
			} else if (count > 0) {
				collectionsReferenced = collectionsReferenced.concat(", "
						+ docDefCollection.getName());
				count++;
			}
		}
		return collectionsReferenced;
	}

	private Set<DocDefCollection> getReferencedCollections(
			PropertyDef propertyDef) {
		Set<DocDefCollection> referencedDocDefCollections = new HashSet<DocDefCollection>();
		Set<DocDefCollection> docDefCollections = slotDefHome.getInstance()
				.getDocDefCollections();
		Iterator<DocDefCollection> iterator = docDefCollections.iterator();
		while (iterator.hasNext()) {
			DocDefCollection docDefCollection = iterator.next();
			PropertyDef conditionalPropertyDef = docDefCollection
					.getConditionalPropertyDef();
			if (conditionalPropertyDef != null
					&& conditionalPropertyDef.equals(propertyDef)) {
				referencedDocDefCollections.add(docDefCollection);
			}
		}
		return referencedDocDefCollections;
	}

	public void checkReference() {
		if (slotDefHome.isReferenced()) {
			FacesMessages
					.instance()
					.add(Severity.WARN,
							"ATTENZIONE! Questa"
									+ (slotDefParameters.getMode().equals(
											SlotDefType.PRIMARY.value()) ? " Busta di Riferimento "
											: " Busta Amministrativa ")
									+ "è referenziata da una o più istanze già compilate!");
		}
	}

	public boolean isConditional() {
		return conditional;
	}

	public void setConditional(boolean conditional) {
		this.conditional = conditional;
	}

	// public boolean isModel() {
	// return model;
	// }
	//
	// public void setModel(boolean model) {
	// this.model = model;
	// }
	//
	// public String getMode() {
	// return mode;
	// }
	//
	// public void setMode(String mode) {
	// this.mode = mode;
	// }

	// Viene eseguito dopo aver settato i parametri, in init() venivano eseguite
	// prima che fossero settati i parametri
	private void setKnownParameters() {
		//
		if (slotDefHome.getInstance().getId() == null) {
			//
			slotDefHome.getInstance().setTemplate(slotDefParameters.isModel());
			if (slotDefParameters.getMode().equals(SlotDefType.PRIMARY.name())
					|| slotDefParameters.getMode().equals(
							SlotDefType.GENERAL.name())) {
				slotDefHome.getInstance().setType(
						SlotDefType.valueOf(slotDefParameters.getMode()));
			}
		}
	}

	public void clearEditing() {
		this.collection = new DocDefCollection();
		this.propertyDef = new PropertyDef();
		this.conditional = false;
		this.edit = false;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	// public boolean isWizard() {
	// return wizard;
	// }
	//
	// public void setWizard(boolean wizard) {
	// this.wizard = wizard;
	// }

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public String retrieveRuleParameterValue(Long ruleId, String paramName) {
		ruleHome.setRuleId(ruleId);
		ruleHome.find();
		ruleEditBean.init();
		PropertiesSourceContainer propertiesSourceContainer = ruleEditBean
				.getTargetPropertiesSourceMap().get(paramName);
		if (propertiesSourceContainer != null) {
			return propertiesSourceContainer.getLabel()
					+ " » "
					+ ruleEditBean.getTargetPropertyMap().get(paramName)
							.getLabel();
		}
		return "";
	}

	public RuleParameterInst retrieveRuleParameterInst(Long ruleId,
			String paramName) {
		ruleHome.setRuleId(ruleId);
		ruleHome.find();
		Rule rule = ruleHome.getInstance();
		return rule.getEmbeddedParametersMap().get(paramName);
	}
}
