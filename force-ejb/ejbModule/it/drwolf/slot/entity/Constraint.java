package it.drwolf.slot.entity;

import it.drwolf.slot.enums.DataType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ConstraintTemplate")
public class Constraint {

	private Long id;

	private String name;

	private String regex;

	private Boolean requiresMatch;

	private Integer minLength;

	private Integer maxLength;

	private Integer minValue;

	private Integer maxValue;

	private DataType dataType;

	@Enumerated(EnumType.STRING)
	public DataType getDataType() {
		return this.dataType;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return this.id;
	}

	public Integer getMaxLength() {
		return this.maxLength;
	}

	public Integer getMaxValue() {
		return this.maxValue;
	}

	public Integer getMinLength() {
		return this.minLength;
	}

	public Integer getMinValue() {
		return this.minValue;
	}

	public String getName() {
		return this.name;
	}

	public String getRegex() {
		return this.regex;
	}

	public Boolean getRequiresMatch() {
		return this.requiresMatch;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
	}

	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	public void setMinValue(Integer minValue) {
		this.minValue = minValue;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public void setRequiresMatch(Boolean requiresMatch) {
		this.requiresMatch = requiresMatch;
	}

}
