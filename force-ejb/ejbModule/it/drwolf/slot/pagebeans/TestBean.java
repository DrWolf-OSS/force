package it.drwolf.slot.pagebeans;

import it.drwolf.slot.alfresco.AlfrescoAdminIdentity;
import it.drwolf.slot.alfresco.AlfrescoInfo;
import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.alfresco.custom.model.SlotModel;
import it.drwolf.slot.alfresco.webscripts.AlfrescoWebScriptClient;
import it.drwolf.slot.digsig.AlfrescoNodeCertsProvider;
import it.drwolf.slot.digsig.Certs;
import it.drwolf.slot.digsig.Utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.alfresco.cmis.client.AlfrescoFolder;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
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
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Identity;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

@Name("testBean")
@Scope(ScopeType.CONVERSATION)
public class TestBean {

	@In(create = true)
	private AlfrescoAdminIdentity alfrescoAdminIdentity;

	@In
	private Identity identity;

	@In(create = true)
	private AlfrescoUserIdentity alfrescoUserIdentity;

	@In(create = true)
	private AlfrescoInfo alfrescoInfo;

	@In(create = true)
	private AlfrescoNodeCertsProvider alfrescoNodeCertsProvider;

	public void test() {
		Session session = alfrescoAdminIdentity.getSession();
		//
		session.clear();
		//
		System.out.println("---> Connected");
		AlfrescoFolder rootFolder = (AlfrescoFolder) session.getRootFolder();
		System.out.println("root:--> " + rootFolder.getPath());

		try {
			CmisObject object = session
					.getObject("workspace://SpacesStore/6411e4ed-1af8-4a5f-9a2a-3a0df3d9c814");
			AlfrescoFolder objFolder = (AlfrescoFolder) object;
			System.out.println("---> sp: " + objFolder.getPath());
			System.out.println("---> type: " + objFolder.getType());
			ItemIterable<Document> checkedOutDocs = objFolder
					.getCheckedOutDocs();
			System.out.println("--> total items: "
					+ checkedOutDocs.getTotalNumItems());
			System.out.println("--> page items: "
					+ checkedOutDocs.getPageNumItems());

			AlfrescoDocument png = (AlfrescoDocument) session
					.getObject("workspace://SpacesStore/cd548e84-be1e-49f5-9a68-cd566f2d01ae");
			System.out.println("---> png paths: " + png.getPaths());
			Collection<ObjectType> aspects = png.getAspects();
			for (ObjectType objectType : aspects) {
				Map<String, PropertyDefinition<?>> propertyDefinitions = objectType
						.getPropertyDefinitions();

				if (propertyDefinitions != null) {
					Set<String> keySet = propertyDefinitions.keySet();
					for (String key : keySet) {
						PropertyDefinition<?> propertyDefinition = propertyDefinitions
								.get(key);
						Property<Object> property = png.getProperty(key);
						System.out.println("propId: "
								+ propertyDefinition.getId() + "| value: "
								+ property.getValueAsString());
					}
				}
				System.out.println("id: " + objectType.getId());
				System.out.println("displayName: "
						+ objectType.getDisplayName());
				System.out.println("localName: " + objectType.getLocalName());
				System.out.println("queryName: " + objectType.getQueryName());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// List<Tree<FileableCmisObject>> descendants = rootFolder
		// .getDescendants(0);
		//
		// ItemIterable<CmisObject> children = rootFolder.getChildren();
		//
		// Collection<ObjectType> aspects = rootFolder.getAspects();
		// for (ObjectType objectType : aspects) {
		// System.out.println("id: " + objectType.getId());
		// System.out.println("displayName: " + objectType.getDisplayName());
		// System.out.println("localName: " + objectType.getLocalName());
		// System.out.println("queryName: " + objectType.getQueryName());
		// }
	}

	public void loadModel() {
		try {
			Serializer serializer = new Persister();
			File source = new File(
					"/home/drwolf/git/force/it.drwolf.slot.alfresco.custom/src/java/it/drwolf/slot/alfresco/content/slotModel.xml");
			SlotModel model = serializer.read(SlotModel.class, source);
			System.out.println(model);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void test2() {

		Session userSession = alfrescoUserIdentity.getSession();
		AlfrescoFolder usersHome = (AlfrescoFolder) userSession
				.getObject("workspace://SpacesStore/065b9204-329b-42bb-b16d-e76811274d25");
		String path = usersHome.getPath();
		AlfrescoFolder userHome = (AlfrescoFolder) userSession
				.getObjectByPath(path + "/"
						+ identity.getCredentials().getUsername());
		System.out.println("-->");
	}

	public void test3() {
		Session session = alfrescoAdminIdentity.getSession();
		AlfrescoDocument object = (AlfrescoDocument) session
				.getObject("workspace://SpacesStore/03917305-2d21-4bc4-af53-ce963bf4de3e");
		System.out.println(object.getPaths().get(0) + " ... "
				+ object.getName());
		System.out.println("---> debug");
	}

	public void executeTest() {
		try {
			String username = alfrescoUserIdentity.getUsername();
			String password = alfrescoUserIdentity.getPassword();
			String url = alfrescoUserIdentity.getUrl();
			AlfrescoWebScriptClient webScriptClient = new AlfrescoWebScriptClient(
					username, password, url);

			AlfrescoDocument document = (AlfrescoDocument) alfrescoUserIdentity
					.getSession()
					.getObject(
							"workspace://SpacesStore/fc318402-3748-4042-9627-12057a434191");

			document.addAspect("P:dw:signed");

			String signatureNodeRef = webScriptClient
					.addSignature(
							"workspace://SpacesStore/fc318402-3748-4042-9627-12057a434191",
							"firma3");
			System.out.println("---> signature: " + signatureNodeRef);

			Session session = alfrescoUserIdentity.getSession();
			AlfrescoDocument signature = (AlfrescoDocument) session
					.getObject(signatureNodeRef);

			Date d = new Date();
			Calendar c = new GregorianCalendar();
			c.setTime(d);

			Map<String, Object> props = new HashMap<String, Object>();
			props.put("dw:validity", Boolean.TRUE);
			props.put("dw:expiry", c);
			props.put("dw:authority", "Authority Value");
			props.put("dw:sign", "Massimo Torelli");
			props.put("dw:cf", "massimotorellicf");

			signature.updateProperties(props);

			System.out.println("---> debug");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void retrieveChildren() {
		Session session = alfrescoAdminIdentity.getSession();
		AlfrescoDocument doc = (AlfrescoDocument) session
				.getObject("workspace://SpacesStore/844d9828-3eea-4388-b41d-98dbca8c8756");
		List<Relationship> relationships = doc.getRelationships();
		for (Relationship relationship : relationships) {
			List<Property<?>> properties = relationship.getProperties();
			ObjectType baseType = relationship.getBaseType();
			String name = relationship.getName();
			CmisObject source = relationship.getSource();
			CmisObject target = relationship.getTarget();
		}

		ItemIterable<QueryResult> results = session
				.query("SELECT cmis:objectId from dw:signature WHERE IN_TREE('workspace://SpacesStore/844d9828-3eea-4388-b41d-98dbca8c8756')",
						true);
		Iterator<QueryResult> iterator = results.iterator();
		while (iterator.hasNext()) {
			QueryResult result = iterator.next();
			// List<PropertyData<?>> properties = result.getProperties();
			// Object objectTypeId = result
			// .getPropertyValueById("cmis:objectTypeId");
			// System.out.println(objectTypeId);
			// Object authority = result.getPropertyValueById("dw:authority");
			Object objectId = result.getPropertyValueById("cmis:objectId");
			System.out.println(objectId);

			Document document = (Document) session.getObject(objectId
					.toString());
			Map<String, Object> props = new HashMap<String, Object>();
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(new Date());

			props.put("dw:validity", Boolean.TRUE);
			props.put("dw:expiry", calendar);
			props.put("dw:authority", "Dr Wolf srl");
			props.put("dw:sign", "Massimo Torelli");
			props.put("dw:cf", "codicefiscalemassimotorelli");
			document.updateProperties(props);
		}
	}

	public void signatureTest() {
		try {

			// //////////////////

			Certs certs = new Certs();

			alfrescoNodeCertsProvider
					.setNodeRef("workspace://SpacesStore/2ef3abaf-fd3d-41d8-b8fb-1fc0989fd1ad");
			certs.setCertsProvider(alfrescoNodeCertsProvider);

			List<X509CertificateObject> list = certs.getCertificates();
			BouncyCastleProvider bcProv = new BouncyCastleProvider();
			Security.addProvider(bcProv);

			//
			Session session = alfrescoAdminIdentity.getSession();
			AlfrescoDocument signedDoc = (AlfrescoDocument) session
					.getObject("workspace://SpacesStore/b28ee78b-c34f-4fc4-914a-701deb08aa12");
			// signedDoc.addAspect("P:digsig:digitalSignature");
			//

			InputStream contentInputStream = signedDoc.getContentStream()
					.getStream();
			//

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
					try {
						X509CertificateObject validCert = certs
								.match(x509Certificate);

						if (validCert != null) {
							System.out.println("----> trovato!");
							String md5 = md5Encode(validCert.getSignature());

							// linkCertificate(signedDoc, x509Certificate,
							// validCert);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			// extractContent(signedDoc, cms);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String md5Encode(byte[] signature) {
		try {
			MessageDigest digest;
			digest = MessageDigest.getInstance("MD5");
			digest.update(signature);
			byte messageDigest[] = digest.digest();

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			}
			System.out.println("---> MD5: " + hexString.toString());
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private void linkCertificate(Document document,
			X509Certificate x509Certificate, X509CertificateObject validCert) {

		Map<String, Object> props = new HashMap<String, Object>();

		// ChildAssociationRef associationRef = nodeService.createNode(nodeRef,
		// PROP_SIGNATURES, PROP_SIGNATURES, PROP_SIGNID);
		List<String> signList = new ArrayList<String>();
		List<Calendar> expiryList = new ArrayList<Calendar>();
		List<String> authorityList = new ArrayList<String>();
		List<String> cfList = new ArrayList<String>();
		List<Boolean> validyList = new ArrayList<Boolean>();

		String mysign = Utils.getCN(x509Certificate.getSubjectDN().toString());

		signList.add(mysign);

		Date notAfter = x509Certificate.getNotAfter();
		Calendar cal = new GregorianCalendar();
		cal.setTime(notAfter);

		expiryList.add(cal);
		authorityList.add(Utils.getCN(validCert.getIssuerDN().toString()));
		cfList.add("");
		validyList.add(Boolean.TRUE);

		props.put("digsig:sign", signList);
		props.put("digsig:expiry", expiryList);
		props.put("digsig:authority", authorityList);
		props.put("digsig:cf", cfList);
		props.put("digsig:validity", validyList);

		// nodeService.setProperty(childRef, PROP_SIGN, mysign);
		// nodeService.setProperty(childRef, PROP_EXPIRY,
		// x509Certificate.getNotAfter());
		// nodeService.setProperty(childRef, PROP_AUTHORITY,
		// Utils.getCN(validCert.getIssuerDN().toString()));
		// nodeService.setProperty(childRef, PROP_VALIDITY,
		// (Serializable) Boolean.TRUE);

		System.out.println("VALID UNTIL " + notAfter);
		System.out.println("SIGNED by: " + mysign);
		System.out.println("ISSUED by: "
				+ Utils.getCN(validCert.getIssuerDN().toString()));

		document.updateProperties(props);
	}

	private void extractContent(Document document, CMSSignedData cms)
			throws IOException {

		CMSProcessable signedContent = cms.getSignedContent();
		final byte[] content = (byte[]) signedContent.getContent();

		ByteArrayInputStream baip = new ByteArrayInputStream(content);

		String origin = document.getName();
		int dotIndex = origin.lastIndexOf(".");
		String name = origin;
		String extension = "";
		if (dotIndex > 0) {
			name = origin.substring(0, dotIndex);
			extension = origin.substring(dotIndex);
		}

		//
		String mimetype = "application/octet-stream";
		ContentStreamImpl contentStreamImpl = new ContentStreamImpl(name,
				new BigInteger("" + content.length), mimetype, baip);

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(PropertyIds.NAME, name);
		properties.put(PropertyIds.OBJECT_TYPE_ID,
				BaseTypeId.CMIS_DOCUMENT.value());

		Folder digsigFolder = (Folder) alfrescoAdminIdentity
				.getSession()
				.getObject(
						"workspace://SpacesStore/e18724d8-23b4-46b8-809e-67bc66c4699f");

		ObjectId objectId = alfrescoAdminIdentity.getSession().createDocument(
				properties, digsigFolder, contentStreamImpl,
				VersioningState.NONE, null, null, null);
		System.out.println(objectId);

		// ContentReader streamReader = new ByteArrayContentReader(content);

		// Serializable name = nodeService.getProperty(nodeRef,
		// ContentModel.PROP_NAME);

		// ContentHandler contenthandler = new BodyContentHandler();
		// Metadata metadata = new Metadata();
		// Parser parser = new AutoDetectParser();
		//
		// // ContentReader tempReader = streamReader; //
		// tempWriter.getReader();
		// try {
		// ParseContext pc = new ParseContext();
		// parser.parse(baip, contenthandler, metadata, pc);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		//
		// String contentType = metadata.get(Metadata.CONTENT_TYPE);

		// NodeRef parentRef = nodeService.getPrimaryParent(nodeRef)
		// .getParentRef();
		// QName contentQName = QName.createQName(
		// NamespaceService.CONTENT_MODEL_1_0_URI, "content");
		// FileInfo fileInfo = fileFolderService.create(parentRef, name
		// + "_signed_content.pdf", contentQName);
		// NodeRef destRef = fileInfo.getNodeRef();
		//
		// tempReader.getContentInputStream().close();
		//
		// tempReader = streamReader.getReader();
		// tempReader.setMimetype(contentType);
		//
		// ContentWriter finalWriter = contentService.getWriter(destRef,
		// contentQName, true);
		//
		// contentService.transform(tempReader, finalWriter);
		//
		// finalWriter.getContentOutputStream().flush();
		// finalWriter.getContentOutputStream().close();
		//
		// // link pdf image file as child of curent node
		// ChildAssociationRef associationRef = nodeService.createNode(nodeRef,
		// PROP_PDFIMAGE, PROP_PDFIMAGE, ContentModel.TYPE_LINK);
		// NodeRef childRef = associationRef.getChildRef();
		//
		// nodeService.setProperty(childRef, ContentModel.PROP_LINK_DESTINATION,
		// destRef);

	}

	public void addRendition() {
		Session session = alfrescoAdminIdentity.getSession();
		OperationContext context = session.createOperationContext();
		context.setRenditionFilterString("*");

		AlfrescoDocument source = (AlfrescoDocument) session.getObject(
				"workspace://SpacesStore/067ee088-8d3b-4cea-bf0b-cae89806e22b",
				context);
		AlfrescoDocument target = (AlfrescoDocument) session.getObject(
				"workspace://SpacesStore/b28ee78b-c34f-4fc4-914a-701deb08aa12",
				context);

		List<Rendition> renditions = source.getRenditions();
		for (Rendition r : renditions) {
			String title = r.getTitle();
			if (title != null && title.equals("imgpreview")) {
				target.getRenditions().add(r);
			}
		}
	}

	public void testName() {

	}

}
