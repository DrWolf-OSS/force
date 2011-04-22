package it.drwolf.slot.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Preference implements Serializable {

	private static final long serialVersionUID = -8118213420785124186L;

	private static Preference ALFRESCO_LOCATION = new Preference(
			"ALFRESCO_LOCATION", "http://localhost:9080/alfresco");
	private static Preference ALFRESCO_ADMIN_USER = new Preference(
			"ALFRESCO_ADMIN_USER", "admin");
	private static Preference ALFRESCO_ADMIN_PWD = new Preference(
			"ALFRESCO_ADMIN_PWD", "uamepumdp");

	public static Preference[] defaults = new Preference[] { ALFRESCO_LOCATION,
			ALFRESCO_ADMIN_USER, ALFRESCO_ADMIN_PWD, };

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
