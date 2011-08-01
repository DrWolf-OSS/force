package it.drwolf.slot.alfresco.custom.support;

import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.enums.DataInstanceMultiplicity;
import it.drwolf.slot.interfaces.DataDefinition;
import it.drwolf.slot.interfaces.DataInstance;

public abstract class DocumentPropertyInst implements DataInstance {

	private Property property;

	// private String stringValue;
	//
	// private Integer integerValue;
	//
	// private Boolean booleanValue;
	//
	// private Date dateValue;

	private boolean editable = true;
	
	private DataInstanceMultiplicity multiplicity;

	public DocumentPropertyInst(Property property) {
		super();
		this.property = property;
	}

	// public String getStringValue() {
	// return stringValue;
	// }
	//
	// public void setStringValue(String stringValue) {
	// this.stringValue = stringValue;
	// }
	//
	// public Integer getIntegerValue() {
	// return integerValue;
	// }
	//
	// public void setIntegerValue(Integer integerValue) {
	// this.integerValue = integerValue;
	// }
	//
	// public Boolean getBooleanValue() {
	// return booleanValue;
	// }
	//
	// public void setBooleanValue(Boolean booleanValue) {
	// this.booleanValue = booleanValue;
	// }
	//
	// public Date getDateValue() {
	// return dateValue;
	// }
	//
	// public void setDateValue(Date dateValue) {
	// this.dateValue = dateValue;
	// }

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	// per le date, se non è nullo (e non dovrebbe esserlo), restituisco prima
	// il Calendar perchè è il tipo che vuole Alfresco per una Property di tipo
	// d:date
	// public Object getValue() {
	// if (this.getStringValue() != null)
	// return this.getStringValue();
	// else if (this.getIntegerValue() != null)
	// return this.getIntegerValue();
	// else if (this.getBooleanValue() != null)
	// return this.getBooleanValue();
	// else if (this.getDateValue() != null) {
	// Calendar calendar = new GregorianCalendar();
	// calendar.setTime(this.getDateValue());
	// return calendar;
	// }
	//
	// return null;
	// }
	//
	// public void setValue(Object value) {
	// // TODO COMPLETARE TRASFORMAZIONI
	// if (value instanceof String)
	// this.setStringValue((String) value);
	// else if (value instanceof BigInteger)
	// this.setIntegerValue(((BigInteger) value).intValue());
	// else if (value instanceof Boolean)
	// this.setBooleanValue((Boolean) value);
	// else if (value instanceof GregorianCalendar) {
	// this.setDateValue(((GregorianCalendar) value).getTime());
	// }
	// }

	@Override
	public String toString() {
		return this.getValue().toString();
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public DataDefinition getDataDefinition() {
		return this.property;
	}

	public DataInstanceMultiplicity getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(DataInstanceMultiplicity multiplicity) {
		this.multiplicity = multiplicity;
	}

}
