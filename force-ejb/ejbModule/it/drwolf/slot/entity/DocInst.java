package it.drwolf.slot.entity;

import it.drwolf.slot.alfresco.AlfrescoUserIdentity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.chemistry.opencmis.client.api.Document;

@Entity
public class DocInst {

	private Long id;

	private DocInstCollection docInstCollection;

	private String nodeRef;

	public DocInst() {
	}

	public DocInst(DocInstCollection docInstCollection, String nodeRef) {
		super();
		this.docInstCollection = docInstCollection;
		this.nodeRef = nodeRef;
	}

	@ManyToOne
	public DocInstCollection getDocInstCollection() {
		return this.docInstCollection;
	}

	@Transient
	public Document getDocument() {
		// Si presuppone che il componente alfrescoUserIdentity sia sempre
		// diverso da null
		AlfrescoUserIdentity alfrescoUserIdentity = (AlfrescoUserIdentity) org.jboss.seam.Component
				.getInstance("alfrescoUserIdentity");
		return (Document) alfrescoUserIdentity.getSession().getObject(
				this.getNodeRef());
	}

	@Id
	@GeneratedValue
	public Long getId() {
		return this.id;
	}

	public String getNodeRef() {
		return this.nodeRef;
	}

	public void setDocInstCollection(DocInstCollection docInstCollection) {
		this.docInstCollection = docInstCollection;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setNodeRef(String nodeRef) {
		this.nodeRef = nodeRef;
	}

	public String toString() {
		return this.nodeRef;
	}

}
