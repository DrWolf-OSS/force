package it.drwolf.slot.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.NotNull;

@Entity
public class DocDefCollection {

	private Long id;

	private String name;

	private Integer min;

	private Integer max;

	private DocDef docDef;

	private SlotDef slotDef;

	private PropertyDef conditionalPropertyDef;

	private PropertyInst conditionalPropertyInst;

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

	// @Override
	// public int hashCode() {
	// final int prime = 31;
	// int result = 1;
	// result = prime
	// * result
	// + ((this.getDocDef() == null) ? 0 : this.getDocDef().hashCode());
	// result = prime * result
	// + ((this.getName() == null) ? 0 : this.getName().hashCode());
	// result = prime
	// * result
	// + ((this.getSlotDef() == null) ? 0 : this.getSlotDef()
	// .hashCode());
	// return result;
	// }
	//
	// @Override
	// public boolean equals(Object obj) {
	// if (this == obj)
	// return true;
	// if (obj == null)
	// return false;
	// if (!(obj instanceof DocDefCollection))
	// return false;
	// DocDefCollection other = (DocDefCollection) obj;
	// if (this.getDocDef() == null) {
	// if (other.getDocDef() != null)
	// return false;
	// } else if (!this.getDocDef().equals(other.getDocDef()))
	// return false;
	// if (this.getName() == null) {
	// if (other.getName() != null)
	// return false;
	// } else if (!this.getName().equals(other.getName()))
	// return false;
	// if (this.getSlotDef() == null) {
	// if (other.getSlotDef() != null)
	// return false;
	// } else if (!this.getSlotDef().equals(other.getSlotDef()))
	// return false;
	// return true;
	// }

}
