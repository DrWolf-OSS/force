package it.drwolf.slot.session;

import it.drwolf.slot.alfresco.AlfrescoInfo;
import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.alfresco.webscripts.AlfrescoWebScriptClient;
import it.drwolf.slot.alfresco.webscripts.model.Authority;
import it.drwolf.slot.alfresco.webscripts.model.AuthorityType;
import it.drwolf.slot.entitymanager.PreferenceManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

@Name("authenticator")
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
		} else if (this.alfrescoUserIdentity.isMemberOf(this.preferenceManager
				.getPreference("FORCE_USER_GROUP").getStringValue())) {
			this.identity.addRole("AZIENDE");
		} else {

		}

		return true;
	}

}
