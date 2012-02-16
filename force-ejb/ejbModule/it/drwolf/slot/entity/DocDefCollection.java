package it.drwolf.slot.entity;

import it.drwolf.slot.entity.listeners.DocDefCollectionListener;
import it.drwolf.slot.enums.CollectionQuantifier;
import it.drwolf.slot.enums.DefStatus;
import it.drwolf.slot.interfaces.Conditionable;
import it.drwolf.slot.interfaces.Deactivable;
import it.drwolf.slot.interfaces.Definition;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.NotNull;

@Entity
@EntityListeners(value = DocDefCollectionListener.class)
public class DocDefCollection implements Deactivable, Definition, Conditionable {

	private Long id;

	private String name;

	private Integer min;

	private Integer max;

	private DocDef docDef;

	private SlotDef slotDef;

	private PropertyDef conditionalPropertyDef;

	private PropertyInst conditionalPropertyInst;

	private CollectionQuantifier quantifier;

	private boolean active = Boolean.TRUE;

	private DefStatus status;

	private String description;

	@ManyToOne(cascade = CascadeType.PERSIST)
	public PropertyDef getConditionalPropertyDef() {
		return this.conditionalPropertyDef;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	public PropertyInst getConditionalPropertyInst() {
		return this.conditionalPropertyInst;
	}

	public String getDescription() {
		return this.description;
	}

	@ManyToOne
	@NotNull
	public DocDef getDocDef() {
		return this.docDef;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return this.id;
	}

	@Transient
	public String getLabel() {
		return this.getName();
	}

	public Integer getMax() {
		return this.max;
	}

	public Integer getMin() {
		return this.min;
	}

	public String getName() {
		return this.name;
	}

	@Transient
	public CollectionQuantifier getQuantifier() {
		return this.quantifier;
	}

	@ManyToOne
	public SlotDef getSlotDef() {
		return this.slotDef;
	}

	@Transient
	public DefStatus getStatus() {
		return this.status;
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

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDocDef(DocDef docDef) {
		this.docDef = docDef;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setQuantifier(CollectionQuantifier quantifier) {
		this.quantifier = quantifier;
	}

	public void setSlotDef(SlotDef slotDef) {
		this.slotDef = slotDef;
	}

	public void setStatus(DefStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return this.name + ":" + this.min + "," + this.max + ";" + this.docDef;
	}

}
