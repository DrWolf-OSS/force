package it.drwolf.slot.session;

import it.drwolf.force.session.UserSession;
import it.drwolf.slot.alfresco.AlfrescoInfo;
import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.entitymanager.PreferenceManager;

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

	@In
	private UserSession userSession;

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

		if (this.alfrescoUserIdentity.authenticate(this.identity
				.getCredentials().getUsername(), this.identity.getCredentials()
				.getPassword(), this.alfrescoInfo.getRepositoryUri())) {

			try {
				this.userSession.init();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return true;
			}

		} else {
			return false;
		}
	}
}
