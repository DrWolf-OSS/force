package it.drwolf.slot.alfresco.webscripts;

import it.drwolf.slot.alfresco.webscripts.model.Authority;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class AlfrescoWebScriptClient {

	private String username;
	private String password;
	private String repositoryUri;

	public AlfrescoWebScriptClient(String username, String password,
			String repositoryUri) {
		super();
		this.username = username;
		this.password = password;
		this.repositoryUri = repositoryUri;
	}

	public List<Authority> getGroupsList(String shortNameFilter, String zone) {
		Gson gson = new Gson();
		int count = 0;
		String serviceUrl = repositoryUri + "/service/api/groups?";
		if (shortNameFilter != null && !shortNameFilter.equals("")) {
			serviceUrl = serviceUrl
					.concat("shortNameFilter=" + shortNameFilter);
			count++;
		}
		if (zone != null && !zone.equals("")) {
			if (count != 0)
				serviceUrl = serviceUrl.concat("&");
			serviceUrl = serviceUrl.concat("zone=" + zone);
		}
		List<Authority> authGroups = new ArrayList<Authority>();
		try {
			JsonElement parsed = openJsonGetRequest(serviceUrl);
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

	public Authority getGroupDetails(String shortName) {
		Gson gson = new Gson();
		try {
			JsonElement parsed = openJsonGetRequest(repositoryUri
					+ "/service/api/groups/" + shortName);

			JsonObject jsonObject = parsed.getAsJsonObject();
			JsonElement jsonElement = jsonObject.get("data");

			Authority authority = gson.fromJson(jsonElement, Authority.class);
			return authority;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Authority> getListOfChildAuthorities(String shortName,
			String authorityType) {
		Gson gson = new Gson();
		List<Authority> children = new ArrayList<Authority>();
		try {
			JsonElement parsed = openJsonGetRequest(repositoryUri
					+ "/service/api/groups/" + shortName
					+ "/children?authorityType=" + authorityType);
			JsonObject groups = parsed.getAsJsonObject();
			JsonArray list = groups.getAsJsonArray("data");
			for (JsonElement e : list) {
				Authority fromJson = gson.fromJson(e, Authority.class);
				children.add(fromJson);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return children;
	}

	// private JsonElement openJsonWebScript(String url)
	// throws MalformedURLException, IOException,
	// UnsupportedEncodingException {
	// URL peopleServiceUrl = new URL(url);
	// URLConnection conn = peopleServiceUrl.openConnection();
	// String auth = "Basic "
	// + Base64.encodeBase64String((username + ":" + password)
	// .getBytes());
	// conn.setRequestProperty("Authorization", auth);
	// conn.connect();
	//
	// InputStream is = conn.getInputStream();
	//
	// JsonElement parsed = new JsonParser().parse(new JsonReader(
	// new InputStreamReader(is, "UTF-8")));
	//
	// return parsed;
	// }

	// new AuthScope("localhost", 9080) se si vogliono mettere i dati precisi
	private JsonElement openJsonGetRequest(String uri) throws HttpException,
			IOException {
		HttpClient client = new HttpClient();
		client.getParams().setParameter("http.useragent", "WebScript Client");
		client.getState().setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(username, password));
		GetMethod method = new GetMethod(uri);
		int statusCode = client.executeMethod(method);
		InputStream responseStream = method.getResponseBodyAsStream();

		JsonElement parsed = new JsonParser().parse(new JsonReader(
				new InputStreamReader(responseStream, "UTF-8")));

		return parsed;
	}

}
