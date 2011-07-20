package it.drwolf.slot.digsig;

import it.drwolf.slot.entitymanager.PreferenceManager;
import it.drwolf.slot.prefs.PreferenceKey;

import java.util.List;

import org.bouncycastle.jce.provider.X509CertificateObject;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("certsController")
@Scope(ScopeType.APPLICATION)
public class CertsController {

	private Certs certs;

	private List<X509CertificateObject> certificates;

	@In(create = true)
	private AlfrescoNodeCertsProvider alfrescoNodeCertsProvider;

	@In(create = true)
	private PreferenceManager preferenceManager;

	@Create
	public void loadCerts() {
		certs = new Certs();
		alfrescoNodeCertsProvider.setNodeRef(preferenceManager.getPreference(
				PreferenceKey.CERTS_LIST_REF.name()).getStringValue());
		certs.setCertsProvider(alfrescoNodeCertsProvider);
		certificates = certs.getCertificates();
	}

	public Certs getCerts() {
		return certs;
	}

	public List<X509CertificateObject> getCertificates() {
		return certificates;
	}

}
