package it.drwolf.slot.digsig;

import it.drwolf.slot.entitymanager.PreferenceManager;

import java.util.List;

import org.bouncycastle.jce.provider.X509CertificateObject;
import org.jboss.seam.ScopeType;
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

	public List<X509CertificateObject> getCertificates() {
		return this.certificates;
	}

	public Certs getCerts() {
		return this.certs;
	}

}
