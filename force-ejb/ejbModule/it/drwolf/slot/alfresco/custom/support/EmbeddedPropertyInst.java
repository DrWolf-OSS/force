package it.drwolf.slot.alfresco.custom.support;

import it.drwolf.slot.alfresco.custom.model.Property;

import java.util.Date;

public class EmbeddedPropertyInst {

	private Property property;

	private String stringValue;

	private Integer integerValue;

	private Boolean booleanValue;

	private Date dateValue;

	public EmbeddedPropertyInst(Property property) {
		super();
		this.property = property;
		if (property.getName().equals("d:date")) {
			this.setDateValue(new Date());
		}
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Integer getIntegerValue() {
		return integerValue;
	}

	public void setIntegerValue(Integer integerValue) {
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
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public Object getValue() {
		if (this.stringValue != null)
			return this.stringValue;
		else if (this.integerValue != null)
			return this.integerValue;
		else if (this.booleanValue != null)
			return this.booleanValue;
		else if (this.dateValue != null)
			return this.dateValue;

		return null;
	}

}
