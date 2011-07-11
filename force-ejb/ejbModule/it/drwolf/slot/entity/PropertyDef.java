package it.drwolf.slot.entity;

import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.interfaces.DataDefinition;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class PropertyDef implements DataDefinition {

	private Long id;

	private String name;

	private DataType type;

	private boolean required = false;

	public PropertyDef(String name, DataType type) {
		super();
		this.name = name;
		this.type = type;
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
	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return this.name + ":" + type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.getId() == null) ? 0 : this.getId().hashCode());
		result = prime * result
				+ ((this.getName() == null) ? 0 : this.getName().hashCode());
		result = prime * result
				+ ((this.getType() == null) ? 0 : this.getType().hashCode());
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
		if (this.getType() != other.getType())
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
	public DataType getDataType() {
		return this.type;
	}

	@Transient
	public boolean isEditable() {
		return true;
	}

	@Transient
	public List<String> getDictionary() {
		// TODO Auto-generated method stub
		return null;
	}

}
