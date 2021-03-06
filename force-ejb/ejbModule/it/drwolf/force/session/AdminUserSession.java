package it.drwolf.force.session;

import it.drwolf.force.entity.Azienda;
import it.drwolf.force.entity.Gara;
import it.drwolf.force.enums.LogType;
import it.drwolf.force.enums.PosizioneCNA;
import it.drwolf.force.enums.StatoAzienda;
import it.drwolf.force.session.lists.AziendaList;
import it.drwolf.force.utils.UserUtil;
import it.drwolf.slot.alfresco.AlfrescoInfo;
import it.drwolf.slot.alfresco.AlfrescoWrapper;
import it.drwolf.slot.alfresco.webscripts.AlfrescoWebScriptClient;
import it.drwolf.slot.prefs.PreferenceKey;
import it.drwolf.slot.prefs.Preferences;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.alfresco.cmis.client.AlfrescoFolder;
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

	@In(create = true)
	private Preferences preferences;

	@In
	private UserUtil userUtil;

	@In
	EntityManager entityManager;

	public String attivaAzienda(Azienda azienda) {
		try {

			// creazione del nome del gruppo
			String groupName = azienda.getRagioneSociale().replaceAll("\\s",
					"_");
			groupName = groupName + "_" + azienda.getId();
			groupName = AlfrescoWrapper.normalizeFolderName(groupName,
					groupName.length(), "_");

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

			// se l'azienda ??? amministrata gli utenti del gruppo "CNA" devono i
			// permessi sulla cartella
			if (azienda.getPosizioneCNA() == PosizioneCNA.AMMINISTRATA
					.toString()) {
				this.alfrescoWrapper.applyACL(alfrescoGroupFolder, "GROUP_CNA");

			}
			// alla fine dovrei mandare una mail con gli accessi al referente
			azienda.setStato(StatoAzienda.ATTIVA);
			azienda.setDataAttivazione(new Date());
			azienda.setAlfrescoGroupId(groupName);
			this.entityManager.persist(azienda);
			// mando la mail con i dati
			String body = new String();
			body = "Gentile " + azienda.getNome() + " " + azienda.getCognome()
					+ "\n";
			body += "ecco le credenziali per accedere ai servizi forniti dalla piattaforma FORCE:\n";
			body += "Username : " + azienda.getEmailReferente() + "\n";
			body += "Password : " + pwd + "\n";
			body += "Per completare la registrazione e rendere il servizio funzionante sono necessari ancora alcuni passi.\n";
			body += "Una volta effettuato il primo login al seguente indirizzo:\n";
			body += "http://forcecna.it\n\n";
			if (azienda.isEdile()) {
				body += "Dovrete indicare le attestazioni SOA di cui l'azienda ?? in possesso o, in alternativa, indicare le attestazioni SOA che corrispondono all???attivit?? svolta. In tal modo verr?? creato un filtro che le permetter?? di ricevere informazioni solo su gare e bandi di effettivo interesse.\n\n";
			} else {
				body += "Dovrete Inserire le Categorie Merceologiche per le quali ?? interessato a ricevere  informazioni su gare e bandi.\n\n";
			}
			body += "Per avere maggiori informazioni sulla procedura di registrazione e configurazione dell\'account FORCE vi inviatiamo a prendere visione della guida, scaricabile al seguente indirizzo:\n";
			body += "http://www.forcecna.it/force/Manuale.pdf";
			body += "\n\n";
			body += "Lo Staff di FORCE\n";
			this.userUtil.sendEmail("Conferma iscrizione FORCE", body, azienda
					.getEmailReferente(), this.preferences
					.getValue(PreferenceKey.FORCE_EMAIL_HOSTNAME.name()),
					this.preferences.getValue(PreferenceKey.FORCE_EMAIL_FROM
							.name()));
			this.userUtil.logMe(azienda, this.getClass().getSimpleName(),
					"Azienda attivata con successo", "attivaAzienda", null,
					LogType.INFO);
			// terimata la creazione devo mettere a null la lista delle aziende
			// e attive e delle nuove per rigenerare la Factory
			this.aziendaList.setAziendeAttive(null);
			this.aziendaList.setAziendeNuove(null);
			return "ok";

		} catch (Exception e) {
			this.userUtil.logMe(azienda, this.getClass().getSimpleName(),
					"Errore nell'attivazione dell'azienda", "inserisciAzienda",
					e.getMessage(), LogType.ERROR);
			return "KO";
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Gara> getBusteRequest() {
		ArrayList<Gara> requests = (ArrayList<Gara>) this.entityManager
				.createQuery(
						"select distinct(g) from ComunicazioneAziendaGara cag join cag.gara g left join g.slotDefs sd where cag.bustaRequest = true and (sd.ownerId !='ADMIN' or sd is null)")
				.getResultList();
		return requests;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Azienda> getBusteRequestByGara(Gara g) {
		ArrayList<Azienda> requests = (ArrayList<Azienda>) this.entityManager
				.createQuery(
						"select cag.azienda from ComunicazioneAziendaGara cag where cag.bustaRequest = true and cag.gara = :gara")
				.setParameter("gara", g).getResultList();
		return requests;
	}

	public void init() {
		// per non faccio niente
		// poi vediamo
	}

}
