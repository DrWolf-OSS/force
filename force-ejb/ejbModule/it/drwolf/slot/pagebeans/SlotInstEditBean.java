package it.drwolf.slot.pagebeans;

import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.alfresco.AlfrescoWrapper;
import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.alfresco.custom.support.DocumentPropertyInst;
import it.drwolf.slot.application.CustomModelController;
import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.entity.DocInst;
import it.drwolf.slot.entity.DocInstCollection;
import it.drwolf.slot.entity.EmbeddedProperty;
import it.drwolf.slot.entity.PropertyDef;
import it.drwolf.slot.entity.PropertyInst;
import it.drwolf.slot.entity.Rule;
import it.drwolf.slot.entity.RuleParameterInst;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.interfaces.IRuleVerifier;
import it.drwolf.slot.pagebeans.support.FileContainer;
import it.drwolf.slot.prefs.PreferenceKey;
import it.drwolf.slot.prefs.Preferences;
import it.drwolf.slot.ruleverifier.RuleParametersResolver;
import it.drwolf.slot.ruleverifier.VerifierMessage;
import it.drwolf.slot.ruleverifier.VerifierMessageType;
import it.drwolf.slot.ruleverifier.VerifierParameterDef;
import it.drwolf.slot.ruleverifier.VerifierParameterInst;
import it.drwolf.slot.ruleverifier.VerifierReport;
import it.drwolf.slot.ruleverifier.VerifierResult;
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
import org.apache.chemistry.opencmis.client.api.Folder;
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
import org.jboss.seam.annotations.Transactional;
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

	@In(create = true)
	private AlfrescoUserIdentity alfrescoUserIdentity;

	@In(create = true)
	private AlfrescoWrapper alfrescoWrapper;

	@In
	private Identity identity;

	@In(create = true)
	private Preferences preferences;

	@In(create = true)
	private RuleParametersResolver ruleParametersResolver;

	private List<PropertyInst> propertyInsts;

	private List<DocInstCollection> docInstCollections;

	private HashMap<Long, List<FileContainer>> datas = new HashMap<Long, List<FileContainer>>();

	private HashMap<Long, List<FileContainer>> primaryDocs = new HashMap<Long, List<FileContainer>>();

	// main messages
	private ArrayList<VerifierMessage> slotMessages = new ArrayList<VerifierMessage>();

	// FileContainer.id, messages
	private HashMap<String, ArrayList<VerifierMessage>> filesMessages = new HashMap<String, ArrayList<VerifierMessage>>();

	// DocDefCollection.id, messages
	private HashMap<Long, ArrayList<VerifierMessage>> collectionsMessages = new HashMap<Long, ArrayList<VerifierMessage>>();

	private Long activeCollectionId;

	private FileContainer activeFileContainer;

	boolean verifiable = true;

	boolean failAllowed = false;

	boolean warning = false;

	private void resetFlags() {
		verifiable = true;
		failAllowed = false;
		// warning = false;
	}

	@Create
	public void init() {
		if (slotInstHome.getId() == null) {
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
					FileContainer fileContainer = buildFileContainer(
							defCollection, document, false);
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
						new ArrayList<FileContainer>());
				Set<DocInst> docInsts = instCollection.getDocInsts();
				for (DocInst docInst : docInsts) {
					try {
						String nodeRef = docInst.getNodeRef();
						AlfrescoDocument document = (AlfrescoDocument) alfrescoUserIdentity
								.getSession().getObject(nodeRef);
						FileContainer container = buildFileContainer(
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
					FileContainer fileContainer = buildFileContainer(
							defCollection, document, false);
					fileContainers.add(fileContainer);
				}
				primaryDocs.put(defCollection.getId(), fileContainers);
			}

			verify();
		}

	}

	private FileContainer buildFileContainer(DocDefCollection docDefCollection,
			Object item, boolean editables) {
		List<DocumentPropertyInst> fileProperties = new ArrayList<DocumentPropertyInst>();
		Set<String> aspectIds = docDefCollection.getDocDef().getAspectIds();
		Set<Property> properties = new HashSet<Property>();
		// Recupero tutte le properties.
		// Essendo un set anche se un aspect è applicato più volte (essendo
		// settato come mandatory su un altro) le sue properties vengono
		// aggiunte una volta sola
		for (String aspectId : aspectIds) {
			properties.addAll(customModelController.getProperties(aspectId));
		}
		if (properties != null) {
			for (Property p : properties) {
				DocumentPropertyInst documentPropertyInst = buildValorisedDocumentPropertyInst(
						item, editables, p);
				fileProperties.add(documentPropertyInst);
			}
		}
		FileContainer container = new FileContainer(item);
		container.setEditable(editables);
		container.setEmbeddedProperties(fileProperties);
		return container;
	}

	private DocumentPropertyInst buildValorisedDocumentPropertyInst(
			Object item, boolean editables, Property p) {
		DocumentPropertyInst embeddedPropertyInst = new DocumentPropertyInst(p);
		if (item instanceof AlfrescoDocument) {
			AlfrescoDocument document = (AlfrescoDocument) item;
			Object propertyValue = document.getPropertyValue(p.getName());
			embeddedPropertyInst.setValue(propertyValue);
		}
		embeddedPropertyInst.setEditable(editables);
		return embeddedPropertyInst;
	}

	private void cleanMessages() {
		Set<String> filesKeys = filesMessages.keySet();
		for (String key : filesKeys) {
			ArrayList<VerifierMessage> messages = filesMessages.get(key);
			if (messages != null) {
				messages.clear();
			}
		}

		Set<Long> collectionsKeys = collectionsMessages.keySet();
		for (Long key : collectionsKeys) {
			ArrayList<VerifierMessage> messages = collectionsMessages.get(key);
			if (messages != null) {
				messages.clear();
			}
		}

		if (this.slotMessages != null) {
			slotMessages.clear();
		}
	}

	public String save() {
		cleanMessages();
		boolean sizeCollectionPassed = checkCollectionsSize();
		if (!sizeCollectionPassed) {
			FacesMessages
					.instance()
					.add("Le dimensioni di alcune collection non rispettano le specifiche");
			return "failed";
		}

		boolean rulesPassed = verify();
		if (!rulesPassed) {
			FacesMessages.instance().add("Alcune regole non sono verificate!");
			return "failed";
		}

		slotInstHome.getInstance().setPropertyInsts(
				new HashSet<PropertyInst>(this.propertyInsts));

		AlfrescoFolder slotFolder = retrieveSlotFolder();

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
									container.getEmbeddedProperties(),
									slotFolder);
							DocInst docInst = new DocInst(instCollection,
									storedRef);
							instCollection.getDocInsts().add(docInst);
						} else if (container.getDocument() != null) {
							System.out.println("si deve copiare "
									+ container.getDocument().getName());
							String storedRef = copyDocumentOnAlfresco(
									container.getDocument(), instCollection,
									container.getEmbeddedProperties(),
									slotFolder);
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
					return "failed";
				}
			}
		}

		slotInstHome.getInstance().setDocInstCollections(
				new HashSet<DocInstCollection>(docInstCollections));
		slotInstHome.getInstance().setOwnerId(
				alfrescoUserIdentity.getActiveGroup().getShortName());
		slotInstHome.persist();
		FacesMessages.instance().add(
				"Slot " + this.slotDefHome.getInstance().getName()
						+ " successfully created");

		if (this.warning) {
			warning = false;
			return "warning";
		}

		return "saved";
	}

	private boolean checkCollectionsSize() {
		boolean passed = true;

		for (DocDefCollection defCollection : slotDefHome.getInstance()
				.getDocDefCollections()) {
			List<FileContainer> list = datas.get(defCollection.getId());
			int size = list.size();
			if (defCollection.getMin() != null && size < defCollection.getMin()) {
				passed = false;
				this.addCollectionMessage(defCollection.getId(),
						new VerifierMessage(
								"La quantità minima di documenti in questa collection è di "
										+ defCollection.getMin()
										+ " documento/i",
								VerifierMessageType.ERROR));
			}

			if (defCollection.getMax() != null && size > defCollection.getMax()) {
				passed = false;
				this.addCollectionMessage(defCollection.getId(),
						new VerifierMessage(
								"La quantità massima di documenti in questa collection è di "
										+ defCollection.getMax()
										+ " documento/i",
								VerifierMessageType.ERROR));
			}
		}
		return passed;
	}

	private AlfrescoFolder retrieveSlotFolder() {
		AlfrescoFolder groupFolder = alfrescoWrapper.findOrCreateFolder(
				preferences.getValue(PreferenceKey.FORCE_GROUPS_PATH.name()),
				alfrescoUserIdentity.getActiveGroup().getShortName());

		AlfrescoFolder slotFolder = alfrescoWrapper.findOrCreateFolder(
				groupFolder, slotInstHome.getInstance().getSlotDef().getName());
		return slotFolder;
	}

	@Transactional
	public String update() {
		cleanMessages();
		boolean sizeCollectionPassed = checkCollectionsSize();
		if (!sizeCollectionPassed) {
			FacesMessages
					.instance()
					.add("Le dimensioni di alcune collection non rispettano le specifiche");
			return "failed";
		}

		boolean rulesPassed = verify();
		if (!rulesPassed) {
			FacesMessages.instance().add("Alcune regole non sono verificate!");

			Long slotInstId = slotInstHome.getSlotInstId();
			entityManager.clear();
			slotInstHome.clearInstance();
			slotInstHome.setId(slotInstId);
			slotInstHome.load();
			init();

			return "failed";
		}

		Set<DocInstCollection> persistedDocInstCollections = slotInstHome
				.getInstance().getDocInstCollections();

		AlfrescoFolder slotFolder = retrieveSlotFolder();

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
								item.getEmbeddedProperties(), slotFolder);
						DocInst docInst = new DocInst(instCollection, storedRef);
						instCollection.getDocInsts().add(docInst);
					} else {
						// elemento nuovo proveniente da primary doc
						System.out.println("Doc " + item.getFileName()
								+ " nuovo da primary docs");
						String storedRef = copyDocumentOnAlfresco(
								item.getDocument(), instCollection,
								item.getEmbeddedProperties(), slotFolder);
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

		if (this.warning) {
			warning = false;
			//
			init();
			//
			return "warning";
		}

		return "updated";
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
		FileContainer container = buildFileContainer(docDefCollection, item,
				true);
		//
		this.activeFileContainer = container;
		//
	}

	public void addActiveItemToDatas() {
		if (!datas.get(this.activeCollectionId).contains(
				this.activeFileContainer)) {
			datas.get(this.activeCollectionId).add(this.activeFileContainer);
		}
	}

	public void remove(Long collectionId, FileContainer container) {
		System.out.println("---> removing " + container.getFileName() + "...");
		List<FileContainer> filesList = datas.get(collectionId);
		filesList.remove(container);
		ArrayList<VerifierMessage> messages = filesMessages.get(container
				.getId());
		if (messages != null) {
			messages.clear();
		}
	}

	private String storeOnAlfresco(UploadItem item,
			DocInstCollection instCollection,
			List<DocumentPropertyInst> embeddedProperties, Folder slotFolder) {
		String nodeRef = "";
		try {
			Session session = alfrescoUserIdentity.getSession();
			// AlfrescoFolder slotFolder = findOrCreateSlotFolder(session);
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

	private String copyDocumentOnAlfresco(AlfrescoDocument document,
			DocInstCollection instCollection,
			List<DocumentPropertyInst> embeddedProperties, Folder slotFolder) {
		String nodeRef = "";

		Session session = alfrescoUserIdentity.getSession();
		// AlfrescoFolder slotFolder = findOrCreateSlotFolder(session);

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
			List<DocumentPropertyInst> embeddedProperties) {
		Map<String, Object> aspectsProperties = new HashMap<String, Object>();
		for (DocumentPropertyInst embeddedPropertyInst : embeddedProperties) {
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
		//
		container.setEditable(false);
		//
		this.activeCollectionId = docDefCollectionId;
		datas.get(docDefCollectionId).add(container);
	}

	public void editItem(Long docDefCollectionId, FileContainer container) {
		this.activeCollectionId = docDefCollectionId;
		this.activeFileContainer = container;
	}

	private boolean verify() {
		SlotDef slotDef = this.slotDefHome.getInstance();
		Set<Rule> rules = slotDef.getRules();
		boolean passed = true;
		if (rules != null && !rules.isEmpty()) {
			for (Rule rule : rules) {
				boolean rulePassed = verifyRule(rule);
				if (!rulePassed) {
					passed = false;
				}
			}
		}
		return passed;
	}

	private boolean verifyRule(Rule rule) {
		resetFlags();
		//
		Map<VerifierParameterInst, FileContainer> processedPropertiesResolverMap = new HashMap<VerifierParameterInst, FileContainer>();
		//

		Map<String, String> encodedParametersMap = rule.getParametersMap();
		IRuleVerifier verifier = rule.getVerifier();
		List<VerifierParameterDef> inParams = verifier.getInParams();

		// mappa da passare al Verifier
		Map<String, List<VerifierParameterInst>> inInstParams = new HashMap<String, List<VerifierParameterInst>>();

		for (VerifierParameterDef verifierParameterDef : inParams) {
			String paramName = verifierParameterDef.getName();

			RuleParameterInst embeddedParameter = rule
					.getEmbeddedParametersMap().get(paramName);
			String encodedParams = encodedParametersMap.get(paramName);

			if (embeddedParameter != null) {
				List<VerifierParameterInst> paramInsts = new ArrayList<VerifierParameterInst>();
				VerifierParameterInst parameterInst = new VerifierParameterInst(
						verifierParameterDef, embeddedParameter.getValue());
				parameterInst.setFallible(false);
				paramInsts.add(parameterInst);
				inInstParams.put(paramName, paramInsts);
			} else if (encodedParams != null && !encodedParams.equals("")) {
				String[] splitted = encodedParams.split("\\|");
				String source = splitted[0];
				String field = splitted[1];
				Object sourceDef = ruleParametersResolver
						.resolveSourceDef(source);
				Object fieldDef = ruleParametersResolver.resolveFieldDef(field);
				List<VerifierParameterInst> paramInsts = retrieveValueInsts(
						rule, processedPropertiesResolverMap,
						verifierParameterDef, sourceDef, fieldDef);
				inInstParams.put(paramName, paramInsts);
			} else {
				if (!verifierParameterDef.isOptional()) {
					verifiable = false;
					if (rule.isMandatory()) {
						this.addMainMessage(new VerifierMessage(
								verifierParameterDef.getLabel()
										+ " not defined in rule!",
								VerifierMessageType.ERROR));
					} else {
						failAllowed = true;
					}
				}
			}

		}

		//
		//
		//
		//
		//
		// Comunicazione di eventuali errori o warnings
		if (verifiable) {
			boolean passed = true;
			VerifierReport report = verifier.verify(inInstParams);
			if (report.getResult().equals(VerifierResult.ERROR)) {
				passed = false;
			} else if (report.getResult().equals(VerifierResult.WARNING)) {
				warning = true;
			}

			List<VerifierParameterInst> failedParams = report.getFailedParams();
			for (VerifierParameterInst parameterInst : failedParams) {
				FileContainer fileContainer = processedPropertiesResolverMap
						.get(parameterInst);
				if (fileContainer != null) {
					String errorMessage = rule.getErrorMessage();
					if (errorMessage == null || errorMessage.equals("")) {
						errorMessage = verifier.getDefaultErrorMessage();
					}
					this.addFileMessage(fileContainer.getId(),
							new VerifierMessage(errorMessage,
									VerifierMessageType.ERROR));
					this.addMainMessage(new VerifierMessage(errorMessage,
							VerifierMessageType.ERROR));
				}
			}

			List<VerifierParameterInst> warningParams = report
					.getWarningParams();
			for (VerifierParameterInst parameterInst : warningParams) {
				FileContainer fileContainer = processedPropertiesResolverMap
						.get(parameterInst);
				String warningMessage = rule.getWarningMessage();
				if (warningMessage == null || warningMessage.equals("")) {
					warningMessage = verifier.getDefaultWarningMessage();
				}
				if (fileContainer != null) {
					this.addFileMessage(fileContainer.getId(),
							new VerifierMessage(warningMessage,
									VerifierMessageType.WARNING));
				} else {
					this.addMainMessage(new VerifierMessage(warningMessage,
							VerifierMessageType.WARNING));
				}
			}
			return passed;

		} else if (failAllowed) {
			return true;
		}

		return false;
	}

	private List<VerifierParameterInst> retrieveValueInsts(
			Rule rule,
			Map<VerifierParameterInst, FileContainer> processedPropertiesResolverMap,
			VerifierParameterDef verifierParameterDef, Object sourceDef,
			Object fieldDef) {
		List<VerifierParameterInst> paramInsts = new ArrayList<VerifierParameterInst>();
		if (sourceDef instanceof DocDefCollection) {
			DocDefCollection docDefCollection = (DocDefCollection) sourceDef;
			Property property = (Property) fieldDef;
			List<FileContainer> list = datas.get(docDefCollection.getId());
			if (list != null && !list.isEmpty()) {
				for (FileContainer fileContainer : list) {
					List<DocumentPropertyInst> embeddedProperties = fileContainer
							.getEmbeddedProperties();
					Iterator<DocumentPropertyInst> iterator = embeddedProperties
							.iterator();
					boolean found = false;
					while (iterator.hasNext() && found == false) {
						DocumentPropertyInst documentPropertyInst = iterator
								.next();
						if (documentPropertyInst.getProperty().equals(property)) {
							Object value = documentPropertyInst.getValue();
							found = true;
							if (value != null) {
								VerifierParameterInst parameterInst = new VerifierParameterInst(
										verifierParameterDef, value, true);
								paramInsts.add(parameterInst);
								//
								processedPropertiesResolverMap.put(
										parameterInst, fileContainer);
								//
							} else {
								if (!verifierParameterDef.isOptional()) {
									verifiable = false;
									if (rule.isMandatory()) {
										this.addFileMessage(
												fileContainer.getId(),
												new VerifierMessage(
														documentPropertyInst
																.getProperty()
																.getTitle()
																+ " non può essere nulla per verificare una regola di tipo "
																+ rule.getType()
																		.value(),
														VerifierMessageType.ERROR));
									} else {
										failAllowed = true;
									}
								}
							}
						}
					}
				}
			} else {
				if (!verifierParameterDef.isOptional()) {
					verifiable = false;
					if (rule.isMandatory()) {
						addCollectionMessage(
								docDefCollection.getId(),
								new VerifierMessage(
										docDefCollection.getName()
												+ " non può essere vuota per verificare una regola di tipo "
												+ rule.getType().value()
												+ " sulla proprietà "
												+ property.getTitle()
												+ " dei files che contiene",
										VerifierMessageType.ERROR));
					} else {
						failAllowed = true;
					}
				}
			}

		} else if (sourceDef instanceof SlotDef) {
			if (fieldDef instanceof PropertyDef) {
				PropertyDef propertyDef = (PropertyDef) fieldDef;
				Iterator<PropertyInst> iterator = this.getPropertyInsts()
						.iterator();
				boolean found = false;
				while (iterator.hasNext() && found == false) {
					PropertyInst propertyInst = iterator.next();
					if (propertyInst.getPropertyDef().equals(propertyDef)) {
						Object value = propertyInst.getValue();
						found = true;
						if (value != null) {
							VerifierParameterInst parameterInst = new VerifierParameterInst(
									verifierParameterDef, value, true);
							paramInsts.add(parameterInst);
						} else {
							if (!verifierParameterDef.isOptional()) {
								verifiable = false;
								if (rule.isMandatory()) {
									this.addMainMessage(new VerifierMessage(
											propertyDef.getName()
													+ " non può essere nulla per verificare una regola di tipo "
													+ rule.getType().value(),
											VerifierMessageType.ERROR));
								} else {
									failAllowed = true;
								}
							}
						}
					}
				}
			} else if (fieldDef instanceof EmbeddedProperty) {
				EmbeddedProperty embeddedProperty = (EmbeddedProperty) fieldDef;
				Object value = embeddedProperty.getValue();
				if (value != null) {
					VerifierParameterInst parameterInst = new VerifierParameterInst(
							verifierParameterDef, value, false);
					paramInsts.add(parameterInst);
				} else {
					if (!verifierParameterDef.isOptional()) {
						verifiable = false;
						if (rule.isMandatory()) {
							this.addMainMessage(new VerifierMessage(
									embeddedProperty.getName()
											+ " non può essere nulla per verificare una regola di tipo "
											+ rule.getType().value(),
									VerifierMessageType.ERROR));
						} else {
							failAllowed = true;
						}
					}
				}
			}

		}
		return paramInsts;
	}

	private void addCollectionMessage(Long collectionId, VerifierMessage message) {
		ArrayList<VerifierMessage> messages = collectionsMessages
				.get(collectionId);
		if (messages == null) {
			messages = new ArrayList<VerifierMessage>();
			collectionsMessages.put(collectionId, messages);
		}
		messages.add(message);
	}

	private void addFileMessage(String fileContainerId, VerifierMessage message) {
		ArrayList<VerifierMessage> messages = filesMessages
				.get(fileContainerId);
		if (messages == null) {
			messages = new ArrayList<VerifierMessage>();
			filesMessages.put(fileContainerId, messages);
		}
		messages.add(message);
	}

	private void addMainMessage(VerifierMessage message) {
		slotMessages.add(message);
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

	public List<EmbeddedProperty> getEmbeddedProperties() {
		return new ArrayList<EmbeddedProperty>(this.slotDefHome.getInstance()
				.getEmbeddedProperties());
	}

	// public class Couple {
	// private Object sourceDef;
	// private Object fieldDef;
	//
	// public Couple(Object sourceDef, Object fieldDef) {
	// super();
	// this.sourceDef = sourceDef;
	// this.fieldDef = fieldDef;
	// }
	//
	// public Object getSourceDef() {
	// return sourceDef;
	// }
	//
	// public void setSourceDef(Object sourceDef) {
	// this.sourceDef = sourceDef;
	// }
	//
	// public Object getFieldDef() {
	// return fieldDef;
	// }
	//
	// public void setFieldDef(Object fieldDef) {
	// this.fieldDef = fieldDef;
	// }
	// }

	public ArrayList<VerifierMessage> getSlotMessages() {
		return slotMessages;
	}

	public void setSlotMessages(ArrayList<VerifierMessage> slotMessages) {
		this.slotMessages = slotMessages;
	}

	public HashMap<String, ArrayList<VerifierMessage>> getFilesMessages() {
		return filesMessages;
	}

	public void setFilesMessages(
			HashMap<String, ArrayList<VerifierMessage>> filesMessages) {
		this.filesMessages = filesMessages;
	}

	public HashMap<Long, ArrayList<VerifierMessage>> getCollectionsMessages() {
		return collectionsMessages;
	}

	public void setCollectionsMessages(
			HashMap<Long, ArrayList<VerifierMessage>> collectionsMessages) {
		this.collectionsMessages = collectionsMessages;
	}

	public FileContainer getActiveFileContainer() {
		return activeFileContainer;
	}

	public void setActiveFileContainer(FileContainer activeFileContainer) {
		this.activeFileContainer = activeFileContainer;
	}

}
