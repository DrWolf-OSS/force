package it.drwolf.force.entity;

import it.drwolf.force.enums.DataType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class PropertyDef {

	private Long id;

	private String name;

	private DataType type;

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

}
