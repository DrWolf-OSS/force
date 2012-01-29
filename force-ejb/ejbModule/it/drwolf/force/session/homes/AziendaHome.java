package it.drwolf.force.session.homes;

import it.drwolf.force.entity.Azienda;
import it.drwolf.force.entity.AziendaSoa;
import it.drwolf.force.entity.AziendaSoaId;
import it.drwolf.force.entity.ComunicazioneAziendaGara;
import it.drwolf.force.entity.ComunicazioneAziendaGaraId;
import it.drwolf.force.entity.Gara;
import it.drwolf.force.entity.Soa;
import it.drwolf.force.enums.ClassificaSoa;
import it.drwolf.force.enums.PosizioneCNA;
import it.drwolf.force.enums.StatoAzienda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

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
	private Identity identity;

	@In
	private EntityManager entityManager;

	private AziendaSoa aziendaSoa = null;

	private Soa soa = null;

	private ClassificaSoa classificaSoa = null;

	public void addSoa() {
		AziendaSoaId id = new AziendaSoaId(this.getInstance().getId(), this
				.getSoa().getId());
		AziendaSoa aziendaSoa = new AziendaSoa(id, this.getClassificaSoa()
				.getNome(), this.getInstance(), this.getSoa());
		this.entityManager.persist(aziendaSoa);
		this.getInstance().getSoa().add(aziendaSoa);
		this.entityManager.flush();
		this.cleanAziendaSoa();
	}

	private void cleanAziendaSoa() {
		this.aziendaSoa = null;
		this.soa = null;
		this.classificaSoa = null;
	}

	@Override
	protected Azienda createInstance() {
		Azienda azienda = new Azienda();
		return azienda;
	}

	public void deleteAziendaSoa(AziendaSoa aziendaSoa) {

	}

	public Integer getAziendaId() {
		return (Integer) this.getId();
	}

	public AziendaSoa getAziendaSoa() {
		return this.aziendaSoa;
	}

	public ClassificaSoa getClassificaSoa() {
		return this.classificaSoa;
	}

	public Azienda getDefinedInstance() {
		return this.isIdDefined() ? this.getInstance() : null;
	}

	public List<PosizioneCNA> getPosizioneCNA() {
		return Arrays.asList(PosizioneCNA.values());
	}

	public Soa getSoa() {
		return this.soa;
	}

	public boolean haveCM() {
		if (this.getInstance().getCategorieMerceologicheAsList().size() > 0) {
			return true;
		}
		return false;
	}

	public boolean haveSoa() {
		if (this.getInstance().getSoaAsList().size() > 0) {
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
				String body = "La ringraziamo per essersi registrato al servizio FORCE.\n";
				body += "Non appena completata la verifica dei dati inseriti riceverà le credenziali per accedere ai servizi forniti dalla piattaforma FORCE\n.";
				// una volta persistito mando una mail al referente
				this.sendEmail("Benvenuto in Force", body, this.getInstance()
						.getEmailReferente());
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

	public void mergeSoa() {
		this.aziendaSoa.setClassifica(this.getClassificaSoa().getNome());
		this.aziendaSoa.setSoa(this.getSoa());
		this.entityManager.merge(this.aziendaSoa);
		this.cleanAziendaSoa();
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

	public void setAziendaSoa(AziendaSoa aziendaSoa) {
		this.aziendaSoa = aziendaSoa;
		this.setSoa(aziendaSoa.getSoa());
		this.setClassificaSoa(ClassificaSoa.valueOf(aziendaSoa.getClassifica()));
	}

	public void setClassificaSoa(ClassificaSoa classificaSoa) {
		this.classificaSoa = classificaSoa;
	}

	public void setSoa(Soa soa) {
		this.soa = soa;
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

	public String updateSoa() {
		this.update();
		ArrayList<Gara> gare = (ArrayList<Gara>) this.entityManager
				.createQuery(
						"select distinct  g from Azienda a join a.Soa soa join soa.gare g  where g.stato = 'INSERITA' and a.id= :id")
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
