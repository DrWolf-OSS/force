package it.drwolf.force.session.homes;

import it.drwolf.force.entity.Comunicato;
import it.drwolf.force.enums.StatoComunicato;
import it.drwolf.slot.prefs.PreferenceKey;
import it.drwolf.slot.prefs.Preferences;

import javax.persistence.EntityManager;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("comunicatoHome")
public class ComunicatoHome extends EntityHome<Comunicato> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1604786159969461373L;

	private String emailTest;

	@In
	EntityManager entityManager;

	@In(create = true)
	private Preferences preferences;

	@Override
	protected Comunicato createInstance() {
		Comunicato comunicato = new Comunicato();
		return comunicato;
	}

	public Integer getComunicatoId() {
		return (Integer) this.getId();
	}

	public Comunicato getDefinedInstance() {
		return this.isIdDefined() ? this.getInstance() : null;
	}

	public String getEmailTest() {
		return this.emailTest;
	}

	public boolean inSpedizione() {
		if (this.getInstance().getStato() != null
				&& this.getInstance().getStato().equals(StatoComunicato.PRONTA)) {
			return true;
		}
		return false;
	}

	public boolean isWired() {
		return true;
	}

	public String sendComunicato() {

		return "OK";
	}

	public String sendTest() {
		try {
			HtmlEmail email = new HtmlEmail();
			email.setCharset("UTF-8");

			email.setHostName(this.preferences
					.getValue(PreferenceKey.FORCE_EMAIL_HOSTNAME.name()));
			email.setSmtpPort(25);
			email.setFrom(this.preferences
					.getValue(PreferenceKey.FORCE_EMAIL_FROM.name()));
			email.setSubject(this.getInstance().getOggetto());
			email.addTo(this.getEmailTest());
			email.setMsg(this.instance.getBody());
			email.send();
			FacesMessages.instance().add(
					Severity.INFO,
					"Invio comunicazione di prova all'indirizzo "
							+ this.getEmailTest() + "avvenuto con successo");
			return "OK";
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			return "KO";
		}
	}

	public void setComunicatoId(Integer id) {
		this.setId(id);
	}

	public void setEmailTest(String emailTest) {
		this.emailTest = emailTest;
	}

	public boolean spedita() {
		if (this.getInstance().getStato() != null
				&& this.getInstance().getStato()
						.equals(StatoComunicato.SPEDITA)) {
			return true;
		}
		return false;
	}

	public String toBeSend() {
		this.getInstance().setStato(StatoComunicato.PRONTA);
		this.entityManager.merge(this.getInstance());
		return "OK";
	}

	public void wire() {
		this.getInstance();
	}

}
