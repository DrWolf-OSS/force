package it.drwolf.slot.entitymanager;

import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.entity.DependentSlotDef;
import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.entity.EmbeddedProperty;
import it.drwolf.slot.entity.PropertyDef;
import it.drwolf.slot.entity.PropertyInst;
import it.drwolf.slot.entity.Rule;
import it.drwolf.slot.entity.RuleParameterInst;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.ruleverifier.RuleParametersResolver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SlotDefCloner {

	private SlotDef model;

	private SlotDef cloned;

	// @In(create = true)
	private RuleParametersResolver ruleParametersResolver = (RuleParametersResolver) org.jboss.seam.Component
			.getInstance("ruleParametersResolver");

	private Map<Long, SlotDefCloner> dependentSlotDefCloners = new HashMap<Long, SlotDefCloner>();

	// @In(create = true)
	// private RuleHome ruleHome;

	// private void cloneConditionaProperties(DependentSlotDef dependentSlotDef,
	// DependentSlotDef dependentCloned) {
	// if (dependentSlotDef.getConditionalPropertyDef() != null) {
	// PropertyDef clonedConditionalPropertyDef = dependentCloned
	// .getParentSlotDef().retrievePropertyDefByName(
	// dependentSlotDef.getConditionalPropertyDef()
	// .getName());
	// PropertyInst clonedConditionalPropertyInst = new PropertyInst(
	// clonedConditionalPropertyDef);
	// dependentCloned
	// .setConditionalPropertyDef(clonedConditionalPropertyDef);
	//
	// PropertyInst conditionalPropertyInst = dependentSlotDef
	// .getConditionalPropertyInst();
	//
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
	// dependentCloned
	// .setConditionalPropertyInst(clonedConditionalPropertyInst);
	// }
	// }

	private DependentSlotDef cloneDependentSlotDef(
			DependentSlotDef dependentSlotDef, SlotDef clonedSlotDef) {
		SlotDefCloner cloner = new SlotDefCloner();
		cloner.setModel(dependentSlotDef);
		cloner.cloneModel();
		DependentSlotDef dependentCloned = (DependentSlotDef) cloner
				.getCloned();
		dependentCloned.setParentSlotDef(clonedSlotDef);

		if (dependentSlotDef.getConditionalPropertyDef() != null) {
			PropertyDef clonedConditionalPropertyDef = dependentCloned
					.getParentSlotDef().retrievePropertyDefByName(
							dependentSlotDef.getConditionalPropertyDef()
									.getName());
			PropertyInst clonedConditionalPropertyInst = new PropertyInst(
					clonedConditionalPropertyDef);
			dependentCloned
					.setConditionalPropertyDef(clonedConditionalPropertyDef);

			PropertyInst conditionalPropertyInst = dependentSlotDef
					.getConditionalPropertyInst();

			//
			clonedConditionalPropertyInst
					.setStringValue(conditionalPropertyInst.getStringValue());
			clonedConditionalPropertyInst
					.setIntegerValue(conditionalPropertyInst.getIntegerValue());
			clonedConditionalPropertyInst
					.setBooleanValue(conditionalPropertyInst.getBooleanValue());
			clonedConditionalPropertyInst.setDateValue(conditionalPropertyInst
					.getDateValue());
			clonedConditionalPropertyInst
					.setMultiplicity(conditionalPropertyInst.getMultiplicity());
			clonedConditionalPropertyInst.setValues(new HashSet<String>(
					conditionalPropertyInst.getValues()));
			//

			dependentCloned
					.setConditionalPropertyInst(clonedConditionalPropertyInst);

			this.dependentSlotDefCloners.put(dependentSlotDef.getId(), cloner);
		}

		return dependentCloned;
	}

	private DocDefCollection cloneDocDefCollection(DocDefCollection collection,
			SlotDef clonedSlotDef) {
		DocDefCollection clonedCollection = new DocDefCollection();
		clonedCollection.setName(collection.getName());
		clonedCollection.setDocDef(collection.getDocDef());
		clonedCollection.setMin(collection.getMin());
		clonedCollection.setMax(collection.getMax());

		//
		clonedCollection.setQuantifier(collection.getQuantifier());

		if (collection.getConditionalPropertyDef() != null) {
			// SlotDef slotDef = collection.getSlotDef();
			PropertyDef clonedConditionalPropertyDef = clonedSlotDef
					.retrievePropertyDefByName(collection
							.getConditionalPropertyDef().getName());
			clonedCollection
					.setConditionalPropertyDef(clonedConditionalPropertyDef);
			PropertyInst conditionalPropertyInst = collection
					.getConditionalPropertyInst();
			PropertyInst clonedConditionalPropertyInst = new PropertyInst(
					clonedConditionalPropertyDef);
			clonedCollection
					.setConditionalPropertyInst(clonedConditionalPropertyInst);
			//
			clonedConditionalPropertyInst
					.setStringValue(conditionalPropertyInst.getStringValue());
			clonedConditionalPropertyInst
					.setIntegerValue(conditionalPropertyInst.getIntegerValue());
			clonedConditionalPropertyInst
					.setBooleanValue(conditionalPropertyInst.getBooleanValue());
			clonedConditionalPropertyInst.setDateValue(conditionalPropertyInst
					.getDateValue());
			clonedConditionalPropertyInst
					.setMultiplicity(conditionalPropertyInst.getMultiplicity());
			clonedConditionalPropertyInst.setValues(new HashSet<String>(
					conditionalPropertyInst.getValues()));
			//

		}
		return clonedCollection;
	}

	private EmbeddedProperty cloneEmbeddedProperty(
			EmbeddedProperty embeddedProperty) {
		EmbeddedProperty clonedEmbeddedProperty = new EmbeddedProperty();
		clonedEmbeddedProperty.setName(embeddedProperty.getName());
		clonedEmbeddedProperty.setDataType(embeddedProperty.getDataType());
		clonedEmbeddedProperty.setMultiple(embeddedProperty.isMultiple());

		clonedEmbeddedProperty.setDictionary(embeddedProperty.getDictionary());
		clonedEmbeddedProperty.setValues(new HashSet<String>());
		return clonedEmbeddedProperty;
	}

	public void cloneModel() {
		// this.model = slotDef;
		SlotDef clonedSlotDef = null;
		if (this.model instanceof DependentSlotDef) {
			clonedSlotDef = new DependentSlotDef();
		} else {
			clonedSlotDef = new SlotDef();
		}
		clonedSlotDef.setName("Copia di " + this.model.getName());
		clonedSlotDef.setType(this.model.getType());

		for (EmbeddedProperty embeddedProperty : this.model
				.getEmbeddedProperties()) {
			if (embeddedProperty.isActive()) {
				EmbeddedProperty clonedEmbeddedProperty = this
						.cloneEmbeddedProperty(embeddedProperty);
				clonedSlotDef.getEmbeddedProperties().add(
						clonedEmbeddedProperty);
			}
		}

		for (PropertyDef propertyDef : this.model.getPropertyDefs()) {
			if (propertyDef.isActive()) {
				PropertyDef clonedPropertyDef = this
						.clonePropertyDef(propertyDef);
				clonedSlotDef.getPropertyDefs().add(clonedPropertyDef);
			}
		}

		for (DocDefCollection collection : this.model.getDocDefCollections()) {
			if (collection.isActive()) {
				DocDefCollection clonedDocDefCollection = this
						.cloneDocDefCollection(collection, clonedSlotDef);
				clonedDocDefCollection.setSlotDef(clonedSlotDef);
				clonedSlotDef.getDocDefCollections()
						.add(clonedDocDefCollection);
			}
		}

		//
		// if (clonedSlotDef instanceof DependentSlotDef) {
		// this.cloneConditionaProperties((DependentSlotDef) this.model,
		// (DependentSlotDef) clonedSlotDef);
		// }
		//

		//
		for (DependentSlotDef dependent : this.model.getDependentSlotDefs()) {
			if (dependent.isActive()) {
				DependentSlotDef clonedDependentSlotDef = this
						.cloneDependentSlotDef(dependent, clonedSlotDef);
				// clonedDependentSlotDef.setParentSlotDef(clonedSlotDef);
				clonedSlotDef.getDependentSlotDefs()
						.add(clonedDependentSlotDef);
			}
		}
		//
		this.cloned = clonedSlotDef;
	}

	private PropertyDef clonePropertyDef(PropertyDef propertyDef) {
		PropertyDef clonedPropertyDef = new PropertyDef();
		clonedPropertyDef.setName(propertyDef.getName());
		clonedPropertyDef.setDataType(propertyDef.getDataType());
		clonedPropertyDef.setMultiple(propertyDef.isMultiple());
		clonedPropertyDef.setRequired(propertyDef.isRequired());
		clonedPropertyDef.setDictionary(propertyDef.getDictionary());
		return clonedPropertyDef;
	}

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
						+ this.cloned.getId().toString();
			} else if (sourceDef instanceof DocDefCollection) {
				clonedParam = clonedParam.concat(DocDefCollection.class
						.getName()) + ":";
				DocDefCollection docDefCollection = (DocDefCollection) sourceDef;
				DocDefCollection clonedDocDefCollection = this.cloned
						.retrieveDocDefCollectionByName(docDefCollection
								.getName());
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
				PropertyDef clonedPropertyDef = this.cloned
						.retrievePropertyDefByName(((PropertyDef) fieldDef)
								.getName());
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
				EmbeddedProperty clonedEmbeddedProperty = this.cloned
						.retrieveEmbeddedPropertyByName(((EmbeddedProperty) fieldDef)
								.getName());
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
		clonedRule.setSlotDef(this.cloned);
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

	public void cloneRules() {
		if (this.model != null && this.cloned != null
				&& this.cloned.getId() != null) {
			for (Rule rule : this.model.getRules()) {
				//
				if (rule.isActive()) {
					Rule clonedRule = this.cloneRule(rule);
					if (clonedRule != null) {
						this.cloned.getRules().add(clonedRule);
						// this.ruleHome.setInstance(clonedRule);
						// this.ruleHome.persist();
					}
				}
				//
			}
		}
	}

	public SlotDef getCloned() {
		return this.cloned;
	}

	public Map<Long, SlotDefCloner> getDependentSlotDefCloners() {
		return this.dependentSlotDefCloners;
	}

	public SlotDef getModel() {
		return this.model;
	}

	public void setModel(SlotDef model) {
		this.model = model;
	}

}
