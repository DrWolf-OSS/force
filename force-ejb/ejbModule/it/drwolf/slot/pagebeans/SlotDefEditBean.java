package it.drwolf.slot.pagebeans;

import it.drwolf.slot.entity.DependentSlotDef;
import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.entity.DocInstCollection;
import it.drwolf.slot.entity.EmbeddedProperty;
import it.drwolf.slot.entity.PropertyDef;
import it.drwolf.slot.entity.PropertyInst;
import it.drwolf.slot.entity.Rule;
import it.drwolf.slot.entity.RuleParameterInst;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.entity.SlotInst;
import it.drwolf.slot.entitymanager.SlotDefCloner;
import it.drwolf.slot.enums.CollectionQuantifier;
import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.enums.SlotDefType;
import it.drwolf.slot.interfaces.Conditionable;
import it.drwolf.slot.interfaces.DataDefinition;
import it.drwolf.slot.interfaces.Deactivable;
import it.drwolf.slot.interfaces.Definition;
import it.drwolf.slot.pagebeans.support.PropertiesSourceContainer;
import it.drwolf.slot.pagebeans.support.PropertyContainer;
import it.drwolf.slot.ruleverifier.VerifierParameterDef;
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

	private final static String CONDITIONED_PROPERTY = "Property";
	private final static String CONDITIONED_COLLECTION = "Collection";
	private final static String CONDITIONED_SLOTDEF = "SlotDef";
	private final static String CONDITIONED_NONE = "none";

	@In(create = true)
	private SlotDefHome slotDefHome;

	private DocDefCollection collection = new DocDefCollection();
	private PropertyDef propertyDef = new PropertyDef();
	private EmbeddedProperty embeddedProperty = new EmbeddedProperty();

	private DependentSlotDef dependentSlotDef;

	private Map<String, PropertyDef> converterPropertyMap = new HashMap<String, PropertyDef>();

	@In(create = true)
	private SlotInstHome slotInstHome;

	private boolean conditional = Boolean.FALSE;
	private boolean edit = Boolean.FALSE;
	private String conditioned = SlotDefEditBean.CONDITIONED_NONE;

	private boolean dirty = Boolean.FALSE;

	@In(create = true)
	private RuleEditBean ruleEditBean;

	@In(create = true)
	private RuleHome ruleHome;

	@In(create = true)
	private SlotDefParameters slotDefParameters;

	// @In(create = true)
	// private DependentSlotDefHome dependentSlotDefHome;

	// private SlotDefCloner slotDefCloner;

	// private Map<Long, SlotDefCloner> slotDefCloners = new HashMap<Long,
	// SlotDefCloner>();

	// Cloned, Original
	private Map<DependentSlotDef, DependentSlotDef> clonedOriginalMap = new HashMap<DependentSlotDef, DependentSlotDef>();

	private DependentSlotDef dependentTmp;

	// Cloned, Original
	// private BiMap<DependentSlotDef, DependentSlotDef> clonedOriginalBiMap =
	// HashBiMap
	// .create();

	public void addCollection() {
		if (!this.slotDefHome.getInstance().getDocDefCollectionsAsList()
				.contains(this.collection)) {
			this.collection.setSlotDef(this.slotDefHome.getInstance());
			this.slotDefHome.getInstance().getDocDefCollections()
					.add(this.collection);
		}
	}

	public void addConditionalPropertyForCollection() {
		this.newCollection();
		this.addProperty();
		this.collection.setConditionalPropertyDef(this.propertyDef);
		this.conditionalPropertyListener(null);

		//
		this.conditional = false;
	}

	public void addConditionalPropertyForProperty() {
		this.addProperty();
		PropertyDef conditionalPropertyDef = this.propertyDef;
		this.propertyDef = new PropertyDef();
		this.propertyDef.setConditionalPropertyDef(conditionalPropertyDef);
		this.conditionalPropertyListener(null);
		this.conditional = false;
	}

	public void addConditionedCollection() {
		this.addCollection();
		this.conditional = false;
	}

	public void addDependentSlotDef() {
		//
		// this.dependentSlotDef.setParentSlotDef(this.slotDefHome.getInstance());
		//
		SlotDefCloner slotDefCloner = new SlotDefCloner();
		slotDefCloner.setModel(this.dependentSlotDef);
		slotDefCloner.cloneModel();
		DependentSlotDef dependentCloned = (DependentSlotDef) slotDefCloner
				.getCloned();

		dependentCloned.setConditionalPropertyDef(this.dependentSlotDef
				.getConditionalPropertyDef());
		dependentCloned.setConditionalPropertyInst(this.dependentSlotDef
				.getConditionalPropertyInst());

		//
		dependentCloned.setNumberOfInstances(this.dependentSlotDef
				.getNumberOfInstances());
		//

		// this.slotDefCloners.put(this.dependentSlotDef.getId(),
		// slotDefCloner);
		this.slotDefHome.getSlotDefCloner().getDependentSlotDefCloners()
				.add(slotDefCloner);
		//

		this.slotDefHome.getInstance().getDependentSlotDefs()
				.add(dependentCloned);
		dependentCloned.setParentSlotDef(this.slotDefHome.getInstance());

		// tengo l'originale per un eventuale successivo edit da interfaccia
		// if (!this.clonedOriginalBiMap.containsValue(this.dependentSlotDef)) {
		// this.clonedOriginalMap.put(dependentCloned, this.dependentSlotDef);
		// }

		this.resetDependentSlotDefModel();
	}

	public void addEmbeddedProperty() {
		if (!this.slotDefHome.getInstance().getEmbeddedPropertiesAsList()
				.contains(this.embeddedProperty)) {
			this.slotDefHome.getInstance().getEmbeddedProperties()
					.add(this.embeddedProperty);
		}
	}

	public void addProperty() {
		SlotDef instance = this.slotDefHome.getInstance();
		if (!instance.getPropertyDefsAsList().contains(this.propertyDef)) {
			instance.getPropertyDefs().add(this.propertyDef);
		}
		if (this.converterPropertyMap.get(this.propertyDef.getUuid()) == null) {
			this.converterPropertyMap.put(this.propertyDef.getUuid(),
					this.propertyDef);
		}
		this.edit = false;
	}

	public void cancelDependentSlotDefEdit() {
		// DependentSlotDef cloned = this.clonedOriginalBiMap.inverse().get(
		// this.dependentSlotDef);

		DependentSlotDef cloned = this.dependentTmp;

		cloned.setConditionalPropertyDef(this.dependentSlotDef
				.getConditionalPropertyDef());
		cloned.setConditionalPropertyInst(this.dependentSlotDef
				.getConditionalPropertyInst());
		cloned.setParentSlotDef(this.slotDefHome.getInstance());

		this.resetDependentSlotDefModel();
		this.edit = false;
	}

	private boolean checkCollectionReferences() {
		boolean result = true;

		for (DocDefCollection collection : this.slotDefHome.getInstance()
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

	public void checkIfDependentSlotDef() {
		if (this.slotDefParameters.getMode().equals(
				SlotDefType.DEPENDENT.name())
				&& this.slotDefHome.getId() == null) {
			DependentSlotDef dependentSlotDef = new DependentSlotDef();
			dependentSlotDef.setTemplate(this.slotDefParameters.isModel());
			this.slotDefHome.setInstance(dependentSlotDef);
		}
	}

	private boolean checkNames() {
		Set<String> differentNames = new HashSet<String>();
		boolean result = true;

		List<DataDefinition> allProperties = new ArrayList<DataDefinition>();
		allProperties.addAll(this.slotDefHome.getInstance()
				.getPropertyDefsAsList());
		allProperties.addAll(this.slotDefHome.getInstance()
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
		for (DocDefCollection d : this.slotDefHome.getInstance()
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

	public void checkReference() {
		if (this.slotDefHome.isReferenced()) {
			FacesMessages
					.instance()
					.add(Severity.WARN,
							"ATTENZIONE! Questa"
									+ (this.slotDefParameters.getMode().equals(
											SlotDefType.PRIMARY.value()) ? " Busta di Riferimento "
											: " Busta Amministrativa ")
									+ "è referenziata da una o più istanze già compilate!");
		}
	}

	public void clearEditing() {
		this.collection = new DocDefCollection();
		this.propertyDef = new PropertyDef();
		this.conditional = false;
		this.edit = false;
		this.conditioned = SlotDefEditBean.CONDITIONED_NONE;
		this.dependentSlotDef = null;
	}

	public void conditionalPropertyListener(ActionEvent event) {
		if (this.conditioned.equals(SlotDefEditBean.CONDITIONED_COLLECTION)) {
			if (this.collection.getConditionalPropertyDef() != null) {
				this.collection.setConditionalPropertyInst(new PropertyInst(
						this.collection.getConditionalPropertyDef()));
			} else {
				this.collection.setConditionalPropertyInst(null);
			}
		} else if (this.conditioned
				.equals(SlotDefEditBean.CONDITIONED_PROPERTY)) {
			if (this.propertyDef.getConditionalPropertyDef() != null) {
				this.propertyDef.setConditionalPropertyInst(new PropertyInst(
						this.propertyDef.getConditionalPropertyDef()));
			} else {
				this.propertyDef.setConditionalPropertyInst(null);
			}
		} else if (this.conditioned.equals(SlotDefEditBean.CONDITIONED_SLOTDEF)) {
			// if (this.dependentSlotDef.getConditionalPropertyDef() != null) {
			// this.dependentSlotDef
			// .setConditionalPropertyInst(new PropertyInst(
			// this.dependentSlotDef
			// .getConditionalPropertyDef()));
			// } else {
			// this.dependentSlotDef.setConditionalPropertyInst(null);
			// }
		}
	}

	public void conditionalPropertyListenerForConditionable(
			Conditionable conditionable) {
		if (conditionable.getConditionalPropertyDef() != null) {
			conditionable.setConditionalPropertyInst(new PropertyInst(
					conditionable.getConditionalPropertyDef()));
		} else {
			conditionable.setConditionalPropertyInst(null);
		}
	}

	// Le regole settate come inattive sono quelle del modello clonato, che nel
	// momento del save non saranno poi copiate.
	//
	// Nel caso le regole referenziate siano quelle proprie dello SlotDef (e che
	// quindi è già stato persistito) questo metodo è impossibile che sia
	// invocato perchè è stato impedito da interfaccia di eliminare un elemento
	// che è referenziato da una regola
	private void deactiveReferencedRules(Object obj) {
		List<Rule> referencedRules = this.retrieveReferencedRules(obj);
		for (Rule rule : referencedRules) {
			rule.setActive(false);
		}
	}

	public void editColl(DocDefCollection coll) {
		this.collection = coll;
		if (this.collection.getConditionalPropertyDef() != null) {
			// this.conditional = true;
			this.conditioned = SlotDefEditBean.CONDITIONED_COLLECTION;
		} else {
			// this.conditional = false;
			this.conditioned = SlotDefEditBean.CONDITIONED_NONE;
		}
		//
		this.edit = true;
	}

	public void editDependentSlotDef(DependentSlotDef dependentSlotDef) {
		// this.dependentTmp = dependentSlotDef;
		//
		// this.slotDefHome.getInstance().getDependentSlotDefs()
		// .remove(dependentSlotDef);
		// dependentSlotDef.setParentSlotDef(null);
		//
		// this.dependentSlotDef = this.clonedOriginalMap.get(dependentSlotDef);
		// this.dependentSlotDef.setConditionalPropertyDef(dependentSlotDef
		// .getConditionalPropertyDef());
		// this.dependentSlotDef.setConditionalPropertyInst(dependentSlotDef
		// .getConditionalPropertyInst());
		//

		this.dependentSlotDef = dependentSlotDef;
		this.edit = true;
	}

	public void editEmbeddedProp(EmbeddedProperty embeddedProp) {
		this.embeddedProperty = embeddedProp;
	}

	public void editProp(PropertyDef prop) {
		this.propertyDef = prop;
		if (prop.getConditionedDocDefCollections().isEmpty()
				&& prop.getConditionedPropertyDefs().isEmpty()) {
			// this.conditional = false;
			this.conditioned = SlotDefEditBean.CONDITIONED_PROPERTY;
		} else {
			// this.conditional = true;
			this.conditioned = SlotDefEditBean.CONDITIONED_NONE;
		}
		//
		this.edit = true;
	}

	public void embeddedPropertyListener() {
		this.embeddedProperty.setDictionary(null);
		this.embeddedProperty.clean();
	}

	private String enqueueNames(Set<Definition> definitions) {
		String names = "";
		Iterator<Definition> iterator = definitions.iterator();
		int count = 0;
		while (iterator.hasNext()) {
			Definition def = iterator.next();
			if (count == 0) {
				names = names.concat(def.getLabel());
				count++;
			} else if (count > 0) {
				names = names.concat(", " + def.getLabel());
				count++;
			}
		}
		return names;
	}

	public DocDefCollection getCollection() {
		return this.collection;
	}

	public String getConditioned() {
		return this.conditioned;
	}

	public Map<String, PropertyDef> getConverterPropertyMap() {
		return this.converterPropertyMap;
	}

	public DependentSlotDef getDependentSlotDef() {
		return this.dependentSlotDef;
	}

	public EmbeddedProperty getEmbeddedProperty() {
		return this.embeddedProperty;
	}

	public PropertyDef getPropertyDef() {
		return this.propertyDef;
	}

	@Factory("dataTypes")
	public List<DataType> getPropertyTypes() {
		return Arrays.asList(DataType.values());
	}

	@Factory("quantifierTypes")
	public List<CollectionQuantifier> getQuantifierfTypes() {
		return Arrays.asList(CollectionQuantifier.values());
	}

	public String getReferencedCollectionsNames(PropertyDef propertyDef) {
		return this.enqueueNames(new HashSet<Definition>(propertyDef
				.getConditionedDocDefCollections()));
	}

	public String getReferencedPropertyNames(PropertyDef propertyDef) {
		return this.enqueueNames(new HashSet<Definition>(propertyDef
				.getConditionedPropertyDefs()));
	}

	// private void removeReferencesFromCollections(PropertyDef prop,
	// boolean invalidateCollections) {
	// List<DocDefCollection> referencedCollections = this
	// .getConditionedCollections(prop);
	// for (DocDefCollection collection : referencedCollections) {
	// collection.getConditionalPropertyInst().setPropertyDef(null);
	// collection.setConditionalPropertyInst(null);
	// collection.setConditionalPropertyDef(null);
	// if (invalidateCollections) {
	// if (collection.getId() != null && slotDefHome.isReferenced()) {
	// collection.setActive(false);
	// } else {
	// // se lo SlotDef non è referenziato o la collection è stata
	// // appena creata (e quindi sicuramente non referenziata
	// // anche se lo SlotDef lo fosse) posso eliminarla del tutto
	// this.removeColl(collection);
	// }
	// }
	// }
	// }

	@Factory("slotDefTypes")
	public List<SlotDefType> getSlotDefTypes() {
		return Arrays.asList(SlotDefType.values());
	}

	@Create
	public void init() {
		this.setKnownParameters();
	}

	public void invalidate(Object obj) {
		Deactivable def = (Deactivable) obj;
		if (def.isActive()) {
			if (obj instanceof PropertyDef) {
				PropertyDef pDef = (PropertyDef) obj;
				this.removeReferencesFromCollections(pDef, true);
			}
			def.setActive(false);
		} else {
			def.setActive(true);
		}
	}

	public boolean isConditional() {
		return this.conditional;
	}

	public boolean isDirty() {
		return this.dirty;
	}

	public boolean isEdit() {
		return this.edit;
	}

	private boolean isRuleReferencedCollection(String propertyName, Rule rule) {
		this.ruleHome.setInstance(rule);
		this.ruleEditBean.init();

		List<VerifierParameterDef> inParams = rule.getVerifier().getInParams();
		HashMap<String, PropertiesSourceContainer> targetPropertiesSourceMap = this.ruleEditBean
				.getTargetPropertiesSourceMap();
		Iterator<VerifierParameterDef> iterator = inParams.iterator();
		while (iterator.hasNext()) {
			VerifierParameterDef parameter = iterator.next();
			PropertiesSourceContainer propertiesSourceContainer = targetPropertiesSourceMap
					.get(parameter.getName());
			if (propertiesSourceContainer != null
					&& propertiesSourceContainer.getDocDefCollection() != null
					&& propertiesSourceContainer.getDocDefCollection()
							.getName().equals(propertyName)) {
				return true;
			}
		}
		return false;
	}

	private boolean isRuleReferencedProperty(String propertyName, Rule rule) {
		this.ruleHome.setInstance(rule);
		this.ruleEditBean.init();

		List<VerifierParameterDef> inParams = rule.getVerifier().getInParams();
		HashMap<String, PropertyContainer> targetPropertyMap = this.ruleEditBean
				.getTargetPropertyMap();

		Iterator<VerifierParameterDef> iterator = inParams.iterator();
		while (iterator.hasNext()) {
			VerifierParameterDef parameter = iterator.next();
			PropertyContainer propertyContainer = targetPropertyMap
					.get(parameter.getName());
			if (propertyContainer != null
					&& propertyContainer.getName().equals(propertyName)) {
				return true;
			}
		}
		return false;
	}

	public void modifyReferencedSlotInsts(Set<PropertyDef> newPropertyDefs,
			Set<DocDefCollection> newDocDefCollections) {
		List<SlotInst> slotInstsReferenced = this.slotDefHome
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
			this.slotInstHome.setInstance(slotInst);
			this.slotInstHome.update();
		}
	}

	public void newCollection() {
		this.collection = new DocDefCollection();
	}

	public void newConditionalPropertyForCollection() {
		this.newConditionaProperty();
		this.conditioned = SlotDefEditBean.CONDITIONED_COLLECTION;
	}

	public void newConditionalPropertyForProperty() {
		this.newConditionaProperty();
		this.conditioned = SlotDefEditBean.CONDITIONED_PROPERTY;
	}

	private void newConditionaProperty() {
		this.newProperty();
		this.conditional = true;
	}

	// public void newDependentSlotDef() {
	// this.dependentSlotDef = new SlotDef();
	// this.conditioned = SlotDefEditBean.CONDITIONED_SLOTDEF;
	// }

	public void newEmbeddedProperty() {
		this.embeddedProperty = new EmbeddedProperty();
	}

	public void newProperty() {
		this.propertyDef = new PropertyDef();
	}

	public void newStandardCollection() {
		this.newCollection();
		this.conditional = false;
		this.conditioned = SlotDefEditBean.CONDITIONED_NONE;
	}

	public void newStandardProperty() {
		this.newProperty();
		this.conditional = false;
		this.conditioned = SlotDefEditBean.CONDITIONED_NONE;
	}

	public void onChangeEmbeddedPropertyDictionary() {
		if (this.embeddedProperty != null) {
			this.embeddedProperty.clean();
			this.embeddedProperty.setConstraint(null);
		}
	}

	// private void persistDependentSlotDefs() {
	// Set<Long> keySet = this.slotDefCloners.keySet();
	// EntityManager entityManager = this.slotDefHome.getEntityManager();
	// for (Long key : keySet) {
	// SlotDefCloner slotDefCloner = this.slotDefCloners.get(key);
	// SlotDef cloned = slotDefCloner.getCloned();
	// entityManager.persist(cloned);
	// slotDefCloner.cloneRules();
	// entityManager.persist(cloned);
	// }
	// }

	// public List<DocDefCollection> getConditionedCollections(
	// PropertyDef propertyDef) {
	// List<DocDefCollection> referencedDocDefCollections = new
	// ArrayList<DocDefCollection>();
	// Set<DocDefCollection> docDefCollections = slotDefHome.getInstance()
	// .getDocDefCollections();
	// Iterator<DocDefCollection> iterator = docDefCollections.iterator();
	// while (iterator.hasNext()) {
	// DocDefCollection docDefCollection = iterator.next();
	// PropertyDef conditionalPropertyDef = docDefCollection
	// .getConditionalPropertyDef();
	// if (conditionalPropertyDef != null
	// && conditionalPropertyDef.equals(propertyDef)) {
	// referencedDocDefCollections.add(docDefCollection);
	// }
	// }
	// return referencedDocDefCollections;
	// }

	// public List<PropertyDef> getConditionedPropertyDefs(PropertyDef
	// propertyDef) {
	// List<PropertyDef> referencedPropertyDef = new ArrayList<PropertyDef>();
	// Set<PropertyDef> propertyDefs = slotDefHome.getInstance()
	// .getPropertyDefs();
	// Iterator<PropertyDef> iterator = propertyDefs.iterator();
	// while (iterator.hasNext()) {
	// PropertyDef pDef = iterator.next();
	// PropertyDef conditionalPropertyDef = pDef
	// .getConditionalPropertyDef();
	// if (conditionalPropertyDef != null
	// && conditionalPropertyDef.equals(propertyDef)) {
	// referencedPropertyDef.add(pDef);
	// }
	// }
	// return referencedPropertyDef;
	// }

	public void propertyDefConditionalPropertyListener(ActionEvent event) {
		if (this.propertyDef.getConditionalPropertyDef() != null) {
			this.propertyDef.setConditionalPropertyInst(new PropertyInst(
					this.propertyDef.getConditionalPropertyDef()));
		} else {
			this.propertyDef.setConditionalPropertyInst(null);
		}
	}

	public void removeColl(DocDefCollection coll) {
		this.slotDefHome.getInstance().getDocDefCollections().remove(coll);
		coll.setSlotDef(null);

		//
		if (coll.getId() == null) {
			this.deactiveReferencedRules(coll);
		} else {
			this.removeReferencedRules(coll);
		}
	}

	private void removeConditionalReferences(
			List<Conditionable> conditionables, boolean cascade) {
		for (Conditionable conditionable : conditionables) {
			conditionable.getConditionalPropertyInst().setPropertyDef(null);
			conditionable.setConditionalPropertyInst(null);
			conditionable.setConditionalPropertyDef(null);
			if (cascade) {
				if (conditionable.getId() != null
						&& this.slotDefHome.isReferenced()) {
					conditionable.setActive(false);
				} else {
					// se lo SlotDef non è referenziato o la collection è stata
					// appena creata (e quindi sicuramente non referenziata
					// anche se lo SlotDef lo fosse) posso eliminarla del tutto
					//
					//
					// Se una prop cancellata era condizionale per un elemento
					// non ancora persistito o non ancora referenziato questo
					// veniva eliminato.. mi pare un po' troppo e non necessario
					// (così si mantiene comportamento uniforme a quando si crea
					// slotDef la prima volta)
					// if (conditionable instanceof DocDefCollection) {
					// this.removeColl((DocDefCollection) conditionable);
					// } else if (conditionable instanceof PropertyDef) {
					// this.removeProp((PropertyDef) conditionable);
					// }
					//
				}
			}
		}
	}

	public void removeEmbeddedProp(EmbeddedProperty embeddedProp) {
		this.slotDefHome.getInstance().getEmbeddedProperties()
				.remove(embeddedProp);
		//
		// deactiveReferencedRules(embeddedProp);
		//
		if (embeddedProp.getId() == null) {
			this.deactiveReferencedRules(embeddedProp);
		} else {
			this.removeReferencedRules(embeddedProp);
		}
	}

	public void removeNotPersistedDependentSlotDef(
			DependentSlotDef dependentSlotDef) {
		this.slotDefHome.getInstance().getDependentSlotDefs()
				.remove(dependentSlotDef);
		dependentSlotDef.setParentSlotDef(null);
		Iterator<SlotDefCloner> iterator = this.slotDefHome.getSlotDefCloner()
				.getDependentSlotDefCloners().iterator();
		while (iterator.hasNext()) {
			SlotDefCloner cloner = iterator.next();
			if (cloner.getCloned().equals(dependentSlotDef)) {
				iterator.remove();
			}
		}
	}

	public void removePersistedDependentSlotDef(
			DependentSlotDef dependentSlotDef) {
		this.slotDefHome.getInstance().getDependentSlotDefs()
				.remove(dependentSlotDef);
		dependentSlotDef.setParentSlotDef(null);
		this.slotDefHome.getEntityManager().remove(dependentSlotDef);
	}

	public void removeProp(PropertyDef prop) {
		this.removeReferencesFromCollections(prop, false);
		this.removeReferencesFromProperties(prop, false);
		this.slotDefHome.getInstance().getPropertyDefs().remove(prop);
		//
		if (prop.getId() == null) {
			this.deactiveReferencedRules(prop);
		} else {
			this.removeReferencedRules(prop);
		}
	}

	private void removeReferencedRules(Object obj) {
		List<Rule> referencedRules = this.retrieveReferencedRules(obj);
		for (Rule rule : referencedRules) {
			this.slotDefHome.getInstance().getRules().remove(rule);
			rule.setSlotDef(null);
		}
	}

	private void removeReferencesFromCollections(PropertyDef prop,
			boolean invalidateCollections) {
		// List<DocDefCollection> referencedCollections = this
		// .getConditionedCollections(prop);
		// for (DocDefCollection collection : referencedCollections) {
		// collection.getConditionalPropertyInst().setPropertyDef(null);
		// collection.setConditionalPropertyInst(null);
		// collection.setConditionalPropertyDef(null);
		// if (invalidateCollections) {
		// if (collection.getId() != null && slotDefHome.isReferenced()) {
		// collection.setActive(false);
		// } else {
		// // se lo SlotDef non è referenziato o la collection è stata
		// // appena creata (e quindi sicuramente non referenziata
		// // anche se lo SlotDef lo fosse) posso eliminarla del tutto
		// this.removeColl(collection);
		// }
		// }
		// }
		this.removeConditionalReferences(
				new ArrayList<Conditionable>(prop
						.getConditionedDocDefCollections()),
				invalidateCollections);
	}

	private void removeReferencesFromProperties(PropertyDef prop,
			boolean invalidateProperties) {
		// List<PropertyDef> conditionedPropertyDefs = this
		// .getConditionedPropertyDefs(prop);
		this.removeConditionalReferences(
				new ArrayList<Conditionable>(prop.getConditionedPropertyDefs()),
				invalidateProperties);
	}

	public void resetDependentSlotDefModel() {
		if (this.dependentSlotDef != null) {
			this.dependentSlotDef.setConditionalPropertyDef(null);
			this.dependentSlotDef.setConditionalPropertyInst(null);
			this.dependentSlotDef.setParentSlotDef(null);
			this.dependentSlotDef.setNumberOfInstances(null);

			this.dependentSlotDef = null;
			//
			this.dependentTmp = null;
			//
		}

		this.clearEditing();
	}

	public void resetPValidator() {
		if (this.propertyDef != null) {
			this.propertyDef.setConstraint(null);
		}
	}

	public List<Rule> retrieveReferencedRules(Object obj) {
		List<Rule> referencedRules = new ArrayList<Rule>();
		SlotDef slotDef = this.slotDefHome.getInstance();
		SlotDef slotDefModel = this.slotDefHome.getModel();

		Set<Rule> rules;
		if (slotDefModel == null) {
			rules = slotDef.getRules();
		} else {
			rules = slotDefModel.getRules();
		}

		for (Rule rule : rules) {
			if (obj instanceof PropertyDef) {
				PropertyDef propertyDef = (PropertyDef) obj;
				if (this.isRuleReferencedProperty(propertyDef.getName(), rule)
						&& rule.isActive()) {
					referencedRules.add(rule);
				}
			} else if (obj instanceof EmbeddedProperty) {
				EmbeddedProperty embeddedProperty = (EmbeddedProperty) obj;
				if (this.isRuleReferencedProperty(embeddedProperty.getName(),
						rule) && rule.isActive()) {
					referencedRules.add(rule);
				}
			} else if (obj instanceof DocDefCollection) {
				DocDefCollection docDefCollection = (DocDefCollection) obj;
				if (this.isRuleReferencedCollection(docDefCollection.getName(),
						rule) && rule.isActive()) {
					referencedRules.add(rule);
				}
			}
		}

		return referencedRules;
	}

	// per stamparli facile nel confirm javascript nella pagina
	public String retrieveReferencedRulesLabel(Object obj) {
		String out = "";
		List<Rule> referencedRules = this.retrieveReferencedRules(obj);
		for (int i = 0; i < referencedRules.size(); i++) {
			Rule rule = referencedRules.get(i);
			out = out.concat("Regola " + rule.getId());
			if (i < referencedRules.size() - 1) {
				out = out.concat(", ");
			}
		}
		return out;
	}

	public RuleParameterInst retrieveRuleParameterInst(Long ruleId,
			String paramName) {
		this.ruleHome.setRuleId(ruleId);
		this.ruleHome.find();
		Rule rule = this.ruleHome.getInstance();
		return rule.getEmbeddedParametersMap().get(paramName);
	}

	public String retrieveRuleParameterValue(Long ruleId, String paramName) {
		this.ruleHome.setRuleId(ruleId);
		this.ruleHome.find();
		this.ruleEditBean.init();
		PropertiesSourceContainer propertiesSourceContainer = this.ruleEditBean
				.getTargetPropertiesSourceMap().get(paramName);
		if (propertiesSourceContainer != null) {
			return propertiesSourceContainer.getLabel()
					+ " » "
					+ this.ruleEditBean.getTargetPropertyMap().get(paramName)
							.getLabel();
		}
		return "";
	}

	public String save() {
		boolean names = this.checkNames();
		boolean references = this.checkCollectionReferences();
		boolean embeddedValues = true;
		if (!this.slotDefHome.getInstance().isTemplate()) {
			embeddedValues = this.checkEmbeddedPropertyValues();
		}
		if (names && references && embeddedValues) {
			// this.persistDependentSlotDefs();

			return this.slotDefHome.persist();
		} else {
			return "failed";
		}
	}

	public void setCollection(DocDefCollection collection) {
		this.collection = collection;
	}

	public void setConditional(boolean conditional) {
		this.conditional = conditional;
	}

	public void setConditioned(String conditioned) {
		this.conditioned = conditioned;
	}

	//
	//

	public void setConverterPropertyMap(
			Map<String, PropertyDef> converterPropertyMap) {
		this.converterPropertyMap = converterPropertyMap;
	}

	public void setDependentSlotDef(DependentSlotDef dependentSlotDef) {
		this.dependentSlotDef = dependentSlotDef;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public void setEmbeddedProperty(EmbeddedProperty embeddedProperty) {
		this.embeddedProperty = embeddedProperty;
	}

	private void setKnownParameters() {
		if (this.slotDefHome.getInstance().getId() == null) {
			this.slotDefHome.getInstance().setTemplate(
					this.slotDefParameters.isModel());
			if ((this.slotDefParameters.getMode() != null && this.slotDefParameters
					.getMode().equals(SlotDefType.PRIMARY.name()))
					|| (this.slotDefParameters.getMode() != null && this.slotDefParameters
							.getMode().equals(SlotDefType.GENERAL.name()))) {
				this.slotDefHome.getInstance().setType(
						SlotDefType.valueOf(this.slotDefParameters.getMode()));
			}
		}
	}

	public void setPropertyDef(PropertyDef propertyDef) {
		this.propertyDef = propertyDef;
	}

	public String update() {
		boolean names = this.checkNames();
		boolean references = this.checkCollectionReferences();
		boolean embeddedValues = true;
		if (!this.slotDefHome.getInstance().isTemplate()) {
			embeddedValues = this.checkEmbeddedPropertyValues();
		}
		if (names && references && embeddedValues) {
			Set<PropertyDef> newPropertyDefs = new HashSet<PropertyDef>();
			Set<DocDefCollection> newDocDefCollections = new HashSet<DocDefCollection>();

			for (PropertyDef propertyDef : this.slotDefHome.getInstance()
					.getPropertyDefsAsList()) {
				if (propertyDef.getId() == null) {
					newPropertyDefs.add(propertyDef);
				}
			}
			for (DocDefCollection collection : this.slotDefHome.getInstance()
					.getDocDefCollectionsAsList()) {
				if (collection.getId() == null) {
					newDocDefCollections.add(collection);
				}
			}
			String updateResult = this.slotDefHome.update();
			if ((!newDocDefCollections.isEmpty() || !newPropertyDefs.isEmpty())
					&& this.slotDefHome.isReferenced()) {
				this.modifyReferencedSlotInsts(newPropertyDefs,
						newDocDefCollections);
			}

			//
			// this.persistDependentSlotDefs();
			//
			return updateResult;
		} else {
			return "failed";
		}
	}

}
