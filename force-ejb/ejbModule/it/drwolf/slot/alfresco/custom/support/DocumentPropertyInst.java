package it.drwolf.slot.alfresco.custom.support;

import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.entity.ValueInstance;
import it.drwolf.slot.interfaces.DataDefinition;
import it.drwolf.slot.interfaces.DataInstance;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DocumentPropertyInst extends ValueInstance implements DataInstance {

	private Property property;

	// private String stringValue;
	//
	// private BigInteger integerValue;
	//
	// private Boolean booleanValue;
	//
	// private Date dateValue;
	//
	// private GregorianCalendar calendarValue;

	private boolean editable = true;

	public DocumentPropertyInst(Property property) {
		super();
		this.property = property;
		// if (property.getType().equals("d:date")) {
		// this.setDateValue(new Date());
		// }
	}

	// public String getStringValue() {
	// return stringValue;
	// }
	//
	// public void setStringValue(String stringValue) {
	// this.stringValue = stringValue;
	// }

	// public Integer getIntegerValue() {
	// return integerValue;
	// }
	//
	// public void setIntegerValue(BigInteger integerValue) {
	// this.integerValue = integerValue;
	// }

	// public Boolean getBooleanValue() {
	// return booleanValue;
	// }
	//
	// public void setBooleanValue(Boolean booleanValue) {
	// this.booleanValue = booleanValue;
	// }

	// public Date getDateValue() {
	// return dateValue;
	// }

	// public void setDateValue(Date dateValue) {
	// this.dateValue = dateValue;
	// this.calendarValue = new GregorianCalendar();
	// this.calendarValue.setTime(this.dateValue);
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
	public Object getValue() {
		if (this.getStringValue() != null)
			return this.getStringValue();
		else if (this.getIntegerValue() != null)
			return this.getIntegerValue();
		else if (this.getBooleanValue() != null)
			return this.getBooleanValue();
		// else if (this.getCalendarValue() != null)
		// return this.getCalendarValue();
		else if (this.getDateValue() != null) {
			// return this.getDateValue();
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(this.getDateValue());
			return calendar;
		}

		return null;
	}

	public void setValue(Object value) {
		// TODO COMPLETARE TRASFORMAZIONI
		if (value instanceof String)
			this.setStringValue((String) value);
		else if (value instanceof BigInteger)
			this.setIntegerValue(((BigInteger) value).intValue());
		else if (value instanceof Boolean)
			this.setBooleanValue((Boolean) value);
		else if (value instanceof GregorianCalendar) {
			this.setDateValue(((GregorianCalendar) value).getTime());
			// this.setCalendarValue((GregorianCalendar) value);
		}
	}

	@Override
	public String toString() {
		return this.getValue().toString();
	}

	// public GregorianCalendar getCalendarValue() {
	// return calendarValue;
	// }
	//
	// public void setCalendarValue(GregorianCalendar calendar) {
	// this.calendarValue = calendar;
	// this.dateValue = calendar.getTime();
	// }

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public DataDefinition getDataDefinition() {
		return this.property;
	}

}
