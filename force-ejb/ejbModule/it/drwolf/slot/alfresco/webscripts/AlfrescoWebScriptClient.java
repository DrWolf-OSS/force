package it.drwolf.slot.alfresco.webscripts;

import it.drwolf.slot.alfresco.webscripts.model.Authority;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

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

	public String addGroupOrUser(String shortName, String fullAuthorityName,
			boolean group) {
		Gson gson = new Gson();
		String request = new String();
		request = "/service/api/groups/" + shortName + "/children/";
		if (group) {
			request += "GROUP_" + fullAuthorityName;
		} else {
			request += fullAuthorityName;
		}

		try {
			JsonElement parsed = this.openJsonPostRequest(this.repositoryUri
					+ request, null);
			JsonObject jsonObject = parsed.getAsJsonObject();
			JsonElement jsonElement = jsonObject.get("data");

		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	public String addPerson(HashMap<String, String> args) {
		Gson gson = new Gson();
		String request = new String();
		request = "/service/api/people";
		try {
			JsonElement parsed = this.openJsonPostRequest(this.repositoryUri
					+ request, args);
			JsonObject jsonObject = parsed.getAsJsonObject();
			JsonElement jsonElement = jsonObject.get("data");

		} catch (Exception e) {
			// TODO: handle exception
		}
		return "";
	}

	public String addSignature(String target, String name) {
		try {
			String serviceLocation = "/service/addSignature";
			String uri = this.repositoryUri + serviceLocation + "?target="
					+ target + "&name=" + name;
			return this.openGetRequest(uri);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String changePassword(String username, String password) {
		try {
			HashMap<String, String> args = new HashMap<String, String>();
			args.put("newpw", password);
			String serviceLocation = "/service/api/person/changepassword/";
			String uri = this.repositoryUri + serviceLocation + username;
			JsonElement parsed = this.openJsonPostRequest(uri, args);
			return "OK";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void embedContentToSignedDoc(String target, String content,
			String name) {
		try {
			String serviceLocation = "/service/embedContent";
			String uri = this.repositoryUri + serviceLocation + "?target="
					+ target + "&content=" + content + "&name=" + name;
			// System.out.println(uri);
			this.openGetRequest(uri);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Authority getGroupDetails(String shortName) {
		Gson gson = new Gson();
		try {
			JsonElement parsed = this.openJsonGetRequest(this.repositoryUri
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

	public List<Authority> getGroupsList(String shortNameFilter, String zone) {
		Gson gson = new Gson();
		int count = 0;
		String serviceUrl = this.repositoryUri + "/service/api/groups?";
		if ((shortNameFilter != null) && !shortNameFilter.equals("")) {
			serviceUrl = serviceUrl
					.concat("shortNameFilter=" + shortNameFilter);
			count++;
		}
		if ((zone != null) && !zone.equals("")) {
			if (count != 0) {
				serviceUrl = serviceUrl.concat("&");
			}
			serviceUrl = serviceUrl.concat("zone=" + zone);
		}
		List<Authority> authGroups = new ArrayList<Authority>();
		try {
			JsonElement parsed = this.openJsonGetRequest(serviceUrl);
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

	public List<Authority> getListOfChildAuthorities(String shortName,
			String authorityType) {
		Gson gson = new Gson();
		List<Authority> children = new ArrayList<Authority>();
		try {
			JsonElement parsed = this.openJsonGetRequest(this.repositoryUri
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

	public String openGetRequest(String uri) throws HttpException, IOException {
		HttpClient client = new HttpClient();
		client.getParams().setParameter("http.useragent", "WebScript Client");
		client.getState().setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(this.username, this.password));
		GetMethod method = new GetMethod(uri);
		int statusCode = client.executeMethod(method);

		InputStream responseStream = method.getResponseBodyAsStream();

		StringBuilder stringBuilder = new StringBuilder();
		String nl = System.getProperty("line.separator");
		Scanner scanner = new Scanner(responseStream);
		while (scanner.hasNextLine()) {
			stringBuilder.append(scanner.nextLine() + nl);
		}
		String response = stringBuilder.toString();

		// String response = method.getResponseBodyAsString();
		// System.out.println("---> response: " + response);
		return response.trim();
	}

	// new AuthScope("localhost", 9080) se si vogliono mettere i dati precisi
	private JsonElement openJsonGetRequest(String uri) throws HttpException,
			IOException {
		HttpClient client = new HttpClient();
		client.getParams().setParameter("http.useragent", "WebScript Client");
		client.getState().setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(this.username, this.password));
		GetMethod method = new GetMethod(uri);
		int statusCode = client.executeMethod(method);
		InputStream responseStream = method.getResponseBodyAsStream();

		JsonElement parsed = new JsonParser().parse(new JsonReader(
				new InputStreamReader(responseStream, "UTF-8")));

		return parsed;
	}

	private JsonElement openJsonPostRequest(String uri,
			HashMap<String, String> map) throws HttpException, IOException {
		HttpClient client = new HttpClient();
		client.getParams().setParameter("http.useragent", "WebScript Client");
		client.getState().setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(this.username, this.password));
		PostMethod method = new PostMethod(uri);

		if (map != null) {
			String request = "{";
			for (String key : map.keySet()) {
				request += "\"" + key + "\":\"" + map.get(key) + "\",";
			}
			request += "}";

			method.setRequestEntity(new StringRequestEntity(request,
					"application/json", null));
		}
		int statusCode = client.executeMethod(method);
		InputStream responseStream = method.getResponseBodyAsStream();
		JsonElement parsed = new JsonParser().parse(new JsonReader(
				new InputStreamReader(responseStream, "UTF-8")));

		return parsed;
	}
}
