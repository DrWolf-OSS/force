package it.drwolf.slot.alfresco;

import it.drwolf.slot.entity.Preference;
import it.drwolf.slot.entitymanager.PreferenceManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("alfrescoInfo")
@Scope(ScopeType.CONVERSATION)
public class AlfrescoInfo {

	@In(create = true)
	PreferenceManager preferenceManager;

	private Preference repository_uri = null;
	private Preference admin_user = null;
	private Preference admin_pwd = null;

	public AlfrescoInfo() {
	}

	@Create
	public void init() {
		try {
			repository_uri = preferenceManager
					.getPreference("ALFRESCO_LOCATION");
			admin_user = preferenceManager.getPreference("ALFRESCO_ADMIN_USER");
			admin_pwd = preferenceManager.getPreference("ALFRESCO_ADMIN_PWD");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getRepository_uri() {
		return repository_uri.getStringValue();
	}

	public String getAdmin_user() {
		return admin_user.getStringValue();
	}

	public String getAdmin_pwd() {
		return admin_pwd.getStringValue();
	}

}
