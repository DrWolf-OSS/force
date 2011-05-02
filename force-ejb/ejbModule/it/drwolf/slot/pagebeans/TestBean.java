package it.drwolf.slot.pagebeans;

import it.drwolf.slot.alfresco.AlfrescoAdminIdentity;
import it.drwolf.slot.alfresco.AlfrescoInfo;
import it.drwolf.slot.alfresco.custom.model.SlotModel;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.alfresco.cmis.client.AlfrescoFolder;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

@Name("testBean")
@Scope(ScopeType.CONVERSATION)
public class TestBean {

	@In(create = true)
	private AlfrescoAdminIdentity alfrescoAdminIdentity;

	@In(create = true)
	private AlfrescoInfo alfrescoInfo;

	public void test() {
		Session session = alfrescoAdminIdentity.getSession();
		//
		session.clear();
		//
		System.out.println("---> Connected");
		AlfrescoFolder rootFolder = (AlfrescoFolder) session.getRootFolder();
		System.out.println("root:--> " + rootFolder.getPath());

		try {
			CmisObject object = session
					.getObject("workspace://SpacesStore/6411e4ed-1af8-4a5f-9a2a-3a0df3d9c814");
			AlfrescoFolder objFolder = (AlfrescoFolder) object;
			System.out.println("---> sp: " + objFolder.getPath());
			System.out.println("---> type: " + objFolder.getType());
			ItemIterable<Document> checkedOutDocs = objFolder
					.getCheckedOutDocs();
			System.out.println("--> total items: "
					+ checkedOutDocs.getTotalNumItems());
			System.out.println("--> page items: "
					+ checkedOutDocs.getPageNumItems());

			AlfrescoDocument png = (AlfrescoDocument) session
					.getObject("workspace://SpacesStore/cd548e84-be1e-49f5-9a68-cd566f2d01ae");
			System.out.println("---> png paths: " + png.getPaths());
			Collection<ObjectType> aspects = png.getAspects();
			for (ObjectType objectType : aspects) {
				Map<String, PropertyDefinition<?>> propertyDefinitions = objectType
						.getPropertyDefinitions();

				if (propertyDefinitions != null) {
					Set<String> keySet = propertyDefinitions.keySet();
					for (String key : keySet) {
						PropertyDefinition<?> propertyDefinition = propertyDefinitions
								.get(key);
						Property<Object> property = png.getProperty(key);
						System.out.println("propId: "
								+ propertyDefinition.getId() + "| value: "
								+ property.getValueAsString());
					}
				}
				System.out.println("id: " + objectType.getId());
				System.out.println("displayName: "
						+ objectType.getDisplayName());
				System.out.println("localName: " + objectType.getLocalName());
				System.out.println("queryName: " + objectType.getQueryName());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// List<Tree<FileableCmisObject>> descendants = rootFolder
		// .getDescendants(0);
		//
		// ItemIterable<CmisObject> children = rootFolder.getChildren();
		//
		// Collection<ObjectType> aspects = rootFolder.getAspects();
		// for (ObjectType objectType : aspects) {
		// System.out.println("id: " + objectType.getId());
		// System.out.println("displayName: " + objectType.getDisplayName());
		// System.out.println("localName: " + objectType.getLocalName());
		// System.out.println("queryName: " + objectType.getQueryName());
		// }
	}

	public void loadModel() {
		try {
			Serializer serializer = new Persister();
			File source = new File(
					"/home/drwolf/git/force/it.drwolf.slot.alfresco.custom/src/java/it/drwolf/slot/alfresco/content/slotModel.xml");
			SlotModel model = serializer.read(SlotModel.class, source);
			System.out.println(model);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
