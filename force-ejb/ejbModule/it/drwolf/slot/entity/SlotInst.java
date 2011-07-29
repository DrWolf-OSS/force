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

	private Set<MultiplePropertyInst> multiPropertyInsts = new HashSet<MultiplePropertyInst>();

	private String ownerId;

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

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String owner) {
		this.ownerId = owner;
	}

	@OrderBy("propertyDef")
	@OneToMany(mappedBy = "slotInst", cascade = CascadeType.ALL)
	public Set<MultiplePropertyInst> getMultiPropertyInsts() {
		return multiPropertyInsts;
	}

	public void setMultiPropertyInsts(
			Set<MultiplePropertyInst> multiPropertyInsts) {
		this.multiPropertyInsts = multiPropertyInsts;
	}

}
