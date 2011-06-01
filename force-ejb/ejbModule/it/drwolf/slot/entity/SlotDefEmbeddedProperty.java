package it.drwolf.slot.entity;

import it.drwolf.slot.enums.DataType;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
public class SlotDefEmbeddedProperty {

	private Long id;

	private String name;

	private DataType type;

	private SlotDef slotDef;

	private String stringValue;

	private Integer integerValue;

	private Boolean booleanValue;

	private Date dateValue;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Enumerated(EnumType.STRING)
	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	@ManyToOne
	public SlotDef getSlotDef() {
		return slotDef;
	}

	public void setSlotDef(SlotDef slotDef) {
		this.slotDef = slotDef;
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
		if (this.getType().equals(DataType.STRING))
			return this.getStringValue();
		else if (this.getType().equals(DataType.INTEGER))
			return this.getIntegerValue();
		else if (this.getType().equals(DataType.DATE))
			return this.getDateValue();
		else if (this.getType().equals(DataType.BOOLEAN))
			return this.getBooleanValue();
		else
			return null;
	}

	@Transient
	public void setValue(Object value) {
		if (value instanceof String) {
			this.setType(DataType.STRING);
			this.setStringValue((String) value);
		} else if (value instanceof Integer) {
			this.setType(DataType.INTEGER);
			this.setIntegerValue((Integer) value);
		} else if (value instanceof Date) {
			this.setType(DataType.DATE);
			this.setDateValue((Date) value);
		} else if (value instanceof Boolean) {
			this.setType(DataType.BOOLEAN);
			this.setBooleanValue((Boolean) value);
		}
	}

}
