package it.drwolf.slot.alfresco.custom.support;

import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.enums.DataInstanceMultiplicity;
import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.interfaces.DataDefinition;
import it.drwolf.slot.interfaces.DataInstance;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DocumentPropertyInst implements DataInstance {

	private Property property;

	private DataInstanceMultiplicity multiplicity;

	//
	private String stringValue;

	private Integer integerValue;

	private Boolean booleanValue;

	private Date dateValue;

	private boolean editable = true;
	//

	private List<String> values = new ArrayList<String>();

	public DocumentPropertyInst(Property property) {
		super();
		this.property = property;
		if (property.isMultiple()) {
			this.setMultiplicity(DataInstanceMultiplicity.MULTIPLE);
		} else {
			this.setMultiplicity(DataInstanceMultiplicity.SINGLE);
		}
	}

	public Boolean getBooleanValue() {
		return this.booleanValue;
	}

	public DataDefinition getDataDefinition() {
		return this.property;
	}

	public Date getDateValue() {
		return this.dateValue;
	}

	public Integer getIntegerValue() {
		return this.integerValue;
	}

	public DataInstanceMultiplicity getMultiplicity() {
		return this.multiplicity;
	}

	public Property getProperty() {
		return this.property;
	}

	public String getStringValue() {
		return this.stringValue;
	}

	// per le date, se non è nullo (e non dovrebbe esserlo), restituisco prima
	// il Calendar perchè è il tipo che vuole Alfresco per una Property di tipo
	// d:date
	public Object getValue() {
		if (!this.getProperty().isMultiple()) {
			if (this.getProperty().getDataType().equals(DataType.STRING)) {
				return this.getStringValue();
			} else if (this.getProperty().getDataType()
					.equals(DataType.INTEGER)) {
				return this.getIntegerValue();
			} else if (this.getProperty().getDataType()
					.equals(DataType.BOOLEAN)) {
				return this.getBooleanValue();
			} else if (this.getProperty().getDataType().equals(DataType.DATE)) {
				Calendar calendar = new GregorianCalendar();
				calendar.setTime(this.getDateValue());
				return calendar;
			}
		} else {
			return this.values;
		}
		return null;
	}

	public List<String> getValues() {
		return this.values;
	}

	public boolean isEditable() {
		return this.editable;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}

	public void setMultiplicity(DataInstanceMultiplicity multiplicity) {
		this.multiplicity = multiplicity;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public void setValue(Object value) {
		if (!this.property.isMultiple()) {
			if (value instanceof String) {
				this.setStringValue((String) value);
			} else if (value instanceof BigInteger) {
				this.setIntegerValue(((BigInteger) value).intValue());
			} else if (value instanceof Boolean) {
				this.setBooleanValue((Boolean) value);
			} else if (value instanceof GregorianCalendar) {
				this.setDateValue(((GregorianCalendar) value).getTime());
			} else if (value instanceof Date) {
				this.setDateValue((Date) value);
			}
		} else {
			// TODO:da fare in caso di proprietà multipla. Serve?
		}
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return this.getValue().toString();
	}

}
