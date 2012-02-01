package it.drwolf.force.utils;

import it.drwolf.force.entity.Azienda;
import it.drwolf.slot.alfresco.AlfrescoInfo;
import it.drwolf.slot.alfresco.webscripts.AlfrescoWebScriptClient;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("userUtil")
@AutoCreate
@Scope(ScopeType.CONVERSATION)
public class UserUtil implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5699721558566989659L;

	@In
	EntityManager entityManager;

	@In(create = true)
	AlfrescoInfo alfrescoInfo;

	private String email = null;

	private String statoRecuperoEmail = null;

	public String getEmail() {
		return this.email;
	}

	public String getStatoRecuperoEmail() {
		return this.statoRecuperoEmail;
	}

	public String recuperaPassword() {
		try {
			Azienda azienda = (Azienda) this.entityManager
					.createQuery(
							"from Azienda a where a.emailReferente = :email")
					.setParameter("email", this.email).getSingleResult();
			// genero la nuova password
			if (azienda != null) {
				AlfrescoWebScriptClient awsc = new AlfrescoWebScriptClient(
						this.alfrescoInfo.getAdminUser(),
						this.alfrescoInfo.getAdminPwd(),
						this.alfrescoInfo.getRepositoryUri());
				String pwd = UUID.randomUUID().toString().substring(5, 13);
				String res = awsc.changePassword(azienda.getEmailReferente(),
						pwd);
				String newPwdMsg = "Gentile "
						+ azienda.getNome()
						+ " "
						+ azienda.getCognome()
						+ "\n"
						+ "la nuova sua nuova password per accedere al servizio FORCE è :\n\n"
						+ pwd + "\n";
				try {
					this.sendEmail("Nuova  password servizio FORCE", newPwdMsg,
							azienda.getEmailReferente());
				} catch (EmailException e) {
					e.printStackTrace();
					return "KO";
				}
				this.setStatoRecuperoEmail("OK");
				return "OK";
			}
		} catch (NoResultException e) {
			FacesMessages
					.instance()
					.add(Severity.ERROR,
							"Non è stata individuato nessun account collegato all'email inserita");
			this.setStatoRecuperoEmail("NO_RESULT");
			return "NO_RESULT";
		} catch (NonUniqueResultException e) {
		}
		FacesMessages.instance().add(Severity.ERROR,
				"Si è verificato un errore  nel cambio password");

		this.setStatoRecuperoEmail("NO_RESULT");
		return "NO_RESULT";
	}

	public String sendEmail(String subject, String body, String to)
			throws EmailException {
		Email email = new SimpleEmail();
		email.setHostName("zimbra.drwolf.it");
		email.setSmtpPort(25);
		email.setFrom("info@forcecna.it");
		email.setSubject(subject);
		email.addTo(to);
		email.setMsg(body);
		return email.send();
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setStatoRecuperoEmail(String statoRecuperoEmail) {
		this.statoRecuperoEmail = statoRecuperoEmail;
	}
}
