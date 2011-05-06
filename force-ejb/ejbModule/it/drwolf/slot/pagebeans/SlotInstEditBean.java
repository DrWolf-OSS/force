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
import javax.servlet.http.HttpServletRequest;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.alfresco.cmis.client.AlfrescoFolder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
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

	private List<PropertyInst> propertyInsts;

	private List<DocInstCollection> docInstCollections;

	private HashMap<Long, List<UploadItem>> datas = new HashMap<Long, List<UploadItem>>();

	private HashMap<Long, HashMap<String, List<EmbeddedPropertyInst>>> datasProperties = new HashMap<Long, HashMap<String, List<EmbeddedPropertyInst>>>();

	private HashMap<Long, String> messages = new HashMap<Long, String>();

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
			}

		} else {
			this.propertyInsts = new ArrayList<PropertyInst>(slotInstHome
					.getInstance().getPropertyInsts());

			this.docInstCollections = new ArrayList<DocInstCollection>(
					slotInstHome.getInstance().getDocInstCollections());
			initDatas();
		}
	}

	private void initDatas() {
		for (DocInstCollection instCollection : this.docInstCollections) {
			Long defCollectionId = instCollection.getDocDefCollection().getId();
			Set<DocInst> docInsts = instCollection.getDocInsts();
			for (DocInst docInst : docInsts) {
				try {
					String nodeRef = docInst.getNodeRef();
					AlfrescoDocument document = (AlfrescoDocument) alfrescoUserIdentity
							.getSession().getObject(nodeRef);
					String fileName = document.getName();
					UploadItem uploadItem = new UploadItem(fileName,
							new Integer(0),
							document.getContentStreamMimeType(), null);
					List<UploadItem> filesList = datas.get(defCollectionId);
					if (filesList == null) {
						filesList = new ArrayList<UploadItem>();
						datas.put(defCollectionId, filesList);
					}
					filesList.add(uploadItem);

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

					Set<String> aspectIds = instCollection
							.getDocDefCollection().getDocDef().getAspectIds();
					for (String aspectId : aspectIds) {
						Aspect aspect = customModelController.getAspect(aspectId);
						List<Property> properties = aspect.getProperties();
						if (properties != null) {
							for (Property p : properties) {
								Object propertyValue = document
										.getPropertyValue(p.getName());
								EmbeddedPropertyInst embeddedPropertyInst = new EmbeddedPropertyInst(
										p);
								embeddedPropertyInst.setValue(propertyValue);
								fileProperties.add(embeddedPropertyInst);
							}
						}
					}

				} catch (CmisObjectNotFoundException e) {
					FacesMessages.instance().add("Missing files on Alfresco");
					e.printStackTrace();
				}
			}
		}
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
					List<UploadItem> files = datas.get(key);
					for (UploadItem file : files) {
						String storedRef = storeOnAlfresco(file, instCollection);
						DocInst docInst = new DocInst(instCollection, storedRef);
						instCollection.getDocInsts().add(docInst);
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
				List<UploadItem> filesList = datas.get(docDefCollectionId);
				UploadItem itemContained = getItemIfContained(filesList,
						fileName);
				if (itemContained == null) {
					document.deleteAllVersions();
					iterator.remove();
					entityManager.remove(docInst);
				} else {
					// Gli uploadItem di file precedentemente salvati sono
					// "fittizi", creati dai documenti già salvati ed hanno il
					// campo file==null. Questo permette di riconoscere files
					// nuovi che hanno però lo stesso fileName di uno
					// precedentemente archiviato nella collection
					if (itemContained.getFile() == null) {
						// il file è quello vecchio ma potrebbe necessitare di
						// un aggiornamento delle properties
						System.out.println("Doc " + document.getName()
								+ " da aggiornare");
						updateProperties(itemContained, instCollection,
								document);
					} else {
						// il file è in realtà un file diverso e va prima
						// cancellato il vecchio
						System.out.println("Doc " + document.getName()
								+ " nuovo ma con stesso nome di uno vecchio");
						document.deleteAllVersions();
						iterator.remove();
						entityManager.remove(docInst);
						String storedRef = storeOnAlfresco(itemContained,
								instCollection);
						DocInst newDocInst = new DocInst(instCollection,
								storedRef);
						instCollection.getDocInsts().add(newDocInst);
					}
				}
			}

			// Aggiungo elementi nuovi
			List<UploadItem> filesList = datas.get(instCollection
					.getDocDefCollection().getId());
			if (filesList != null) {
				for (UploadItem item : filesList) {
					System.out.println("Doc " + item.getFileName() + " nuovo");
					String storedRef = storeOnAlfresco(item, instCollection);
					DocInst docInst = new DocInst(instCollection, storedRef);
					instCollection.getDocInsts().add(docInst);
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
	private UploadItem getItemIfContained(List<UploadItem> filesList,
			String fileName) {
		if (filesList != null) {
			Iterator<UploadItem> iterator = filesList.iterator();
			while (iterator.hasNext()) {
				UploadItem item = iterator.next();
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
		String fileName = item.getFileName();
		String docDefCollectionIdAsString = ((HttpServletRequest) javax.faces.context.FacesContext
				.getCurrentInstance().getExternalContext().getRequest())
				.getParameter("docDefCollectionId");
		Long docDefCollectionId = new Long(docDefCollectionIdAsString);

		messages.put(docDefCollectionId, "");

		if (!isAlreadyUploaded(fileName, docDefCollectionId)) {
			List<UploadItem> list = datas.get(docDefCollectionId);
			if (list == null) {
				list = new ArrayList<UploadItem>();
				datas.put(docDefCollectionId, list);
			}

			list.add(item);
			System.out.println("-> " + fileName + " successfully uploaded");
			System.out.println("-> docDefCollectionId: "
					+ docDefCollectionIdAsString);
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
			Set<String> aspectsIds = docDefCollection.getDocDef().getAspectIds();
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
		List<UploadItem> itemList = datas.get(docDefCollectionId);
		if (itemList != null) {
			Iterator<UploadItem> iterator = itemList.iterator();
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

	public void removeCleared(Long collectionId, String fileName) {
		System.out.println("---> removing " + fileName + "...");
		List<UploadItem> filesList = datas.get(collectionId);
		if (filesList != null) {
			Iterator<UploadItem> iterator = filesList.iterator();
			while (iterator.hasNext()) {
				UploadItem item = iterator.next();
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
			AlfrescoFolder homeFolder = alfrescoUserIdentity
					.getUserHomeFolder();

			// cerco la cartella con il nome dello slot e se non c'è la creo
			String slotName = slotInstHome.getInstance().getSlotDef().getName();
			String userHomePath = alfrescoUserIdentity.getUserHomePath();
			AlfrescoFolder slotFolder;
			try {
				slotFolder = (AlfrescoFolder) session
						.getObjectByPath(userHomePath + "/" + slotName);
			} catch (CmisObjectNotFoundException e) {
				HashMap<String, Object> props = new HashMap<String, Object>();
				props.put(PropertyIds.NAME, slotName);
				props.put(PropertyIds.OBJECT_TYPE_ID,
						BaseTypeId.CMIS_FOLDER.value());
				slotFolder = (AlfrescoFolder) homeFolder.createFolder(props,
						null, null, null, session.createOperationContext());
			}

			Set<String> aspects = instCollection.getDocDefCollection()
					.getDocDef().getAspectIds();

			String contentType = new MimetypesFileTypeMap().getContentType(item
					.getFileName());
			ContentStreamImpl contentStreamImpl = new ContentStreamImpl(
					item.getFileName(), new BigInteger(""
							+ item.getFile().length()), contentType,
					new FileInputStream(item.getFile()));

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
			updateProperties(item, instCollection, document);
			nodeRef = objectId.getId();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nodeRef;
	}

	private void updateProperties(UploadItem item,
			DocInstCollection instCollection, AlfrescoDocument document) {
		List<EmbeddedPropertyInst> docPropertyValues = datasProperties.get(
				instCollection.getDocDefCollection().getId()).get(
				item.getFileName());
		Map<String, Object> aspectsProperties = new HashMap<String, Object>();
		for (EmbeddedPropertyInst embeddedPropertyInst : docPropertyValues) {
			if (embeddedPropertyInst.getValue() != null) {
				aspectsProperties.put(embeddedPropertyInst.getProperty()
						.getName(), embeddedPropertyInst.getValue());
			}
		}
		document.updateProperties(aspectsProperties);
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

	public HashMap<Long, List<UploadItem>> getDatas() {
		return datas;
	}

	public void setDatas(HashMap<Long, List<UploadItem>> datas) {
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
}
