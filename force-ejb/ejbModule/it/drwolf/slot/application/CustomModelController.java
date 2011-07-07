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
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.apache.chemistry.opencmis.client.api.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;

@Name("customModelController")
@Scope(ScopeType.APPLICATION)
public class CustomModelController {

	private SlotModel slotModel;

	@In(create = true)
	private Preferences preferences;

	@In(create = true)
	private AlfrescoAdminIdentity alfrescoAdminIdentity;

	// AspectId, Properties
	private HashMap<String, Set<Property>> propertiesMap = new HashMap<String, Set<Property>>();

	@Create
	public void init() {
		loadModel();
	}

	private void loadModel() {
		try {
			Strategy strategy = new CycleStrategy("id", "ref");
			Serializer serializer = new Persister(strategy);
			// Serializer serializer = new Persister();
			Session session = alfrescoAdminIdentity.getSession();
			AlfrescoDocument documentModel = (AlfrescoDocument) session
					.getObjectByPath(preferences
							.getValue(PreferenceKey.CUSTOM_MODEL_XML_PATH
									.name())
							+ "/"
							+ preferences
									.getValue(PreferenceKey.CUSTOM_MODEL_XML_NAME
											.name()));

			// leggo l'xml originale
			StringBuilder stringBuilder = new StringBuilder();
			String nl = System.getProperty("line.separator");
			Scanner scanner = new Scanner(documentModel.getContentStream()
					.getStream());
			while (scanner.hasNextLine()) {
				stringBuilder.append(scanner.nextLine() + nl);
			}
			String originalModel = stringBuilder.toString();

			// aggiungo il campo id ai vari field
			String modifiedModel = addIdValueToXml(originalModel);

			slotModel = serializer.read(SlotModel.class, modifiedModel);
			//
			initMap();
			//
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ATTENZIONE:
	// Aggiungo ad ogni elemento con il campo "name" un campo "id" con lo stesso
	// valore.
	// Mi serve perchè il campo id viene usato come riferimento nel
	// CycleStrategy (se avessimo usato direttamente il campo name i field name
	// delle classi sarebbero stati tutti settati a null perchè a quanto
	// pare un campo non può essere usato contemporaneamente come identificativo
	// per la CycleStrategy e come normale field di una classe)
	//
	private String addIdValueToXml(String originalModel) {

		// regex che aggiunge a TUTTI i campi name un campo id con ugual valore
		// String patternTxt = "(\\sname=\"\\w+:\\w+\")";

		// regex che aggiunge SOLO ai constraint un campo id con lo stesso
		// valore del campo name
		String patternTxt = "(<constraint name=\"\\w+:\\w+\")";

		Pattern pattern = Pattern.compile(patternTxt);
		Matcher matcher = pattern.matcher(originalModel);

		String modifiedModel = "";
		String subfix = "";
		int begin = 0;

		while (matcher.find()) {
			String group = matcher.group();

			String[] split = group.split("=");
			String idValue = " id=" + split[1];

			String prefix = originalModel.substring(begin, matcher.start());
			modifiedModel = modifiedModel.concat(prefix);

			subfix = originalModel.substring(matcher.end(),
					originalModel.length());

			String toAdd = group + idValue;
			modifiedModel = modifiedModel.concat(toAdd);

			begin = matcher.end();
		}
		return modifiedModel = modifiedModel.concat(subfix);
	}

	public void reloadModel() {
		loadModel();
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
