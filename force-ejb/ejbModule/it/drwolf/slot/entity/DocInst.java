package it.drwolf.slot.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class DocInst {

	private Long id;

	private DocInstCollection docInstCollection;

	private String nodeRef;

	public DocInst(DocInstCollection docInstCollection, String nodeRef) {
		super();
		this.docInstCollection = docInstCollection;
		this.nodeRef = nodeRef;
	}

	public DocInst() {
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
	public DocInstCollection getDocInstCollection() {
		return docInstCollection;
	}

	public void setDocInstCollection(DocInstCollection docInstCollection) {
		this.docInstCollection = docInstCollection;
	}

	public String getNodeRef() {
		return nodeRef;
	}

	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
	}

}
