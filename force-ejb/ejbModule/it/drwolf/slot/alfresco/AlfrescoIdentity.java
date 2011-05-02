package it.drwolf.slot.alfresco;

import java.util.HashMap;
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

	public boolean authenticate(String username, String password, String url) {

		this.url = url;
		this.username = username;
		this.password = password;

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

		return true;
	}

	public String getPassword() {
		return this.password;
	}

	public Session getSession() {
		return this.session;
	}

	public String getUrl() {
		return this.url;
	}

	public String getUsername() {
		return this.username;
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

}
