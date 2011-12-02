package it.drwolf.slot.alfresco;

import it.drwolf.slot.alfresco.webscripts.AlfrescoWebScriptClient;
import it.drwolf.slot.digsig.CertsController;
import it.drwolf.slot.digsig.Signature;
import it.drwolf.slot.digsig.Utils;
import it.drwolf.slot.pagebeans.support.FileContainer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.Principal;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
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

@Name("signatureVerifier")
@Scope(ScopeType.CONVERSATION)
public class SignatureVerifier {

	@In(create = true)
	private CertsController certsController;

	@In(create = true)
	private AlfrescoUserIdentity alfrescoUserIdentity;

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

	/**
	 * Verifica se il documento è firmato, aggiunge la firma ed embedda il
	 * contenuto non compresso come figlio che Alfresco fa la preview
	 * 
	 * @param document
	 */
	public void verify(AlfrescoDocument document) {
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
