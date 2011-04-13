package it.drwolf.force.alfresco;

import java.util.HashMap;
import java.util.Map;

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

		Repository r = f.getRepositories(parameter).get(0);

		parameter.put(SessionParameter.REPOSITORY_ID, r.getId());

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

}
