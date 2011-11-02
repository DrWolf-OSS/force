package it.drwolf.slot.entity;

import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.enums.DefStatus;
import it.drwolf.slot.interfaces.Conditionable;
import it.drwolf.slot.interfaces.DataDefinition;
import it.drwolf.slot.interfaces.Deactivable;
import it.drwolf.slot.validators.Validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;

@Entity
public class PropertyDef implements DataDefinition, Deactivable, Conditionable {

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

	private Set<PropertyDef> conditionedPropertyDefs = new HashSet<PropertyDef>();

	private Set<DocDefCollection> conditionedDocDefCollections = new HashSet<DocDefCollection>();

	private Set<DependentSlotDef> conditionedSlotDefs = new HashSet<DependentSlotDef>();

	private Set<DependentSlotDef> slotDefsIsNumberOfInstancesOf = new HashSet<DependentSlotDef>();

	private Constraint constraint;

	private Validator validator = new Validator();

	private DefStatus status;

	public PropertyDef() {
	}

	public PropertyDef(String name, DataType type) {
		super();
		this.name = name;
		this.dataType = type;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PropertyDef)) {
			return false;
		}
		PropertyDef other = (PropertyDef) obj;
		if (this.getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!this.getId().equals(other.getId())) {
			return false;
		}
		if (this.getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!this.getName().equals(other.getName())) {
			return false;
		}
		if (this.getDataType() != other.getDataType()) {
			return false;
		}
		return true;
	}

	@ManyToOne(cascade = CascadeType.PERSIST)
	public PropertyDef getConditionalPropertyDef() {
		return this.conditionalPropertyDef;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	public PropertyInst getConditionalPropertyInst() {
		return this.conditionalPropertyInst;
	}

	@OneToMany(mappedBy = "conditionalPropertyDef")
	public Set<DocDefCollection> getConditionedDocDefCollections() {
		return this.conditionedDocDefCollections;
	}

	@Transient
	public List<DocDefCollection> getConditionedDocDefCollectionsAsList() {
		return new ArrayList<DocDefCollection>(
				this.conditionedDocDefCollections);
	}

	@OneToMany(mappedBy = "conditionalPropertyDef")
	public Set<PropertyDef> getConditionedPropertyDefs() {
		return this.conditionedPropertyDefs;
	}

	@Transient
	public List<PropertyDef> getConditionedPropertyDefsAsList() {
		return new ArrayList<PropertyDef>(this.conditionedPropertyDefs);
	}

	@OneToMany(mappedBy = "conditionalPropertyDef")
	public Set<DependentSlotDef> getConditionedSlotDefs() {
		return this.conditionedSlotDefs;
	}

	@ManyToOne
	public Constraint getConstraint() {
		return this.constraint;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	public DataType getDataType() {
		return this.dataType;
	}

	@ManyToOne
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

	@Transient
	public String getLabel() {
		return this.name;
	}

	public String getName() {
		return this.name;
	}

	@OneToMany(mappedBy = "numberOfInstances")
	public Set<DependentSlotDef> getSlotDefsIsNumberOfInstancesOf() {
		return this.slotDefsIsNumberOfInstancesOf;
	}

	@Transient
	public DefStatus getStatus() {
		return this.status;
	}

	@Transient
	public String getUuid() {
		return this.uuid;
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

	public boolean isRequired() {
		return this.required;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setConditionalPropertyDef(PropertyDef conditionalPropertyDef) {
		this.conditionalPropertyDef = conditionalPropertyDef;
	}

	public void setConditionalPropertyInst(PropertyInst conditionalPropertyInst) {
		this.conditionalPropertyInst = conditionalPropertyInst;
	}

	public void setConditionedDocDefCollections(
			Set<DocDefCollection> conditionedDocDefCollections) {
		this.conditionedDocDefCollections = conditionedDocDefCollections;
	}

	public void setConditionedPropertyDefs(
			Set<PropertyDef> conditionedPropertyDefs) {
		this.conditionedPropertyDefs = conditionedPropertyDefs;
	}

	public void setConditionedSlotDefs(Set<DependentSlotDef> conditionedSlotDefs) {
		this.conditionedSlotDefs = conditionedSlotDefs;
	}

	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}

	public void setDataType(DataType type) {
		this.dataType = type;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public void setSlotDefsIsNumberOfInstancesOf(
			Set<DependentSlotDef> slotDefsIsNumberOfInstancesOf) {
		this.slotDefsIsNumberOfInstancesOf = slotDefsIsNumberOfInstancesOf;
	}

	public void setStatus(DefStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return this.name + ":" + this.dataType;
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
