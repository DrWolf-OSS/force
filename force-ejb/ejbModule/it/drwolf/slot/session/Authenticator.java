package it.drwolf.slot.session;

import it.drwolf.slot.alfresco.AlfrescoInfo;
import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.alfresco.webscripts.AlfrescoWebScriptClient;
import it.drwolf.slot.alfresco.webscripts.model.Authority;
import it.drwolf.slot.alfresco.webscripts.model.AuthorityType;

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

	public boolean authenticate() {
		log.info("authenticating {0}", credentials.getUsername());
		// write your authentication logic here,
		// return true if the authentication was
		// successful, false otherwise
		// if ("admin".equals(credentials.getUsername())) {
		// identity.addRole("admin");
		// return true;
		// }

		if (identity.getCredentials().getUsername().equals(""))
			return false;

		if (identity.getCredentials().getPassword().equals(""))
			return false;

		alfrescoUserIdentity.authenticate(identity.getCredentials()
				.getUsername(), identity.getCredentials().getPassword(),
				alfrescoInfo.getRepositoryUri());

		assignGroups();

		return true;
	}

	private void assignGroups() {
		AlfrescoWebScriptClient alfrescoWebScriptClient = new AlfrescoWebScriptClient(
				identity.getCredentials().getUsername(), identity
						.getCredentials().getPassword(),
				alfrescoInfo.getRepositoryUri());

		List<Authority> userGroups = new ArrayList<Authority>();
		List<Authority> groups = alfrescoWebScriptClient.getGroupsList("*", "");
		for (Authority group : groups) {
			List<Authority> users = alfrescoWebScriptClient
					.getListOfChildAuthorities(group.getShortName(),
							AuthorityType.USER.name());
			Iterator<Authority> iterator = users.iterator();
			boolean found = false;
			while (iterator.hasNext() && found == false) {
				Authority user = iterator.next();
				if (user.getShortName().equals(
						identity.getCredentials().getUsername())) {
					userGroups.add(group);
					found = true;
				}
			}
		}
		alfrescoUserIdentity.setGroups(userGroups);

		// SETTO A MERDA il primo della lista come activeGroup
		alfrescoUserIdentity.setActiveGroup(userGroups.get(0));
		System.out.println("---> " + identity.getCredentials().getUsername()
				+ " entered as \""
				+ alfrescoUserIdentity.getActiveGroup().getShortName()
				+ "\" member");
	}

}
