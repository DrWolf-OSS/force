package it.drwolf.slot.application;

import it.drwolf.slot.alfresco.AlfrescoAdminIdentity;
import it.drwolf.slot.alfresco.custom.model.Aspect;
import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.alfresco.custom.model.SlotModel;
import it.drwolf.slot.prefs.PreferenceKey;
import it.drwolf.slot.prefs.Preferences;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.apache.chemistry.opencmis.client.api.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

@Name("customModelController")
@Scope(ScopeType.APPLICATION)
public class CustomModelController {

	private SlotModel slotModel;

	@In(create = true)
	private Preferences preferences;

	@In(create = true)
	private AlfrescoAdminIdentity alfrescoAdminIdentity;

	private HashMap<String, Set<Property>> propertiesMap = new HashMap<String, Set<Property>>();

	@Create
	public void init() {
		// loadModel();
		loadModelFromInternalFolder();
	}

	// private void loadModel() {
	// try {
	// String customModelJarPath = preferences
	// .getValue(PreferenceKey.CUSTOM_MODEL_JAR_PATH.name());
	// String customModelXmlName = preferences
	// .getValue(PreferenceKey.CUSTOM_MODEL_XML_PATH_IN_JAR.name());
	// JarFile jar;
	// jar = new JarFile(customModelJarPath);
	//
	// InputStream xmlSource = null;
	// for (@SuppressWarnings("rawtypes")
	// Enumeration entries = jar.entries(); entries.hasMoreElements();) {
	// JarEntry jarElement = (JarEntry) entries.nextElement();
	// String zipEntryName = jarElement.getName();
	// if (zipEntryName.equals(customModelXmlName)) {
	// xmlSource = jar.getInputStream(jarElement);
	// }
	// }
	// Serializer serializer = new Persister();
	// this.slotModel = serializer.read(SlotModel.class, xmlSource);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	private void loadModelFromInternalFolder() {
		try {
			Serializer serializer = new Persister();
			Session session = alfrescoAdminIdentity.getSession();
			AlfrescoDocument documentModel = (AlfrescoDocument) session
					.getObjectByPath(preferences
							.getValue(PreferenceKey.CUSTOM_MODEL_XML_PATH
									.name())
							+ "/"
							+ preferences
									.getValue(PreferenceKey.CUSTOM_MODEL_XML_NAME
											.name()));
			slotModel = serializer.read(SlotModel.class, documentModel
					.getContentStream().getStream());
			//
			initMap();
			//
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void reloadModel() {
		loadModelFromInternalFolder();
	}

	public SlotModel getSlotModel() {
		return slotModel;
	}

	public void setSlotModel(SlotModel slotModel) {
		this.slotModel = slotModel;
	}

	public Aspect getAspect(String aspectId) {
		boolean found = false;
		Iterator<Aspect> aspectIterator = this.slotModel.getAspects()
				.iterator();
		while (aspectIterator.hasNext() && found == false) {
			Aspect aspect = aspectIterator.next();
			if (aspect.getId().equals(aspectId)) {
				found = true;
				return aspect;
			}
		}
		return null;
	}

	public Property getProperty(String propertyName) {
		boolean found = false;
		Iterator<Aspect> aspectIterator = this.slotModel.getAspects()
				.iterator();
		while (aspectIterator.hasNext() && found == false) {
			Aspect aspect = aspectIterator.next();
			Iterator<Property> propertyIterator = aspect.getProperties()
					.iterator();
			while (propertyIterator.hasNext() && found == false) {
				Property property = propertyIterator.next();
				if (property.getName().equals(propertyName)) {
					found = true;
					return property;
				}
			}
		}
		return null;
	}

	public Set<Property> getProperties(String aspectId) {
		return this.propertiesMap.get(aspectId);
	}

	private void initMap() {
		List<Aspect> aspects = this.getSlotModel().getAspects();
		for (Aspect aspect : aspects) {
			Set<Property> properties = retrieveAllProperties(aspect);
			propertiesMap.put(aspect.getId(), properties);
		}
	}

	private Set<Property> retrieveAllProperties(Aspect aspect) {
		Set<Property> properties = new HashSet<Property>();
		// Aspect aspect = this.getAspect(aspectId);
		if (aspect != null) {
			properties.addAll(aspect.getProperties());
			if (aspect.getMandatoryAspectIds() != null) {
				for (String mandatoryAspectId : aspect.getMandatoryAspectIds()) {
					properties.addAll(retrieveAllProperties(this.getAspect("P:"
							+ mandatoryAspectId)));
				}
			}
		}
		return properties;
	}

}
