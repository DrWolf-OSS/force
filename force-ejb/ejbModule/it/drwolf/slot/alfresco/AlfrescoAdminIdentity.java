package it.drwolf.slot.alfresco;

import org.apache.chemistry.opencmis.client.api.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@AutoCreate
@Name("alfrescoAdminIdentity")
@Scope(ScopeType.APPLICATION)
public class AlfrescoAdminIdentity extends AlfrescoIdentity {

	@In(create = true)
	private AlfrescoInfo alfrescoInfo;

	public AlfrescoAdminIdentity() {
	}

	@Override
	public Session getSession() {
		try {
			if (super.getSession() == null) {
				this.authenticate(alfrescoInfo.getAdmin_user(),
						alfrescoInfo.getAdmin_pwd(),
						alfrescoInfo.getRepository_uri());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.getSession();
	}
}
