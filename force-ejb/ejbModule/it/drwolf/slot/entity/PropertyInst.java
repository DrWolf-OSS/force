package it.drwolf.slot.entity;

import it.drwolf.slot.enums.DataInstanceMultiplicity;
import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.interfaces.DataDefinition;
import it.drwolf.slot.interfaces.DataInstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.CollectionOfElements;

@Entity
public class PropertyInst implements DataInstance {

	private Long id;

	private PropertyDef propertyDef;

	private SlotInst slotInst;

	private DataInstanceMultiplicity multiplicity;

	// Single properties
	private String stringValue;

	private Integer integerValue;

	private Boolean booleanValue;

	private Date dateValue;
	//

	// Multiple properties
	private Set<String> values = new HashSet<String>();

	public PropertyInst() {
	}

	public PropertyInst(PropertyDef propertyDef, SlotInst slotInst) {
		this(propertyDef);
		this.slotInst = slotInst;
		if (propertyDef.isMultiple()) {
			this.setMultiplicity(DataInstanceMultiplicity.MULTIPLE);
		} else {
			this.setMultiplicity(DataInstanceMultiplicity.SINGLE);
		}
	}

	public PropertyInst(PropertyDef propertyDef) {
		super();
		this.propertyDef = propertyDef;
		if (propertyDef.isMultiple()) {
			this.setMultiplicity(DataInstanceMultiplicity.MULTIPLE);
		} else {
			this.setMultiplicity(DataInstanceMultiplicity.SINGLE);
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

	@ManyToOne
	public SlotInst getSlotInst() {
		return slotInst;
	}

	public void setSlotInst(SlotInst slotInst) {
		this.slotInst = slotInst;
	}

	@Transient
	public DataDefinition getDataDefinition() {
		return this.propertyDef;
	}

	@Enumerated(EnumType.STRING)
	public DataInstanceMultiplicity getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(DataInstanceMultiplicity multiplicity) {
		this.multiplicity = multiplicity;
	}

	//

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
	public String getLinkValue() {
		if (this.getStringValue().startsWith("http://")) {
			return stringValue;
		} else {
			return "http://".concat(stringValue);
		}
	}

	//

	@CollectionOfElements
	public Set<String> getValues() {
		return values;
	}

	public void setValues(Set<String> values) {
		this.values = values;
	}

	@Transient
	public List<String> getValuesAsList() {
		return new ArrayList<String>(this.values);
	}

	@Transient
	public void setValuesAsList(List<String> valuesAsList) {
		this.values = new HashSet<String>(valuesAsList);
	}

	//

	@Transient
	public Object getValue() {
		if (!this.getPropertyDef().isMultiple()) {
			if (this.getPropertyDef().getDataType().equals(DataType.STRING))
				return this.getStringValue();
			else if (this.getPropertyDef().getDataType()
					.equals(DataType.INTEGER))
				return this.getIntegerValue();
			else if (this.getPropertyDef().getDataType().equals(DataType.DATE))
				return this.getDateValue();
			else if (this.getPropertyDef().getDataType()
					.equals(DataType.BOOLEAN))
				return this.getBooleanValue();
			else if (this.getPropertyDef().getDataType().equals(DataType.LINK))
				return this.getStringValue();
		} else {
			return this.getValues();
		}
		return null;
	}

	@Transient
	public void clean() {
		this.setStringValue(null);
		this.setIntegerValue(null);
		this.setDateValue(null);
		this.setBooleanValue(null);
		this.getValues().clear();
	}

}
