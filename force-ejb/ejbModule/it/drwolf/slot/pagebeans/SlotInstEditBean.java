package it.drwolf.slot.pagebeans;

import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.alfresco.custom.model.Aspect;
import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.alfresco.custom.support.EmbeddedPropertyInst;
import it.drwolf.slot.application.CustomModelController;
import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.entity.DocInst;
import it.drwolf.slot.entity.DocInstCollection;
import it.drwolf.slot.entity.PropertyDef;
import it.drwolf.slot.entity.PropertyInst;
import it.drwolf.slot.session.SlotDefHome;
import it.drwolf.slot.session.SlotInstHome;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.MimetypesFileTypeMap;
import javax.persistence.EntityManager;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.alfresco.cmis.client.AlfrescoFolder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.security.Identity;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

@Name("slotInstEditBean")
@Scope(ScopeType.CONVERSATION)
public class SlotInstEditBean {

	@In(create = true)
	private SlotDefHome slotDefHome;

	@In(create = true)
	private SlotInstHome slotInstHome;

	@In(create = true)
	private EntityManager entityManager;

	@In(create = true)
	private CustomModelController customModelController;

	@In
	private AlfrescoUserIdentity alfrescoUserIdentity;

	@In
	private Identity identity;

	private List<PropertyInst> propertyInsts;

	private List<DocInstCollection> docInstCollections;

	private HashMap<Long, List<FileContainer>> datas = new HashMap<Long, List<FileContainer>>();

	private HashMap<Long, HashMap<String, List<EmbeddedPropertyInst>>> datasProperties = new HashMap<Long, HashMap<String, List<EmbeddedPropertyInst>>>();

	private HashMap<Long, String> messages = new HashMap<Long, String>();

	private HashMap<Long, List<AlfrescoDocument>> primaryDocs = new HashMap<Long, List<AlfrescoDocument>>();

	private Long activeCollectionId;
	private String activeFileName;
	private boolean activeExists;

	@Create
	public void init() {
		if (!slotInstHome.isIdDefined()) {
			this.propertyInsts = new ArrayList<PropertyInst>();
			for (PropertyDef propertyDef : slotDefHome.getInstance()
					.getPropertyDefs()) {
				PropertyInst propertyInst = new PropertyInst(propertyDef,
						slotInstHome.getInstance());
				propertyInsts.add(propertyInst);
			}

			this.docInstCollections = new ArrayList<DocInstCollection>();
			for (DocDefCollection defCollection : slotDefHome.getInstance()
					.getDocDefCollections()) {
				DocInstCollection instCollection = new DocInstCollection(
						slotInstHome.getInstance(), defCollection);
				docInstCollections.add(instCollection);

				//
				datas.put(defCollection.getId(), new ArrayList<FileContainer>());
				primaryDocs
						.put(defCollection.getId(), this
								.retrievePrimaryDocs(defCollection.getDocDef()
										.getId()));
			}

		} else {
			this.propertyInsts = new ArrayList<PropertyInst>(slotInstHome
					.getInstance().getPropertyInsts());

			this.docInstCollections = new ArrayList<DocInstCollection>(
					slotInstHome.getInstance().getDocInstCollections());
			initDatas();

			for (DocDefCollection defCollection : slotDefHome.getInstance()
					.getDocDefCollections()) {
				primaryDocs
						.put(defCollection.getId(), this
								.retrievePrimaryDocs(defCollection.getDocDef()
										.getId()));
			}
		}
	}

	private void initDatas() {
		for (DocInstCollection instCollection : this.docInstCollections) {
			DocDefCollection docDefCollection = instCollection
					.getDocDefCollection();
			// Long defCollectionId = docDefCollection.getId();
			Set<DocInst> docInsts = instCollection.getDocInsts();
			for (DocInst docInst : docInsts) {
				try {
					String nodeRef = docInst.getNodeRef();
					AlfrescoDocument document = (AlfrescoDocument) alfrescoUserIdentity
							.getSession().getObject(nodeRef);
					initDocAndProperties(docDefCollection, document, true);

				} catch (CmisObjectNotFoundException e) {
					FacesMessages.instance().add("Missing files on Alfresco");
					e.printStackTrace();
				}
			}
		}
	}

	private void initDocAndProperties(DocDefCollection docDefCollection,
			AlfrescoDocument document, boolean editables) {
		Long defCollectionId = docDefCollection.getId();
		String fileName = document.getName();
		// UploadItem uploadItem = new UploadItem(fileName, new Integer(0),
		// document.getContentStreamMimeType(), null);
		FileContainer container = new FileContainer(document);
		List<FileContainer> filesList = datas.get(defCollectionId);
		if (filesList == null) {
			filesList = new ArrayList<FileContainer>();
			datas.put(defCollectionId, filesList);
		}
		filesList.add(container);

		HashMap<String, List<EmbeddedPropertyInst>> filePropertiesMap = datasProperties
				.get(defCollectionId);
		if (filePropertiesMap == null) {
			filePropertiesMap = new HashMap<String, List<EmbeddedPropertyInst>>();
			datasProperties.put(defCollectionId, filePropertiesMap);
		}
		List<EmbeddedPropertyInst> fileProperties = filePropertiesMap
				.get(fileName);
		if (fileProperties == null) {
			fileProperties = new ArrayList<EmbeddedPropertyInst>();
			filePropertiesMap.put(fileName, fileProperties);
		}

		Set<String> aspectIds = docDefCollection.getDocDef().getAspectIds();
		for (String aspectId : aspectIds) {
			Aspect aspect = customModelController.getAspect(aspectId);
			List<Property> properties = aspect.getProperties();
			if (properties != null) {
				for (Property p : properties) {
					Object propertyValue = document.getPropertyValue(p
							.getName());
					EmbeddedPropertyInst embeddedPropertyInst = new EmbeddedPropertyInst(
							p);
					embeddedPropertyInst.setValue(propertyValue);
					embeddedPropertyInst.setEditable(editables);
					fileProperties.add(embeddedPropertyInst);
				}
			}
		}
	}

	private UploadItem buildUploadItemFromDocInst(DocInst docInst) {
		String nodeRef = docInst.getNodeRef();
		AlfrescoDocument document = (AlfrescoDocument) alfrescoUserIdentity
				.getSession().getObject(nodeRef);
		String fileName = document.getName();
		UploadItem uploadItem = new UploadItem(fileName, new Integer(0),
				document.getContentStreamMimeType(), null);
		return uploadItem;
	}

	public void save() {
		slotInstHome.getInstance().setPropertyInsts(
				new HashSet<PropertyInst>(this.propertyInsts));

		Set<Long> keySet = datas.keySet();
		for (Long key : keySet) {
			DocInstCollection instCollection = null;
			// controllo se c'è una collection che corrisponde a quell'id (ed è
			// la destinataria dei files uploadati)
			boolean found = false;
			Iterator<DocInstCollection> instCollIterator = docInstCollections
					.iterator();
			while (instCollIterator.hasNext() && found == false) {
				instCollection = instCollIterator.next();
				if (instCollection.getDocDefCollection().getId().equals(key)) {
					found = true;
				}
			}
			if (found) {
				try {
					List<FileContainer> containers = datas.get(key);
					for (FileContainer container : containers) {
						if (container.getUploadItem() != null) {
							String storedRef = storeOnAlfresco(
									container.getUploadItem(), instCollection);
							DocInst docInst = new DocInst(instCollection,
									storedRef);
							instCollection.getDocInsts().add(docInst);
						} else if (container.getDocument() != null) {
							System.out.println("si deve copiare "
									+ container.getDocument().getName());
							String storedRef = copyDocumentOnAlfresco(
									container.getDocument(), instCollection);
							DocInst docInst = new DocInst(instCollection,
									storedRef);
							instCollection.getDocInsts().add(docInst);
						}
					}
				} catch (Exception e) {
					FacesMessages.instance().add(
							"Errors in storing files on Alfresco");
					// TODO: se il documento è stato creato farsi dare l'id e
					// toglierlo per avere una garanzia "transazionale"
					e.printStackTrace();
					return;
				}
			}
		}

		slotInstHome.getInstance().setDocInstCollections(
				new HashSet<DocInstCollection>(docInstCollections));
		slotInstHome.getInstance().setOwnerId(
				identity.getCredentials().getUsername());
		slotInstHome.persist();
		FacesMessages.instance().add(
				"Slot " + this.slotDefHome.getInstance().getName()
						+ " successfully created");
	}

	public void update() {
		Set<DocInstCollection> persistedDocInstCollections = slotInstHome
				.getInstance().getDocInstCollections();
		for (DocInstCollection instCollection : persistedDocInstCollections) {
			Set<DocInst> docInsts = instCollection.getDocInsts();
			Iterator<DocInst> iterator = docInsts.iterator();
			while (iterator.hasNext()) {
				DocInst docInst = iterator.next();
				AlfrescoDocument document = (AlfrescoDocument) alfrescoUserIdentity
						.getSession().getObject(docInst.getNodeRef());
				String fileName = document.getName();
				Long docDefCollectionId = instCollection.getDocDefCollection()
						.getId();
				List<FileContainer> filesList = datas.get(docDefCollectionId);
				FileContainer itemContained = getItemIfContained(filesList,
						fileName);
				if (itemContained == null) {
					document.deleteAllVersions();
					iterator.remove();
					entityManager.remove(docInst);
				} else {
					if (itemContained.getDocument() != null) {
						// il file è quello vecchio ma potrebbe necessitare di
						// un aggiornamento delle properties
						if (itemContained.getDocument().getId()
								.equals(docInst.getNodeRef())) {
							System.out.println("Doc " + document.getName()
									+ " da aggiornare");
							updateProperties(itemContained.getFileName(),
									instCollection, document);
						} else {
							System.out
									.println("Primary Doc "
											+ document.getName()
											+ " nuovo ma con stesso nome di uno vecchio");
							document.deleteAllVersions();
							iterator.remove();
							entityManager.remove(docInst);
							String storedRef = copyDocumentOnAlfresco(
									itemContained.getDocument(), instCollection);
							DocInst newDocInst = new DocInst(instCollection,
									storedRef);
							instCollection.getDocInsts().add(newDocInst);
							// docInst.setNodeRef(storedRef);
						}
					} else if (itemContained.getUploadItem() != null) {
						// il file è in realtà un file diverso e va prima
						// cancellato il vecchio
						System.out.println("Doc " + document.getName()
								+ " nuovo ma con stesso nome di uno vecchio");
						document.deleteAllVersions();
						iterator.remove();
						entityManager.remove(docInst);
						String storedRef = storeOnAlfresco(
								itemContained.getUploadItem(), instCollection);
						DocInst newDocInst = new DocInst(instCollection,
								storedRef);
						instCollection.getDocInsts().add(newDocInst);
					}
				}
			}

			// Aggiungo elementi nuovi
			List<FileContainer> filesList = datas.get(instCollection
					.getDocDefCollection().getId());
			if (filesList != null) {
				for (FileContainer item : filesList) {
					System.out.println("Doc " + item.getFileName() + " nuovo");
					if (item.getUploadItem() != null) {
						// elemento nuovo da upload
						String storedRef = storeOnAlfresco(
								item.getUploadItem(), instCollection);
						DocInst docInst = new DocInst(instCollection, storedRef);
						instCollection.getDocInsts().add(docInst);
					} else {
						// elemento nuovo proveniente da primary doc
						String storedRef = copyDocumentOnAlfresco(
								item.getDocument(), instCollection);
						DocInst docInst = new DocInst(instCollection, storedRef);
						instCollection.getDocInsts().add(docInst);
					}
				}
			}
		}
		slotInstHome.update();
		FacesMessages.instance().add(
				"Slot " + this.slotDefHome.getInstance().getName()
						+ " successfully updated");
	}

	// quando trovo l'element lo tolgo così lascio solo quelli del tutto nuovi
	// su cui ciclare alla fine ed aggiungerli nell'update
	private FileContainer getItemIfContained(List<FileContainer> filesList,
			String fileName) {
		if (filesList != null) {
			Iterator<FileContainer> iterator = filesList.iterator();
			while (iterator.hasNext()) {
				FileContainer item = iterator.next();
				if (item.getFileName().equals(fileName)) {
					iterator.remove();
					return item;
				}
			}
		}
		return null;
	}

	public void listener(UploadEvent event) {
		UploadItem item = event.getUploadItem();
		addItemToDatas(item);

	}

	private void addItemToDatas(UploadItem item) {
		String fileName = item.getFileName();
		// String docDefCollectionIdAsString = ((HttpServletRequest)
		// javax.faces.context.FacesContext
		// .getCurrentInstance().getExternalContext().getRequest())
		// .getParameter("docDefCollectionId");
		// Long docDefCollectionId = new Long(docDefCollectionIdAsString);
		Long docDefCollectionId = this.activeCollectionId;

		messages.put(docDefCollectionId, "");

		// ///////////////////////////////////////////////
		// Controllo se è già presente un file con quel nome
		// AlfrescoFolder slotFolder = null;
		// Session session = alfrescoUserIdentity.getSession();
		// String userHomePath = alfrescoUserIdentity.getUserHomePath();
		// try {
		// slotFolder = (AlfrescoFolder) session.getObjectByPath(userHomePath
		// + "/" + this.slotDefHome.getInstance().getName());
		// session.getObjectByPath(slotFolder.getPath() + "/" + fileName);
		// DocDefCollection docDefCollection = entityManager.find(
		// DocDefCollection.class, docDefCollectionId);
		//
		// // un file con quel fileName esiste già nella cartella
		// this.messages.put(docDefCollectionId, slotFolder.getName()
		// + " already contains a file named " + fileName + "!");
		// return;
		// } catch (CmisObjectNotFoundException e) {
		// // DO NOTHING
		// }
		// /////////////////////////////////////////////

		if (!isAlreadyUploaded(fileName, docDefCollectionId)) {
			List<FileContainer> list = datas.get(docDefCollectionId);
			if (list == null) {
				list = new ArrayList<FileContainer>();
				datas.put(docDefCollectionId, list);
			}

			list.add(new FileContainer(item));
			System.out.println("-> " + fileName + " successfully uploaded");
			// System.out.println("-> docDefCollectionId: "
			// + docDefCollectionIdAsString);
			HashMap<String, List<EmbeddedPropertyInst>> filePropertiesMap = datasProperties
					.get(docDefCollectionId);
			if (filePropertiesMap == null) {
				filePropertiesMap = new HashMap<String, List<EmbeddedPropertyInst>>();
				datasProperties.put(docDefCollectionId, filePropertiesMap);
			}
			List<EmbeddedPropertyInst> fileProperties = filePropertiesMap
					.get(item.getFileName());
			if (fileProperties == null) {
				fileProperties = new ArrayList<EmbeddedPropertyInst>();
				filePropertiesMap.put(item.getFileName(), fileProperties);
			}
			DocDefCollection docDefCollection = entityManager.find(
					DocDefCollection.class, docDefCollectionId);
			Set<String> aspectsIds = docDefCollection.getDocDef()
					.getAspectIds();
			for (String aspectId : aspectsIds) {
				Aspect aspect = customModelController.getAspect(aspectId);
				List<Property> properties = aspect.getProperties();
				for (Property property : properties) {
					EmbeddedPropertyInst embeddedPropertyInst = new EmbeddedPropertyInst(
							property);
					if (!fileProperties.contains(embeddedPropertyInst)) {
						fileProperties.add(embeddedPropertyInst);
					}
				}
			}
		} else {
			System.out.println("---> file already uploaded in this collection");
		}
	}

	private boolean isAlreadyUploaded(String fileName, Long docDefCollectionId) {
		List<FileContainer> itemList = datas.get(docDefCollectionId);
		if (itemList != null) {
			Iterator<FileContainer> iterator = itemList.iterator();
			while (iterator.hasNext()) {
				if (iterator.next().getFileName().equals(fileName)) {
					this.messages.put(docDefCollectionId, fileName
							+ " already uploaded!");
					return true;
				}
			}
		}
		return false;
	}

	public void checkFileExistence() {
		System.out
				.println("---------------------------------------------> current file: "
						+ this.activeFileName);
		this.activeExists = true;
	}

	public void removeCleared(Long collectionId, String fileName) {
		System.out.println("---> removing " + fileName + "...");
		List<FileContainer> filesList = datas.get(collectionId);
		if (filesList != null) {
			Iterator<FileContainer> iterator = filesList.iterator();
			while (iterator.hasNext()) {
				FileContainer item = iterator.next();
				if (item.getFileName().equals(fileName)) {
					iterator.remove();
					datasProperties.get(collectionId).get(fileName).clear();
					return;
				}
			}
		}
	}

	private String storeOnAlfresco(UploadItem item,
			DocInstCollection instCollection) {
		String nodeRef = "";
		try {
			Session session = alfrescoUserIdentity.getSession();
			AlfrescoFolder slotFolder = findOrCreateSlotFolder(session);
			String contentType = new MimetypesFileTypeMap().getContentType(item
					.getFileName());
			ContentStreamImpl contentStreamImpl = new ContentStreamImpl(
					item.getFileName(), new BigInteger(""
							+ item.getFile().length()), contentType,
					new FileInputStream(item.getFile()));

			Set<String> aspects = instCollection.getDocDefCollection()
					.getDocDef().getAspectIds();

			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(PropertyIds.NAME, item.getFileName());
			properties.put(PropertyIds.OBJECT_TYPE_ID,
					BaseTypeId.CMIS_DOCUMENT.value());

			ObjectId objectId = session.createDocument(properties, slotFolder,
					contentStreamImpl, VersioningState.NONE, null, null, null);
			System.out.println(objectId);

			AlfrescoDocument document = (AlfrescoDocument) session
					.getObject(objectId);

			// prima si aggiungono gli aspect
			for (String aspect : aspects) {
				document.addAspect(aspect);
			}

			// poi si aggiungono i valori delle relative properties
			updateProperties(item.getFileName(), instCollection, document);
			nodeRef = objectId.getId();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nodeRef;
	}

	private AlfrescoFolder findOrCreateSlotFolder(Session session) {
		AlfrescoFolder homeFolder = alfrescoUserIdentity.getUserHomeFolder();

		// cerco la cartella con il nome dello slot e se non c'è la creo
		String slotName = slotInstHome.getInstance().getSlotDef().getName();
		String userHomePath = alfrescoUserIdentity.getUserHomePath();
		AlfrescoFolder slotFolder;
		try {
			slotFolder = (AlfrescoFolder) session.getObjectByPath(userHomePath
					+ "/" + slotName);
		} catch (CmisObjectNotFoundException e) {
			HashMap<String, Object> props = new HashMap<String, Object>();
			props.put(PropertyIds.NAME, slotName);
			props.put(PropertyIds.OBJECT_TYPE_ID,
					BaseTypeId.CMIS_FOLDER.value());
			slotFolder = (AlfrescoFolder) homeFolder.createFolder(props, null,
					null, null, session.createOperationContext());
		}
		return slotFolder;
	}

	private String copyDocumentOnAlfresco(AlfrescoDocument document,
			DocInstCollection instCollection) {
		String nodeRef = "";

		Session session = alfrescoUserIdentity.getSession();
		AlfrescoFolder slotFolder = findOrCreateSlotFolder(session);

		// String contentType = new MimetypesFileTypeMap().getContentType(item
		// .getFileName());
		ContentStream contentStream = document.getContentStream();
		Set<String> aspects = instCollection.getDocDefCollection().getDocDef()
				.getAspectIds();

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(PropertyIds.NAME, document.getName());
		properties.put(PropertyIds.OBJECT_TYPE_ID,
				BaseTypeId.CMIS_DOCUMENT.value());

		ObjectId objectId = session.createDocument(properties, slotFolder,
				contentStream, VersioningState.NONE, null, null, null);
		System.out.println(objectId);

		AlfrescoDocument documentCopy = (AlfrescoDocument) session
				.getObject(objectId);

		// prima si aggiungono gli aspect
		for (String aspect : aspects) {
			documentCopy.addAspect(aspect);
		}

		// poi si aggiungono i valori delle relative properties
		updateProperties(documentCopy.getName(), instCollection, documentCopy);
		nodeRef = objectId.getId();

		// Document copy = document.copy(slotFolder);
		// nodeRef = copy.getId();

		return nodeRef;
	}

	private void updateProperties(String fileName,
			DocInstCollection instCollection, AlfrescoDocument document) {
		List<EmbeddedPropertyInst> docPropertyValues = datasProperties.get(
				instCollection.getDocDefCollection().getId()).get(fileName);
		Map<String, Object> aspectsProperties = new HashMap<String, Object>();
		for (EmbeddedPropertyInst embeddedPropertyInst : docPropertyValues) {
			if (embeddedPropertyInst.getValue() != null) {
				aspectsProperties.put(embeddedPropertyInst.getProperty()
						.getName(), embeddedPropertyInst.getValue());
			}
		}
		document.updateProperties(aspectsProperties);
	}

	@SuppressWarnings("unchecked")
	public List<UploadItem> getPrimaryDatas(Long docDefId) {
		List<UploadItem> uploadItems = new ArrayList<UploadItem>();
		List<DocInst> resultList = entityManager
				.createQuery(
						"from DocInst d where d.docInstCollection.slotInst.slotDef.type = 'Primary' and d.docInstCollection.slotInst.ownerId=:ownerId and d.docInstCollection.docDefCollection.docDef.id=:docDefId")
				.setParameter("ownerId",
						identity.getCredentials().getUsername())
				.setParameter("docDefId", docDefId).getResultList();
		if (resultList != null) {
			for (DocInst docInst : resultList) {
				UploadItem uploadItem = buildUploadItemFromDocInst(docInst);
				uploadItems.add(uploadItem);
			}
		}
		return uploadItems;
	}

	@SuppressWarnings("unchecked")
	private List<AlfrescoDocument> retrievePrimaryDocs(Long docDefId) {
		List<AlfrescoDocument> primaryDocs = new ArrayList<AlfrescoDocument>();
		List<DocInst> resultList = entityManager
				.createQuery(
						"from DocInst d where d.docInstCollection.slotInst.slotDef.type = 'Primary' and d.docInstCollection.slotInst.ownerId=:ownerId and d.docInstCollection.docDefCollection.docDef.id=:docDefId")
				.setParameter("ownerId",
						identity.getCredentials().getUsername())
				.setParameter("docDefId", docDefId).getResultList();
		if (resultList != null) {
			for (DocInst docInst : resultList) {
				String nodeRef = docInst.getNodeRef();
				AlfrescoDocument document = (AlfrescoDocument) alfrescoUserIdentity
						.getSession().getObject(nodeRef);
				primaryDocs.add(document);
			}
		}
		return primaryDocs;
	}

	public void addDocFromPrimary(Long docDefCollectionId,
			AlfrescoDocument document) {
		this.activeCollectionId = docDefCollectionId;
		DocDefCollection docDefCollection = entityManager.find(
				DocDefCollection.class, docDefCollectionId);
		initDocAndProperties(docDefCollection, document, false);
	}

	public List<PropertyInst> getPropertyInsts() {
		return propertyInsts;
	}

	public void setPropertyInsts(List<PropertyInst> propertyInsts) {
		this.propertyInsts = propertyInsts;
	}

	public List<DocInstCollection> getDocInstCollections() {
		return docInstCollections;
	}

	public void setDocInstCollections(List<DocInstCollection> docInstCollections) {
		this.docInstCollections = docInstCollections;
	}

	public HashMap<Long, List<FileContainer>> getDatas() {
		return datas;
	}

	public void setDatas(HashMap<Long, List<FileContainer>> datas) {
		this.datas = datas;
	}

	public HashMap<Long, HashMap<String, List<EmbeddedPropertyInst>>> getDatasProperties() {
		return datasProperties;
	}

	public void setDatasProperties(
			HashMap<Long, HashMap<String, List<EmbeddedPropertyInst>>> datasProperties) {
		this.datasProperties = datasProperties;
	}

	public HashMap<Long, String> getMessages() {
		return messages;
	}

	public void setMessages(HashMap<Long, String> messages) {
		this.messages = messages;
	}

	public Long getActiveCollectionId() {
		return activeCollectionId;
	}

	public void setActiveCollectionId(Long activeCollection) {
		this.activeCollectionId = activeCollection;
	}

	public HashMap<Long, List<AlfrescoDocument>> getPrimaryDocs() {
		return primaryDocs;
	}

	public void setPrimaryDocs(HashMap<Long, List<AlfrescoDocument>> primaryDocs) {
		this.primaryDocs = primaryDocs;
	}

	public class FileContainer {
		private UploadItem uploadItem;
		private AlfrescoDocument document;

		public FileContainer(AlfrescoDocument alfrescoDocument) {
			super();
			this.document = alfrescoDocument;
		}

		public FileContainer(UploadItem uploadItem) {
			super();
			this.uploadItem = uploadItem;
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

		public String getFileName() {
			if (uploadItem != null && !uploadItem.getFileName().equals("")) {
				return uploadItem.getFileName();
			} else if (document != null && !document.getName().equals("")) {
				return document.getName();
			}
			return "";
		}

	}

	public String getActiveFileName() {
		return activeFileName;
	}

	public void setActiveFileName(String activeFileName) {
		this.activeFileName = activeFileName;
	}

	public boolean isActiveExists() {
		return activeExists;
	}

	public void setActiveExists(boolean activeExists) {
		this.activeExists = activeExists;
	}

}
