package it.drwolf.slot.entity;

import it.drwolf.slot.enums.DataInstanceMultiplicity;
import it.drwolf.slot.enums.DataType;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
public class SinglePropertyInst extends PropertyInst {

	private String stringValue;

	private Integer integerValue;

	private Boolean booleanValue;

	private Date dateValue;

	public SinglePropertyInst() {
		this.setMultiplicity(DataInstanceMultiplicity.SINGLE);
	}

	public SinglePropertyInst(PropertyDef propertyDef) {
		super(propertyDef);
		this.setMultiplicity(DataInstanceMultiplicity.SINGLE);
	}

	public SinglePropertyInst(PropertyDef propertyDef, SlotInst slotInst) {
		super(propertyDef, slotInst);
		this.setMultiplicity(DataInstanceMultiplicity.SINGLE);
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

	@Temporal(TemporalType.DATE)
	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	@Transient
	public Object getValue() {
		if (this.getPropertyDef().getDataType().equals(DataType.STRING))
			return this.getStringValue();
		else if (this.getPropertyDef().getDataType().equals(DataType.INTEGER))
			return this.getIntegerValue();
		else if (this.getPropertyDef().getDataType().equals(DataType.DATE))
			return this.getDateValue();
		else if (this.getPropertyDef().getDataType().equals(DataType.BOOLEAN))
			return this.getBooleanValue();
		else
			return null;
	}

	@Transient
	public void clean() {
		if (this.getPropertyDef().getDataType().equals(DataType.STRING))
			this.setStringValue(null);
		else if (this.getPropertyDef().getDataType().equals(DataType.INTEGER))
			this.setIntegerValue(null);
		else if (this.getPropertyDef().getDataType().equals(DataType.DATE))
			this.setDateValue(null);
		else if (this.getPropertyDef().getDataType().equals(DataType.BOOLEAN))
			this.setBooleanValue(null);
	}

}
