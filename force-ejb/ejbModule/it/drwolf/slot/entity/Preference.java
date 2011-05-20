package it.drwolf.slot.entity;

import it.drwolf.slot.prefs.PreferenceKey;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Preference implements Serializable {

	private static final long serialVersionUID = -8118213420785124186L;

	private static Preference ALFRESCO_LOCATION = new Preference(
			PreferenceKey.ALFRESCO_LOCATION.name(),
			"http://localhost:9080/alfresco");
	private static Preference ALFRESCO_ADMIN_USER = new Preference(
			PreferenceKey.ALFRESCO_ADMIN_USER.name(), "admin");
	private static Preference ALFRESCO_ADMIN_PWD = new Preference(
			PreferenceKey.ALFRESCO_ADMIN_PWD.name(), "uamepumdp");
	private static Preference ALFRESCO_USERS_HOME = new Preference(
			PreferenceKey.ALFRESCO_USERS_HOME.name(),
			"workspace://SpacesStore/065b9204-329b-42bb-b16d-e76811274d25");
	private static Preference CUSTOM_MODEL_JAR_PATH = new Preference(
			PreferenceKey.CUSTOM_MODEL_JAR_PATH.name(),
			"/home/drwolf/alfresco-3.4.d/tomcat/webapps/alfresco/WEB-INF/lib/it.drwolf.slot.alfresco.custom.jar");
	private static Preference CUSTOM_MODEL_XML_PATH_IN_JAR = new Preference(
			PreferenceKey.CUSTOM_MODEL_XML_PATH_IN_JAR.name(),
			"it/drwolf/slot/alfresco/content/slotModel.xml");
	private static Preference CUSTOM_MODEL_XML_PATH = new Preference(
			PreferenceKey.CUSTOM_MODEL_XML_PATH.name(),
			"/Dizionario dei dati/Modelli");
	private static Preference CUSTOM_MODEL_XML_NAME = new Preference(
			PreferenceKey.CUSTOM_MODEL_XML_NAME.name(), "slotModel.xml");
	private static Preference FORCE_GROUPS_PATH = new Preference(
			PreferenceKey.FORCE_GROUPS_PATH.name(), "/Force");

	public static Preference[] defaults = new Preference[] { ALFRESCO_LOCATION,
			ALFRESCO_ADMIN_USER, ALFRESCO_ADMIN_PWD, ALFRESCO_USERS_HOME,
			CUSTOM_MODEL_JAR_PATH, CUSTOM_MODEL_XML_PATH_IN_JAR,
			CUSTOM_MODEL_XML_PATH, CUSTOM_MODEL_XML_NAME, FORCE_GROUPS_PATH };

	private Integer id;
	private String keyValue;
	private String stringValue;

	public Preference() {
		super();
	}

	public Preference(String key, String value) {
		super();
		this.keyValue = key;
		this.stringValue = value;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(String key) {
		this.keyValue = key;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String value) {
		this.stringValue = value;
	}

}
