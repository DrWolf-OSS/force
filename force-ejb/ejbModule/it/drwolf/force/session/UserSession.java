package it.drwolf.force.session;

import it.drwolf.force.entity.Azienda;
import it.drwolf.slot.alfresco.AlfrescoInfo;
import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.alfresco.AlfrescoWrapper;
import it.drwolf.slot.alfresco.webscripts.AlfrescoWebScriptClient;
import it.drwolf.slot.alfresco.webscripts.model.Authority;
import it.drwolf.slot.alfresco.webscripts.model.AuthorityType;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.entity.SlotInst;
import it.drwolf.slot.entitymanager.PreferenceManager;
import it.drwolf.slot.pagebeans.SlotInstEditBean;
import it.drwolf.slot.prefs.PreferenceKey;
import it.drwolf.slot.prefs.Preferences;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
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
	PreferenceManager preferenceManager;

	@In(create = true)
	private AlfrescoWrapper alfrescoWrapper;

	@In(create = true)
	SlotInstEditBean slotInstEditBean;

	@In(create = true)
	private Preferences preferences;

	private SlotDef primarySlotDef;

	private SlotInst primarySlotInst;

	private Integer aziendaId;

	private AlfrescoFolder groupFolder; // cartella di alfresco dell'azienda

	private AlfrescoFolder primarySlotFolder; // caretella di alfreso dello slot
												// primary dell'azienda

	private boolean llpp = false;

	//
	// @In(create = true)
	// SlotDefHome slotDefHome;

	//

	private void assignGroups() {
		AlfrescoWebScriptClient alfrescoWebScriptClient = new AlfrescoWebScriptClient(
				this.identity.getCredentials().getUsername(), this.identity
						.getCredentials().getPassword(),
				this.alfrescoInfo.getRepositoryUri());

		List<Authority> userGroups = new ArrayList<Authority>();
		List<Authority> groups = alfrescoWebScriptClient.getGroupsList("*", "");
		for (Authority group : groups) {
			List<Authority> users = alfrescoWebScriptClient
					.getListOfChildAuthorities(group.getShortName(),
							AuthorityType.USER.name());
			Iterator<Authority> iterator = users.iterator();
			boolean found = false;
			while (iterator.hasNext() && (found == false)) {
				Authority user = iterator.next();
				if (user.getShortName().equals(
						this.identity.getCredentials().getUsername())) {
					userGroups.add(group);
					found = true;
				}
			}
		}
		this.alfrescoUserIdentity.setGroups(userGroups);

		// SETTO A MERDA il primo della lista come activeGroup
		this.alfrescoUserIdentity.setActiveGroup(userGroups.get(0));
		System.out.println("---> "
				+ this.identity.getCredentials().getUsername()
				+ " entered as \""
				+ this.alfrescoUserIdentity.getActiveGroup().getShortName()
				+ "\" member");
	}

	public Azienda getAzienda() {
		return (Azienda) this.entityManager
				.createQuery("from Azienda where id = :id")
				.setParameter("id", this.getAziendaId()).getSingleResult();
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
		return this.primarySlotFolder;
	}

	public SlotInst getPrimarySlotInst() {
		return this.primarySlotInst;
	}

	public void init() {
		this.assignGroups();
		if (this.alfrescoUserIdentity.isMemberOf(this.preferenceManager
				.getPreference("FORCE_ADMIN").getStringValue())) {
			this.identity.addRole("ADMIN");
		} else {
			AlfrescoWebScriptClient awsc = new AlfrescoWebScriptClient(
					this.alfrescoInfo.getAdminUser(),
					this.alfrescoInfo.getAdminPwd(),
					this.alfrescoInfo.getRepositoryUri());
			List<Authority> lista = awsc.getListOfChildAuthorities(
					this.preferenceManager.getPreference("FORCE_USER_GROUP")
							.getStringValue(), "GROUP");
			for (Authority authority : lista) {
				if (authority.getShortName().equals(
						this.alfrescoUserIdentity.getActiveGroup()
								.getShortName())) {
					this.identity.addRole("AZIENDE");
					// devo prendere l'id dello slotdef associato
					Azienda azienda = (Azienda) this.entityManager
							.createQuery(
									"from Azienda where emailReferente = :username")
							.setParameter(
									"username",
									this.identity.getCredentials()
											.getUsername()).getSingleResult();
					this.setPrimarySlotDef(azienda.getSettore().getSlotDef());
					//
					// this.slotDefHome.setInstance(this.primarySlotDef);
					// /
					this.setAziendaId(azienda.getId());
					if (azienda.getSettore().getNome().equals("Edilizia")) {
						this.setLlpp(true);
					}
					SlotInst slonInst;
					try {
						slonInst = (SlotInst) this.entityManager
								.createQuery(
										"from SlotInst where slotDef = :slotDef and ownerId = :ownerId")
								.setParameter("slotDef",
										azienda.getSettore().getSlotDef())
								.setParameter("ownerId",
										azienda.getAlfrescoGroupId())
								.getSingleResult();
						if (slonInst != null) {
							this.setPrimarySlotInst(slonInst);
						}
					} catch (Exception e) {
						// Non è ancora stato creato uno slotInst
						System.out
								.println("---> Più di uno slot inst primary!!!!!");
					}
					break;
				}

			}
			// prelevo la cartella principale:
			this.groupFolder = this.alfrescoWrapper.findOrCreateFolder(
					this.preferences.getValue(PreferenceKey.FORCE_GROUPS_PATH
							.name()), this.alfrescoUserIdentity
							.getActiveGroup().getShortName());

			// PALA
			// Se il Primary SlotInst è stato creato il nodeRef della cartella è
			// settato on SlotInst.nodeRef
			if (this.primarySlotInst != null) {
				this.primarySlotFolder = (AlfrescoFolder) this.alfrescoUserIdentity
						.getSession().getObject(
								this.primarySlotInst.getNodeRef());
			}
		}

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

	public boolean isLlpp() {
		return this.llpp;
	}

	public void setAziendaId(Integer aziendaId) {
		this.aziendaId = aziendaId;
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
