package it.drwolf.slot.digsig;

import it.drwolf.slot.digsig.ICertsProvider.ICertsProviderListener;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.jce.provider.X509CertParser;
import org.bouncycastle.jce.provider.X509CertificateObject;

public class Certs implements ICertsProviderListener {

	private List<X509CertificateObject> certificates = null;
	private ICertsProvider certsProvider = null;

	public Certs() {
	}

	private void get_list_of_certificates(InputStream is) throws Exception {
		CMSSignedData cms = new CMSSignedData(is);
		byte[] content = (byte[]) cms.getSignedContent().getContent();
		// FileOutputStream fos = new FileOutputStream(new File("out.zip"));
		// fos.write(content);
		// fos.close();
		ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(
				content));
		while (zis.getNextEntry() != null) {

			X509CertParser parser = new X509CertParser();
			parser.engineInit(zis);
			X509CertificateObject cert = (X509CertificateObject) parser
					.engineRead();
			if (cert != null) {
				this.certificates.add(cert);

			}

		}
	}

	public List<X509CertificateObject> getCertificates() {
		if (this.certificates == null) {
			try {
				certificates = new ArrayList<X509CertificateObject>();
				get_list_of_certificates(getCertsProvider().getResource());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				getCertsProvider().dispose();
			}
		}
		return this.certificates;
	}

	public void setCertsProvider(ICertsProvider certsProvider) {
		assert certsProvider != null : "Missing ICertsProvider implementation";
		this.certsProvider = certsProvider;
		certificates = null;
	}

	public ICertsProvider getCertsProvider() {
		return certsProvider;
	}

	public void changeResource(ICertsProvider provider) {
		setCertsProvider(provider);
	}

	public X509CertificateObject match(X509Certificate x509Certificate) {
		for (X509CertificateObject validCert : this.getCertificates()) {
			try {
				x509Certificate.verify(validCert.getPublicKey());
			} catch (Exception e) {
				continue;
			}
			return validCert;
		}
		return null;
	}

}
