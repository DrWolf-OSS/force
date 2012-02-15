package it.drwolf.slot.pagebeans;

import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.alfresco.AlfrescoWrapper;
import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.alfresco.custom.support.DocumentPropertyInst;
import it.drwolf.slot.application.CustomModelController;
import it.drwolf.slot.entity.DependentSlotDef;
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
import it.drwolf.slot.enums.SlotInstStatus;
import it.drwolf.slot.exceptions.WrongDataTypeException;
import it.drwolf.slot.interfaces.Conditionable;
import it.drwolf.slot.interfaces.DataDefinition;
import it.drwolf.slot.interfaces.DataInstance;
import it.drwolf.slot.interfaces.IRuleVerifier;
import it.drwolf.slot.pagebeans.support.FileContainer;
import it.drwolf.slot.pagebeans.support.ValueChangeListener;
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

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.alfresco.cmis.client.AlfrescoFolder;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.commons.io.FilenameUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

@Name("slotInstEditBean")
@Scope(ScopeType.CONVERSATION)
@Transactional
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

	// @In(create = true)
	// private AlfrescoWrapper alfrescoWrapper;
	//
	// @In(create = true)
	// private Preferences preferences;

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

	// private String validationResult;
	private String dirty = "false";

	@In(create = true)
	private ValueChangeListener valueChangeListener;

	@In(value = "#{facesContext.externalContext}", required = false)
	private ExternalContext extCtx;

	private static final int LENGHT_LIMIT = 150;
	private static final String SPACER = " ";

	@In(create = true)
	private SlotInstParameters slotInstParameters;

	public void addActiveItemToDatas() {
		if (!this.datas.get(this.activeCollectionId).contains(
				this.activeFileContainer)) {
			try {
				DocInstCollection instCollection = this
						.findInstCollection(this.activeCollectionId);
				//
				AlfrescoFolder slotFolder = this.slotInstHome
						.retrieveOrCreateSlotFolder();
				//
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

	public String buildIdsToRerender(PropertyDef propertyDef) {
		return this.enqueueNames(new HashSet<Conditionable>(propertyDef
				.getConditionedPropertyDefs()));
	}

	private Map<String, List<VerifierParameterInst>> buildVerifierParameterInstsMap(
			Rule rule,
			Map<VerifierParameterInst, FileContainer> processedPropertiesResolverMap) {
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
		return inInstParams;
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
				//
				int size = 0;
				if (list != null) {
					size = list.size();
				}
				// int size = list.size();
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

	public void cleanActiveElements() {
		this.activeCollectionId = null;
		this.activeFileContainer = null;
	}

	private void cleanCollection(Long docDefCollectionId) {
		List<FileContainer> itemslist = this.datas.get(docDefCollectionId);
		if (itemslist != null) {
			itemslist.clear();
		} else {
			itemslist = new ArrayList<FileContainer>();
		}
	}

	public void cleanConditionedElements(PropertyInst propertyInst) {

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

		// Replicato per le Property
		Iterator<PropertyInst> iterator2 = this.propertyInsts.iterator();
		while (iterator2.hasNext()) {
			PropertyInst instProperty = iterator2.next();
			if ((instProperty.getPropertyDef().getConditionalPropertyDef() != null)
					&& instProperty.getPropertyDef()
							.getConditionalPropertyDef().getId()
							.equals(propertyInst.getPropertyDef().getId())) {
				Object conditionalValue = instProperty.getPropertyDef()
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
					// this.cleanCollection(instProperty.getPropertyDef().getId());
					instProperty.clean();
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

	private void cloneValuesOnPropertyInst(PropertyInst source,
			PropertyInst target) {
		target.setValues(new HashSet<String>(source.getValues()));
		target.setStringValue(source.getStringValue());
		target.setIntegerValue(source.getIntegerValue());
		target.setDateValue(source.getDateValue());
		target.setBooleanValue(source.getBooleanValue());
		// this.propertyInsts.add(target);
	}

	private String copyDocumentOnAlfresco(AlfrescoDocument document,
			DocInstCollection instCollection,
			List<DocumentPropertyInst> documentProperties, Folder slotFolder) {
		String nodeRef = "";

		Session session = this.alfrescoUserIdentity.getSession();
		// ContentStream contentStream = document.getContentStream();

		//
		String mimetype = "application/octet-stream";
		ContentStreamImpl contentStreamImpl = (ContentStreamImpl) document
				.getContentStream();
		// resetto il mimetype per farlo decidere e settare direttamente da
		// Alfresco
		contentStreamImpl.setMimeType(mimetype);
		//

		Set<String> aspects = instCollection.getDocDefCollection().getDocDef()
				.getAspectIds();

		Map<String, Object> props = new HashMap<String, Object>();
		props.put(PropertyIds.NAME, FileContainer.encodeFilename(FileContainer
				.decodeFilename(document.getName())));
		props.put(PropertyIds.OBJECT_TYPE_ID, BaseTypeId.CMIS_DOCUMENT.value());

		ObjectId objectId = session.createDocument(props, slotFolder,
				contentStreamImpl, VersioningState.NONE, null, null, null);
		System.out.println(objectId);

		AlfrescoDocument documentCopy = (AlfrescoDocument) session
				.getObject(objectId);

		// prima si aggiungono gli aspect
		for (String aspect : aspects) {
			documentCopy.addAspect(aspect);
		}

		// poi si aggiungono i valori delle relative properties
		this.updateProperties(documentCopy, documentProperties);
		// this.verifySignature(documentCopy);

		nodeRef = objectId.getId();

		return nodeRef;
	}

	private void copyTransientStatusInPersistedStatus() {

		SlotInst parent = this.slotInstHome.getInstance().getParentSlotInst();
		Set<SlotInst> dependentSlotInsts = null;
		if (parent != null) {
			parent.setStatus(parent.getTransientStatus());
			dependentSlotInsts = parent.getDependentSlotInsts();
		} else {
			this.slotInstHome.getInstance().setStatus(
					this.slotInstHome.getInstance().getTransientStatus());
			dependentSlotInsts = this.slotInstHome.getInstance()
					.getDependentSlotInsts();
		}

		for (SlotInst slotInst : dependentSlotInsts) {
			slotInst.setStatus(slotInst.getTransientStatus());
		}
	}

	private void createDependentSlotInsts(DependentSlotDef dependentSlotDef,
			Integer numberOfInstances) {
		for (int i = 0; i < numberOfInstances; i++) {
			SlotInst dependentSlotInst = new SlotInst(dependentSlotDef);
			dependentSlotInst.setOwnerId(this.slotInstHome.getInstance()
					.getOwnerId());
			dependentSlotInst
					.setParentSlotInst(this.slotInstHome.getInstance());
			this.slotInstHome.getInstance().getDependentSlotInsts()
					.add(dependentSlotInst);
		}
	}

	private void createEmptyDependetSlotInsts() {

		Set<DependentSlotDef> dependentSlotDefs = this.slotDefHome
				.getInstance().getDependentSlotDefs();
		for (DependentSlotDef dependentSlotDef : dependentSlotDefs) {
			if (!this.isAlreadyInstanced(dependentSlotDef)) {

				if (dependentSlotDef.getConditionalPropertyDef() != null
						&& dependentSlotDef
								.getConditionalPropertyInst()
								.getValue()
								.equals(this.slotInstHome
										.getInstance()
										.retrievePropertyInstByDef(
												dependentSlotDef
														.getConditionalPropertyDef())
										.getValue())
						|| dependentSlotDef.getConditionalPropertyDef() == null) {

					Integer numberOfInstances = null;
					if (dependentSlotDef.getEmbeddedNumberOfInstances() != null) {
						numberOfInstances = dependentSlotDef
								.getEmbeddedNumberOfInstances()
								.getIntegerValue();
					} else if (dependentSlotDef.getNumberOfInstances() != null) {
						PropertyInst numberOfInstancesPropertyInst = this.slotInstHome
								.getInstance()
								.retrievePropertyInstByDef(
										dependentSlotDef.getNumberOfInstances());
						numberOfInstances = numberOfInstancesPropertyInst
								.getIntegerValue();
					}

					this.createDependentSlotInsts(dependentSlotDef,
							numberOfInstances);

				}
			}

		}
	}

	private void deleteAllDocuments(SlotInst slotInst) {
		Set<SlotInst> dependentSlotInsts = slotInst.getDependentSlotInsts();
		if (dependentSlotInsts != null && !dependentSlotInsts.isEmpty()) {
			for (SlotInst dependent : dependentSlotInsts) {
				this.deleteAllDocuments(dependent);
			}
		}

		for (DocInstCollection docInstCollection : slotInst
				.getDocInstCollections()) {
			for (DocInst docInst : docInstCollection.getDocInsts()) {
				docInst.getDocument().deleteAllVersions();
			}
		}
	}

	public void downloadDocumentsZip() {
		try {

			Collection<List<FileContainer>> values = this.datas.values();
			List<FileContainer> fileContainers = new ArrayList<FileContainer>();
			for (List<FileContainer> list : values) {
				fileContainers.addAll(list);
			}

			String outFilename = AlfrescoWrapper.normalizeFolderName(
					this.slotDefHome.getInstance().getName(),
					AlfrescoWrapper.LENGHT_LIMIT, AlfrescoWrapper.SPACER);

			HttpServletResponse response = (HttpServletResponse) this.extCtx
					.getResponse();
			response.setContentType("ZIP");

			response.addHeader("Content-disposition", "attachment; filename=\""
					+ outFilename + "\"");
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control",
					"must-revalidate, post-check=0, pre-check=0");

			// Create a buffer for reading the files
			byte[] buf = new byte[1024];

			ServletOutputStream os = response.getOutputStream();
			// Create the ZIP file
			ZipOutputStream out = new ZipOutputStream(os);

			// Compress the files
			for (FileContainer fc : fileContainers) {
				// FileInputStream in = new FileInputStream(fc.getFileName());
				InputStream in = fc.getDocument().getContentStream()
						.getStream();
				// Add ZIP entry to output stream.
				out.putNextEntry(new ZipEntry(fc.getFileName()));

				// Transfer bytes from the file to the ZIP file
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				// Complete the entry
				out.closeEntry();
				in.close();
			}

			Set<SlotInst> dependentSlotInsts = this.slotInstHome.getInstance()
					.getDependentSlotInsts();
			for (SlotInst slotInst : dependentSlotInsts) {
				List<Document> documentsList = slotInst.getDocumentsList();
				for (Document document : documentsList) {
					InputStream in = document.getContentStream().getStream();

					out.putNextEntry(new ZipEntry("/"
							+ AlfrescoWrapper.normalizeFolderName(
									slotInst.getId() + " "
											+ slotInst.getSlotDef().getName(),
									AlfrescoWrapper.LENGHT_LIMIT,
									AlfrescoWrapper.SPACER) + "/"
							+ document.getName()));

					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}

					out.closeEntry();
					in.close();
				}
			}

			// Complete the ZIP file
			out.close();
			FacesContext.getCurrentInstance().responseComplete();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void editItem(Long docDefCollectionId, FileContainer container) {
		this.activeCollectionId = docDefCollectionId;
		this.activeFileContainer = container;
	}

	private String enqueueNames(Set<Conditionable> conditionables) {
		String names = "";
		if (conditionables != null) {
			Iterator<Conditionable> iterator = conditionables.iterator();
			int count = 0;
			while (iterator.hasNext()) {
				Conditionable def = iterator.next();
				names = names.concat("p_" + def.getId().toString() + ",");
				count++;
				if (count == conditionables.size()) {
					names = names.substring(0, names.length() - 1);
				}
			}
		}
		return names;
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

	private PropertyInst findTmpPropertyInst(PropertyDef propertyDef) {
		Iterator<PropertyInst> iterator = this.propertyInsts.iterator();
		while (iterator.hasNext()) {
			PropertyInst propertyInst = iterator.next();
			if (propertyInst.getPropertyDef().getId()
					.equals(propertyDef.getId())) {
				iterator.remove();
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

	public String getDirty() {
		return this.dirty;
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
						.retrievePrimaryDocs(defCollection.getDocDef().getId(),
								this.alfrescoUserIdentity.getMyOwnerId());
				List<FileContainer> fileContainers = new ArrayList<FileContainer>();
				Set<String> aspectIds = defCollection.getDocDef()
						.getAspectIds();
				Set<Property> properties = this.customModelController
						.getProperties(aspectIds);
				for (AlfrescoDocument document : collPrimaryDocs) {
					FileContainer fileContainer = new FileContainer(document,
							properties, false);
					fileContainers.add(fileContainer);
				}
				this.primaryDocs.put(defCollection.getId(), fileContainers);
			}

		} else {

			List<PropertyInst> originalPropertyInsts = new ArrayList<PropertyInst>(
					this.slotInstHome.getInstance().getPropertyInsts());
			//
			this.propertyInsts = new ArrayList<PropertyInst>();
			for (PropertyInst opInst : originalPropertyInsts) {
				PropertyInst tmpPropertyInst = new PropertyInst();
				tmpPropertyInst.setPropertyDef(opInst.getPropertyDef());

				this.cloneValuesOnPropertyInst(opInst, tmpPropertyInst);
				this.propertyInsts.add(tmpPropertyInst);
			}

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
						.retrievePrimaryDocs(defCollection.getDocDef().getId(),
								this.slotInstHome.getInstance().getOwnerId());
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

			if (this.slotInstHome.getInstance().getStatus() != null
					&& !this.slotInstHome.getInstance().getStatus()
							.equals(SlotInstStatus.EMPTY)) {
				this.validationRoutine();
			} else {
				this.slotInstHome.getInstance().setTransientStatus(
						SlotInstStatus.EMPTY);
			}

			//
			if (this.slotInstParameters.getOutcome() != null
					&& this.slotInstParameters.getOutcome().equals(
							SlotInstParameters.UPDATED)) {
				this.addMainMessage(new VerifierMessage(
						"Busta aggiornata con successo",
						VerifierMessageType.VALID));
			} else if (this.slotInstParameters.getOutcome() != null
					&& this.slotInstParameters.getOutcome().equals(
							SlotInstParameters.SAVED)) {
				this.slotMessages
						.add(new VerifierMessage("Busta creata con successo",
								VerifierMessageType.VALID));
			}
			//
		}

	}

	// Controlla se quel DependentSLotDef è stato già istanziato almeno una
	// volta.
	// Dal momento che non si può cambiare il numero di istanze dopo la prima
	// volta se quello SlotDef è già presente vuol dire che è uno di cui non
	// vanno create le rispettive Inst
	private boolean isAlreadyInstanced(DependentSlotDef dependentSlotDef) {
		Set<SlotInst> dependentSlotInsts = this.slotInstHome.getInstance()
				.getDependentSlotInsts();
		Iterator<SlotInst> iterator = dependentSlotInsts.iterator();
		while (iterator.hasNext()) {
			SlotInst slotInst = iterator.next();
			if (slotInst.getSlotDef().equals(dependentSlotDef)) {
				return true;
			}
		}
		return false;
	}

	public void listener(UploadEvent event) {
		UploadItem item = event.getUploadItem();

		String fileName = FilenameUtils.getName(item.getFileName());
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

	public String removeSlot() {
		SlotInst instance = this.slotInstHome.getInstance();
		SlotInst merged = this.entityManager.merge(instance);
		this.slotInstHome.setInstance(merged);

		//
		this.deleteAllDocuments(merged);
		//

		return this.slotInstHome.remove();
	}

	private void resetFlags() {
		this.verifiable = true;
		this.failAllowed = false;
		// warning = false;
	}

	@SuppressWarnings("unchecked")
	private List<AlfrescoDocument> retrievePrimaryDocs(Long docDefId,
			String ownerId) {
		List<AlfrescoDocument> primaryDocs = new ArrayList<AlfrescoDocument>();
		List<DocInst> resultList = this.entityManager
				.createQuery(
						"from DocInst d where d.docInstCollection.slotInst.slotDef.type = 'Primary' and d.docInstCollection.slotInst.ownerId=:ownerId and d.docInstCollection.docDefCollection.docDef.id=:docDefId")
				.setParameter("ownerId", ownerId)
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
		this.validationRoutine();
		this.copyTransientStatusInPersistedStatus();

		this.slotInstHome.getInstance().setPropertyInsts(
				new HashSet<PropertyInst>(this.propertyInsts));

		//
		AlfrescoFolder slotFolder = this.slotInstHome
				.retrieveOrCreateSlotFolder();
		//

		Set<Long> keySet = this.datas.keySet();
		for (Long key : keySet) {
			DocInstCollection instCollection = this.findInstCollection(key);
			if (instCollection != null) {
				try {

					this.addCollectionsNewDocumentsToSlot(slotFolder,
							instCollection);

				} catch (Exception e) {
					FacesMessages.instance().add(Severity.ERROR,
							"Errors in Alfresco storage process");
					System.out
							.println("----------------> Errors in Alfresco storage process!!!!!!!");
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
		//
		this.createEmptyDependetSlotInsts();
		//
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

	public void setDirty(String dirty) {
		this.dirty = dirty;
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

		// this.verifySignature(document);

		fileContainer.setDocument(document);

		return document;
	}

	public String update() {
		SlotInst instance = this.slotInstHome.getInstance();

		this.validationRoutine();
		this.copyTransientStatusInPersistedStatus();

		this.entityManager.merge(instance);

		//
		Set<PropertyInst> persistedPropertyInsts = instance.getPropertyInsts();
		for (PropertyInst opInst : persistedPropertyInsts) {
			PropertyInst tmpPropertyInst = this.findTmpPropertyInst(opInst
					.getPropertyDef());
			opInst.clean();
			this.cloneValuesOnPropertyInst(tmpPropertyInst, opInst);
		}
		//

		Set<DocInstCollection> persistedDocInstCollections = instance
				.getDocInstCollections();

		//
		AlfrescoFolder slotFolder = this.slotInstHome
				.retrieveOrCreateSlotFolder();
		//

		for (DocInstCollection instCollection : persistedDocInstCollections) {
			// entityManager.merge(instCollection);
			Set<DocInst> docInsts = instCollection.getDocInsts();
			Iterator<DocInst> iterator = docInsts.iterator();
			while (iterator.hasNext()) {
				DocInst docInst = iterator.next();
				try {
					AlfrescoDocument document = (AlfrescoDocument) this.alfrescoUserIdentity
							.getSession().getObject(docInst.getNodeRef());
					Long docDefCollectionId = instCollection
							.getDocDefCollection().getId();
					List<FileContainer> filesList = this.datas
							.get(docDefCollectionId);

					// Controllo se il document è ancora presente nella lista
					// dei
					// file associati, se c'è ancora ne aggiorno le properties
					// altrimenti lo cancello da alfresco e lo tolgo dalla
					// relativa
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
				} catch (CmisObjectNotFoundException e) {
					System.out.println("----->" + docInst.getNodeRef()
							+ " NOT FOUND on repository");
					e.printStackTrace();
				}
			}

			this.addCollectionsNewDocumentsToSlot(slotFolder, instCollection);
		}

		//
		this.createEmptyDependetSlotInsts();
		//

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

	public String validate() {
		Boolean dirty = null;
		if (this.dirty != null && !this.dirty.equals("")) {
			try {
				dirty = new Boolean(this.dirty);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				dirty = Boolean.TRUE;
				e.printStackTrace();
			}
		} else {
			dirty = new Boolean(false);
		}

		// Se lo Slot non è stato modificato persisto gli status
		String result = this.validationRoutine();
		if (!dirty) {
			this.copyTransientStatusInPersistedStatus();
		}
		return result;
	}

	@Transactional
	private String validationRoutine() {

		this.cleanMessages();
		boolean sizeCollectionPassed = this.checkCollectionsSize();
		if (!sizeCollectionPassed) {
			this.addMainMessage(new VerifierMessage(
					"Validazione fallita! Le dimensioni di alcune buste non rispettano le specifiche",
					VerifierMessageType.ERROR));
		}

		boolean rulesPassed = this.verify();
		if (!rulesPassed) {
			this.addMainMessage(new VerifierMessage(
					"Validazione fallita! Alcune regole non sono verificate",
					VerifierMessageType.ERROR));
		}

		// La presente istanza ha passato la validazione
		SlotInst parentSlotInst = this.slotInstHome.getInstance()
				.getParentSlotInst();
		if (sizeCollectionPassed && rulesPassed) {
			if (parentSlotInst == null) { // E' uno SlotInst padre
				boolean validityChildren = true;
				Set<SlotInst> dependentSlotInsts = this.slotInstHome
						.getInstance().getDependentSlotInsts();
				for (SlotInst slotInst : dependentSlotInsts) {
					if (slotInst.getStatus().equals(SlotInstStatus.INVALID)) {
						this.addMainMessage(new VerifierMessage(
								"La sottobusta " + slotInst.getId() + " "
										+ slotInst.getSlotDef().getName()
										+ " non è valida",
								VerifierMessageType.ERROR));
						validityChildren = false;
					}
				}
				if (validityChildren) {
					this.slotInstHome.getInstance().setTransientStatus(
							SlotInstStatus.VALID);
					this.addMainMessage(new VerifierMessage(
							"La busta ha passato la validazione",
							VerifierMessageType.VALID));
					return "validated";
				} else {
					SlotInst instance = this.slotInstHome.getInstance();
					instance.setTransientStatus(SlotInstStatus.INVALID);

					this.addMainMessage(new VerifierMessage(
							"La busta corrente ha passato la validazione ma alcune sottobuste non sono valide",
							VerifierMessageType.WARNING));

					return "failed";
				}
			} else { // E' uno SlotInst figlio
				this.slotInstHome.getInstance().setTransientStatus(
						SlotInstStatus.VALID);
				if (parentSlotInst.getStatus().equals(SlotInstStatus.VALID)) {
					this.addMainMessage(new VerifierMessage(
							"La busta ha passato la validazione",
							VerifierMessageType.VALID));

					return "validated";
				}

				Set<SlotInst> dependentSlotInsts = parentSlotInst
						.getDependentSlotInsts();
				boolean validityChildren = true;
				for (SlotInst dependentSlotInst : dependentSlotInsts) {
					if (dependentSlotInst.getStatus() != null
							&& dependentSlotInst.getStatus().equals(
									SlotInstStatus.INVALID)
							&& !dependentSlotInst.equals(this.slotInstHome
									.getInstance())) {
						validityChildren = false;
					}
				}

				if (validityChildren) {
					this.addMainMessage(new VerifierMessage(
							"La busta corrente ha passato la validazione, controllare la validità globale dalla busta contenitrice",
							VerifierMessageType.WARNING));
					return "failed";
				} else {
					this.addMainMessage(new VerifierMessage(
							"La busta corrente ha passato la validazione ma altre sottobuste non sono valide",
							VerifierMessageType.WARNING));
					return "failed";
				}
			}
		} else {
			if (parentSlotInst != null) {
				parentSlotInst.setTransientStatus(SlotInstStatus.INVALID);
			}
			this.slotInstHome.getInstance().setTransientStatus(
					SlotInstStatus.INVALID);
			return "failed";
		}
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

		// Map<String, String> encodedParametersMap = rule.getParametersMap();
		IRuleVerifier verifier = rule.getVerifier();
		// List<VerifierParameterDef> verifierParameterDefs = verifier
		// .getInParams();

		Map<String, List<VerifierParameterInst>> verifierParameterInstsMap = this
				.buildVerifierParameterInstsMap(rule,
						processedPropertiesResolverMap);

		//
		//
		//
		//
		//
		// Comunicazione di eventuali errori o warnings
		if (this.verifiable) {
			try {
				boolean passed = true;
				VerifierReport report = verifier
						.verify(verifierParameterInstsMap);
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

}
