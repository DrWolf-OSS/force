package it.drwolf.slot.alfresco.custom.support;

import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.enums.DataInstanceMultiplicity;

import java.util.ArrayList;
import java.util.List;

public class DocumentMultiplePropertyInst extends DocumentPropertyInst {

	// private Property property;
	//
	// private boolean editable = true;

	private List<String> values = new ArrayList<String>();

	public DocumentMultiplePropertyInst(Property property) {
		super(property);
		this.setMultiplicity(DataInstanceMultiplicity.MULTIPLE);
	}

	public Object getValue() {
		return values;
	}

	// public DataDefinition getDataDefinition() {
	// return property;
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
	// public boolean isEditable() {
	// return editable;
	// }
	//
	// public void setEditable(boolean editable) {
	// this.editable = editable;
	// }

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

}
