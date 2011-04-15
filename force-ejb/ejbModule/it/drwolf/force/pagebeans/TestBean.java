package it.drwolf.force.pagebeans;

import it.drwolf.force.alfresco.AlfrescoAdminIdentity;
import it.drwolf.force.alfresco.AlfrescoInfo;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("testBean")
@Scope(ScopeType.CONVERSATION)
public class TestBean {

	@In(create = true)
	private AlfrescoAdminIdentity alfrescoAdminIdentity;

	@In(create = true)
	private AlfrescoInfo alfrescoInfo;

	public void test() {
		alfrescoAdminIdentity.getSession();
		System.out.println("---> Connected");
	}
}
