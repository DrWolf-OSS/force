package it.drwolf.slot.pagebeans;

import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.entity.DocInstCollection;
import it.drwolf.slot.entity.EmbeddedProperty;
import it.drwolf.slot.entity.PropertyDef;
import it.drwolf.slot.entity.PropertyInst;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.entity.SlotInst;
import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.enums.SlotDefType;
import it.drwolf.slot.interfaces.DataDefinition;
import it.drwolf.slot.session.DocDefCollectionHome;
import it.drwolf.slot.session.PropertytDefHome;
import it.drwolf.slot.session.SlotDefEmbeddedPropertyHome;
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

	@In(create = true)
	private PropertytDefHome propertytDefHome;

	@In(create = true)
	private DocDefCollectionHome docDefCollectionHome;

	@In(create = true)
	private SlotDefEmbeddedPropertyHome slotDefEmbeddedPropertyHome;

	private DocDefCollection collection = new DocDefCollection();
	private PropertyDef propertyDef = new PropertyDef();
	private EmbeddedProperty embeddedProperty = new EmbeddedProperty();

	private Map<String, PropertyDef> converterPropertyMap = new HashMap<String, PropertyDef>();

	@In(create = true)
	private SlotInstHome slotInstHome;

	@Create
	public void init() {
	}

	public void newPoperty() {
		this.propertyDef = new PropertyDef();
	}

	public void addProperty() {
		SlotDef instance = slotDefHome.getInstance();
		if (!instance.getPropertyDefsAsList().contains(this.propertyDef)) {
			instance.getPropertyDefs().add(this.propertyDef);
		}
		if (converterPropertyMap.get(propertyDef.getUuid()) == null) {
			converterPropertyMap.put(propertyDef.getUuid(), propertyDef);
		}
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

	public void newEmbeddedProperty() {
		this.embeddedProperty = new EmbeddedProperty();
	}

	public void addEmbeddedProperty() {
		if (!slotDefHome.getInstance().getEmbeddedPropertiesAsList()
				.contains(this.embeddedProperty)) {
			slotDefHome.getInstance().getEmbeddedProperties()
					.add(this.embeddedProperty);
		}
	}

	@Factory("dataTypes")
	public List<DataType> getPropertyTypes() {
		return Arrays.asList(DataType.values());
	}

	@Factory("slotDefTypes")
	public List<SlotDefType> getSlotDefTypes() {
		return Arrays.asList(SlotDefType.values());
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

	public String save() {
		if (checkNames()) {
			return slotDefHome.persist();
		} else {
			return "failed";
		}
	}

	public String update() {
		if (checkNames()) {
			Set<DocDefCollection> docDefCollections = slotDefHome.getInstance()
					.getDocDefCollections();
			Iterator<DocDefCollection> iterator = docDefCollections.iterator();
			while (iterator.hasNext()) {
				DocDefCollection docDefCollection = iterator.next();
				if (!slotDefHome.getInstance().getDocDefCollectionsAsList()
						.contains(docDefCollection)) {
					iterator.remove();
					docDefCollectionHome.setInstance(docDefCollection);
					docDefCollectionHome.remove();
				}
			}
			Set<PropertyDef> propertyDefs = slotDefHome.getInstance()
					.getPropertyDefs();
			Iterator<PropertyDef> iterator2 = propertyDefs.iterator();
			while (iterator2.hasNext()) {
				PropertyDef propertyDef = iterator2.next();
				if (!slotDefHome.getInstance().getPropertyDefsAsList()
						.contains(propertyDef)) {
					iterator2.remove();
					propertytDefHome.setInstance(propertyDef);
					propertytDefHome.remove();
				}
			}
			Set<EmbeddedProperty> slotDefEmbeddedProperties = slotDefHome
					.getInstance().getEmbeddedProperties();
			Iterator<EmbeddedProperty> iterator3 = slotDefEmbeddedProperties
					.iterator();
			while (iterator3.hasNext()) {
				EmbeddedProperty embeddedProperty = iterator3.next();
				if (!slotDefHome.getInstance().getEmbeddedPropertiesAsList()
						.contains(embeddedProperty)) {
					iterator3.remove();
					slotDefEmbeddedPropertyHome.setInstance(embeddedProperty);
					slotDefEmbeddedPropertyHome.remove();
				}
			}

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
			//
			modifyReferencedSlotInsts(newPropertyDefs, newDocDefCollections);
			//
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
	}

	public void editEmbeddedProp(EmbeddedProperty embeddedProp) {
		this.embeddedProperty = embeddedProp;
	}

	public void removeProp(PropertyDef prop) {
		slotDefHome.getInstance().getPropertyDefs().remove(prop);
	}

	public void editProp(PropertyDef prop) {
		this.propertyDef = prop;
	}

	public void removeColl(DocDefCollection coll) {
		slotDefHome.getInstance().getDocDefCollections().remove(coll);
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

}
