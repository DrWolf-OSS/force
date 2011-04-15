package it.drwolf.force.pagebeans;

import it.drwolf.force.entity.DocDefCollection;
import it.drwolf.force.entity.PropertyDef;
import it.drwolf.force.enums.DataType;
import it.drwolf.force.session.SlotDefHome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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

	private ArrayList<PropertyDef> properties = new ArrayList<PropertyDef>();
	private ArrayList<DocDefCollection> collections = new ArrayList<DocDefCollection>();

	private String name;
	private DataType type;

	private DocDefCollection collection = new DocDefCollection();

	@Create
	public void init() {
		this.properties.addAll(slotDefHome.getInstance().getPropertyDefs());
	}

	public ArrayList<PropertyDef> getProperties() {
		return properties;
	}

	public void setProperties(ArrayList<PropertyDef> properties) {
		this.properties = properties;
	}

	public void newPoperty() {
		this.name = "";
		this.type = DataType.STRING;
	}

	public void addProperty() {
		properties.add(new PropertyDef(name, type));
	}

	public void newCollection() {
		this.collection = new DocDefCollection();
	}

	public void addCollection() {
		collections.add(this.collection);
		collection = new DocDefCollection();
	}

	// public PropertyDef getPropertyToEdit() {
	// return propertyToEdit;
	// }
	//
	// public void setPropertyToEdit(PropertyDef propertyToEdit) {
	// this.propertyToEdit = propertyToEdit;
	// }

	@Factory("dataTypes")
	public List<DataType> getPropertyTypes() {
		return Arrays.asList(DataType.values());
	}

	public void save() {
		slotDefHome.getInstance().setPropertyDefs(
				new HashSet<PropertyDef>(properties));
		slotDefHome.persist();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
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

}
