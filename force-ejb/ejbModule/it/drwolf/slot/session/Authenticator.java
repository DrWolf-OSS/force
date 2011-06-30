package it.drwolf.slot.session;

import it.drwolf.force.entity.Azienda;
import it.drwolf.slot.alfresco.AlfrescoInfo;
import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.alfresco.webscripts.AlfrescoWebScriptClient;
import it.drwolf.slot.alfresco.webscripts.model.Authority;
import it.drwolf.slot.alfresco.webscripts.model.AuthorityType;
import it.drwolf.slot.entity.SlotInst;
import it.drwolf.slot.entitymanager.PreferenceManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

@Name("authenticator")
@Scope(ScopeType.SESSION)
public class Authenticator {
	@Logger
	private Log log;

	@In
	Identity identity;
	@In
	Credentials credentials;

	@In(create = true)
	AlfrescoUserIdentity alfrescoUserIdentity;

	@In(create = true)
	AlfrescoInfo alfrescoInfo;

	@In(create = true)
	PreferenceManager preferenceManager;

	@In
	EntityManager entityManager;

	private Long slotDefId;

	private Long slotInstId;

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

	public boolean authenticate() {
		this.log.info("authenticating {0}", this.credentials.getUsername());
		// write your authentication logic here,
		// return true if the authentication was
		// successful, false otherwise
		// if ("admin".equals(credentials.getUsername())) {
		// identity.addRole("admin");
		// return true;
		// }

		if (this.identity.getCredentials().getUsername().equals("")) {
			return false;
		}

		if (this.identity.getCredentials().getPassword().equals("")) {
			return false;
		}

		this.alfrescoUserIdentity.authenticate(this.identity.getCredentials()
				.getUsername(), this.identity.getCredentials().getPassword(),
				this.alfrescoInfo.getRepositoryUri());

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
					Long slotDefId = azienda.getSettore().getSlotDef().getId();
					this.setSlotDefId(slotDefId);
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
							this.setSlotInstId(slonInst.getId());
						}
					} catch (Exception e) {
						// Non è ancora stato creato uno slotInst
					}
					return true;
				}
			}

		}

		return true;
	}

	public Long getSlotDefId() {
		return this.slotDefId;
	}

	public Long getSlotInstId() {
		return this.slotInstId;
	}

	public void setSlotDefId(Long slotDefId) {
		this.slotDefId = slotDefId;
	}

	public void setSlotInstId(Long slotInstId) {
		this.slotInstId = slotInstId;
	}

}
