package it.drwolf.slot.alfresco.custom.support;

import it.drwolf.slot.alfresco.custom.model.Property;

import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;

public class EmbeddedPropertyInst {

	private Property property;

	private String stringValue;

	private BigInteger integerValue;

	private Boolean booleanValue;

	private Date dateValue;

	private GregorianCalendar calendarValue;

	private boolean editable = true;

	public EmbeddedPropertyInst(Property property) {
		super();
		this.property = property;
		if (property.getType().equals("d:date")) {
			this.setDateValue(new Date());
		}
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public BigInteger getIntegerValue() {
		return integerValue;
	}

	public void setIntegerValue(BigInteger integerValue) {
		this.integerValue = integerValue;
	}

	public Boolean getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
		this.calendarValue = new GregorianCalendar();
		this.calendarValue.setTime(this.dateValue);
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	// per le date, se non è nullo (e non dovrebbe esserlo), restituisco prima
	// il Calendar perchè è il tipo che vuole Alfresco per una Property di tipo
	// d:date
	public Object getValue() {
		if (this.stringValue != null)
			return this.stringValue;
		else if (this.integerValue != null)
			return this.integerValue;
		else if (this.booleanValue != null)
			return this.booleanValue;
		else if (this.calendarValue != null)
			return this.calendarValue;
		else if (this.dateValue != null)
			return this.dateValue;

		return null;
	}

	public void setValue(Object value) {
		// TODO COMPLETARE TRASFORMAZIONI
		if (value instanceof String)
			this.stringValue = (String) value;
		else if (value instanceof BigInteger)
			this.integerValue = (BigInteger) value;
		else if (value instanceof Integer)
			this.integerValue = (BigInteger) value;
		else if (value instanceof Boolean)
			this.booleanValue = (Boolean) value;
		else if (value instanceof GregorianCalendar) {
			this.dateValue = ((GregorianCalendar) value).getTime();
			this.calendarValue = (GregorianCalendar) value;
		}
	}

	@Override
	public String toString() {
		return this.getValue().toString();
	}

	public GregorianCalendar getCalendarValue() {
		return calendarValue;
	}

	public void setCalendarValue(GregorianCalendar calendar) {
		this.calendarValue = calendar;
		this.dateValue = calendar.getTime();
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

}
