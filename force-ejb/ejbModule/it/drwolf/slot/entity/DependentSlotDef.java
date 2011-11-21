package it.drwolf.slot.entity;

import it.drwolf.slot.enums.SlotDefType;
import it.drwolf.slot.interfaces.Conditionable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;

@Entity
public class DependentSlotDef extends SlotDef implements Conditionable {

	private PropertyDef conditionalPropertyDef;

	private PropertyInst conditionalPropertyInst;

	private boolean active = Boolean.TRUE;

	private SlotDef parentSlotDef;

	private PropertyDef numberOfInstances;

	private EmbeddedProperty embeddedNumberOfInstances;

	public DependentSlotDef() {
		this.setType(SlotDefType.DEPENDENT);
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

	@OneToOne(cascade = CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	public EmbeddedProperty getEmbeddedNumberOfInstances() {
		return this.embeddedNumberOfInstances;
	}

	@ManyToOne
	public PropertyDef getNumberOfInstances() {
		return this.numberOfInstances;
	}

	@ManyToOne
	public SlotDef getParentSlotDef() {
		return this.parentSlotDef;
	}

	public boolean isActive() {
		return this.active;
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

	public void setEmbeddedNumberOfInstances(
			EmbeddedProperty embeddedNumberOfInstances) {
		this.embeddedNumberOfInstances = embeddedNumberOfInstances;
	}

	public void setNumberOfInstances(PropertyDef numberOfInstances) {
		this.numberOfInstances = numberOfInstances;
	}

	public void setParentSlotDef(SlotDef parentSlotDef) {
		this.parentSlotDef = parentSlotDef;
	}

}
