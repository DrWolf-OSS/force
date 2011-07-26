package it.drwolf.slot.alfresco;

import it.drwolf.slot.digsig.Signature;
import it.drwolf.slot.pagebeans.support.FileContainer;
import it.drwolf.slot.prefs.PreferenceKey;
import it.drwolf.slot.prefs.Preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
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

	// public static final String DRAWINGS_FOLDER_REF =
	// "6880b7a5-ba84-464c-a328-f5428426b450";

	public static ObjectId ref2id(String ref) {
		if (("" + ref).length() < 10) {
			System.out.println("null ref");
		}
		return ref.startsWith(AlfrescoWrapper.NODEREF_PREFIX) ? new ObjectIdImpl(
				ref) : new ObjectIdImpl(AlfrescoWrapper.NODEREF_PREFIX + ref);
	}

	public static String id2ref(String id) {
		int slashIndex = id.lastIndexOf("/");
		if (slashIndex > 0) {
			return id.substring(slashIndex, id.length());
		}
		return "";
	}

	@In
	private AlfrescoAdminIdentity alfrescoAdminIdentity;

	@In
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

	public AlfrescoFolder findOrCreateFolder(Folder father, String folderName) {
		AlfrescoFolder folder = null;
		// cerco la cartella con il nome dello slot e se non c'è la creo
		Session session = this.alfrescoUserIdentity.getSession();
		// faccio un workaround per recuperare la session senza stravolgere....
		// si deve fare refactor e far passare la session come parametro
		if (session == null) {
			session = this.alfrescoAdminIdentity.getSession();
		}
		try {
			folder = (AlfrescoFolder) session.getObjectByPath(father.getPath()
					+ "/" + folderName);
		} catch (CmisObjectNotFoundException e) {
			HashMap<String, Object> props = new HashMap<String, Object>();
			props.put(PropertyIds.NAME, folderName);
			props.put(PropertyIds.OBJECT_TYPE_ID,
					BaseTypeId.CMIS_FOLDER.value());
			folder = (AlfrescoFolder) father.createFolder(props, null, null,
					null, session.createOperationContext());
		}
		return folder;
	}

	public AlfrescoFolder findOrCreateFolder(String path, String folderName) {
		Session session = this.alfrescoUserIdentity.getSession();
		AlfrescoFolder parent = (AlfrescoFolder) session.getObjectByPath(path);
		return this.findOrCreateFolder(parent, folderName);
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
							// System.out.println("---> content id: "
							// + contentNodeRef);
							// System.out.println("---> cmis:name: " +
							// cmisName);
							// doc = (AlfrescoDocument) session.getObject(
							// contentNodeRef, context);
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

	// private String retrieveContentFileName(String encodedArchiveFileName) {
	// String archiveFileName = FileContainer
	// .decodeFilename(encodedArchiveFileName);
	// int dotIndex = archiveFileName.lastIndexOf(".");
	// String name = archiveFileName;
	// // String extension = "";
	// if (dotIndex > 0) {
	// name = archiveFileName.substring(0, dotIndex);
	// // extension = archiveFileName.substring(dotIndex);
	// }
	// return name;
	// }

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

	// private JsonElement openJsonWebScript(String url)
	// throws MalformedURLException, IOException,
	// UnsupportedEncodingException {
	// URL peopleServiceUrl = new URL(url);
	// URLConnection conn = peopleServiceUrl.openConnection();
	// String auth = "Basic "
	// + Base64.encodeBase64String((identity.getCredentials()
	// .getUsername() + ":" + identity.getCredentials()
	// .getPassword()).getBytes());
	// conn.setRequestProperty("Authorization", auth);
	// conn.connect();
	//
	// InputStream is = conn.getInputStream();
	//
	// JsonElement parsed = new JsonParser().parse(new JsonReader(
	// new InputStreamReader(is, "UTF-8")));
	//
	// return parsed;
	// }
	//
	// public List<Authority> getAlfrescoGroups() {
	// Gson gson = new Gson();
	// List<Authority> authGroups = new ArrayList<Authority>();
	// try {
	// JsonElement parsed = openJsonWebScript(alfrescoInfo
	// .getRepositoryUri()
	// + "/service/api/groups?shortNameFilter=*");
	// JsonObject groups = parsed.getAsJsonObject();
	// JsonArray list = groups.getAsJsonArray("data");
	// for (JsonElement e : list) {
	// Authority fromJson = gson.fromJson(e, Authority.class);
	// authGroups.add(fromJson);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return authGroups;
	// }

}
