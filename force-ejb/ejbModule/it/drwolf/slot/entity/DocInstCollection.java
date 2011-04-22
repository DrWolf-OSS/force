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

@Entity
public class DocInstCollection {

	private Long id;

	private SlotInst slotInst;

	private DocDefCollection docDefCollection;

	private Set<DocInst> docInsts = new HashSet<DocInst>();

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
	public Set<DocInst> getDocInsts() {
		return docInsts;
	}

	public void setDocInsts(Set<DocInst> docInsts) {
		this.docInsts = docInsts;
	}

}
