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
			"http://localhost:8080/alfresco");
	private static Preference ALFRESCO_ADMIN_USER = new Preference(
			PreferenceKey.ALFRESCO_ADMIN_USER.name(), "admin");
	private static Preference ALFRESCO_ADMIN_PWD = new Preference(
			PreferenceKey.ALFRESCO_ADMIN_PWD.name(), "uamepumdp");
	private static Preference CUSTOM_MODEL_XML_PATH = new Preference(
			PreferenceKey.CUSTOM_MODEL_XML_PATH.name(),
			"/Dizionario dei dati/Modelli");
	private static Preference CUSTOM_MODEL_XML_NAME = new Preference(
			PreferenceKey.CUSTOM_MODEL_XML_NAME.name(), "slotModel.xml");
	private static Preference FORCE_GROUPS_PATH = new Preference(
			PreferenceKey.FORCE_GROUPS_PATH.name(), "/Force");
	private static Preference FORCE_ADMIN = new Preference(
			PreferenceKey.FORCE_ADMIN.name(), "CNA");
	private static Preference FORCE_USER_GROUP = new Preference(
			PreferenceKey.FORCE_USER_GROUP.name(), "aziende");
	private static Preference FORCE_EMAIL_HOSTNAME = new Preference(
			PreferenceKey.FORCE_EMAIL_HOSTNAME.name(), "localhost");
	private static Preference FORCE_EMAIL_FROM = new Preference(
			PreferenceKey.FORCE_EMAIL_FROM.name(), "info@forcecna.it");

	public static Preference[] defaults = new Preference[] {
			Preference.ALFRESCO_LOCATION, Preference.ALFRESCO_ADMIN_USER,
			Preference.ALFRESCO_ADMIN_PWD, Preference.CUSTOM_MODEL_XML_PATH,
			Preference.CUSTOM_MODEL_XML_NAME, Preference.FORCE_GROUPS_PATH,
			Preference.FORCE_ADMIN, Preference.FORCE_USER_GROUP,
			Preference.FORCE_EMAIL_HOSTNAME, Preference.FORCE_EMAIL_FROM };

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
		return this.id;
	}

	public String getKeyValue() {
		return this.keyValue;
	}

	public String getStringValue() {
		return this.stringValue;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setKeyValue(String key) {
		this.keyValue = key;
	}

	public void setStringValue(String value) {
		this.stringValue = value;
	}

}
