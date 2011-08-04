package it.drwolf.slot.entity;

import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.interfaces.DataDefinition;
import it.drwolf.slot.interfaces.DataInstance;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.CollectionOfElements;

@Entity
public class EmbeddedProperty implements DataDefinition, DataInstance {

	private Long id;

	private String name;

	private DataType dataType;

	private String stringValue;

	private Integer integerValue;

	private Boolean booleanValue;

	private Date dateValue;

	private boolean multiple = Boolean.FALSE;

	private Dictionary dictionary;

	private Set<String> values = new HashSet<String>();

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
	@Column(name = "type")
	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType type) {
		this.dataType = type;
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
	public String getLinkValue() {
		if (this.getStringValue().startsWith("http://")) {
			return stringValue;
		} else {
			return "http://".concat(stringValue);
		}
	}

	@Transient
	public Object getValue() {
		if (!this.isMultiple()) {
			if (this.getDataType().equals(DataType.STRING))
				return this.getStringValue();
			else if (this.getDataType().equals(DataType.INTEGER))
				return this.getIntegerValue();
			else if (this.getDataType().equals(DataType.DATE))
				return this.getDateValue();
			else if (this.getDataType().equals(DataType.BOOLEAN))
				return this.getBooleanValue();
			else if (this.getDataType().equals(DataType.LINK))
				return this.getStringValue();
		} else {
			return this.getValues();
		}
		return null;
	}

	@Transient
	public void setValue(Object value) {
		if (value instanceof String) {
			this.setDataType(DataType.STRING);
			this.setStringValue((String) value);
		} else if (value instanceof Integer) {
			this.setDataType(DataType.INTEGER);
			this.setIntegerValue((Integer) value);
		} else if (value instanceof Date) {
			this.setDataType(DataType.DATE);
			this.setDateValue((Date) value);
		} else if (value instanceof Boolean) {
			this.setDataType(DataType.BOOLEAN);
			this.setBooleanValue((Boolean) value);
		}
	}

	@Transient
	public DataDefinition getDataDefinition() {
		return this;
	}

	@Transient
	public String getLabel() {
		return this.name;
	}

	@Transient
	public boolean isRequired() {
		return true;
	}

	@Transient
	public boolean isEditable() {
		return true;
	}

	@Transient
	public List<String> getDictionaryValues() {
		if (this.getDictionary() != null
				&& !this.getDictionary().getValues().isEmpty()) {
			return this.getDictionary().getValues();
		}
		return null;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	@CollectionOfElements
	public Set<String> getValues() {
		return values;
	}

	public void setValues(Set<String> values) {
		this.values = values;
	}

	@Transient
	public Dictionary getDictionary() {
		return dictionary;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	@Transient
	public List<String> getValuesAsList() {
		return new ArrayList<String>(this.values);
	}

	@Transient
	public void setValuesAsList(List<String> valuesAsList) {
		this.values = new HashSet<String>(valuesAsList);
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
