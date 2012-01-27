package it.drwolf.slot.pagebeans.support;

import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.alfresco.AlfrescoWrapper;
import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.alfresco.custom.support.DocumentPropertyInst;
import it.drwolf.slot.digsig.DigsigModel;
import it.drwolf.slot.digsig.Signature;
import it.drwolf.slot.enums.DataType;
import it.drwolf.utils.mimetypes.Resolver;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.commons.io.FilenameUtils;
import org.richfaces.model.UploadItem;

public class FileContainer {
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

	private UploadItem uploadItem;
	private AlfrescoDocument document;
	private List<DocumentPropertyInst> documentProperties = new ArrayList<DocumentPropertyInst>();
	private boolean editable = true;

	private String id = UUID.randomUUID().toString();

	private List<Signature> signatures;

	private String mimetype;

	private Boolean notYetValid;
	private Boolean expired;
	private Boolean globalValidity;

	public FileContainer(AlfrescoDocument alfrescoDocument,
			Set<Property> properties, boolean editable) {
		super();
		this.document = alfrescoDocument;
		this.editable = editable;
		this.retrieveSignatures();
		this.buildPropertyInsts(properties);
	}

	public FileContainer(Object item, Set<Property> properties, boolean editable) {
		if (item instanceof AlfrescoDocument) {
			this.document = (AlfrescoDocument) item;
			this.retrieveSignatures();
		} else if (item instanceof UploadItem) {
			this.uploadItem = (UploadItem) item;
		}
		this.editable = editable;
		this.buildPropertyInsts(properties);
	}

	public FileContainer(UploadItem uploadItem, Set<Property> properties,
			boolean editable) {
		super();
		this.uploadItem = uploadItem;
		this.editable = editable;
		this.buildPropertyInsts(properties);
	}

	private void buildPropertyInsts(Set<Property> properties) {
		for (Property p : properties) {
			DocumentPropertyInst documentProperty = this
					.buildValorisedDocumentPropertyInst(p);
			this.documentProperties.add(documentProperty);
		}
	}

	private DocumentPropertyInst buildValorisedDocumentPropertyInst(Property p) {
		DocumentPropertyInst documentPropertyInst = new DocumentPropertyInst(p);
		if (this.document != null) {
			if (!p.isMultiple()) {
				Object propertyValue = this.document.getPropertyValue(p
						.getName());
				documentPropertyInst.setValue(propertyValue);
			} else {
				List<Object> objValues = this.document.getPropertyValue(p
						.getName());
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
				documentPropertyInst.setValues(stringValues);
			}
		}
		documentPropertyInst.setEditable(this.editable);
		return documentPropertyInst;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FileContainer)) {
			return false;
		}
		FileContainer other = (FileContainer) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public AlfrescoDocument getDocument() {
		return this.document;
	}

	public List<DocumentPropertyInst> getDocumentProperties() {
		return this.documentProperties;
	}

	public Boolean getExpired() {
		return this.expired;
	}

	public String getExtension() {
		String fileName = this.getFileName();
		int dotIndex = fileName.lastIndexOf(".");
		return fileName.substring(dotIndex + 1);
	}

	public String getFileName() {
		if (this.document != null) {
			return FileContainer.decodeFilename(this.getRealFileName());
		} else if (this.uploadItem != null) {
			return this.uploadItem.getFileName();
		}
		return "";
	}

	public Boolean getGlobalValidity() {
		return this.globalValidity;
	}

	public String getId() {
		return this.id;
	}

	public String getMimetype() {
		if (this.mimetype == null) {
			this.mimetype = this.retrieveContentMimetype();
		}
		return this.mimetype;
	}

	public String getNodeRef() {
		if (this.document != null) {
			return AlfrescoWrapper.id2ref(this.document.getId());
		}
		return "";
	}

	public Boolean getNotYetValid() {
		return this.notYetValid;
	}

	public String getRealFileName() {
		if (this.document != null && !this.document.getName().equals("")) {
			return FilenameUtils.getName(this.document.getName());
		} else if (this.uploadItem != null
				&& !this.uploadItem.getFileName().equals("")) {
			return FilenameUtils.getName(this.uploadItem.getFileName());
		}
		return "";
	}

	public List<Signature> getSignatures() {
		return this.signatures;
	}

	public UploadItem getUploadItem() {
		return this.uploadItem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	public boolean isEditable() {
		return this.editable;
	}

	private String retrieveContentMimetype() {
		try {
			if (this.document.hasAspect(DigsigModel.ASPECT_SIGNED)
					&& !this.document.getContentStream().getMimeType()
							.contains("pdf")) {
				AlfrescoUserIdentity alfrescoUserIdentity = (AlfrescoUserIdentity) org.jboss.seam.Component
						.getInstance("alfrescoUserIdentity");
				ItemIterable<QueryResult> results = alfrescoUserIdentity
						.getSession().query(
								"SELECT cmis:objectId, cmis:name FROM cmis:document WHERE IN_TREE('"
										+ this.document.getId() + "')", true);
				if (results.getTotalNumItems() != 0) {
					Iterator<QueryResult> iterator = results.iterator();
					while (iterator.hasNext()) {
						QueryResult result = iterator.next();
						String cmisName = result
								.getPropertyValueById("cmis:name");
						int p7mIndex = this.document.getName().lastIndexOf(
								".p7m");
						if (cmisName.equals(this.document.getName().substring(
								0, p7mIndex))) {
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

	private void retrieveSignatures() {
		this.signatures = new ArrayList<Signature>();
		if (this.document != null && this.document.hasAspect("P:dw:signed")) {
			AlfrescoUserIdentity alfrescoUserIdentity = (AlfrescoUserIdentity) org.jboss.seam.Component
					.getInstance("alfrescoUserIdentity");
			ItemIterable<QueryResult> results = alfrescoUserIdentity
					.getSession().query(
							"SELECT cmis:objectId,"
									+ DigsigModel.SIGNATURE_VALIDITY + ","
									+ DigsigModel.SIGNATURE_NOT_AFTER + ","
									+ DigsigModel.SIGNATURE_NOT_BEFORE + ","
									+ DigsigModel.SIGNATURE_AUTHORITY + ","
									+ DigsigModel.SIGNATURE_SIGN + ","
									+ DigsigModel.SIGNATURE_CF + " from "
									+ DigsigModel.TYPE_SIGNATURE
									+ " WHERE IN_TREE('"
									+ this.document.getId() + "')", true);
			if (results.getTotalNumItems() != 0) {
				Iterator<QueryResult> iterator = results.iterator();
				while (iterator.hasNext()) {
					QueryResult result = iterator.next();
					String nodeRef = result
							.getPropertyValueById("cmis:objectId");
					Boolean validity = result
							.getPropertyValueById(DigsigModel.SIGNATURE_VALIDITY);
					Calendar expiry = result
							.getPropertyValueById(DigsigModel.SIGNATURE_NOT_AFTER);
					Calendar notBefore = result
							.getPropertyValueById(DigsigModel.SIGNATURE_NOT_BEFORE);
					String authority = result
							.getPropertyValueById(DigsigModel.SIGNATURE_AUTHORITY);
					String sign = result
							.getPropertyValueById(DigsigModel.SIGNATURE_SIGN);
					String cf = result
							.getPropertyValueById(DigsigModel.SIGNATURE_CF);

					Signature signature = new Signature(validity,
							expiry.getTime(), notBefore.getTime(), authority,
							sign, cf, nodeRef);
					this.signatures.add(signature);
				}
			}

			this.expired = ((Calendar) this.document
					.getPropertyValue(DigsigModel.SIGNED_SIGNATURE_EXPIRATION))
					.getTime().before(new Date());
			this.notYetValid = ((Calendar) this.document
					.getPropertyValue(DigsigModel.SIGNED_SIGNATURE_BEGINNING))
					.getTime().after(new Date());
			this.globalValidity = this.document
					.getPropertyValue(DigsigModel.SIGNED_GLOBAL_VALIDITY);
		}
	}

	public void setDocument(AlfrescoDocument alfrescoDocument) {
		this.document = alfrescoDocument;
		this.retrieveSignatures();
	}

	public void setDocumentProperties(
			List<DocumentPropertyInst> documentProperties) {
		this.documentProperties = documentProperties;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public void setExpired(Boolean expired) {
		this.expired = expired;
	}

	public void setGlobalValidity(Boolean globalValidity) {
		this.globalValidity = globalValidity;
	}

	public void setNotYetValid(Boolean notYetValid) {
		this.notYetValid = notYetValid;
	}

	public void setUploadItem(UploadItem uploadItem) {
		this.uploadItem = uploadItem;
	}

}
