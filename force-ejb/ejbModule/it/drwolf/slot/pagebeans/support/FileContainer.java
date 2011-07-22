package it.drwolf.slot.pagebeans.support;

import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.alfresco.AlfrescoWrapper;
import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.alfresco.custom.support.DocumentPropertyInst;
import it.drwolf.slot.digsig.Signature;
import it.drwolf.utils.mimetypes.Resolver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.richfaces.model.UploadItem;

public class FileContainer {
	private UploadItem uploadItem;
	private AlfrescoDocument document;
	private List<DocumentPropertyInst> embeddedProperties = new ArrayList<DocumentPropertyInst>();
	private boolean editable = true;
	private String id = UUID.randomUUID().toString();
	private List<Signature> signatures;

	public FileContainer(AlfrescoDocument alfrescoDocument) {
		super();
		this.document = alfrescoDocument;
		retrieveSignatures();
	}

	public FileContainer(UploadItem uploadItem) {
		super();
		this.uploadItem = uploadItem;
	}

	public FileContainer(Object item) {
		if (item instanceof AlfrescoDocument) {
			this.document = (AlfrescoDocument) item;
			retrieveSignatures();
		} else if (item instanceof UploadItem)
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

	public String getNodeRef() {
		if (this.document != null) {
			return AlfrescoWrapper.id2ref(this.document.getId());
		}
		return "";
	}

	public String getExtension() {
		String fileName = this.getFileName();
		int dotIndex = fileName.lastIndexOf(".");
		return fileName.substring(dotIndex + 1);
	}

	public String getMimetype() {
		try {
			return this.document.getContentStreamMimeType();
		} catch (Exception e) {
			return Resolver.mimetypeForExtension(this.getExtension());
		}
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

	public void retrieveSignatures() {
		this.signatures = new ArrayList<Signature>();
		if (this.document != null) {
			AlfrescoUserIdentity alfrescoUserIdentity = (AlfrescoUserIdentity) org.jboss.seam.Component
					.getInstance("alfrescoUserIdentity");
			ItemIterable<QueryResult> results = alfrescoUserIdentity
					.getSession().query(
							"SELECT cmis:objectId," + Signature.VALIDITY + ","
									+ Signature.EXPIRY + ","
									+ Signature.AUTHORITY + ","
									+ Signature.SIGN + "," + Signature.CF
									+ " from dw:signature WHERE IN_TREE('"
									+ document.getId() + "')", true);
			if (results.getTotalNumItems() != 0) {
				Iterator<QueryResult> iterator = results.iterator();
				while (iterator.hasNext()) {
					QueryResult result = iterator.next();
					String nodeRef = result
							.getPropertyValueById("cmis:objectId");
					Boolean validity = result
							.getPropertyValueById(Signature.VALIDITY);
					Calendar expiry = result
							.getPropertyValueById(Signature.EXPIRY);
					String authority = result
							.getPropertyValueById(Signature.AUTHORITY);
					String sign = result.getPropertyValueById(Signature.SIGN);
					String cf = result.getPropertyValueById(Signature.CF);

					Signature signature = new Signature(validity,
							expiry.getTime(), authority, sign, cf, nodeRef);
					signatures.add(signature);
				}
			}
		}
	}

	private void buildPropertyInsts(Set<Property> properties, Object item,
			boolean editables) {
		// Set<String> aspectIds = docDefCollection.getDocDef().getAspectIds();
		// Set<Property> properties = customModelController
		// .getProperties(aspectIds);

		List<DocumentPropertyInst> fileProperties = new ArrayList<DocumentPropertyInst>();
		for (Property p : properties) {
			DocumentPropertyInst documentPropertyInst = buildValorisedDocumentPropertyInst(
					item, editables, p);
			fileProperties.add(documentPropertyInst);
		}

		// FileContainer container = new FileContainer(item);
		this.editable = editables;
		this.setEmbeddedProperties(fileProperties);
		// return container;
	}

	private DocumentPropertyInst buildValorisedDocumentPropertyInst(
			Object item, boolean editables, Property p) {
		DocumentPropertyInst embeddedPropertyInst = new DocumentPropertyInst(p);
		if (item instanceof AlfrescoDocument) {
			// AlfrescoDocument document = (AlfrescoDocument) item;
			Object propertyValue = document.getPropertyValue(p.getName());
			embeddedPropertyInst.setValue(propertyValue);
		}
		embeddedPropertyInst.setEditable(this.editable);
		return embeddedPropertyInst;
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

	public List<Signature> getSignatures() {
		return signatures;
	}

	public void setSignatures(List<Signature> signatures) {
		this.signatures = signatures;
	}

}
