package it.drwolf.slot.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
public class SlotInst {

	private Long id;

	private SlotDef slotDef;

	private Set<DocInstCollection> docInstCollections = new HashSet<DocInstCollection>();

	private Set<PropertyInst> propertyInsts = new HashSet<PropertyInst>();

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	public SlotDef getSlotDef() {
		return slotDef;
	}

	public void setSlotDef(SlotDef slotDef) {
		this.slotDef = slotDef;
	}

	@OrderBy("docDefCollection")
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "slotInst")
	public Set<DocInstCollection> getDocInstCollections() {
		return docInstCollections;
	}

	public void setDocInstCollections(Set<DocInstCollection> docInstCollections) {
		this.docInstCollections = docInstCollections;
	}

	@OrderBy("propertyDef")
	@OneToMany(mappedBy = "slotInst", cascade = CascadeType.ALL)
	public Set<PropertyInst> getPropertyInsts() {
		return propertyInsts;
	}

	public void setPropertyInsts(Set<PropertyInst> propertyInsts) {
		this.propertyInsts = propertyInsts;
	}

}
