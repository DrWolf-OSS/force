package it.drwolf.slot.entity;

import it.drwolf.slot.enums.SlotInstStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

@Entity
public class SlotInst {

	private Long id;

	private SlotDef slotDef;

	private Set<DocInstCollection> docInstCollections = new HashSet<DocInstCollection>();

	private Set<PropertyInst> propertyInsts = new HashSet<PropertyInst>();

	private String nodeRef;

	private Set<SlotInst> dependentSlotInsts = new HashSet<SlotInst>();

	private SlotInst parentSlotInst;

	private String ownerId;

	private SlotInstStatus status = SlotInstStatus.EMPTY;

	public SlotInst() {
	}

	public SlotInst(SlotDef slotDef) {
		super();
		this.setSlotDef(slotDef);
		for (PropertyDef depPropertyDef : this.getSlotDef().getPropertyDefs()) {
			PropertyInst depPropertyInst = new PropertyInst(depPropertyDef,
					this);
			this.getPropertyInsts().add(depPropertyInst);
		}

		for (DocDefCollection depDocDefCollection : this.getSlotDef()
				.getDocDefCollections()) {
			DocInstCollection depDocInstCollection = new DocInstCollection(
					this, depDocDefCollection);
			this.getDocInstCollections().add(depDocInstCollection);
		}
	}

	@OrderBy("id")
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "parentSlotInst")
	// @JoinColumn(name = "parentSlotInst_id")
	public Set<SlotInst> getDependentSlotInsts() {
		return this.dependentSlotInsts;
	}

	@Transient
	public List<SlotInst> getDependentSlotInstsAsList() {
		return new ArrayList<SlotInst>(this.getDependentSlotInsts());
	}

	@OrderBy("docDefCollection")
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "slotInst")
	public Set<DocInstCollection> getDocInstCollections() {
		return this.docInstCollections;
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return this.id;
	}

	public String getNodeRef() {
		return this.nodeRef;
	}

	public String getOwnerId() {
		return this.ownerId;
	}

	@ManyToOne
	public SlotInst getParentSlotInst() {
		return this.parentSlotInst;
	}

	@OrderBy("propertyDef")
	@OneToMany(mappedBy = "slotInst", cascade = CascadeType.ALL)
	public Set<PropertyInst> getPropertyInsts() {
		return this.propertyInsts;
	}

	@Transient
	public List<PropertyInst> getPropertyInstsAsList() {
		return new ArrayList<PropertyInst>(this.getPropertyInsts());
	}

	@ManyToOne
	public SlotDef getSlotDef() {
		return this.slotDef;
	}

	@Enumerated(EnumType.STRING)
	public SlotInstStatus getStatus() {
		return this.status;
	}

	@Transient
	public PropertyInst retrievePropertyInstByDef(PropertyDef propertyDef) {
		Iterator<PropertyInst> iterator = this.getPropertyInsts().iterator();
		while (iterator.hasNext()) {
			PropertyInst propertyInst = iterator.next();
			if (propertyInst.getPropertyDef().getId() == propertyDef.getId()) {
				return propertyInst;
			}
		}
		return null;
	}

	public void setDependentSlotInsts(Set<SlotInst> dependentSlotInsts) {
		this.dependentSlotInsts = dependentSlotInsts;
	}

	public void setDocInstCollections(Set<DocInstCollection> docInstCollections) {
		this.docInstCollections = docInstCollections;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
	}

	public void setOwnerId(String owner) {
		this.ownerId = owner;
	}

	public void setParentSlotInst(SlotInst parentSlotInst) {
		this.parentSlotInst = parentSlotInst;
	}

	public void setPropertyInsts(Set<PropertyInst> propertyInsts) {
		this.propertyInsts = propertyInsts;
	}

	public void setSlotDef(SlotDef slotDef) {
		this.slotDef = slotDef;
	}

	public void setStatus(SlotInstStatus status) {
		this.status = status;
	}

}
