package it.drwolf.slot.alfresco.webscripts;

import it.drwolf.slot.alfresco.AlfrescoInfo;
import it.drwolf.slot.alfresco.webscripts.model.Authority;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Identity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

@Name("alfrescoWebScriptClient")
@Scope(ScopeType.CONVERSATION)
public class AlfrescoWebScriptClient {

	@In
	private Identity identity;

	@In
	private AlfrescoInfo alfrescoInfo;

	public List<Authority> getAlfrescoGroups() {
		Gson gson = new Gson();
		List<Authority> authGroups = new ArrayList<Authority>();
		try {
			JsonElement parsed = openJsonWebScript(alfrescoInfo
					.getRepositoryUri()
					+ "/service/api/groups?shortNameFilter=*");
			JsonObject groups = parsed.getAsJsonObject();
			JsonArray list = groups.getAsJsonArray("data");
			for (JsonElement e : list) {
				Authority fromJson = gson.fromJson(e, Authority.class);
				authGroups.add(fromJson);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return authGroups;
	}

	private JsonElement openJsonWebScript(String url)
			throws MalformedURLException, IOException,
			UnsupportedEncodingException {
		URL peopleServiceUrl = new URL(url);
		URLConnection conn = peopleServiceUrl.openConnection();
		String auth = "Basic "
				+ Base64.encodeBase64String((identity.getCredentials()
						.getUsername() + ":" + identity.getCredentials()
						.getPassword()).getBytes());
		conn.setRequestProperty("Authorization", auth);
		conn.connect();

		InputStream is = conn.getInputStream();

		JsonElement parsed = new JsonParser().parse(new JsonReader(
				new InputStreamReader(is, "UTF-8")));

		return parsed;
	}

}
