package it.drwolf.slot.session;

import it.drwolf.slot.alfresco.AlfrescoInfo;
import it.drwolf.slot.alfresco.AlfrescoUserIdentity;

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

		return true;
	}

}
