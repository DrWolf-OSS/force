package it.drwolf.slot.entity;

import it.drwolf.slot.enums.DataType;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class PropertyInst {

	private Long id;

	private PropertyDef propertyDef;

	private String stringValue;

	private Integer integerValue;

	private Boolean booleanValue;

	private Date dateValue;

	private SlotInst slotInst;

	public PropertyInst() {
	}

	public PropertyInst(PropertyDef propertyDef, SlotInst slotInst) {
		this(propertyDef);
		// this.propertyDef = propertyDef;
		// if (propertyDef.getType().equals(DataType.DATE)) {
		// this.setDateValue(new Date());
		// } else if (propertyDef.getType().equals(DataType.BOOLEAN)) {
		// this.setBooleanValue(false);
		// }
		this.slotInst = slotInst;
	}

	public PropertyInst(PropertyDef propertyDef) {
		super();
		this.propertyDef = propertyDef;
		if (propertyDef.getType().equals(DataType.DATE)) {
			this.setDateValue(new Date());
		} else if (propertyDef.getType().equals(DataType.BOOLEAN)) {
			this.setBooleanValue(false);
		}
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public PropertyDef getPropertyDef() {
		return propertyDef;
	}

	public void setPropertyDef(PropertyDef propertytDef) {
		this.propertyDef = propertytDef;
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

	@ManyToOne
	public SlotInst getSlotInst() {
		return slotInst;
	}

	public void setSlotInst(SlotInst slotInst) {
		this.slotInst = slotInst;
	}

}
