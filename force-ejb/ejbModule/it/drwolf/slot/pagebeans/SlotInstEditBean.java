package it.drwolf.slot.pagebeans;

import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
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

	@In
	private AlfrescoUserIdentity alfrescoUserIdentity;

	private List<PropertyInst> propertyInsts;

	private List<DocInstCollection> docInstCollections;

	private HashMap<Long, List<UploadItem>> datas = new HashMap<Long, List<UploadItem>>();

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
					e.printStackTrace();
					return;
				}
			}
		}

		slotInstHome.getInstance().setDocInstCollections(
				new HashSet<DocInstCollection>(docInstCollections));
		slotInstHome.persist();
	}

	public void listener(UploadEvent event) {
		UploadItem item = event.getUploadItem();
		String fileName = item.getFileName();
		String docDefCollectionIdAsString = ((HttpServletRequest) javax.faces.context.FacesContext
				.getCurrentInstance().getExternalContext().getRequest())
				.getParameter("docDefCollectionId");
		Long docDefCollectionId = new Long(docDefCollectionIdAsString);
		List<UploadItem> list = datas.get(docDefCollectionId);
		if (list == null) {
			list = new ArrayList<UploadItem>();
			datas.put(docDefCollectionId, list);
		}
		list.add(item);
		System.out.println("-> " + fileName + " successfully uploaded");
		System.out.println("-> docDefCollectionId: "
				+ docDefCollectionIdAsString);
	}

	public String storeOnAlfresco(UploadItem item,
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
					.getDocDef().getAspects();

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
			System.out.println("ObjectID: " + objectId);

			AlfrescoDocument document = (AlfrescoDocument) session
					.getObject(objectId);

			for (String aspect : aspects) {
				document.addAspect(aspect);
			}

			nodeRef = objectId.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return nodeRef;
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

	public String getExtension(String fileName) {
		String[] fileNameSplitted = fileName.split("\\.");
		if (fileNameSplitted.length < 2) {
			return "";
		}
		return fileNameSplitted[fileNameSplitted.length - 1];
	}

	public HashMap<Long, List<UploadItem>> getDatas() {
		return datas;
	}

	public void setDatas(HashMap<Long, List<UploadItem>> datas) {
		this.datas = datas;
	}
}
