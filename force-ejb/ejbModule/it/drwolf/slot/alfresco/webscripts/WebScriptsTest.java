package it.drwolf.slot.alfresco.webscripts;

import it.drwolf.slot.alfresco.webscripts.model.Authority;

import java.util.List;

public class WebScriptsTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AlfrescoWebScriptClient alfrescoWebScriptClient = new AlfrescoWebScriptClient(
				"admin", "uamepumdp", "http://localhost:9080/alfresco");
		// Authority groupDetails =
		// alfrescoWebScriptClient.getGroupDetails("gp1");
		List<Authority> groupsList = alfrescoWebScriptClient.getGroupsList("*",
				"");
		System.out.println(groupsList);
	}

}
