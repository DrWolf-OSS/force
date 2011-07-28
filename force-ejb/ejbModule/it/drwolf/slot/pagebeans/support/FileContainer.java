package it.drwolf.slot.pagebeans.support;

import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.alfresco.AlfrescoWrapper;
import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.alfresco.custom.support.DocumentPropertyInst;
import it.drwolf.slot.alfresco.custom.support.MultipleDocumentPropertyInst;
import it.drwolf.slot.digsig.Signature;
import it.drwolf.slot.enums.DataType;
import it.drwolf.utils.mimetypes.Resolver;

import java.math.BigInteger;
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
	private List<DocumentPropertyInst> singleProperties = new ArrayList<DocumentPropertyInst>();
	private List<MultipleDocumentPropertyInst> multipleProperties = new ArrayList<MultipleDocumentPropertyInst>();
	private boolean editable = true;
	private String id = UUID.randomUUID().toString();
	private List<Signature> signatures;
	private String mimetype;

	public final static String decodeFilename(String encoded) {
		String name = encoded;
		String extension = "";
		int dotIndex = encoded.lastIndexOf(".");
		if (dotIndex != -1) {
			extension = encoded.substring(dotIndex);
			name = encoded.substring(0, dotIndex);
		}

		int underscoreIndex = encoded.lastIndexOf("_");
		String fileName = encoded;
		if (underscoreIndex != -1) {
			fileName = encoded.substring(0, underscoreIndex);
		} else {
			fileName = name;
		}
		return fileName.concat(extension);
	}

	public final static String encodeFilename(String origin) {
		int dotIndex = origin.lastIndexOf(".");
		String name = origin;
		String extension = "";
		if (dotIndex > 0) {
			name = origin.substring(0, dotIndex);
			extension = origin.substring(dotIndex);
		}
		String newName = name + "_" + System.currentTimeMillis() + extension;
		return newName;
	}

	public final static String retrieveContentFilename(
			String encodedArchiveFilename) {
		String archiveFileName = FileContainer
				.decodeFilename(encodedArchiveFilename);
		int dotIndex = archiveFileName.lastIndexOf(".");
		String name = archiveFileName;
		if (dotIndex > 0) {
			name = archiveFileName.substring(0, dotIndex);
		}
		return name;
	}

	public FileContainer(AlfrescoDocument alfrescoDocument,
			Set<Property> properties, boolean editable) {
		super();
		this.document = alfrescoDocument;
		this.editable = editable;
		retrieveSignatures();
		buildPropertyInsts(properties);
	}

	public FileContainer(UploadItem uploadItem, Set<Property> properties,
			boolean editable) {
		super();
		this.uploadItem = uploadItem;
		this.editable = editable;
		buildPropertyInsts(properties);
	}

	public FileContainer(Object item, Set<Property> properties, boolean editable) {
		if (item instanceof AlfrescoDocument) {
			this.document = (AlfrescoDocument) item;
			retrieveSignatures();
		} else if (item instanceof UploadItem) {
			this.uploadItem = (UploadItem) item;
		}
		this.editable = editable;
		buildPropertyInsts(properties);
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
		retrieveSignatures();
	}

	public String getRealFileName() {
		if (document != null && !document.getName().equals("")) {
			return document.getName();
		} else if (uploadItem != null && !uploadItem.getFileName().equals("")) {
			return uploadItem.getFileName();
		}
		return "";
	}

	public String getFileName() {
		if (document != null)
			return FileContainer.decodeFilename(this.getRealFileName());
		else if (uploadItem != null) {
			return uploadItem.getFileName();
		}
		return "";
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
		if (mimetype == null) {
			mimetype = retrieveContentMimetype();
		}
		return this.mimetype;
	}

	private String retrieveContentMimetype() {
		try {
			if (document.hasAspect(Signature.ASPECT_SIGNED)) {
				AlfrescoUserIdentity alfrescoUserIdentity = (AlfrescoUserIdentity) org.jboss.seam.Component
						.getInstance("alfrescoUserIdentity");
				ItemIterable<QueryResult> results = alfrescoUserIdentity
						.getSession().query(
								"SELECT cmis:objectId, cmis:name FROM cmis:document WHERE IN_TREE('"
										+ document.getId() + "')", true);
				if (results.getTotalNumItems() != 0) {
					Iterator<QueryResult> iterator = results.iterator();
					while (iterator.hasNext()) {
						QueryResult result = iterator.next();
						String cmisName = result
								.getPropertyValueById("cmis:name");
						if (cmisName.equals(FileContainer
								.retrieveContentFilename(document.getName()))) {
							String contentNodeRef = result
									.getPropertyValueById("cmis:objectId");
							AlfrescoDocument content = (AlfrescoDocument) alfrescoUserIdentity
									.getSession().getObject(contentNodeRef);
							return content.getContentStreamMimeType();
						}
					}
				}
			}
			return this.document.getContentStreamMimeType();

		} catch (Exception e) {
			return Resolver.mimetypeForExtension(this.getExtension());
		}
	}

	public List<DocumentPropertyInst> getSingleProperties() {
		return singleProperties;
	}

	public void setSingleProperties(
			List<DocumentPropertyInst> embeddedProperties) {
		this.singleProperties = embeddedProperties;
	}

	public String getId() {
		return id;
	}

	private void retrieveSignatures() {
		this.signatures = new ArrayList<Signature>();
		if (this.document != null && this.document.hasAspect("P:dw:signed")) {
			AlfrescoUserIdentity alfrescoUserIdentity = (AlfrescoUserIdentity) org.jboss.seam.Component
					.getInstance("alfrescoUserIdentity");
			ItemIterable<QueryResult> results = alfrescoUserIdentity
					.getSession().query(
							"SELECT cmis:objectId," + Signature.VALIDITY + ","
									+ Signature.EXPIRY + ","
									+ Signature.AUTHORITY + ","
									+ Signature.SIGN + "," + Signature.CF
									+ " from " + Signature.SIGNATURE_TYPE
									+ " WHERE IN_TREE('" + document.getId()
									+ "')", true);
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

	private void buildPropertyInsts(Set<Property> properties) {
		for (Property p : properties) {
			if (!p.isMultiple()) {
				DocumentPropertyInst singlePropertyInst = buildValorisedDocumentPropertyInst(p);
				singleProperties.add(singlePropertyInst);
			} else if (p.isMultiple()) {
				MultipleDocumentPropertyInst multiplePropertyInst = buildValorisedMultipleDocumentPropertyInst(p);
				multipleProperties.add(multiplePropertyInst);
			}
		}
	}

	private DocumentPropertyInst buildValorisedDocumentPropertyInst(Property p) {
		DocumentPropertyInst embeddedPropertyInst = new DocumentPropertyInst(p);
		if (this.document != null) {
			Object propertyValue = document.getPropertyValue(p.getName());
			embeddedPropertyInst.setValue(propertyValue);
		}
		embeddedPropertyInst.setEditable(this.editable);
		return embeddedPropertyInst;
	}

	private MultipleDocumentPropertyInst buildValorisedMultipleDocumentPropertyInst(
			Property p) {
		MultipleDocumentPropertyInst multiplePropertyInst = new MultipleDocumentPropertyInst(
				p);
		if (this.document != null) {
			List<Object> objValues = document.getPropertyValue(p.getName());
			List<String> stringValues = new ArrayList<String>();
			if (p.getDataType().equals(DataType.STRING)) {
				for (Object obj : objValues) {
					String value = ((String) obj).toString();
					stringValues.add(value);
				}
			}
			if (p.getDataType().equals(DataType.INTEGER)) {
				for (Object obj : objValues) {
					String value = ((BigInteger) obj).toString();
					stringValues.add(value);
				}
			}
			multiplePropertyInst.setValues(stringValues);
		}
		multiplePropertyInst.setEditable(this.editable);
		return multiplePropertyInst;
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

	public List<MultipleDocumentPropertyInst> getMultipleProperties() {
		return multipleProperties;
	}

	public void setMultipleProperties(
			List<MultipleDocumentPropertyInst> multipleProperties) {
		this.multipleProperties = multipleProperties;
	}

	// public void setSignatures(List<Signature> signatures) {
	// this.signatures = signatures;
	// }

}
