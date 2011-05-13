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

	private HashMap<Long, List<FileContainer>> primaryDocs = new HashMap<Long, List<FileContainer>>();

	private Long activeCollectionId;

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
				List<AlfrescoDocument> collPrimaryDocs = this
						.retrievePrimaryDocs(defCollection.getDocDef().getId());
				List<FileContainer> fileContainers = new ArrayList<FileContainer>();
				for (AlfrescoDocument document : collPrimaryDocs) {
					FileContainer fileContainer = buildContainer(defCollection,
							document, false);
					fileContainers.add(fileContainer);
				}
				primaryDocs.put(defCollection.getId(), fileContainers);
			}

		} else {
			this.propertyInsts = new ArrayList<PropertyInst>(slotInstHome
					.getInstance().getPropertyInsts());

			this.docInstCollections = new ArrayList<DocInstCollection>(
					slotInstHome.getInstance().getDocInstCollections());
			for (DocInstCollection instCollection : this.docInstCollections) {
				DocDefCollection docDefCollection = instCollection
						.getDocDefCollection();
				this.datas.put(docDefCollection.getId(),
						new ArrayList<SlotInstEditBean.FileContainer>());
				Set<DocInst> docInsts = instCollection.getDocInsts();
				for (DocInst docInst : docInsts) {
					try {
						String nodeRef = docInst.getNodeRef();
						AlfrescoDocument document = (AlfrescoDocument) alfrescoUserIdentity
								.getSession().getObject(nodeRef);
						FileContainer container = buildContainer(
								docDefCollection, document, true);
						this.datas.get(docDefCollection.getId()).add(container);
					} catch (CmisObjectNotFoundException e) {
						FacesMessages.instance().add(
								"Missing files on Alfresco");
						e.printStackTrace();
					}
				}
			}

			for (DocDefCollection defCollection : slotDefHome.getInstance()
					.getDocDefCollections()) {
				List<AlfrescoDocument> collPrimaryDocs = this
						.retrievePrimaryDocs(defCollection.getDocDef().getId());
				List<FileContainer> fileContainers = new ArrayList<FileContainer>();
				for (AlfrescoDocument document : collPrimaryDocs) {
					// FileContainer fileContainer = new
					// FileContainer(document);
					FileContainer fileContainer = buildContainer(defCollection,
							document, false);
					fileContainers.add(fileContainer);
				}
				primaryDocs.put(defCollection.getId(), fileContainers);
			}
		}
	}

	private FileContainer buildContainer(DocDefCollection docDefCollection,
			AlfrescoDocument document, boolean editables) {
		FileContainer container = new FileContainer(document);
		List<EmbeddedPropertyInst> fileProperties = new ArrayList<EmbeddedPropertyInst>();
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
		container.setEmbeddedProperties(fileProperties);
		return container;
	}

	private FileContainer buildContainer(DocDefCollection docDefCollection,
			UploadItem uploadItem, boolean editables) {
		FileContainer container = new FileContainer(uploadItem);
		List<EmbeddedPropertyInst> fileProperties = new ArrayList<EmbeddedPropertyInst>();
		Set<String> aspectIds = docDefCollection.getDocDef().getAspectIds();
		for (String aspectId : aspectIds) {
			Aspect aspect = customModelController.getAspect(aspectId);
			List<Property> properties = aspect.getProperties();
			if (properties != null) {
				for (Property p : properties) {
					EmbeddedPropertyInst embeddedPropertyInst = new EmbeddedPropertyInst(
							p);
					embeddedPropertyInst.setEditable(editables);
					fileProperties.add(embeddedPropertyInst);
				}
			}
		}
		container.setEmbeddedProperties(fileProperties);
		return container;
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
									container.getUploadItem(), instCollection,
									container.getEmbeddedProperties());
							DocInst docInst = new DocInst(instCollection,
									storedRef);
							instCollection.getDocInsts().add(docInst);
						} else if (container.getDocument() != null) {
							System.out.println("si deve copiare "
									+ container.getDocument().getName());
							String storedRef = copyDocumentOnAlfresco(
									container.getDocument(), instCollection,
									container.getEmbeddedProperties());
							DocInst docInst = new DocInst(instCollection,
									storedRef);
							instCollection.getDocInsts().add(docInst);
						}
					}
				} catch (Exception e) {
					FacesMessages.instance().add(
							"Errors in Alfresco storage process");
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
				Long docDefCollectionId = instCollection.getDocDefCollection()
						.getId();
				List<FileContainer> filesList = datas.get(docDefCollectionId);

				// Controllo se il document è ancora presente nella lista dei
				// file associati, se c'è ancora ne aggiorno le properties
				// altrimenti lo cancello da alfresco e lo tolgo dalla relativa
				// collection
				FileContainer itemContained = getItemIfContained(filesList,
						document.getId());
				if (itemContained == null) {
					document.deleteAllVersions();
					iterator.remove();
					entityManager.remove(docInst);
				} else {
					// il file è quello vecchio ma potrebbe necessitare di
					// un aggiornamento delle properties
					System.out.println("Doc " + document.getName()
							+ " da aggiornare");
					updateProperties(document,
							itemContained.getEmbeddedProperties());
				}
			}

			// Aggiungo elementi nuovi
			List<FileContainer> filesList = datas.get(instCollection
					.getDocDefCollection().getId());
			if (filesList != null) {
				for (FileContainer item : filesList) {
					if (item.getUploadItem() != null) {
						// elemento nuovo da upload
						System.out.println("Doc " + item.getFileName()
								+ " nuovo da upload");
						String storedRef = storeOnAlfresco(
								item.getUploadItem(), instCollection,
								item.getEmbeddedProperties());
						DocInst docInst = new DocInst(instCollection, storedRef);
						instCollection.getDocInsts().add(docInst);
					} else {
						// elemento nuovo proveniente da primary doc
						System.out.println("Doc " + item.getFileName()
								+ " nuovo da primary docs");
						String storedRef = copyDocumentOnAlfresco(
								item.getDocument(), instCollection,
								item.getEmbeddedProperties());
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
			String docRef) {
		if (filesList != null) {
			Iterator<FileContainer> iterator = filesList.iterator();
			while (iterator.hasNext()) {
				FileContainer item = iterator.next();
				if (item.getDocument() != null
						&& item.getDocument().getId().equals(docRef)) {
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
		Long docDefCollectionId = this.activeCollectionId;
		List<FileContainer> list = datas.get(docDefCollectionId);
		if (list == null) {
			list = new ArrayList<FileContainer>();
			datas.put(docDefCollectionId, list);
		}
		System.out.println("-> " + fileName + " successfully uploaded");
		DocDefCollection docDefCollection = entityManager.find(
				DocDefCollection.class, docDefCollectionId);
		FileContainer container = buildContainer(docDefCollection, item, true);
		datas.get(docDefCollection.getId()).add(container);
	}

	public void remove(Long collectionId, FileContainer container) {
		System.out.println("---> removing " + container.getFileName() + "...");
		List<FileContainer> filesList = datas.get(collectionId);
		filesList.remove(container);
	}

	private String storeOnAlfresco(UploadItem item,
			DocInstCollection instCollection,
			List<EmbeddedPropertyInst> embeddedProperties) {
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
			properties.put(PropertyIds.NAME,
					this.encodeFilename(item.getFileName()));
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
			updateProperties(document, embeddedProperties);
			nodeRef = objectId.getId();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nodeRef;
	}

	private String encodeFilename(String origin) {
		int dotIndex = origin.lastIndexOf(".");
		String name = origin.substring(0, dotIndex);
		String extension = origin.substring(dotIndex);
		String newName = name + "_" + System.currentTimeMillis() + extension;
		return newName;
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
			DocInstCollection instCollection,
			List<EmbeddedPropertyInst> embeddedProperties) {
		String nodeRef = "";

		Session session = alfrescoUserIdentity.getSession();
		AlfrescoFolder slotFolder = findOrCreateSlotFolder(session);

		ContentStream contentStream = document.getContentStream();
		Set<String> aspects = instCollection.getDocDefCollection().getDocDef()
				.getAspectIds();

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(PropertyIds.NAME, encodeFilename(new FileContainer(
				document).getFileName()));
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
		updateProperties(documentCopy, embeddedProperties);
		nodeRef = objectId.getId();

		return nodeRef;
	}

	private void updateProperties(AlfrescoDocument document,
			List<EmbeddedPropertyInst> embeddedProperties) {
		Map<String, Object> aspectsProperties = new HashMap<String, Object>();
		for (EmbeddedPropertyInst embeddedPropertyInst : embeddedProperties) {
			if (embeddedPropertyInst.getValue() != null) {
				aspectsProperties.put(embeddedPropertyInst.getProperty()
						.getName(), embeddedPropertyInst.getValue());
			}
		}
		document.updateProperties(aspectsProperties);
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
			FileContainer container) {
		this.activeCollectionId = docDefCollectionId;
		datas.get(docDefCollectionId).add(container);
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

	public Long getActiveCollectionId() {
		return activeCollectionId;
	}

	public void setActiveCollectionId(Long activeCollection) {
		this.activeCollectionId = activeCollection;
	}

	public HashMap<Long, List<FileContainer>> getPrimaryDocs() {
		return primaryDocs;
	}

	public void setPrimaryDocs(HashMap<Long, List<FileContainer>> primaryDocs) {
		this.primaryDocs = primaryDocs;
	}

	public class FileContainer {
		private UploadItem uploadItem;
		private AlfrescoDocument document;
		private List<EmbeddedPropertyInst> embeddedProperties = new ArrayList<EmbeddedPropertyInst>();

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

		public List<EmbeddedPropertyInst> getEmbeddedProperties() {
			return embeddedProperties;
		}

		public void setEmbeddedProperties(
				List<EmbeddedPropertyInst> embeddedProperties) {
			this.embeddedProperties = embeddedProperties;
		}

	}

}
