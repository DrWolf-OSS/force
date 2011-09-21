package it.drwolf.slot.entity;

import it.drwolf.slot.entity.listeners.DocDefCollectionListener;
import it.drwolf.slot.enums.CollectionQuantifier;
import it.drwolf.slot.interfaces.Deactivable;

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
public class DocDefCollection implements Deactivable {

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

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	@ManyToOne
	@NotNull
	public DocDef getDocDef() {
		return docDef;
	}

	public void setDocDef(DocDef docDef) {
		this.docDef = docDef;
	}

	@ManyToOne
	public SlotDef getSlotDef() {
		return slotDef;
	}

	public void setSlotDef(SlotDef slotDef) {
		this.slotDef = slotDef;
	}

	@Override
	public String toString() {
		return name + ":" + min + "," + max + ";" + docDef;
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

	@Transient
	public CollectionQuantifier getQuantifier() {
		return quantifier;
	}

	public void setQuantifier(CollectionQuantifier quantifier) {
		this.quantifier = quantifier;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
