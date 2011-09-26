package it.drwolf.force.session;

import it.drwolf.force.entity.Azienda;
import it.drwolf.slot.alfresco.AlfrescoInfo;
import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.alfresco.AlfrescoWrapper;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.entity.SlotInst;
import it.drwolf.slot.pagebeans.SlotInstEditBean;
import it.drwolf.slot.prefs.PreferenceKey;
import it.drwolf.slot.prefs.Preferences;

import java.io.Serializable;

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

	private AlfrescoFolder groupFolder; // cartella di alfresco dell'azienda

	private AlfrescoFolder primarySlotFolder; // caretella di alfreso dello slot
												// primary dell'azienda

	private boolean llpp = false;

	public String checkStatus() {
		if (this.isLlpp() && (this.azienda.getSOA().size() == 0)) {
			return "GO_TO_SOA";
		} else if (!this.isLlpp()
				&& (this.azienda.getCategorieMerceologiche().size() == 0)) {
			return "GO_TO_CM";
		} else if (this.getPrimarySlotInst() == null) {
			return "GO_TO_SLOTINST";
		}
		return "GO";
	}

	//
	// @In(create = true)
	// SlotDefHome slotDefHome;

	//

	public Azienda getAzienda() {
		return this.azienda;
	}

	public AlfrescoFolder getGroupFolder() {
		return this.groupFolder;
	}

	public SlotDef getPrimarySlotDef() {
		return this.primarySlotDef;
	}

	public AlfrescoFolder getPrimarySlotFolder() {
		return this.primarySlotFolder;
	}

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
		if (azienda.getSettore().getNome().equals("Edilizia")) {
			this.setLlpp(true);
		}
		// una volta individuata l'azienda devo vedere se ha delle
		// categorie merceologiche già settate o delle SOA
		if (this.llpp && (azienda.getSOA().size() == 0)) {
			// mando l'impresa alla pagina di edit delle SOA
		}
		this.setPrimarySlotDef(azienda.getSettore().getSlotDef());
		//
		// this.slotDefHome.setInstance(this.primarySlotDef);
		// /
		this.setAzienda(azienda);
		SlotInst slonInst = this.retriveSlotInst();
		// prelevo la cartella principale:
		this.groupFolder = this.alfrescoWrapper.findOrCreateFolder(
				this.preferences.getValue(PreferenceKey.FORCE_GROUPS_PATH
						.name()), this.alfrescoUserIdentity.getActiveGroup()
						.getShortName());

		// PALA
		// Se il Primary SlotInst è stato creato il nodeRef della cartella è
		// settato on SlotInst.nodeRef
		if (this.primarySlotInst != null) {
			this.primarySlotFolder = (AlfrescoFolder) this.alfrescoUserIdentity
					.getSession().getObject(this.primarySlotInst.getNodeRef());
		}
	}

	public boolean isLlpp() {
		return this.llpp;
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

	private SlotInst retriveSlotInst() {
		SlotInst slonInst = null;
		try {
			slonInst = (SlotInst) this.entityManager
					.createQuery(
							"from SlotInst where slotDef = :slotDef and ownerId = :ownerId")
					.setParameter("slotDef",
							this.getAzienda().getSettore().getSlotDef())
					.setParameter("ownerId",
							this.getAzienda().getAlfrescoGroupId())
					.getSingleResult();
			if (slonInst != null) {
				this.setPrimarySlotInst(slonInst);
			}
		} catch (Exception e) {
			// Non è ancora stato creato uno slotInst
			System.out.println("---> Più di uno slot inst primary!!!!!");
		}
		return slonInst;
	}

	public void setAzienda(Azienda azienda) {
		this.azienda = azienda;
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
}
