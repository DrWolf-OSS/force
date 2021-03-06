package it.drwolf.slot.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

@Entity
public class DocInstCollection {

	private Long id;

	private SlotInst slotInst;

	private DocDefCollection docDefCollection;

	private Set<DocInst> docInsts = new HashSet<DocInst>();

	public DocInstCollection() {
	}

	public DocInstCollection(SlotInst slotInst,
			DocDefCollection docDefCollection) {
		super();
		this.slotInst = slotInst;
		this.docDefCollection = docDefCollection;
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
	public SlotInst getSlotInst() {
		return slotInst;
	}

	public void setSlotInst(SlotInst slotInst) {
		this.slotInst = slotInst;
	}

	@OneToOne
	public DocDefCollection getDocDefCollection() {
		return docDefCollection;
	}

	public void setDocDefCollection(DocDefCollection docDefCollection) {
		this.docDefCollection = docDefCollection;
	}

	@OneToMany(mappedBy = "docInstCollection", cascade = CascadeType.ALL)
	@OrderBy("id")
	public Set<DocInst> getDocInsts() {
		return docInsts;
	}

	public void setDocInsts(Set<DocInst> docInsts) {
		this.docInsts = docInsts;
	}

	public String toString() {
		return this.getDocDefCollection().getName() + ":"
				+ this.docInsts.toString();
	}

}
