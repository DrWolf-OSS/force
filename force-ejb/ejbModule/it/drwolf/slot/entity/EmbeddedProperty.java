package it.drwolf.slot.entity;

import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.interfaces.DataDefinition;
import it.drwolf.slot.interfaces.DataInstance;
import it.drwolf.slot.interfaces.Deactivable;
import it.drwolf.slot.validators.Validator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.Column;
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
public class EmbeddedProperty implements DataDefinition, DataInstance,
		Deactivable {

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

	private boolean active = Boolean.TRUE;

	private Constraint constraint;

	private Validator validator = new Validator();

	@Transient
	public void clean() {
		this.setStringValue(null);
		this.setIntegerValue(null);
		this.setDateValue(null);
		this.setBooleanValue(null);
		this.getValues().clear();
	}

	public Boolean getBooleanValue() {
		return this.booleanValue;
	}

	@ManyToOne
	public Constraint getConstraint() {
		return this.constraint;
	}

	@Transient
	public DataDefinition getDataDefinition() {
		return this;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	public DataType getDataType() {
		return this.dataType;
	}

	@Temporal(TemporalType.DATE)
	public Date getDateValue() {
		return this.dateValue;
	}

	@Transient
	public Dictionary getDictionary() {
		return this.dictionary;
	}

	@Transient
	public List<String> getDictionaryValues() {
		if (this.getDictionary() != null
				&& !this.getDictionary().getValues().isEmpty()) {
			return this.getDictionary().getValues();
		}
		return null;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return this.id;
	}

	public Integer getIntegerValue() {
		return this.integerValue;
	}

	@Transient
	public String getLabel() {
		return this.name;
	}

	@Transient
	public String getLinkValue() {
		if (this.getStringValue().startsWith("http://")) {
			return this.stringValue;
		} else {
			return "http://".concat(this.stringValue);
		}
	}

	public String getName() {
		return this.name;
	}

	public String getStringValue() {
		return this.stringValue;
	}

	@Transient
	public Object getValue() {
		if (!this.isMultiple()) {
			if (this.getDataType().equals(DataType.STRING)) {
				return this.getStringValue();
			} else if (this.getDataType().equals(DataType.INTEGER)) {
				return this.getIntegerValue();
			} else if (this.getDataType().equals(DataType.DATE)) {
				return this.getDateValue();
			} else if (this.getDataType().equals(DataType.BOOLEAN)) {
				return this.getBooleanValue();
			} else if (this.getDataType().equals(DataType.LINK)) {
				return this.getStringValue();
			}
		} else {
			return this.getValues();
		}
		return null;
	}

	@CollectionOfElements
	public Set<String> getValues() {
		return this.values;
	}

	@Transient
	public List<String> getValuesAsList() {
		return new ArrayList<String>(this.values);
	}

	public boolean isActive() {
		return this.active;
	}

	@Transient
	public boolean isEditable() {
		return true;
	}

	public boolean isMultiple() {
		return this.multiple;
	}

	@Transient
	public boolean isRequired() {
		return true;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}

	public void setDataType(DataType type) {
		this.dataType = type;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
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

	public void setValues(Set<String> values) {
		this.values = values;
	}

	@Transient
	public void setValuesAsList(List<String> valuesAsList) {
		this.values = new HashSet<String>(valuesAsList);
	}

	public void validate(FacesContext context, UIComponent component, Object obj)
			throws ValidatorException {

		if (this.getConstraint() != null) {
			switch (this.getDataType()) {
			case STRING:
				if (this.getConstraint().getRegex() != null
						&& !this.getConstraint().getRegex().equals("")) {
					this.validator.validateRegex((String) obj, this
							.getConstraint().getRegex(), this.getConstraint()
							.getRequiresMatch());
				}

				if (this.getConstraint().getMinLength() != null
						|| this.getConstraint().getMaxLength() != null) {
					this.validator.validateLength((String) obj, this
							.getConstraint().getMinLength(), this
							.getConstraint().getMaxLength());
				}
				break;

			case INTEGER:
				if (this.getConstraint().getMinValue() != null
						|| this.getConstraint().getMaxValue() != null) {
					this.validator.validateMinMax((Integer) obj, this
							.getConstraint().getMinValue(), this
							.getConstraint().getMaxValue());
				}

			default:
				break;
			}
		}
	}

}
