package it.drwolf.slot.entity;

import it.drwolf.slot.enums.DataInstanceMultiplicity;
import it.drwolf.slot.interfaces.DataDefinition;
import it.drwolf.slot.interfaces.DataInstance;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class PropertyInst implements DataInstance {

	private Long id;

	private PropertyDef propertyDef;

	private SlotInst slotInst;

	private DataInstanceMultiplicity multiplicity;

	public PropertyInst() {
	}

	public PropertyInst(PropertyDef propertyDef, SlotInst slotInst) {
		this(propertyDef);
		this.slotInst = slotInst;
	}

	public PropertyInst(PropertyDef propertyDef) {
		super();
		this.propertyDef = propertyDef;
		// if (propertyDef.getType().equals(DataType.DATE)) {
		// this.setDateValue(new Date());
		// } else
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

	// @Transient
	// public Object getValue() {
	// if (this.getPropertyDef().getType().equals(DataType.STRING))
	// return this.getStringValue();
	// else if (this.getPropertyDef().getType().equals(DataType.INTEGER))
	// return this.getIntegerValue();
	// else if (this.getPropertyDef().getType().equals(DataType.DATE))
	// return this.getDateValue();
	// else if (this.getPropertyDef().getType().equals(DataType.BOOLEAN))
	// return this.getBooleanValue();
	// else
	// return null;
	// }

	@Transient
	public DataDefinition getDataDefinition() {
		return this.propertyDef;
	}

	@Transient
	abstract public void clean();

	@Enumerated(EnumType.STRING)
	public DataInstanceMultiplicity getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(DataInstanceMultiplicity multiplicity) {
		this.multiplicity = multiplicity;
	}

	// @Transient
	// public void clean() {
	// if (this.getPropertyDef().getType().equals(DataType.STRING))
	// this.setStringValue(null);
	// else if (this.getPropertyDef().getType().equals(DataType.INTEGER))
	// this.setIntegerValue(null);
	// else if (this.getPropertyDef().getType().equals(DataType.DATE))
	// this.setDateValue(null);
	// else if (this.getPropertyDef().getType().equals(DataType.BOOLEAN))
	// this.setBooleanValue(null);
	// }

}
