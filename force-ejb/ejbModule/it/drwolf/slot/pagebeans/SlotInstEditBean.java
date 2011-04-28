package it.drwolf.slot.pagebeans;

import it.drwolf.slot.alfresco.AlfrescoAdminIdentity;
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
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
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
	private AlfrescoAdminIdentity alfrescoAdminIdentity;

	private List<PropertyInst> propertyInsts;

	private List<DocInstCollection> docInstCollections;

	private HashMap<String, List<UploadItem>> datas = new HashMap<String, List<UploadItem>>();

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

		Set<String> keySet = datas.keySet();
		for (String key : keySet) {
			DocInstCollection instCollection = null;
			boolean found = false;
			Iterator<DocInstCollection> instCollIterator = docInstCollections
					.iterator();
			while (instCollIterator.hasNext() && found == false) {
				instCollection = instCollIterator.next();
				if (instCollection.getDocDefCollection().getId().toString()
						.trim().equalsIgnoreCase(key)) {
					found = true;
				}
			}
			if (found) {
				List<UploadItem> files = datas.get(key);
				for (UploadItem file : files) {
					instCollection.getDocInsts().add(
							new DocInst(instCollection, storeOnAlfresco(file,
									instCollection)));
				}
			}
		}
		slotInstHome.persist();
	}

	public void listener(UploadEvent event) {
		UploadItem item = event.getUploadItem();
		String fileName = item.getFileName();
		String docDefCollectionId = ((HttpServletRequest) javax.faces.context.FacesContext
				.getCurrentInstance().getExternalContext().getRequest())
				.getParameter("docDefCollectionId");
		List<UploadItem> list = datas.get(docDefCollectionId);
		if (list == null) {
			list = new ArrayList<UploadItem>();
			datas.put(docDefCollectionId, list);
		}
		list.add(item);
		System.out.println("-> " + fileName + " successfully uploaded");
		System.out.println("-> docDefCollectionId: " + docDefCollectionId);
	}

	public String storeOnAlfresco(UploadItem item,
			DocInstCollection instCollection) {
		String nodeRef = "";
		try {

			Session session = alfrescoAdminIdentity.getSession();
			AlfrescoFolder folder = (AlfrescoFolder) session
					.getObject("workspace://SpacesStore/6411e4ed-1af8-4a5f-9a2a-3a0df3d9c814");

			Set<String> aspects = instCollection.getDocDefCollection()
					.getDocDef().getAspects();

			String contentType = new MimetypesFileTypeMap().getContentType(item
					.getFileName());
			ContentStreamImpl contentStreamImpl = new ContentStreamImpl(
					item.getFileName(), new BigInteger(""
							+ item.getFile().length()), contentType,
					new FileInputStream(item.getFile()));

			//
			// Folder folder = (Folder) session
			// .getObject("workspace://SpacesStore/6411e4ed-1af8-4a5f-9a2a-3a0df3d9c814");
			//

			// Folder folder = (Folder) session
			// .getObject(AlfrescoWrapper
			// .ref2id("workspace://SpacesStore/d980911d-5906-4e1e-a9f7-efc9c1f4b6df"));

			// String contentType = new
			// MimetypesFileTypeMap().getContentType(item
			// .getFileName());
			// FileInputStream fileInputStream = new FileInputStream(
			// item.getFile());
			// byte[] byteArray = IOUtils.toByteArray(fileInputStream);
			// ContentStreamImpl contentStreamImpl = new ContentStreamImpl(
			// item.getFileName(), new BigInteger(""
			// + item.getFile().length()), contentType,
			// new ByteArrayInputStream(byteArray));

			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(PropertyIds.NAME, item.getFileName());
			properties.put(PropertyIds.OBJECT_TYPE_ID,
					BaseTypeId.CMIS_DOCUMENT.value());
			// Document document = folder.createDocument(properties,
			// contentStreamImpl, VersioningState.NONE, null, null, null,
			// null);
			ObjectId objectId = session.createDocument(properties, folder,
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
}
