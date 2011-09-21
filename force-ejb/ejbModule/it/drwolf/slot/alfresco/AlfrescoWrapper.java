package it.drwolf.slot.alfresco;

import it.drwolf.slot.digsig.Signature;
import it.drwolf.slot.pagebeans.support.FileContainer;
import it.drwolf.slot.prefs.PreferenceKey;
import it.drwolf.slot.prefs.Preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javax.activation.MimetypesFileTypeMap;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.alfresco.cmis.client.AlfrescoFolder;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.Ace;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlEntryImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.AccessControlPrincipalDataImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.commons.io.IOUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("alfrescoWrapper")
@Scope(ScopeType.CONVERSATION)
public class AlfrescoWrapper {

	public static final String NODEREF_PREFIX = "workspace://SpacesStore/";

	public static final int LENGHT_LIMIT = 150;
	public static final String SPACER = " ";

	// public static final String DRAWINGS_FOLDER_REF =
	// "6880b7a5-ba84-464c-a328-f5428426b450";

	public static String id2ref(String id) {
		int slashIndex = id.lastIndexOf("/");
		if (slashIndex > 0) {
			return id.substring(slashIndex, id.length());
		}
		return "";
	}

	public static ObjectId ref2id(String ref) {
		if (("" + ref).length() < 10) {
			System.out.println("null ref");
		}
		return ref.startsWith(AlfrescoWrapper.NODEREF_PREFIX) ? new ObjectIdImpl(
				ref) : new ObjectIdImpl(AlfrescoWrapper.NODEREF_PREFIX + ref);
	}

	public static String normalizeFolderName(String oggetto, int lenghtLimit,
			String spacer) {
		String normalized = Normalizer.normalize(oggetto, Normalizer.Form.NFD);
		normalized = normalized.trim().replaceAll("\\W+", spacer).trim();
		int length = normalized.length();
		if (length > lenghtLimit) {
			normalized = normalized.substring(0, lenghtLimit);
		}
		int last = normalized.lastIndexOf(spacer);
		if (last == length - 1) {
			normalized = normalized.substring(0, length - 1);
		}
		return normalized;
	}

	@In
	private AlfrescoAdminIdentity alfrescoAdminIdentity;

	@In(create = true)
	private AlfrescoUserIdentity alfrescoUserIdentity;

	@In(create = true)
	Preferences preferences;

	@In(create = true)
	AlfrescoInfo alfrescoInfo;

	@In(value = "#{facesContext.externalContext}", required = false)
	private ExternalContext extCtx;

	public void applyACL(Folder folder, String groupName) {
		Session session = this.alfrescoUserIdentity.getSession();
		// faccio un workaround per recuperare la session senza stravolgere....
		// si deve fare refactor e far passare la session come parametro
		if (session == null) {
			session = this.alfrescoAdminIdentity.getSession();
		}
		try {
			Ace ace = new AccessControlEntryImpl(
					new AccessControlPrincipalDataImpl(groupName),
					Arrays.asList("cmis:read", " cmis:write", "cmis:all"));

			folder.applyAcl(Arrays.asList(ace), new ArrayList<Ace>(),
					AclPropagation.PROPAGATE);
		} catch (CmisObjectNotFoundException e) {

		}
	}

	public void download(String id) {
		HttpServletResponse response = (HttpServletResponse) this.extCtx
				.getResponse();

		Document d = (Document) this.alfrescoAdminIdentity.getSession()
				.getObject(AlfrescoWrapper.ref2id(id));

		ContentStream cs = d.getContentStream();

		response.setContentType(cs.getMimeType());

		response.addHeader("Content-disposition", "attachment; filename=\""
				+ d.getProperty(PropertyIds.NAME).getValueAsString() + "\"");
		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control",
				"must-revalidate, post-check=0, pre-check=0");
		try {
			response.setContentLength((int) cs.getLength());
			ServletOutputStream os = response.getOutputStream();

			byte[] buffer = new byte[8192];
			int len;
			while ((len = cs.getStream().read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
			os.flush();
			os.close();
			FacesContext.getCurrentInstance().responseComplete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public AlfrescoFolder findOrCreateFolder(Folder parent, String folderName) {
		AlfrescoFolder folder = null;
		// cerco la cartella con il nome dello slot e se non c'è la creo
		Session session = this.alfrescoUserIdentity.getSession();
		// faccio un workaround per recuperare la session senza stravolgere....
		// si deve fare refactor e far passare la session come parametro
		if (session == null) {
			session = this.alfrescoAdminIdentity.getSession();
		}
		try {
			folder = (AlfrescoFolder) session.getObjectByPath(parent.getPath()
					+ "/" + folderName);
		} catch (CmisObjectNotFoundException e) {
			HashMap<String, Object> props = new HashMap<String, Object>();
			props.put(PropertyIds.NAME, folderName);
			props.put(PropertyIds.OBJECT_TYPE_ID,
					BaseTypeId.CMIS_FOLDER.value());
			folder = (AlfrescoFolder) parent.createFolder(props, null, null,
					null, session.createOperationContext());
		}
		return folder;
	}

	public AlfrescoFolder findOrCreateFolder(String path, String folderName) {
		Session session = this.alfrescoUserIdentity.getSession();
		AlfrescoFolder parent = (AlfrescoFolder) session.getObjectByPath(path);
		return this.findOrCreateFolder(parent, folderName);
	}

	/**
	 * @param aspectId
	 * @param folderId
	 * @param orderBy
	 * @return
	 */
	public ArrayList<String> getDocumentIdsInFolderByAspect(String aspectId,
			String folderId, String orderBy, int limit) {
		// ora il paramentro order by lo gestisco come stringa, vediamo se
		// conviene gestirlo con lista di stringe
		ArrayList<String> lists = new ArrayList<String>();
		try {
			Session session = this.alfrescoAdminIdentity.getSession();
			OperationContext context = session.createOperationContext();
			String query = "SELECT D.cmis:objectId FROM cmis:document AS D JOIN "
					+ aspectId
					+ " AS A ON D.cmis:objectId=A.cmis:objectId   WHERE IN_TREE(D, '"
					+ folderId + "')";
			if (orderBy != null) {
				query += " order by A." + orderBy;
			}
			ItemIterable<QueryResult> results = session.query(query, true);
			if (limit > 0) {
				results = results.getPage(limit);
			}
			if (results.getTotalNumItems() != 0) {
				Iterator<QueryResult> iterator = results.iterator();
				while (iterator.hasNext()) {
					QueryResult result = iterator.next();
					String cmisObjectId = result
							.getPropertyValueById("cmis:objectId");
					lists.add(cmisObjectId);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return lists;
	}

	public Folder getMainProjectFolder() {
		String value = this.preferences
				.getValue(PreferenceKey.FORCE_GROUPS_PATH.name());
		// faccio un workaround per recuperare la session senza stravolgere....
		// si deve fare refactor e far passare la session come parametro

		Session session = this.alfrescoUserIdentity.getSession();
		if (session == null) {
			session = this.alfrescoAdminIdentity.getSession();
		}
		Folder mainFolder = (Folder) session.getObjectByPath(value);
		return mainFolder;
	}

	public String getNodeName(String ref) {
		if (("" + ref).length() < 10) {
			return "NO-ID";
		}
		return this.alfrescoUserIdentity.getSession()
				.getObject(AlfrescoWrapper.ref2id(ref))
				.getProperty(PropertyIds.NAME).getValueAsString();
	}

	public Object getThumbnail(String id, String name) {
		if ((id == null) || (id.length() > 20)) {
			try {
				Session session = this.alfrescoAdminIdentity.getSession();
				OperationContext context = session.createOperationContext();

				context.setRenditionFilterString("*");

				AlfrescoDocument doc = (AlfrescoDocument) session.getObject(
						AlfrescoWrapper.ref2id(id), context);

				if (doc.hasAspect(Signature.ASPECT_SIGNED)) {
					ItemIterable<QueryResult> results = session.query(
							"SELECT cmis:objectId, cmis:name FROM cmis:document WHERE IN_TREE('"
									+ doc.getId() + "')", true);
					if (results.getTotalNumItems() != 0) {
						Iterator<QueryResult> iterator = results.iterator();
						while (iterator.hasNext()) {
							QueryResult result = iterator.next();
							String cmisName = result
									.getPropertyValueById("cmis:name");
							if (cmisName.equals(FileContainer
									.retrieveContentFilename(doc.getName()))) {
								String contentNodeRef = result
										.getPropertyValueById("cmis:objectId");
								doc = (AlfrescoDocument) session.getObject(
										contentNodeRef, context);
							}
						}
					}
				}

				for (Rendition r : doc.getRenditions()) {
					if (name.equals(r.getTitle())) {
						return IOUtils.toByteArray(r.getContentStream()
								.getStream());
					}
				}
				return IOUtils.toByteArray(AlfrescoWrapper.class
						.getResourceAsStream("notavailable.gif"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public AlfrescoFolder retrieveGroupFolder(String path, String shortName) {
		return (AlfrescoFolder) this.alfrescoUserIdentity.getSession()
				.getObjectByPath(path + "/" + shortName);
	}

	/**
	 * DA TESTARE
	 * 
	 */
	public void storeFile(Folder folder, File file) throws Exception {
		Session adminSession = this.alfrescoUserIdentity.getSession();

		HashMap<String, Object> props = new HashMap<String, Object>();
		String contentType = new MimetypesFileTypeMap().getContentType(file
				.getName());
		ContentStreamImpl contentStreamImpl = new ContentStreamImpl(
				file.getName(), new BigInteger("" + file.length()),
				contentType, new FileInputStream(file));
		props.put(PropertyIds.NAME, file.getName());
		props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");

		adminSession.createDocument(props, folder, contentStreamImpl,
				VersioningState.NONE, null, null, null);
	}

}
