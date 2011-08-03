package it.drwolf.slot.entity;

import it.drwolf.slot.enums.DataType;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.CollectionOfElements;

@Entity
public class Dictionary {

	private Long id;

	private String name;

	private List<String> values = new ArrayList<String>();

	private DataType dataType;

	public Dictionary() {
	}

	public Dictionary(String name, List<String> values, DataType dataType) {
		super();
		this.name = name;
		this.values = values;
		this.dataType = dataType;
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

	@CollectionOfElements
	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> list) {
		this.values = list;
	}

	@Enumerated(EnumType.STRING)
	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

}
