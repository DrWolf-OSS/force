package it.drwolf.force.session;

import it.drwolf.force.entity.Azienda;
import it.drwolf.force.entity.ComunicazioneAziendaGara;
import it.drwolf.slot.alfresco.AlfrescoInfo;
import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.alfresco.AlfrescoWrapper;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.entity.SlotInst;
import it.drwolf.slot.pagebeans.SlotInstEditBean;
import it.drwolf.slot.prefs.PreferenceKey;
import it.drwolf.slot.prefs.Preferences;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.alfresco.cmis.client.AlfrescoFolder;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Identity;

@Name("userSession")
@Scope(ScopeType.SESSION)
@AutoCreate
public class UserSession implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6324378307852452895L;

	@In
	private EntityManager entityManager;

	@In
	Identity identity;

	@In(create = true)
	AlfrescoUserIdentity alfrescoUserIdentity;

	@In(create = true)
	AlfrescoInfo alfrescoInfo;

	@In(create = true)
	private AlfrescoWrapper alfrescoWrapper;

	@In(create = true)
	SlotInstEditBean slotInstEditBean;

	@In(create = true)
	private Preferences preferences;

	private SlotDef primarySlotDef;

	private SlotInst primarySlotInst = null;

	private Azienda azienda;

	// mi tengo anche l'id dell'azienda per essere sicuro di poter accedere a
	// tutte gli attributi dell'azienda.
	// Essendo questo bean a scope session nel passare da una conversation ad un
	// altra non riesco ad accedere
	// agli attributi lazy
	private Integer aziendaId;

	private AlfrescoFolder groupFolder; // cartella di alfresco dell'azienda

	private AlfrescoFolder primarySlotFolder; // caretella di alfreso dello slot

	private boolean llpp = false;

	private boolean beni = false;

	private boolean servizi = false;

	public String checkStatus() {
		if (this.isLlpp() && (this.azienda.getSoa().size() == 0)
				&& (this.azienda.getCategorieMerceologiche().size() == 0)) {
			return "GO_TO_SOA";
		} else if (!this.isLlpp()
				&& (this.azienda.getCategorieMerceologiche().size() == 0)) {
			return "GO_TO_CM";
		} else if (this.getPrimarySlotInst() == null) {
			return "GO_TO_SLOTINST";
		}
		return "GO";
	}

	public Azienda getAzienda() {
		this.azienda = this.entityManager.find(Azienda.class,
				this.getAziendaId());
		return this.azienda;
	}

	public Integer getAziendaId() {
		return this.aziendaId;
	}

	public AlfrescoFolder getGroupFolder() {
		return this.groupFolder;
	}

	public SlotDef getPrimarySlotDef() {
		return this.primarySlotDef;
	}

	public AlfrescoFolder getPrimarySlotFolder() {
		if (this.primarySlotFolder == null) {
			AlfrescoFolder slotFolder = this.retriveSlotFolder();
			if (slotFolder != null) {
				this.setPrimarySlotFolder(slotFolder);
			}
		}
		return this.primarySlotFolder;
	}

	// primary dell'azienda

	public SlotInst getPrimarySlotInst() {
		if (this.primarySlotInst == null) {
			SlotInst slotInst = this.retriveSlotInst();
			if (slotInst != null) {
				this.setPrimarySlotInst(slotInst);
			}
		}
		return this.primarySlotInst;
	}

	public void init() {
		// devo prendere l'id dello slotdef associato
		Azienda azienda = (Azienda) this.entityManager
				.createQuery("from Azienda where emailReferente = :username")
				.setParameter("username",
						this.identity.getCredentials().getUsername())
				.getSingleResult();
		if (azienda.getSettore().getNome().equals("Lavori")) {
			this.setLlpp(true);
		} else if (azienda.getSettore().getNome().equals("Beni")) {
			this.setBeni(true);
		} else if (azienda.getSettore().getNome().equals("Servizi")) {
			this.setServizi(true);
		}
		// una volta individuata l'azienda devo vedere se ha delle
		// categorie merceologiche già settate o delle SOA
		if (this.llpp && (azienda.getSoa().size() == 0)) {
			// mando l'impresa alla pagina di edit delle SOA
		}
		this.setPrimarySlotDef(azienda.getSettore().getSlotDef());
		//
		// this.slotDefHome.setInstance(this.primarySlotDef);
		// /
		this.setAzienda(azienda);
		// SlotInst slonInst = this.retriveSlotInst();
		// prelevo la cartella principale:
		this.groupFolder = this.alfrescoWrapper.findOrCreateFolder(
				this.preferences.getValue(PreferenceKey.FORCE_GROUPS_PATH
						.name()), this.alfrescoUserIdentity.getActiveGroup()
						.getShortName());

		// PALA
		// Se il Primary SlotInst è stato creato il nodeRef della cartella è
		// settato on SlotInst.nodeRef
		if (this.primarySlotInst != null) {
			this.primarySlotFolder = this.retriveSlotFolder();
		}
	}

	//
	// @In(create = true)
	// SlotDefHome slotDefHome;

	//

	public boolean isBeni() {
		return this.beni;
	}

	public boolean isLlpp() {
		return this.llpp;
	}

	public boolean isServizi() {
		return this.servizi;
	}

	private AlfrescoFolder retriveSlotFolder() {
		if (this.getPrimarySlotInst() != null) {
			return (AlfrescoFolder) this.alfrescoUserIdentity.getSession()
					.getObject(this.getPrimarySlotInst().getNodeRef());
		}
		return null;
	}

	// PALA
	@SuppressWarnings("unchecked")
	private SlotInst retriveSlotInst() {
		List<SlotInst> resultList = this.entityManager
				.createQuery(
						"from SlotInst where slotDef = :slotDef and ownerId = :ownerId order by id desc")
				.setParameter("slotDef",
						this.getAzienda().getSettore().getSlotDef())
				.setParameter("ownerId", this.getAzienda().getAlfrescoGroupId())
				.getResultList();
		if (resultList != null && resultList.size() > 0) {
			if (resultList.size() > 1) {
				System.out
						.println("---> WARNING: Il gruppo "
								+ this.getAzienda().getAlfrescoGroupId()
								+ " ha "
								+ resultList.size()
								+ " istanze di SlotInst associate allo SlotDef di tipo primary con nome \""
								+ this.getAzienda().getSettore().getSlotDef()
										.getName()
								+ "\" e id "
								+ this.getAzienda().getSettore().getSlotDef()
										.getId());
			}
			return resultList.get(0);
		}
		return null;
	}

	public void setAzienda(Azienda azienda) {
		this.azienda = azienda;
		// quando setto l'azienda mi setto anche l'id
		this.setAziendaId(azienda.getId());
	}

	public void setAziendaId(Integer aziendaId) {
		this.aziendaId = aziendaId;
	}

	public void setBeni(boolean beni) {
		this.beni = beni;
	}

	// Lo SlotInst può essere null all'inizo della Session ed essere
	// visualizzato in seguito. In realtà potrebbe anche essere modificata
	// l'istanza collegata nel corso della session (non basta controllare se
	// null, potrebbe essere un'istanza cancellata e andrebbe aggiornata)
	// private SlotInst retrievePrimarySlotInst() {
	// SlotInst slonInst = null;
	// try {
	// slonInst = (SlotInst) this.entityManager
	// .createQuery(
	// "from SlotInst where slotDef = :slotDef and ownerId = :ownerId")
	// .setParameter("slotDef", azienda.getSettore().getSlotDef())
	// .setParameter("ownerId", azienda.getAlfrescoGroupId())
	// .getSingleResult();
	// if (slonInst != null) {
	// this.setPrimarySlotInst(slonInst);
	// }
	// } catch (Exception e) {
	// // Non è ancora stato creato uno slotInst
	// }
	// }

	public void setGaraVisisted(Integer gara_id) {
		List<ComunicazioneAziendaGara> resultList = this.entityManager
				.createQuery(
						"from ComunicazioneAziendaGara cag where cag.id.aziendaId = :a and cag.id.garaId= :g")
				.setParameter("a", this.azienda.getId())
				.setParameter("g", gara_id).setMaxResults(1).getResultList();
		if (resultList.size() > 0) {
			ComunicazioneAziendaGara cag = resultList.get(0);
			cag.setWeb(true);
			this.entityManager.merge(cag);
			this.entityManager.flush();
		}
	}

	public void setGroupFolder(AlfrescoFolder groupFolder) {
		this.groupFolder = groupFolder;
	}

	public void setLlpp(boolean llpp) {
		this.llpp = llpp;
	}

	public void setPrimarySlotDef(SlotDef primarySlotDef) {
		this.primarySlotDef = primarySlotDef;
	}

	public void setPrimarySlotFolder(AlfrescoFolder primarySlotFolder) {
		this.primarySlotFolder = primarySlotFolder;
	}

	public void setPrimarySlotInst(SlotInst primarySlotInst) {
		this.primarySlotInst = primarySlotInst;
	}

	public void setServizi(boolean servizi) {
		this.servizi = servizi;
	}
}
