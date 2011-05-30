package it.drwolf.slot.pagebeans;

import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.entity.PropertyDef;
import it.drwolf.slot.entity.SlotDefEmbeddedProperty;
import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.enums.SlotDefType;
import it.drwolf.slot.session.DocDefCollectionHome;
import it.drwolf.slot.session.PropertytDefHome;
import it.drwolf.slot.session.SlotDefEmbeddedPropertyHome;
import it.drwolf.slot.session.SlotDefHome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

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

	private ArrayList<PropertyDef> properties = new ArrayList<PropertyDef>();
	private ArrayList<DocDefCollection> collections = new ArrayList<DocDefCollection>();
	private ArrayList<SlotDefEmbeddedProperty> embeddedProperties = new ArrayList<SlotDefEmbeddedProperty>();

	private DocDefCollection collection = new DocDefCollection();
	private PropertyDef propertyDef = new PropertyDef();
	private SlotDefEmbeddedProperty embeddedProperty = new SlotDefEmbeddedProperty();

	@Create
	public void init() {
		this.properties.addAll(slotDefHome.getInstance().getPropertyDefs());
		this.collections.addAll(slotDefHome.getInstance()
				.getDocDefCollections());
		this.embeddedProperties.addAll(slotDefHome.getInstance()
				.getEmbeddedProperties());
	}

	public ArrayList<PropertyDef> getProperties() {
		return properties;
	}

	public void setProperties(ArrayList<PropertyDef> properties) {
		this.properties = properties;
	}

	public void newPoperty() {
		this.propertyDef = new PropertyDef();
	}

	public void addProperty() {
		if (!properties.contains(this.propertyDef)) {
			properties.add(this.propertyDef);
		}
	}

	public void newCollection() {
		this.collection = new DocDefCollection();
	}

	public void addCollection() {
		if (!this.collections.contains(this.collection)) {
			collection.setSlotDef(slotDefHome.getInstance());
			collections.add(this.collection);
		}
	}

	public void newEmbeddedProperty() {
		this.embeddedProperty = new SlotDefEmbeddedProperty();
	}

	public void addEmbeddedProperty() {
		if (!this.embeddedProperties.contains(this.embeddedProperty)) {
			embeddedProperty.setSlotDef(slotDefHome.getInstance());
			embeddedProperties.add(this.embeddedProperty);
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

	public void save() {
		slotDefHome.getInstance().setPropertyDefs(
				new HashSet<PropertyDef>(properties));
		slotDefHome.getInstance().setDocDefCollections(
				new HashSet<DocDefCollection>(collections));
		slotDefHome.getInstance().setEmbeddedProperties(
				new HashSet<SlotDefEmbeddedProperty>(embeddedProperties));
		slotDefHome.persist();
	}

	public void update() {
		Set<DocDefCollection> docDefCollections = slotDefHome.getInstance()
				.getDocDefCollections();
		Iterator<DocDefCollection> iterator = docDefCollections.iterator();
		while (iterator.hasNext()) {
			DocDefCollection docDefCollection = iterator.next();
			if (!this.collections.contains(docDefCollection)) {
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
			if (!this.properties.contains(propertyDef)) {
				iterator2.remove();
				propertytDefHome.setInstance(propertyDef);
				propertytDefHome.remove();
			}
		}

		Set<SlotDefEmbeddedProperty> slotDefEmbeddedProperties = slotDefHome
				.getInstance().getEmbeddedProperties();
		Iterator<SlotDefEmbeddedProperty> iterator3 = slotDefEmbeddedProperties
				.iterator();
		while (iterator3.hasNext()) {
			SlotDefEmbeddedProperty embeddedProperty = iterator3.next();
			if (!this.embeddedProperties.contains(embeddedProperty)) {
				iterator3.remove();
				slotDefEmbeddedPropertyHome.setInstance(embeddedProperty);
				slotDefEmbeddedPropertyHome.remove();
			}
		}

		for (PropertyDef propertyDef : properties) {
			if (!propertyDefs.contains(propertyDef)) {
				propertyDefs.add(propertyDef);
			}
		}

		for (DocDefCollection collection : collections) {
			if (!docDefCollections.contains(collection)) {
				docDefCollections.add(collection);
			}
		}

		for (SlotDefEmbeddedProperty embeddedProperty : embeddedProperties) {
			if (!slotDefEmbeddedProperties.contains(embeddedProperty)) {
				slotDefEmbeddedProperties.add(embeddedProperty);
			}
		}

		slotDefHome.update();
	}

	public void removeEmbeddedProp(SlotDefEmbeddedProperty embeddedProp) {
		this.embeddedProperties.remove(embeddedProp);
	}

	public void editEmbeddedProp(SlotDefEmbeddedProperty embeddedProp) {
		this.embeddedProperty = embeddedProp;
	}

	public void removeProp(PropertyDef prop) {
		this.properties.remove(prop);
	}

	public void editProp(PropertyDef prop) {
		this.propertyDef = prop;
	}

	public void removeColl(DocDefCollection coll) {
		this.collections.remove(coll);
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

	public ArrayList<DocDefCollection> getCollections() {
		return collections;
	}

	public void setCollections(ArrayList<DocDefCollection> collections) {
		this.collections = collections;
	}

	public PropertyDef getPropertyDef() {
		return propertyDef;
	}

	public void setPropertyDef(PropertyDef propertyDef) {
		this.propertyDef = propertyDef;
	}

	public ArrayList<SlotDefEmbeddedProperty> getEmbeddedProperties() {
		return embeddedProperties;
	}

	public void setEmbeddedProperties(
			ArrayList<SlotDefEmbeddedProperty> embeddedProperties) {
		this.embeddedProperties = embeddedProperties;
	}

	public SlotDefEmbeddedProperty getEmbeddedProperty() {
		return embeddedProperty;
	}

	public void setEmbeddedProperty(SlotDefEmbeddedProperty embeddedProperty) {
		this.embeddedProperty = embeddedProperty;
	}

}
