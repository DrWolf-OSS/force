package it.drwolf.force.session;

import it.drwolf.force.entity.Azienda;
import it.drwolf.force.enums.PosizioneCNA;
import it.drwolf.force.enums.StatoAzienda;
import it.drwolf.force.session.lists.AziendaList;
import it.drwolf.slot.alfresco.AlfrescoInfo;
import it.drwolf.slot.alfresco.AlfrescoWrapper;
import it.drwolf.slot.alfresco.webscripts.AlfrescoWebScriptClient;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.alfresco.cmis.client.AlfrescoFolder;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("adminUserSession")
@Scope(ScopeType.SESSION)
@AutoCreate
public class AdminUserSession implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8437341415271744919L;

	@In(create = true)
	AlfrescoInfo alfrescoInfo;

	@In(create = true)
	AlfrescoWrapper alfrescoWrapper;

	@In(create = true)
	AziendaList aziendaList;

	@In
	EntityManager entityManager;

	public String attivaAzienda(Azienda azienda) {
		try {

			// creazione del nome del gruppo
			String groupName = azienda.getRagioneSociale().replaceAll("\\s",
					"_");
			groupName = groupName + "_" + azienda.getId();

			AlfrescoWebScriptClient awsc = new AlfrescoWebScriptClient(
					this.alfrescoInfo.getAdminUser(),
					this.alfrescoInfo.getAdminPwd(),
					this.alfrescoInfo.getRepositoryUri());

			String res = awsc.addGroupOrUser("aziende", groupName, true);

			// creo la cartella groupName sotto il progetto Force
			AlfrescoFolder alfrescoGroupFolder = this.alfrescoWrapper
					.findOrCreateFolder(
							this.alfrescoWrapper.getMainProjectFolder(),
							groupName);

			String pwd = UUID.randomUUID().toString().substring(5, 13);
			// creo l'utente con l'email del referente
			HashMap<String, String> args = new HashMap<String, String>();
			args.put("userName", azienda.getEmailReferente());
			args.put("firstName", azienda.getNome());
			args.put("lastName", azienda.getCognome());
			args.put("email", azienda.getEmailReferente());
			args.put("password", pwd);
			awsc.addPerson(args);

			// Aggiungo l'utente al gruppo creato in precedenza
			awsc.addGroupOrUser(groupName, azienda.getEmailReferente(), false);

			// Aggiungo i provilegi di collaboratore all'utente creato sulla
			// cartella
			// groupName
			this.alfrescoWrapper.applyACL(alfrescoGroupFolder, "GROUP_"
					+ groupName);

			// se l'azienda � amministrata gli utenti del gruppo "CNA" devono i
			// permessi sulla cartella
			if (azienda.getPosizioneCNA() == PosizioneCNA.AMMINISTRATA
					.toString()) {
				this.alfrescoWrapper.applyACL(alfrescoGroupFolder, "GROUP_CNA");

			}
			// alla fine dovrei mandare una mail con gli accessi al referente
			azienda.setStato(StatoAzienda.ATTIVA.getNome());
			azienda.setAlfrescoGroupId(groupName);
			this.entityManager.persist(azienda);
			// mando la mail con i dati
			String body = new String();
			body = "Gentile " + azienda.getNome() + " " + azienda.getCognome()
					+ "\n";
			body += "ecco le credenziali per accedere al servizio ai servizi forniti dalla piattaforma FORCE:";
			body += "Username : " + azienda.getEmailReferente() + "\n";
			body += "Password : " + pwd + "\n";
			body += "Per completare la registrazione e rendere il servizio funzionante sono necessari ancora alcuni passi.\n";
			body += "Una volta effettuato il primo login al seguente indirizzo:\n";
			body += "http://forcecna.it\n\n";
			if (azienda.isEdile()) {
				body += "Dovrete indicare le attestazioni SOA di cui l’azienda è in possesso o, in alternativa, indicare le attestazioni SOA che corrispondono all’attività svolta. In tal modo verrà creato un filtro che le permetterà di ricevere informazioni solo su gare e bandi di effettivo interesse.\n";
			} else {
				body += "Dovrete Inserire le Categorie Merceologiche per le quali è interessato a ricevere  informazioni su gare e bandi.\n";
			}
			this.sendEmail("Conferma iscrizione FORCE", body,
					azienda.getEmailReferente());
			// terimata la creazione devo mettere a null la lista delle aziende
			// e attive e delle nuove per rigenerare la Factory
			this.aziendaList.setAziendeAttive(null);
			this.aziendaList.setAziendeNuove(null);
			return "ok";

		} catch (Exception e) {
			e.printStackTrace();
			return "KO";
		}
	}

	public void init() {
		// per non faccio niente
		// poi vediamo
	}

	private void sendEmail(String subject, String body, String to)
			throws EmailException {
		Email email = new SimpleEmail();
		email.setHostName("zimbra.drwolf.it");
		email.setSmtpPort(25);
		email.setFrom("info@forcecna.it");
		email.setSubject(subject);
		email.addTo(to);
		email.setMsg(body);
		email.send();
	}
}
