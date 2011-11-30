package it.drwolf.slot.alfresco;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Identity;

@AutoCreate
@Name("alfrescoUserIdentity")
@Scope(ScopeType.SESSION)
public class AlfrescoUserIdentity extends AlfrescoIdentity {

	@In
	private Identity identity;

	public String getMyOwnerId() {
		// if (this.identity == null) {
		// this.identity = (Identity) org.jboss.seam.Component
		// .getInstance("identity");
		// }
		String ownerId = null;
		if (!this.identity.hasRole("ADMIN")) {
			ownerId = this.getActiveGroup().getShortName();
		} else {
			ownerId = "ADMIN";
		}
		return ownerId;
	}

}
