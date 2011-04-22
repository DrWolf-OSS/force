package it.drwolf.slot.alfresco;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.HashMap;

import javax.activation.MimetypesFileTypeMap;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
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

	@In
	private AlfrescoAdminIdentity alfrescoAdminIdentity;

	public static ObjectId ref2id(String ref) {
		if (("" + ref).length() < 10) {
			System.out.println("null ref");
		}
		return ref.startsWith(AlfrescoWrapper.NODEREF_PREFIX) ? new ObjectIdImpl(
				ref) : new ObjectIdImpl(AlfrescoWrapper.NODEREF_PREFIX + ref);
	}

	public String getNodeName(String ref) {
		if (("" + ref).length() < 10) {
			return "NO-ID";
		}
		return this.alfrescoAdminIdentity.getSession()
				.getObject(AlfrescoWrapper.ref2id(ref))
				.getProperty(PropertyIds.NAME).getValueAsString();
	}

	public Object getThumbnail(String id, String name) {
		if (id == null || id.length() > 20) {
			try {
				Session session = this.alfrescoAdminIdentity.getSession();
				OperationContext context = session.createOperationContext();

				context.setRenditionFilterString("*");

				Document doc = (Document) session.getObject(
						AlfrescoWrapper.ref2id(id), context);

				if (doc.getContentStream().getMimeType().contains("image")) {

					for (Rendition r : doc.getRenditions()) {
						if (name.equals(r.getTitle())) {
							return IOUtils.toByteArray(r.getContentStream()
									.getStream());
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return AlfrescoWrapper.class.getResourceAsStream("image-missing.png");
	}

	/**
	 * DA TESTARE
	 * 
	 */
	public void storeFile(Folder folder, File file) throws Exception {
		Session adminSession = this.alfrescoAdminIdentity.getSession();

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
