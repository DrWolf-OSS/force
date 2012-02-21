package it.drwolf.slot.alfresco;

import it.drwolf.slot.prefs.PreferenceKey;
import it.drwolf.slot.prefs.Preferences;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("alfrescoInfo")
@Scope(ScopeType.CONVERSATION)
public class AlfrescoInfo {

	@In(create = true)
	private Preferences preferences;

	public AlfrescoInfo() {
	}

	public String getAdminPwd() {
		return this.preferences.getValue(PreferenceKey.ALFRESCO_ADMIN_PWD
				.name());
	}

	public String getAdminUser() {
		return this.preferences.getValue(PreferenceKey.ALFRESCO_ADMIN_USER
				.name());
	}

	public String getRepositoryUri() {
		return this.preferences
				.getValue(PreferenceKey.ALFRESCO_LOCATION.name());
	}

}
