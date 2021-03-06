package it.drwolf.force.session.homes;

import it.drwolf.force.entity.Azienda;
import it.drwolf.force.entity.ComunicazioneAziendaGara;
import it.drwolf.force.entity.Gara;
import it.drwolf.force.enums.TipoFonte;
import it.drwolf.force.enums.TipoGara;
import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.entity.SlotInst;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("garaHome")
public class GaraHome extends EntityHome<Gara> {

	private static final long serialVersionUID = -1214123840587373460L;

	@In
	EntityManager entityManager;

	@In
	private AlfrescoUserIdentity alfrescoUserIdentity;

	public String attivaGara() {
		this.getInstance().setType(TipoGara.GESTITA.getNome());
		this.persist();
		return "OK";
	}

	@Override
	protected Gara createInstance() {
		Gara gara = new Gara();
		return gara;
	}

	public SlotDef getAssociatedSlotDef() {
		// Identity identity = this.getIdentity();
		// Iterator<SlotDef> iterator = this.getInstance().getSlotDefs()
		// .iterator();
		//
		// String ownerId = null;
		// if (identity.hasRole("ADMIN")
		// // OR il cliente è un cliente che sta pagando
		// ) {
		// ownerId = "ADMIN";
		// } else {
		// ownerId = this.alfrescoUserIdentity.getActiveGroup().getShortName();
		// }
		//
		// while (iterator.hasNext()) {
		// SlotDef slotDef = iterator.next();
		// if (slotDef.getOwnerId().equals(ownerId)) {
		// return slotDef;
		// }
		// }
		// return null;
		if (this.getInstance() != null) {
			return this.getInstance().getAssociatedSlotDef();
		}
		return null;
	}

	// Anche se ce ne sono più di uno restituisco l'ultimo istanziato
	@SuppressWarnings("unchecked")
	public SlotInst getAssociatedSlotInst() {
		SlotDef associatedSlotDef = this.getAssociatedSlotDef();
		if (associatedSlotDef != null) {
			List<SlotInst> resultList = this.entityManager
					.createQuery(
							"from SlotInst s where s.slotDef=:slotDef and s.ownerId=:ownerId order by s.id desc")
					.setParameter("slotDef", associatedSlotDef)
					.setParameter("ownerId",
							this.alfrescoUserIdentity.getMyOwnerId())
					.getResultList();
			if (!resultList.isEmpty()) {
				return resultList.get(0);
			}
		}
		return null;
	}

	public Gara getDefinedInstance() {
		return this.isIdDefined() ? this.getInstance() : null;
	}

	public Integer getGaraId() {
		return (Integer) this.getId();
	}

	public boolean isFromAVCP() {
		if (this.getInstance().getFonte().getTipo()
				.equals(TipoFonte.AVCP.name())) {
			return true;
		}
		return false;

	}

	public boolean isRequestSend(Azienda azienda) {
		if (azienda != null) {
			for (ComunicazioneAziendaGara cag : this.getInstance().getAziende()) {
				if (cag.getAzienda().equals(azienda)) {
					return cag.isBustaRequest();
				}
			}
		}
		return false;
	}

	public boolean isWired() {
		return true;
	}

	public void load() {
		if (this.isIdDefined()) {
			this.wire();
		}
	}

	@Override
	public String persist() {
		return super.persist();
	}

	public String scartaGara() {
		this.getInstance().setType(TipoGara.SCARTATA.getNome());
		this.persist();
		return "OK";
	}

	// invia una richista all'amministratore di richiesta di creazione di una
	// busta amministrativa
	// il metodo da per scontato che l'azienda richiedente possa effettuara la
	// richiesta (PREMIUM)
	public void sendBustaRequest(Azienda azienda) {
		if (azienda != null) {
			for (ComunicazioneAziendaGara cag : this.getInstance().getAziende()) {
				if (cag.getAzienda().equals(azienda)) {
					cag.setBustaRequest(true);
				}
			}
			// Manda un mail all'amministratore con la richiesta di creaiozne
			// della
			// busta.
			// deve essere anche mantenuta traccia sul db?
		}

	}

	public void setGaraId(Integer id) {
		this.setId(id);
	}

	public void wire() {
		this.getInstance();
	}

}
