package it.drwolf.slot.pagebeans;

import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.application.CustomModelController;
import it.drwolf.slot.entity.DocDef;
import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.entity.Rule;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.entity.listeners.RuleListener;
import it.drwolf.slot.enums.RuleType;
import it.drwolf.slot.interfaces.IRuleVerifier;
import it.drwolf.slot.pagebeans.support.PropertiesSourceContainer;
import it.drwolf.slot.pagebeans.support.PropertyContainer;
import it.drwolf.slot.ruleverifier.VerifierParameter;
import it.drwolf.slot.session.RuleHome;
import it.drwolf.slot.session.SlotDefHome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("ruleEditBean")
@Scope(ScopeType.CONVERSATION)
public class RuleEditBean {

	@In(create = true)
	private SlotDefHome slotDefHome;

	@In(create = true)
	private RuleHome ruleHome;

	@In(create = true)
	private CustomModelController customModelController;

	private RuleListener ruleListener = new RuleListener();

	private DocDefCollection activeCollection;

	private HashMap<Long, List<PropertyContainer>> properties = new HashMap<Long, List<PropertyContainer>>();

	private HashMap<String, List<PropertiesSourceContainer>> sourcePropertiesSourceMap = new HashMap<String, List<PropertiesSourceContainer>>();

	private HashMap<String, PropertiesSourceContainer> targetPropertiesSourceMap = new HashMap<String, PropertiesSourceContainer>();
	private HashMap<String, PropertyContainer> targetPropertyMap = new HashMap<String, PropertyContainer>();

	public void init() {
		SlotDef slotDef = slotDefHome.getInstance();
		Set<DocDefCollection> docDefCollections = slotDef
				.getDocDefCollections();
		for (DocDefCollection collection : docDefCollections) {

			Set<Property> aspectProperties = new HashSet<Property>();
			DocDef docDef = collection.getDocDef();
			Set<String> aspectIds = docDef.getAspectIds();
			for (String aspectId : aspectIds) {
				aspectProperties.addAll(customModelController
						.getProperties(aspectId));
			}

			ArrayList<PropertyContainer> aspectPropertiesAsList = new ArrayList<PropertyContainer>();
			for (Property property : aspectProperties) {
				PropertyContainer container = new PropertyContainer(property);
				aspectPropertiesAsList.add(container);
			}
			properties.put(docDef.getId(), aspectPropertiesAsList);
		}
	}

	@Factory("ruleTypes")
	public List<RuleType> getRuleTypes() {
		return Arrays.asList(RuleType.values());
	}

	public void ruleTypeListener(ActionEvent event) {
		Rule instance = this.ruleHome.getInstance();
		ruleListener.setVerifier(instance);
		IRuleVerifier verifier = instance.getVerifier();
		sourcePropertiesSourceMap = new HashMap<String, List<PropertiesSourceContainer>>();

		if (verifier != null) {
			List<VerifierParameter> inParams = verifier.getInParams();

			SlotDef slotDef = slotDefHome.getInstance();

			Set<DocDefCollection> docDefCollections = slotDef
					.getDocDefCollections();

			for (VerifierParameter verifierParameter : inParams) {
				ArrayList<PropertiesSourceContainer> propertiesSourceContainerList = new ArrayList<PropertiesSourceContainer>();
				for (DocDefCollection collection : docDefCollections) {
					PropertiesSourceContainer sourceContainer = new PropertiesSourceContainer(
							collection);
					propertiesSourceContainerList.add(sourceContainer);
				}
				propertiesSourceContainerList
						.add(new PropertiesSourceContainer(slotDef));
				sourcePropertiesSourceMap.put(verifierParameter.getName(),
						propertiesSourceContainerList);
			}

			System.out.println("---> assigned listener " + verifier);
		}
	}

	public DocDefCollection getActiveCollection() {
		return activeCollection;
	}

	public void setActiveCollection(DocDefCollection activeCollection) {
		this.activeCollection = activeCollection;
	}

	public List<Property> getProperties(Long docDefCollectionId) {
		Set<String> aspectIds = activeCollection.getDocDef().getAspectIds();
		Set<Property> properties = new HashSet<Property>();
		for (String aspectId : aspectIds) {
			properties.addAll(customModelController.getProperties(aspectId));
		}
		return new ArrayList<Property>(properties);
	}

	// public class PropertyContainer {
	//
	// private Property property;
	// private PropertyDef propertyDef;
	//
	// public PropertyContainer(PropertyDef propertyDef) {
	// super();
	// this.propertyDef = propertyDef;
	// }
	//
	// public PropertyContainer(Property property) {
	// super();
	// this.property = property;
	// }
	//
	// public Property getProperty() {
	// return property;
	// }
	//
	// public void setProperty(Property property) {
	// this.property = property;
	// }
	//
	// public PropertyDef getPropertyDef() {
	// return propertyDef;
	// }
	//
	// public void setPropertyDef(PropertyDef propertyDef) {
	// this.propertyDef = propertyDef;
	// }
	//
	// public String getLabel() {
	// if (property != null) {
	// return property.getTitle();
	// } else if (propertyDef != null) {
	// return propertyDef.getName();
	// }
	// return "";
	// }
	//
	// @Override
	// public String toString() {
	// if (property != null) {
	// return "Property:" + property.getTitle();
	// } else if (propertyDef != null) {
	// return "PropertyDef:" + propertyDef.getName();
	// }
	// return "";
	// }
	//
	// }

	// public class PropertiesSourceContainer {
	// private SlotDef slotDef;
	// private DocDefCollection docDefCollection;
	// private List<PropertyContainer> properties;
	//
	// public PropertiesSourceContainer(Object object) {
	// if (object instanceof DocDefCollection) {
	// this.docDefCollection = (DocDefCollection) object;
	// initPropertiesAsCollection();
	// } else if (object instanceof SlotDef) {
	// this.slotDef = (SlotDef) object;
	// }
	// }
	//
	// public PropertiesSourceContainer(DocDefCollection docDefCollection) {
	// super();
	// this.docDefCollection = docDefCollection;
	// initPropertiesAsCollection();
	// }
	//
	// public PropertiesSourceContainer(SlotDef slotDef) {
	// super();
	// this.slotDef = slotDef;
	// }
	//
	// public SlotDef getSlotDef() {
	// return slotDef;
	// }
	//
	// public void setSlotDef(SlotDef slotDef) {
	// this.slotDef = slotDef;
	// }
	//
	// public DocDefCollection getDocDefCollection() {
	// return docDefCollection;
	// }
	//
	// public void setDocDefCollection(DocDefCollection docDefCollection) {
	// this.docDefCollection = docDefCollection;
	// }
	//
	// public List<PropertyContainer> getProperties() {
	// return properties;
	// }
	//
	// public void setProperties(List<PropertyContainer> properties) {
	// this.properties = properties;
	// }
	//
	// public String getLabel() {
	// if (slotDef != null) {
	// return slotDef.getName();
	// } else if (docDefCollection != null) {
	// return docDefCollection.getName();
	// }
	// return "";
	// }
	//
	// private void initPropertiesAsCollection() {
	// Set<Property> aspectProperties = new HashSet<Property>();
	// DocDef docDef = this.docDefCollection.getDocDef();
	// Set<String> aspectIds = docDef.getAspectIds();
	// for (String aspectId : aspectIds) {
	// aspectProperties.addAll(customModelController
	// .getProperties(aspectId));
	// }
	//
	// ArrayList<PropertyContainer> aspectPropertiesAsList = new
	// ArrayList<PropertyContainer>();
	// for (Property property : aspectProperties) {
	// PropertyContainer container = new PropertyContainer(property);
	// aspectPropertiesAsList.add(container);
	// }
	// this.setProperties(aspectPropertiesAsList);
	// }
	//
	// @Override
	// public String toString() {
	// if (slotDef != null) {
	// return "SlotDef:" + slotDef.getId();
	// } else if (docDefCollection != null) {
	// return "DocDefCollection:" + docDefCollection.getId();
	// }
	// return "";
	// }
	//
	// }

	public HashMap<String, List<PropertiesSourceContainer>> getSourcePropertiesSourceMap() {
		return sourcePropertiesSourceMap;
	}

	public void setSourcePropertiesSourceMap(
			HashMap<String, List<PropertiesSourceContainer>> sourcePropertiesSourceMap) {
		this.sourcePropertiesSourceMap = sourcePropertiesSourceMap;
	}

	public HashMap<String, PropertiesSourceContainer> getTargetPropertiesSourceMap() {
		return targetPropertiesSourceMap;
	}

	public void setTargetPropertiesSourceMap(
			HashMap<String, PropertiesSourceContainer> targetPropertiesSourceMap) {
		this.targetPropertiesSourceMap = targetPropertiesSourceMap;
	}

	public HashMap<String, PropertyContainer> getTargetPropertyMap() {
		return targetPropertyMap;
	}

	public void setTargetPropertyMap(
			HashMap<String, PropertyContainer> targetPropertyMap) {
		this.targetPropertyMap = targetPropertyMap;
	}

}
