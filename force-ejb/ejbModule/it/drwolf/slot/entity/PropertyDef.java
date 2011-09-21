package it.drwolf.slot.entity;

import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.interfaces.DataDefinition;
import it.drwolf.slot.interfaces.Deactivable;

import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;

@Entity
public class PropertyDef implements DataDefinition, Deactivable {

	private Long id;

	private String name;

	private DataType dataType;

	private boolean required = Boolean.FALSE;

	private Dictionary dictionary;

	private PropertyDef conditionalPropertyDef;

	private PropertyInst conditionalPropertyInst;

	private boolean multiple = Boolean.FALSE;

	private String uuid = UUID.randomUUID().toString();

	private boolean active = Boolean.TRUE;

	public PropertyDef(String name, DataType type) {
		super();
		this.name = name;
		this.dataType = type;
	}

	public PropertyDef() {
	}

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

	@Override
	public String toString() {
		return this.name + ":" + dataType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.getId() == null) ? 0 : this.getId().hashCode());
		result = prime * result
				+ ((this.getName() == null) ? 0 : this.getName().hashCode());
		result = prime
				* result
				+ ((this.getDataType() == null) ? 0 : this.getDataType()
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PropertyDef))
			return false;
		PropertyDef other = (PropertyDef) obj;
		if (this.getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!this.getId().equals(other.getId()))
			return false;
		if (this.getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!this.getName().equals(other.getName()))
			return false;
		if (this.getDataType() != other.getDataType())
			return false;
		return true;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	@Transient
	public String getLabel() {
		return this.name;
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

	@ManyToOne
	public Dictionary getDictionary() {
		return dictionary;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	@Transient
	public String getUuid() {
		return uuid;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@ManyToOne(cascade = CascadeType.PERSIST)
	public PropertyDef getConditionalPropertyDef() {
		return conditionalPropertyDef;
	}

	public void setConditionalPropertyDef(PropertyDef conditionalPropertyDef) {
		this.conditionalPropertyDef = conditionalPropertyDef;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	public PropertyInst getConditionalPropertyInst() {
		return conditionalPropertyInst;
	}

	public void setConditionalPropertyInst(PropertyInst conditionalPropertyInst) {
		this.conditionalPropertyInst = conditionalPropertyInst;
	}

}
