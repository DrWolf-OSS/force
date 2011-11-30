package it.drwolf.slot.alfresco;

import it.drwolf.slot.alfresco.webscripts.model.Authority;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.cmis.client.AlfrescoFolder;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

public abstract class AlfrescoIdentity {

	private Session session;

	private String url;

	private String username;

	private String password;

	private List<Authority> groups;

	private Authority activeGroup;

	// private Identity identity;

	public boolean authenticate(String username, String password, String url) {

		this.url = url;
		this.username = username;
		this.password = password;
		try {

			SessionFactory f = SessionFactoryImpl.newInstance();
			Map<String, String> parameter = new HashMap<String, String>();

			// user credentials
			parameter.put(SessionParameter.USER, username);
			parameter.put(SessionParameter.PASSWORD, password);

			url += "/s/cmis";

			// connection settings
			parameter.put(SessionParameter.ATOMPUB_URL, url);
			parameter.put(SessionParameter.BINDING_TYPE,
					BindingType.ATOMPUB.value());
			//
			Repository r = f.getRepositories(parameter).get(0);

			parameter.put(SessionParameter.REPOSITORY_ID, r.getId());

			// Alfresco object factory
			parameter.put(SessionParameter.OBJECT_FACTORY_CLASS,
					"org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");

			this.session = new SynchSession(f.createSession(parameter));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public Authority getActiveGroup() {
		return this.activeGroup;
	}

	public List<Authority> getGroups() {
		return this.groups;
	}

	// public String getMyOwnerId() {
	// if (this.identity == null) {
	// this.identity = (Identity) org.jboss.seam.Component
	// .getInstance("identity");
	// }
	// String ownerId = null;
	// if (!this.identity.hasRole("ADMIN")) {
	// ownerId = this.getActiveGroup().getShortName();
	// } else {
	// ownerId = "ADMIN";
	// }
	// return ownerId;
	// }

	public String getPassword() {
		return this.password;
	}

	public Session getSession() {
		return this.session;
	}

	public String getUrl() {
		return this.url;
	}

	public AlfrescoFolder getUserHomeFolder() {
		AlfrescoInfo alfrescoInfo = (AlfrescoInfo) org.jboss.seam.Component
				.getInstance("alfrescoInfo");
		AlfrescoFolder usersHome = (AlfrescoFolder) this.getSession()
				.getObject(alfrescoInfo.getUsersHomeRef());
		return (AlfrescoFolder) this.getSession().getObjectByPath(
				usersHome.getPath() + "/" + this.username);
	}

	public String getUserHomePath() {
		return this.getUserHomeFolder().getPath();
	}

	public String getUserHomeRef() {
		return this.getUserHomeFolder().getId();
	}

	public String getUsername() {
		return this.username;
	}

	public Boolean isMemberOf(String groupName) {
		for (Authority auth : this.groups) {
			if (auth.getShortName().equals(groupName)) {
				return true;
			}
		}
		return false;
	}

	public void setActiveGroup(Authority activeGroup) {
		this.activeGroup = activeGroup;
	}

	public void setGroups(List<Authority> groups) {
		this.groups = groups;
	}

}
