package it.drwolf.slot.application;

import it.drwolf.slot.alfresco.AlfrescoAdminIdentity;
import it.drwolf.slot.alfresco.custom.model.Aspect;
import it.drwolf.slot.alfresco.custom.model.Constraint;
import it.drwolf.slot.alfresco.custom.model.Parameter;
import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.alfresco.custom.model.SlotModel;
import it.drwolf.slot.entity.Dictionary;
import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.prefs.PreferenceKey;
import it.drwolf.slot.prefs.Preferences;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
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

	public Set<Property> getProperties(Set<String> aspectIds) {
		Set<Property> properties = new TreeSet<Property>();
		// Recupero tutte le properties.
		// Essendo un set anche se un aspect è applicato più volte (essendo
		// settato come mandatory su un altro) le sue properties vengono
		// aggiunte una volta sola
		for (String aspectId : aspectIds) {
			properties.addAll(this.getProperties(aspectId));
		}
		return properties;
	}

	public Set<Property> getProperties(String aspectId) {
		return this.propertiesMap.get(aspectId);
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

	public SlotModel getSlotModel() {
		return this.slotModel;
	}

	@Create
	public void init() {
		this.loadModel();
	}

	private void initMap() {
		List<Aspect> aspects = this.getSlotModel().getAspects();
		for (Aspect aspect : aspects) {
			Set<Property> properties = this.retrieveAllProperties(aspect);
			this.propertiesMap.put(aspect.getId(), properties);
		}
	}

	private void loadModel() {
		try {
			Strategy strategy = new CycleStrategy("id", "ref");
			Serializer serializer = new Persister(strategy);
			// Serializer serializer = new Persister();
			Session session = this.alfrescoAdminIdentity.getSession();
			AlfrescoDocument documentModel = (AlfrescoDocument) session
					.getObjectByPath(this.preferences
							.getValue(PreferenceKey.CUSTOM_MODEL_XML_PATH
									.name())
							+ "/"
							+ this.preferences
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
			String modifiedModel = this.addIdValueToXml(originalModel);

			this.slotModel = serializer.read(SlotModel.class, modifiedModel);

			//
			this.setPositionToProperties();
			//
			this.initMap();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// il DataType dev'essere passato perche' nell'xml del modello un costrait
	// non e' associato esplicitamente a nessun DataType
	public Dictionary makeDictionaryFromConstraint(String constraintName,
			DataType dataType) {
		List<Constraint> constraints = this.slotModel.getConstraints();
		if (constraints != null) {
			Iterator<Constraint> iterator = constraints.iterator();
			while (iterator.hasNext()) {
				Constraint constraint = iterator.next();
				if (constraint.getType().equals(Constraint.LIST)
						&& constraint.getName().equals(constraintName)) {
					List<Parameter> parameters = constraint.getParameters();
					Iterator<Parameter> iterator2 = parameters.iterator();
					while (iterator2.hasNext()) {
						Parameter parameter = iterator2.next();
						if (parameter.getName()
								.equals(Parameter.ALLOWED_VALUES)) {
							return new Dictionary(constraintName,
									parameter.getList(), dataType);
						}
					}
				}
			}
		}
		return null;
	}

	public void reloadModel() {
		this.loadModel();
	}

	private Set<Property> retrieveAllProperties(Aspect aspect) {
		Set<Property> properties = new TreeSet<Property>();
		// Aspect aspect = this.getAspect(aspectId);
		if (aspect != null) {
			properties.addAll(aspect.getProperties());
			if (aspect.getMandatoryAspectIds() != null) {
				for (String mandatoryAspectId : aspect.getMandatoryAspectIds()) {
					properties.addAll(this.retrieveAllProperties(this
							.getAspect("P:" + mandatoryAspectId)));
				}
			}
		}
		return properties;
	}

	private void setPositionToProperties() {
		List<Aspect> aspects = this.slotModel.getAspects();
		int count = 0;
		for (Aspect aspect : aspects) {
			List<Property> properties = aspect.getProperties();
			for (Property property : properties) {
				property.setPosition(count);
				count++;
			}
		}
	}

	public void setSlotModel(SlotModel slotModel) {
		this.slotModel = slotModel;
	}

}
