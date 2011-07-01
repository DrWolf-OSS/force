package it.drwolf.slot.pagebeans.support;

import it.drwolf.slot.alfresco.custom.support.DocumentPropertyInst;
import it.drwolf.utils.mimetypes.Resolver;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.richfaces.model.UploadItem;

public class FileContainer {
	private UploadItem uploadItem;
	private AlfrescoDocument document;
	private List<DocumentPropertyInst> embeddedProperties = new ArrayList<DocumentPropertyInst>();
	private boolean editable = true;
	private String id = UUID.randomUUID().toString();

	public FileContainer(AlfrescoDocument alfrescoDocument) {
		super();
		this.document = alfrescoDocument;
	}

	public FileContainer(UploadItem uploadItem) {
		super();
		this.uploadItem = uploadItem;
	}

	public FileContainer(Object item) {
		if (item instanceof AlfrescoDocument)
			this.document = (AlfrescoDocument) item;
		else if (item instanceof UploadItem)
			this.uploadItem = (UploadItem) item;
	}

	public UploadItem getUploadItem() {
		return uploadItem;
	}

	public void setUploadItem(UploadItem uploadItem) {
		this.uploadItem = uploadItem;
	}

	public AlfrescoDocument getDocument() {
		return document;
	}

	public void setDocument(AlfrescoDocument alfrescoDocument) {
		this.document = alfrescoDocument;
	}

	public String getRealFileName() {
		if (uploadItem != null && !uploadItem.getFileName().equals("")) {
			return uploadItem.getFileName();
		} else if (document != null && !document.getName().equals("")) {
			return document.getName();
		}
		return "";
	}

	public String getFileName() {
		String realFileName = getRealFileName();
		String name = realFileName;
		String extension = "";
		int dotIndex = realFileName.lastIndexOf(".");
		if (dotIndex != -1) {
			extension = realFileName.substring(dotIndex);
			name = realFileName.substring(0, dotIndex);
		}

		int underscoreIndex = realFileName.lastIndexOf("_");
		String fileName = realFileName;
		if (underscoreIndex != -1) {
			fileName = realFileName.substring(0, underscoreIndex);
		} else {
			fileName = name;
		}
		return fileName.concat(extension);
	}

	public String getExtension() {
		String fileName = this.getFileName();
		int dotIndex = fileName.lastIndexOf(".");
		return fileName.substring(dotIndex + 1);
	}

	public String getMimetype() {
		return Resolver.mimetypeForExtension(this.getExtension());
	}

	public List<DocumentPropertyInst> getEmbeddedProperties() {
		return embeddedProperties;
	}

	public void setEmbeddedProperties(
			List<DocumentPropertyInst> embeddedProperties) {
		this.embeddedProperties = embeddedProperties;
	}

	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FileContainer))
			return false;
		FileContainer other = (FileContainer) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

}
