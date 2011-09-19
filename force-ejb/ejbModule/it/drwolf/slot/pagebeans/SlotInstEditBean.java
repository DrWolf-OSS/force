package it.drwolf.slot.pagebeans;

import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.alfresco.AlfrescoWrapper;
import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.alfresco.custom.support.DocumentPropertyInst;
import it.drwolf.slot.alfresco.webscripts.AlfrescoWebScriptClient;
import it.drwolf.slot.application.CustomModelController;
import it.drwolf.slot.digsig.CertsController;
import it.drwolf.slot.digsig.Signature;
import it.drwolf.slot.digsig.Utils;
import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.entity.DocInst;
import it.drwolf.slot.entity.DocInstCollection;
import it.drwolf.slot.entity.EmbeddedProperty;
import it.drwolf.slot.entity.PropertyDef;
import it.drwolf.slot.entity.PropertyInst;
import it.drwolf.slot.entity.Rule;
import it.drwolf.slot.entity.RuleParameterInst;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.entity.SlotInst;
import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.exceptions.WrongDataTypeException;
import it.drwolf.slot.interfaces.DataDefinition;
import it.drwolf.slot.interfaces.DataInstance;
import it.drwolf.slot.interfaces.IRuleVerifier;
import it.drwolf.slot.pagebeans.support.FileContainer;
import it.drwolf.slot.pagebeans.support.ValueChangeListener;
import it.drwolf.slot.prefs.PreferenceKey;
import it.drwolf.slot.prefs.Preferences;
import it.drwolf.slot.ruleverifier.ParameterCoordinates;
import it.drwolf.slot.ruleverifier.RuleParametersResolver;
import it.drwolf.slot.ruleverifier.VerifierMessage;
import it.drwolf.slot.ruleverifier.VerifierMessageType;
import it.drwolf.slot.ruleverifier.VerifierParameterDef;
import it.drwolf.slot.ruleverifier.VerifierParameterInst;
import it.drwolf.slot.ruleverifier.VerifierReport;
import it.drwolf.slot.ruleverifier.VerifierResult;
import it.drwolf.slot.session.SlotDefHome;
import it.drwolf.slot.session.SlotInstHome;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.Principal;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSTypedStream;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.provider.X509CertificateObject;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
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

	@In(create = true)
	private ValueChangeListener valueChangeListener;

	@In(create = true)
	private CertsController certsController;

	private static final int LENGHT_LIMIT = 150;
	private static final String SPACER = " ";

	public void addActiveItemToDatas() {
		if (!this.datas.get(this.activeCollectionId).contains(
				this.activeFileContainer)) {
			try {
				DocInstCollection instCollection = this
						.findInstCollection(this.activeCollectionId);
				AlfrescoFolder slotFolder = this.retrieveSlotFolder();
				this.storeOnAlfresco(this.activeFileContainer, instCollection,
						slotFolder);
				this.datas.get(this.activeCollectionId).add(
						this.activeFileContainer);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				FacesMessages
						.instance()
						.add(Severity.ERROR,
								"Errore interno del sistema di salvataggio dei documenti!");
				e.printStackTrace();
			}
		}
	}

	private void addCollectionMessage(Long collectionId, VerifierMessage message) {
		ArrayList<VerifierMessage> messages = this.collectionsMessages
				.get(collectionId);
		if (messages == null) {
			messages = new ArrayList<VerifierMessage>();
			this.collectionsMessages.put(collectionId, messages);
		}
		messages.add(message);
	}

	private void addCollectionsNewDocumentsToSlot(AlfrescoFolder slotFolder,
			DocInstCollection instCollection) {
		// Aggiungo elementi nuovi
		List<FileContainer> containers = this.datas.get(instCollection
				.getDocDefCollection().getId());
		if (containers != null) {
			for (FileContainer container : containers) {
				if (container.getDocument().hasAspect("P:util:tmp")) {
					//
					this.updateProperties(container.getDocument(),
							container.getDocumentProperties());
					//
					container.getDocument().removeAspect("P:util:tmp");
					DocInst docInst = new DocInst(instCollection, container
							.getDocument().getId());
					instCollection.getDocInsts().add(docInst);
				} else {
					System.out.println("si deve copiare "
							+ container.getDocument().getName());
					String storedRef = this.copyDocumentOnAlfresco(
							container.getDocument(), instCollection,
							container.getDocumentProperties(), slotFolder);
					DocInst docInst = new DocInst(instCollection, storedRef);
					instCollection.getDocInsts().add(docInst);
				}
			}
		}
	}

	public void addDocFromPrimary(Long docDefCollectionId,
			FileContainer container) {
		//
		container.setEditable(false);
		//
		this.activeCollectionId = docDefCollectionId;
		this.datas.get(docDefCollectionId).add(container);
	}

	private void addFileMessage(String fileContainerId, VerifierMessage message) {
		ArrayList<VerifierMessage> messages = this.filesMessages
				.get(fileContainerId);
		if (messages == null) {
			messages = new ArrayList<VerifierMessage>();
			this.filesMessages.put(fileContainerId, messages);
		}
		messages.add(message);
	}

	private void addMainMessage(VerifierMessage message) {
		this.slotMessages.add(message);
	}

	private void addSignature(AlfrescoDocument document,
			X509Certificate x509Certificate, X509CertificateObject validCert) {
		try {
			Principal subjectDN = x509Certificate.getSubjectDN();

			String mysign = Utils.getCN(subjectDN.toString());
			String cf = Utils.getCF(subjectDN.toString());
			Date notAfter = x509Certificate.getNotAfter();
			String authority = Utils.getCN(validCert.getIssuerDN().toString());
			Boolean validity = Boolean.TRUE;

			String username = this.alfrescoUserIdentity.getUsername();
			String password = this.alfrescoUserIdentity.getPassword();
			String url = this.alfrescoUserIdentity.getUrl();
			AlfrescoWebScriptClient webScriptClient = new AlfrescoWebScriptClient(
					username, password, url);

			document.addAspect(Signature.ASPECT_SIGNED);

			String signatureNodeRef = webScriptClient.addSignature(
					document.getId(),
					"sign_" + Utils.md5Encode(x509Certificate.getSignature()));

			Session session = this.alfrescoUserIdentity.getSession();
			AlfrescoDocument signatureDoc = (AlfrescoDocument) session
					.getObject(signatureNodeRef);

			Map<String, Object> props = new HashMap<String, Object>();
			props.put(Signature.VALIDITY, validity);
			props.put(Signature.EXPIRY, Utils.dateToCalendar(notAfter));
			props.put(Signature.AUTHORITY, authority);
			props.put(Signature.SIGN, mysign);
			props.put(Signature.CF, cf);

			signatureDoc.updateProperties(props);

			System.out.println("-> Signature added to " + document.getName());

		} catch (Exception e) {
			System.out
					.println("---> Errore nell aggiungere una firma al documento "
							+ document.getName());
			e.printStackTrace();
		}
	}

	private boolean checkCollectionsSize() {
		boolean passed = true;

		for (DocDefCollection defCollection : this.slotDefHome.getInstance()
				.getDocDefCollections()) {
			if ((defCollection.getConditionalPropertyDef() == null)
					|| ((defCollection.getConditionalPropertyDef() != null)
							&& (this.findPropertyInstByDefId(
									defCollection.getConditionalPropertyDef()
											.getId()).getValue() != null) && this
							.findPropertyInstByDefId(
									defCollection.getConditionalPropertyDef()
											.getId())
							.getValue()
							.equals(defCollection.getConditionalPropertyInst()
									.getValue()))) {

				List<FileContainer> list = this.datas
						.get(defCollection.getId());
				int size = list.size();
				if ((defCollection.getMin() != null)
						&& (size < defCollection.getMin())) {
					passed = false;
					this.addCollectionMessage(defCollection.getId(),
							new VerifierMessage(
									"Questa collection deve contenere almeno "
											+ defCollection.getMin()
											+ " documento/i",
									VerifierMessageType.ERROR));
				}
				if ((defCollection.getMax() != null)
						&& (size > defCollection.getMax())) {
					passed = false;
					this.addCollectionMessage(defCollection.getId(),
							new VerifierMessage(
									"Questa collection deve contenere al massimo "
											+ defCollection.getMax()
											+ " documento/i",
									VerifierMessageType.ERROR));
				}
			}
		}
		return passed;
	}

	private void cleanCollection(Long docDefCollectionId) {
		List<FileContainer> itemslist = this.datas.get(docDefCollectionId);
		if (itemslist != null) {
			itemslist.clear();
		} else {
			itemslist = new ArrayList<FileContainer>();
		}
	}

	public void cleanConditionalCollection(PropertyInst propertyInst) {

		Object value = this.valueChangeListener.getValue();
		if ((value == null)
				&& !propertyInst.getPropertyDef().getDataType()
						.equals(DataType.BOOLEAN)) {
			propertyInst.clean();
		}

		Iterator<DocInstCollection> iterator = this.docInstCollections
				.iterator();
		while (iterator.hasNext()) {
			DocInstCollection instCollection = iterator.next();
			if ((instCollection.getDocDefCollection()
					.getConditionalPropertyDef() != null)
					&& instCollection.getDocDefCollection()
							.getConditionalPropertyDef().getId()
							.equals(propertyInst.getPropertyDef().getId())) {
				Object conditionalValue = instCollection.getDocDefCollection()
						.getConditionalPropertyInst().getValue();
				// TODO: riguardare questo caso!!!
				// Se alla proprietà che condiziona la collection è stato
				// cambiato DataType e non è stata aggiornata la definizione
				// della collection che la referenzia aggiornando il valore
				// della
				// property, "ConditionalPropertyInst().getValue()" restituirà
				// null. In questo caso cancello cmq il contenuto della
				// collection perchè i valori non matchano
				if ((conditionalValue == null)
						|| !conditionalValue.equals(propertyInst.getValue())) {
					this.cleanCollection(instCollection.getDocDefCollection()
							.getId());
				}
			}
		}

	}

	private void cleanMessages() {
		Set<String> filesKeys = this.filesMessages.keySet();
		for (String key : filesKeys) {
			ArrayList<VerifierMessage> messages = this.filesMessages.get(key);
			if (messages != null) {
				messages.clear();
			}
		}

		Set<Long> collectionsKeys = this.collectionsMessages.keySet();
		for (Long key : collectionsKeys) {
			ArrayList<VerifierMessage> messages = this.collectionsMessages
					.get(key);
			if (messages != null) {
				messages.clear();
			}
		}

		if (this.slotMessages != null) {
			this.slotMessages.clear();
		}
	}

	private String copyDocumentOnAlfresco(AlfrescoDocument document,
			DocInstCollection instCollection,
			List<DocumentPropertyInst> documentProperties, Folder slotFolder) {
		String nodeRef = "";

		Session session = this.alfrescoUserIdentity.getSession();
		// AlfrescoFolder slotFolder = findOrCreateSlotFolder(session);

		ContentStream contentStream = document.getContentStream();
		Set<String> aspects = instCollection.getDocDefCollection().getDocDef()
				.getAspectIds();
		// Set<Property> properties =
		// customModelController.getProperties(aspects);

		Map<String, Object> props = new HashMap<String, Object>();
		props.put(PropertyIds.NAME, FileContainer.encodeFilename(FileContainer
				.decodeFilename(document.getName())));
		props.put(PropertyIds.OBJECT_TYPE_ID, BaseTypeId.CMIS_DOCUMENT.value());

		ObjectId objectId = session.createDocument(props, slotFolder,
				contentStream, VersioningState.NONE, null, null, null);
		System.out.println(objectId);

		AlfrescoDocument documentCopy = (AlfrescoDocument) session
				.getObject(objectId);

		// prima si aggiungono gli aspect
		for (String aspect : aspects) {
			documentCopy.addAspect(aspect);
		}

		// poi si aggiungono i valori delle relative properties
		this.updateProperties(documentCopy, documentProperties);
		this.verifySignature(documentCopy);
		nodeRef = objectId.getId();

		return nodeRef;
	}

	public void editItem(Long docDefCollectionId, FileContainer container) {
		this.activeCollectionId = docDefCollectionId;
		this.activeFileContainer = container;
	}

	private void extractAndEmbedContent(AlfrescoDocument document,
			CMSSignedData cms) {

		try {
			CMSProcessable signedContent = cms.getSignedContent();
			final byte[] content = (byte[]) signedContent.getContent();

			ByteArrayInputStream baip = new ByteArrayInputStream(content);

			String contentFilename = FileContainer
					.retrieveContentFilename(document.getName());
			String encodedContentFilename = FileContainer
					.encodeFilename(contentFilename);
			//
			String mimetype = "application/octet-stream";
			ContentStreamImpl contentStreamImpl = new ContentStreamImpl(
					encodedContentFilename,
					new BigInteger("" + content.length), mimetype, baip);

			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(PropertyIds.NAME, encodedContentFilename);
			properties.put(PropertyIds.OBJECT_TYPE_ID,
					BaseTypeId.CMIS_DOCUMENT.value());

			List<Folder> parents = document.getParents();
			Folder folder = parents.get(0);

			ObjectId objectId = this.alfrescoUserIdentity.getSession()
					.createDocument(properties, folder, contentStreamImpl,
							VersioningState.NONE, null, null, null);
			AlfrescoDocument contentDoc = (AlfrescoDocument) this.alfrescoUserIdentity
					.getSession().getObject(objectId.getId());
			contentDoc.addAspect("P:util:tmp");

			//
			String username = this.alfrescoUserIdentity.getUsername();
			String password = this.alfrescoUserIdentity.getPassword();
			String url = this.alfrescoUserIdentity.getUrl();
			AlfrescoWebScriptClient webScriptClient = new AlfrescoWebScriptClient(
					username, password, url);
			webScriptClient.embedContentToSignedDoc(document.getId(),
					contentDoc.getId(),
					FileContainer.retrieveContentFilename(document.getName()));
			//
			contentDoc.deleteAllVersions();
			//
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private DocInstCollection findInstCollection(Long defCollectionId) {
		DocInstCollection instCollection = null;
		// controllo se c'è una collection che corrisponde a quell'id (ed è
		// la destinataria dei files uploadati)
		// boolean found = false;
		Iterator<DocInstCollection> instCollIterator = this.docInstCollections
				.iterator();
		while (instCollIterator.hasNext()) {
			instCollection = instCollIterator.next();
			if (instCollection.getDocDefCollection().getId()
					.equals(defCollectionId)) {
				// found = true;
				return instCollection;
			}
		}
		return null;
	}

	public PropertyInst findPropertyInstByDefId(Long propertyDefId) {
		Iterator<PropertyInst> iterator = this.propertyInsts.iterator();
		while (iterator.hasNext()) {
			PropertyInst propertyInst = iterator.next();
			if (propertyInst.getPropertyDef().getId().equals(propertyDefId)) {
				return propertyInst;
			}
		}
		return null;
	}

	public Long getActiveCollectionId() {
		return this.activeCollectionId;
	}

	public FileContainer getActiveFileContainer() {
		return this.activeFileContainer;
	}

	public HashMap<Long, ArrayList<VerifierMessage>> getCollectionsMessages() {
		return this.collectionsMessages;
	}

	public HashMap<Long, List<FileContainer>> getDatas() {
		return this.datas;
	}

	public List<DocInstCollection> getDocInstCollections() {
		return this.docInstCollections;
	}

	public List<EmbeddedProperty> getEmbeddedProperties() {
		return new ArrayList<EmbeddedProperty>(this.slotDefHome.getInstance()
				.getEmbeddedProperties());
	}

	public HashMap<String, ArrayList<VerifierMessage>> getFilesMessages() {
		return this.filesMessages;
	}

	// quando trovo l'element lo tolgo così lascio solo quelli del tutto nuovi
	// su cui ciclare alla fine ed aggiungerli nell'update
	private FileContainer getItemIfContained(List<FileContainer> filesList,
			String docRef) {
		if (filesList != null) {
			Iterator<FileContainer> iterator = filesList.iterator();
			while (iterator.hasNext()) {
				FileContainer item = iterator.next();
				if ((item.getDocument() != null)
						&& item.getDocument().getId().equals(docRef)) {
					iterator.remove();
					return item;
				}
			}
		}
		return null;
	}

	public HashMap<Long, List<FileContainer>> getPrimaryDocs() {
		return this.primaryDocs;
	}

	public List<PropertyInst> getPropertyInsts() {
		return this.propertyInsts;
	}

	public ArrayList<VerifierMessage> getSlotMessages() {
		return this.slotMessages;
	}

	@Create
	public void init() {
		if (this.slotInstHome.getId() == null) {
			this.propertyInsts = new ArrayList<PropertyInst>();
			for (PropertyDef propertyDef : this.slotDefHome.getInstance()
					.getPropertyDefs()) {
				PropertyInst propertyInst = new PropertyInst(propertyDef,
						this.slotInstHome.getInstance());
				this.propertyInsts.add(propertyInst);
			}
			// /

			this.docInstCollections = new ArrayList<DocInstCollection>();
			for (DocDefCollection defCollection : this.slotDefHome
					.getInstance().getDocDefCollections()) {
				DocInstCollection instCollection = new DocInstCollection(
						this.slotInstHome.getInstance(), defCollection);
				this.docInstCollections.add(instCollection);

				//
				this.datas.put(defCollection.getId(),
						new ArrayList<FileContainer>());
				List<AlfrescoDocument> collPrimaryDocs = this
						.retrievePrimaryDocs(defCollection.getDocDef().getId());
				List<FileContainer> fileContainers = new ArrayList<FileContainer>();
				Set<String> aspectIds = defCollection.getDocDef()
						.getAspectIds();
				Set<Property> properties = this.customModelController
						.getProperties(aspectIds);
				for (AlfrescoDocument document : collPrimaryDocs) {
					FileContainer fileContainer = new FileContainer(document,
							properties, false);
					// FileContainer fileContainer = buildFileContainer(
					// properties, document, false);
					fileContainers.add(fileContainer);
				}
				this.primaryDocs.put(defCollection.getId(), fileContainers);
			}

		} else {
			this.propertyInsts = new ArrayList<PropertyInst>(this.slotInstHome
					.getInstance().getPropertyInsts());

			this.docInstCollections = new ArrayList<DocInstCollection>(
					this.slotInstHome.getInstance().getDocInstCollections());
			for (DocInstCollection instCollection : this.docInstCollections) {
				DocDefCollection docDefCollection = instCollection
						.getDocDefCollection();
				this.datas.put(docDefCollection.getId(),
						new ArrayList<FileContainer>());
				Set<DocInst> docInsts = instCollection.getDocInsts();
				Set<String> aspectIds = docDefCollection.getDocDef()
						.getAspectIds();
				Set<Property> properties = this.customModelController
						.getProperties(aspectIds);
				for (DocInst docInst : docInsts) {
					try {
						String nodeRef = docInst.getNodeRef();
						AlfrescoDocument document = (AlfrescoDocument) this.alfrescoUserIdentity
								.getSession().getObject(nodeRef);
						// FileContainer container = buildFileContainer(
						// properties, document, true);
						FileContainer container = new FileContainer(document,
								properties, true);

						this.datas.get(docDefCollection.getId()).add(container);
					} catch (CmisObjectNotFoundException e) {
						FacesMessages.instance().add(
								"Missing files on Alfresco");
						e.printStackTrace();
					}
				}
			}

			for (DocDefCollection defCollection : this.slotDefHome
					.getInstance().getDocDefCollections()) {
				List<AlfrescoDocument> collPrimaryDocs = this
						.retrievePrimaryDocs(defCollection.getDocDef().getId());
				Set<String> aspectIds = defCollection.getDocDef()
						.getAspectIds();
				Set<Property> properties = this.customModelController
						.getProperties(aspectIds);
				List<FileContainer> fileContainers = new ArrayList<FileContainer>();
				for (AlfrescoDocument document : collPrimaryDocs) {
					FileContainer container = new FileContainer(document,
							properties, false);
					fileContainers.add(container);
				}
				this.primaryDocs.put(defCollection.getId(), fileContainers);
			}

			this.verify();
		}

	}

	public void listener(UploadEvent event) {
		UploadItem item = event.getUploadItem();

		String fileName = item.getFileName();
		Long docDefCollectionId = this.activeCollectionId;
		List<FileContainer> list = this.datas.get(docDefCollectionId);
		if (list == null) {
			list = new ArrayList<FileContainer>();
			this.datas.put(docDefCollectionId, list);
		}
		System.out.println("-> " + fileName + " successfully uploaded");
		DocDefCollection docDefCollection = this.entityManager.find(
				DocDefCollection.class, docDefCollectionId);
		Set<String> aspectIds = docDefCollection.getDocDef().getAspectIds();
		Set<Property> properties = this.customModelController
				.getProperties(aspectIds);
		FileContainer container = new FileContainer(item, properties, true);
		//
		this.activeFileContainer = container;
		//
	}

	public void remove(Long collectionId, FileContainer container) {
		System.out.println("---> removing " + container.getFileName() + "...");
		List<FileContainer> filesList = this.datas.get(collectionId);
		filesList.remove(container);
		ArrayList<VerifierMessage> messages = this.filesMessages.get(container
				.getId());
		if (messages != null) {
			messages.clear();
		}
	}

	private void resetFlags() {
		this.verifiable = true;
		this.failAllowed = false;
		// warning = false;
	}

	@SuppressWarnings("unchecked")
	private List<AlfrescoDocument> retrievePrimaryDocs(Long docDefId) {
		List<AlfrescoDocument> primaryDocs = new ArrayList<AlfrescoDocument>();
		List<DocInst> resultList = this.entityManager
				.createQuery(
						"from DocInst d where d.docInstCollection.slotInst.slotDef.type = 'Primary' and d.docInstCollection.slotInst.ownerId=:ownerId and d.docInstCollection.docDefCollection.docDef.id=:docDefId")
				.setParameter(
						"ownerId",
						this.alfrescoUserIdentity.getActiveGroup()
								.getShortName())
				.setParameter("docDefId", docDefId).getResultList();
		if (resultList != null) {
			for (DocInst docInst : resultList) {
				String nodeRef = docInst.getNodeRef();
				AlfrescoDocument document = (AlfrescoDocument) this.alfrescoUserIdentity
						.getSession().getObject(nodeRef);
				primaryDocs.add(document);
			}
		}
		return primaryDocs;
	}

	// private AlfrescoFolder retrieveSlotFolder() {
	// AlfrescoFolder groupFolder = this.alfrescoWrapper.findOrCreateFolder(
	// this.preferences.getValue(PreferenceKey.FORCE_GROUPS_PATH
	// .name()), this.alfrescoUserIdentity.getActiveGroup()
	// .getShortName());
	//
	// AlfrescoFolder slotFolder = this.alfrescoWrapper.findOrCreateFolder(
	// groupFolder, this.slotInstHome.getInstance().getSlotDef()
	// .getName());
	// return slotFolder;
	// }

	public AlfrescoFolder retrieveSlotFolder() {
		if ((this.slotInstHome.getInstance() != null)
				&& (this.slotInstHome.getInstance().getNodeRef() != null)
				&& !this.slotInstHome.getInstance().getNodeRef().equals("")) {
			return (AlfrescoFolder) this.alfrescoUserIdentity.getSession()
					.getObject(this.slotInstHome.getInstance().getNodeRef());
		}

		AlfrescoFolder groupFolder = this.alfrescoWrapper.findOrCreateFolder(
				this.preferences.getValue(PreferenceKey.FORCE_GROUPS_PATH
						.name()), this.alfrescoUserIdentity.getActiveGroup()
						.getShortName());

		AlfrescoFolder slotFolder = this.alfrescoWrapper.findOrCreateFolder(
				groupFolder,
				this.slotDefHome.getInstance().getId()
						+ " "
						+ AlfrescoWrapper.normalizeFolderName(this.slotDefHome
								.getInstance().getName(),
								SlotInstEditBean.LENGHT_LIMIT,
								SlotInstEditBean.SPACER));
		this.slotInstHome.getInstance().setNodeRef(slotFolder.getId());
		return slotFolder;
	}

	private List<VerifierParameterInst> retrieveValueInsts(
			Rule rule,
			Map<VerifierParameterInst, FileContainer> processedPropertiesResolverMap,
			VerifierParameterDef verifierParameterDef, Object sourceDef,
			Object fieldDef) {
		ParameterCoordinates parameterCoordinates = new ParameterCoordinates(
				sourceDef, (DataDefinition) fieldDef);

		List<VerifierParameterInst> paramInsts = new ArrayList<VerifierParameterInst>();
		if (sourceDef instanceof DocDefCollection) {
			DocDefCollection docDefCollection = (DocDefCollection) sourceDef;
			Property property = (Property) fieldDef;
			List<FileContainer> list = this.datas.get(docDefCollection.getId());
			if ((list != null) && !list.isEmpty()) {
				for (FileContainer fileContainer : list) {
					//
					List<DocumentPropertyInst> allProperties = fileContainer
							.getDocumentProperties();
					//
					Iterator<DocumentPropertyInst> iterator = allProperties
							.iterator();
					boolean found = false;
					while (iterator.hasNext() && (found == false)) {
						DocumentPropertyInst documentPropertyInst = iterator
								.next();
						if (documentPropertyInst.getProperty().equals(property)) {
							Object value = documentPropertyInst.getValue();
							found = true;
							if ((value != null) && !value.equals("")) {
								VerifierParameterInst parameterInst = new VerifierParameterInst(
										verifierParameterDef, value, true);
								paramInsts.add(parameterInst);
								//
								parameterInst
										.setParameterCoordinates(parameterCoordinates);
								//
								processedPropertiesResolverMap.put(
										parameterInst, fileContainer);
								//
							} else {
								if (!verifierParameterDef.isOptional()) {
									this.verifiable = false;
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
										this.failAllowed = true;
									}
								}
							}
						}
					}
				}
			} else {
				if (!verifierParameterDef.isOptional()) {
					this.verifiable = false;
					if (rule.isMandatory()) {
						this.addCollectionMessage(
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
						this.failAllowed = true;
					}
				}
			}

		} else if (sourceDef instanceof SlotDef) {
			if (fieldDef instanceof PropertyDef) {
				PropertyDef propertyDef = (PropertyDef) fieldDef;
				Iterator<PropertyInst> iterator = this.getPropertyInsts()
						.iterator();
				boolean found = false;
				while (iterator.hasNext() && (found == false)) {
					PropertyInst propertyInst = iterator.next();
					if (propertyInst.getPropertyDef().equals(propertyDef)) {
						Object value = propertyInst.getValue();
						found = true;
						if ((value != null) && !value.equals("")) {
							VerifierParameterInst parameterInst = new VerifierParameterInst(
									verifierParameterDef, value, true);
							//
							parameterInst
									.setParameterCoordinates(parameterCoordinates);
							//
							paramInsts.add(parameterInst);
						} else {
							if (!verifierParameterDef.isOptional()) {
								this.verifiable = false;
								if (rule.isMandatory()) {
									this.addMainMessage(new VerifierMessage(
											propertyDef.getName()
													+ " non può essere nulla per verificare una regola di tipo "
													+ rule.getType().value(),
											VerifierMessageType.ERROR));
								} else {
									this.failAllowed = true;
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
					//
					parameterInst.setParameterCoordinates(parameterCoordinates);
					//
					paramInsts.add(parameterInst);
				} else {
					if (!verifierParameterDef.isOptional()) {
						this.verifiable = false;
						if (rule.isMandatory()) {
							this.addMainMessage(new VerifierMessage(
									embeddedProperty.getName()
											+ " non può essere nulla per verificare una regola di tipo "
											+ rule.getType().value(),
									VerifierMessageType.ERROR));
						} else {
							this.failAllowed = true;
						}
					}
				}
			}

		}
		return paramInsts;
	}

	public String save() {
		this.cleanMessages();
		boolean sizeCollectionPassed = this.checkCollectionsSize();
		if (!sizeCollectionPassed) {
			FacesMessages
					.instance()
					.add(Severity.ERROR,
							"Le dimensioni di alcune collection non rispettano le specifiche");
			return "failed";
		}

		boolean rulesPassed = this.verify();
		if (!rulesPassed) {
			FacesMessages.instance().add(Severity.ERROR,
					"Alcune regole non sono verificate!");
			return "failed";
		}

		this.slotInstHome.getInstance().setPropertyInsts(
				new HashSet<PropertyInst>(this.propertyInsts));

		AlfrescoFolder slotFolder = this.retrieveSlotFolder();

		Set<Long> keySet = this.datas.keySet();
		for (Long key : keySet) {
			DocInstCollection instCollection = this.findInstCollection(key);
			if (instCollection != null) {
				try {

					this.addCollectionsNewDocumentsToSlot(slotFolder,
							instCollection);

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

		this.slotInstHome.getInstance().setDocInstCollections(
				new HashSet<DocInstCollection>(this.docInstCollections));
		this.slotInstHome.getInstance().setOwnerId(
				this.alfrescoUserIdentity.getActiveGroup().getShortName());
		this.slotInstHome.persist();
		FacesMessages.instance().add(
				"Slot " + this.slotDefHome.getInstance().getName()
						+ " successfully created");

		if (this.warning) {
			this.warning = false;
			return "warning";
		}

		return "saved";
	}

	public void setActiveCollectionId(Long activeCollection) {
		this.activeCollectionId = activeCollection;
	}

	public void setActiveFileContainer(FileContainer activeFileContainer) {
		this.activeFileContainer = activeFileContainer;
	}

	public void setCollectionsMessages(
			HashMap<Long, ArrayList<VerifierMessage>> collectionsMessages) {
		this.collectionsMessages = collectionsMessages;
	}

	public void setDatas(HashMap<Long, List<FileContainer>> datas) {
		this.datas = datas;
	}

	public void setDocInstCollections(List<DocInstCollection> docInstCollections) {
		this.docInstCollections = docInstCollections;
	}

	public void setFilesMessages(
			HashMap<String, ArrayList<VerifierMessage>> filesMessages) {
		this.filesMessages = filesMessages;
	}

	public void setPrimaryDocs(HashMap<Long, List<FileContainer>> primaryDocs) {
		this.primaryDocs = primaryDocs;
	}

	public void setPropertyInsts(List<PropertyInst> propertyInsts) {
		this.propertyInsts = propertyInsts;
	}

	public void setSlotMessages(ArrayList<VerifierMessage> slotMessages) {
		this.slotMessages = slotMessages;
	}

	private AlfrescoDocument storeOnAlfresco(FileContainer fileContainer,
			DocInstCollection instCollection, Folder slotFolder)
			throws Exception {
		UploadItem item = fileContainer.getUploadItem();
		Session session = this.alfrescoUserIdentity.getSession();

		String fileName = item.getFileName();

		// Metto un mimetype generico, ci pensa Alfresco a mettere il mimetype
		// giusto tramite l'esecuzione di uno script
		String mimetype = "application/octet-stream";

		ContentStreamImpl contentStreamImpl = new ContentStreamImpl(fileName,
				new BigInteger("" + item.getFile().length()), mimetype,
				new FileInputStream(item.getFile()));

		Set<String> aspectIds = instCollection.getDocDefCollection()
				.getDocDef().getAspectIds();

		Map<String, Object> properties = new HashMap<String, Object>();
		properties
				.put(PropertyIds.NAME, FileContainer.encodeFilename(fileName));
		properties.put(PropertyIds.OBJECT_TYPE_ID,
				BaseTypeId.CMIS_DOCUMENT.value());

		ObjectId objectId = session.createDocument(properties, slotFolder,
				contentStreamImpl, VersioningState.NONE, null, null, null);
		// System.out.println(objectId);

		AlfrescoDocument document = (AlfrescoDocument) session
				.getObject(objectId);
		//
		document.addAspect("P:util:tmp");

		// prima si aggiungono gli aspect
		for (String aspect : aspectIds) {
			document.addAspect(aspect);
		}

		// poi si aggiungono i valori delle relative properties
		this.updateProperties(document, fileContainer.getDocumentProperties());

		this.verifySignature(document);
		fileContainer.setDocument(document);

		return document;
	}

	public String update() {
		this.cleanMessages();
		SlotInst instance = this.slotInstHome.getInstance();

		boolean sizeCollectionPassed = this.checkCollectionsSize();
		if (!sizeCollectionPassed) {
			FacesMessages
					.instance()
					.add(Severity.ERROR,
							"Le dimensioni di alcune collection non rispettano le specifiche");
			return "failed";
		}

		boolean rulesPassed = this.verify();
		if (!rulesPassed) {
			FacesMessages.instance().add(Severity.ERROR,
					"Alcune regole non sono verificate!");
			this.entityManager.clear();
			return "failed";
		}

		this.entityManager.merge(instance);
		Set<DocInstCollection> persistedDocInstCollections = instance
				.getDocInstCollections();

		AlfrescoFolder slotFolder = this.retrieveSlotFolder();

		for (DocInstCollection instCollection : persistedDocInstCollections) {
			// entityManager.merge(instCollection);
			Set<DocInst> docInsts = instCollection.getDocInsts();
			Iterator<DocInst> iterator = docInsts.iterator();
			while (iterator.hasNext()) {
				DocInst docInst = iterator.next();
				AlfrescoDocument document = (AlfrescoDocument) this.alfrescoUserIdentity
						.getSession().getObject(docInst.getNodeRef());
				Long docDefCollectionId = instCollection.getDocDefCollection()
						.getId();
				List<FileContainer> filesList = this.datas
						.get(docDefCollectionId);

				// Controllo se il document è ancora presente nella lista dei
				// file associati, se c'è ancora ne aggiorno le properties
				// altrimenti lo cancello da alfresco e lo tolgo dalla relativa
				// collection
				FileContainer itemContained = this.getItemIfContained(
						filesList, document.getId());
				if (itemContained == null) {
					document.deleteAllVersions();
					iterator.remove();
					Long id = docInst.getId();
					DocInst foundDocInst = this.entityManager.find(
							DocInst.class, id);
					this.entityManager.remove(foundDocInst);
				} else {
					// il file è quello vecchio ma potrebbe necessitare di
					// un aggiornamento delle properties
					System.out.println("Doc " + document.getName()
							+ " da aggiornare");
					this.updateProperties(document,
							itemContained.getDocumentProperties());
				}
			}

			this.addCollectionsNewDocumentsToSlot(slotFolder, instCollection);
		}

		this.entityManager.merge(instance);
		this.entityManager.flush();

		FacesMessages.instance().add(
				"Slot " + this.slotDefHome.getInstance().getName()
						+ " successfully updated");

		if (this.warning) {
			this.warning = false;
			return "warning";
		}

		return "updated";
	}

	private void updateProperties(AlfrescoDocument document,
			List<DocumentPropertyInst> documentProperties) {
		Map<String, Object> aspectsProperties = new HashMap<String, Object>();
		for (DocumentPropertyInst documentPropertyInst : documentProperties) {
			if (documentPropertyInst.getValue() != null) {
				aspectsProperties.put(documentPropertyInst.getProperty()
						.getName(), documentPropertyInst.getValue());
			}
		}
		document.updateProperties(aspectsProperties);
	}

	private boolean verify() {
		SlotDef slotDef = this.slotDefHome.getInstance();
		Set<Rule> rules = slotDef.getRules();
		boolean passed = true;
		if ((rules != null) && !rules.isEmpty()) {
			for (Rule rule : rules) {
				boolean rulePassed = this.verifyRule(rule);
				if (!rulePassed) {
					passed = false;
				}
			}
		}
		return passed;
	}

	private boolean verifyRule(Rule rule) {
		this.resetFlags();
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
			} else if ((encodedParams != null) && !encodedParams.equals("")) {
				String[] splitted = encodedParams.split("\\|");
				String source = splitted[0];
				String field = splitted[1];
				Object sourceDef = this.ruleParametersResolver
						.resolveSourceDef(source);
				Object fieldDef = this.ruleParametersResolver
						.resolveFieldDef(field);
				List<VerifierParameterInst> paramInsts = this
						.retrieveValueInsts(rule,
								processedPropertiesResolverMap,
								verifierParameterDef, sourceDef, fieldDef);
				inInstParams.put(paramName, paramInsts);
			} else {
				if (!verifierParameterDef.isOptional()) {
					this.verifiable = false;
					if (rule.isMandatory()) {
						this.addMainMessage(new VerifierMessage(
								verifierParameterDef.getLabel()
										+ " not defined in rule!",
								VerifierMessageType.ERROR));
					} else {
						this.failAllowed = true;
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
		if (this.verifiable) {
			try {
				boolean passed = true;
				VerifierReport report = verifier.verify(inInstParams);
				if (report.getResult().equals(VerifierResult.ERROR)) {
					passed = false;
				} else if (report.getResult().equals(VerifierResult.WARNING)) {
					this.warning = true;
				}

				List<VerifierParameterInst> failedParams = report
						.getFailedParams();
				for (VerifierParameterInst parameterInst : failedParams) {
					FileContainer fileContainer = processedPropertiesResolverMap
							.get(parameterInst);

					String prefix = "La proprietà "
							+ parameterInst.getParameterCoordinates()
									.getFieldDef().getLabel()
							+ " ha un valore errato. ";
					String errorMessage = rule.getErrorMessage();
					if (fileContainer != null) {
						if ((errorMessage == null) || errorMessage.equals("")) {
							errorMessage = prefix.concat(verifier
									.getDefaultErrorMessage());
						}
						this.addFileMessage(fileContainer.getId(),
								new VerifierMessage(errorMessage,
										VerifierMessageType.ERROR));
					} else {
						this.addMainMessage(new VerifierMessage(prefix
								.concat(errorMessage),
								VerifierMessageType.ERROR));
					}
				}

				List<VerifierParameterInst> warningParams = report
						.getWarningParams();
				for (VerifierParameterInst parameterInst : warningParams) {
					FileContainer fileContainer = processedPropertiesResolverMap
							.get(parameterInst);
					String warningMessage = rule.getWarningMessage();
					if ((warningMessage == null) || warningMessage.equals("")) {
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
			} catch (WrongDataTypeException e) {
				e.printStackTrace();
				DataInstance dataInstance = e.getDataInstance();
				VerifierParameterInst parameterInst = (VerifierParameterInst) dataInstance;
				String prefix = "La proprietà \""
						+ parameterInst.getParameterCoordinates().getFieldDef()
								.getLabel()
						+ "\" ha un DataType errato per essere utilizzata come parametro \""
						+ parameterInst.getVerifierParameterDef().getLabel()
						+ "\" in una regola di tipo " + rule.getType().value()
						+ "!!!";
				FileContainer fileContainer = processedPropertiesResolverMap
						.get(parameterInst);
				if (fileContainer != null) {
					this.addFileMessage(fileContainer.getId(),
							new VerifierMessage(prefix,
									VerifierMessageType.ERROR));
				} else {
					this.addMainMessage(new VerifierMessage(prefix,
							VerifierMessageType.ERROR));
				}
				return false;
			}

		} else if (this.failAllowed) {
			return true;
		}

		return false;
	}

	private void verifySignature(AlfrescoDocument document) {
		try {
			BouncyCastleProvider bcProv = new BouncyCastleProvider();
			Security.addProvider(bcProv);
			InputStream contentInputStream = document.getContentStream()
					.getStream();
			CMSSignedData cms = new CMSSignedData(contentInputStream);

			CMSTypedStream typedStream = new CMSTypedStream(contentInputStream);
			String type = typedStream.getContentType();

			CertStore certStore = cms
					.getCertificatesAndCRLs("Collection", "BC");

			// ottenimento delle firme
			SignerInformationStore infos = cms.getSignerInfos();

			// per ogni signer ottiene l'insieme dei certificati
			Collection<SignerInformation> signers = infos.getSigners();

			for (SignerInformation info : signers) {

				SignerId sid = info.getSID();

				Collection<X509Certificate> certsCollection = (Collection<X509Certificate>) certStore
						.getCertificates(sid);

				for (X509Certificate x509Certificate : certsCollection) {
					X509CertificateObject validCert = this.certsController
							.getCerts().match(x509Certificate);

					if (validCert != null) {
						System.out.println("----> certificato trovato!");
						this.addSignature(document, x509Certificate, validCert);
					}
				}
			}
			this.extractAndEmbedContent(document, cms);

		} catch (Exception e) {
			System.out.println(document.getName()
					+ " non è firmato digitalmente");
		}
	}

}
