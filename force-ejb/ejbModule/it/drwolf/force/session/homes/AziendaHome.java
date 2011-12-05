package it.drwolf.force.session.homes;

import it.drwolf.force.entity.Azienda;
import it.drwolf.force.entity.ComunicazioneAziendaGara;
import it.drwolf.force.entity.ComunicazioneAziendaGaraId;
import it.drwolf.force.entity.Gara;
import it.drwolf.force.enums.PosizioneCNA;
import it.drwolf.force.enums.StatoAzienda;
import it.drwolf.slot.alfresco.AlfrescoInfo;
import it.drwolf.slot.alfresco.AlfrescoWrapper;
import it.drwolf.slot.alfresco.webscripts.AlfrescoWebScriptClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.alfresco.cmis.client.AlfrescoFolder;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.security.Identity;

@Name("aziendaHome")
public class AziendaHome extends EntityHome<Azienda> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1214123840587373460L;

	@In
	Identity identity;

	@In(create = true)
	AlfrescoInfo alfrescoInfo;

	@In(create = true)
	AlfrescoWrapper alfrescoWrapper;

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
			// forse dovrei settare la password dato che di default � a
			// "password"?
			// args.put("password", "");
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
			body = "Conferma iscrizione\n";
			body += "Username : " + azienda.getEmailReferente() + "\n";
			body += "Password : " + pwd + "\n";
			this.sendEmail("Progetto force - Conferma iscrizione", body,
					azienda.getEmailReferente());
			return "ok";

		} catch (Exception e) {
			e.printStackTrace();
			return "KO";
		}
	}

	@Override
	protected Azienda createInstance() {
		Azienda azienda = new Azienda();
		return azienda;
	}

	public Integer getAziendaId() {
		return (Integer) this.getId();
	}

	public Azienda getDefinedInstance() {
		return this.isIdDefined() ? this.getInstance() : null;
	}

	public List<PosizioneCNA> getPosizioneCNA() {
		return Arrays.asList(PosizioneCNA.values());
	}

	public boolean haveCM() {
		if (this.getInstance().getCategorieMerceologicheAsList().size() > 0) {
			return true;
		}
		return false;
	}

	public boolean haveSoa() {
		if (this.getInstance().getSOAAsList().size() > 0) {
			return true;
		}
		return false;
	}

	public String inserisciAzienda() {
		try {
			this.getInstance().setStato(StatoAzienda.NUOVA.toString());
			// persisto l'entity azienda
			this.persist();
			try {
				String body = "Grazie per esserti registrato al servizio FORCE.\nA breve riceverai le  credenziali per accedere al servizio, appena concluse le pratiche di verifica dei dati inseriti.";
				// una volta persistito mando una mail al referente
				this.sendEmail("Benvenuto nel servizio Force", body, this
						.getInstance().getEmailReferente());
			} catch (EmailException e) {
				// TODO: handle exception
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "KO";
		}

		return "OK";

	}

	public boolean isWired() {
		return true;
	}

	public void load() {
		if (this.isIdDefined()) {
			this.wire();
		}
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

	public void setAziendaId(Integer id) {
		this.setId(id);
	}

	public String updateCategorieMerceologiche() {
		this.update();
		ArrayList<Gara> gare = (ArrayList<Gara>) this.entityManager
				.createQuery(
						"select distinct  g from Azienda a join a.categorieMerceologiche cm join cm.gare g where g.stato = 'INSERITA' and a.id= :id")
				.setParameter("id", this.getInstance().getId()).getResultList();
		// per ogni gara devo controllare se è già presente nelle comunicaiozni.
		// Se non lo è la inserisco
		for (Gara gara : gare) {
			ComunicazioneAziendaGaraId id = new ComunicazioneAziendaGaraId(this
					.getInstance().getId(), gara.getId());
			ComunicazioneAziendaGara cag = this.entityManager.find(
					ComunicazioneAziendaGara.class, id);
			if (cag == null) {
				cag = new ComunicazioneAziendaGara(id, false, false,
						this.getInstance(), gara);
				this.entityManager.persist(cag);
			}

		}
		return "updated";
	}

	public String updateSOA() {
		this.update();
		ArrayList<Gara> gare = (ArrayList<Gara>) this.entityManager
				.createQuery(
						"select distinct  g from Azienda a join a.SOA soa join soa.gare g  where g.stato = 'INSERITA' and a.id= :id")
				.setParameter("id", this.getInstance().getId()).getResultList();
		// per ogni gara devo controllare se è già presente nelle comunicaiozni.
		// Se non lo è la inserisco
		for (Gara gara : gare) {
			ComunicazioneAziendaGaraId id = new ComunicazioneAziendaGaraId(this
					.getInstance().getId(), gara.getId());
			ComunicazioneAziendaGara cag = this.entityManager.find(
					ComunicazioneAziendaGara.class, id);
			if (cag == null) {
				cag = new ComunicazioneAziendaGara(id, false, false,
						this.getInstance(), gara);
				this.entityManager.persist(cag);
			}

		}
		return "updated";
	}

	public void wire() {
		this.getInstance();
	}
}
