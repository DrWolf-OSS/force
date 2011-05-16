package it.drwolf.slot.alfresco.custom.model;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Aspect {

	@Attribute
	private String name;

	@Element
	private String title;

	@ElementList
	private List<Property> properties;

	@ElementList(name = "mandatory-aspects", required = false)
	private List<String> mandatoryAspectIds;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public String getId() {
		return "P:" + this.name;
	}

	public List<String> getMandatoryAspectIds() {
		return mandatoryAspectIds;
	}

	public void setMandatoryAspectIds(List<String> mandatoryAspects) {
		this.mandatoryAspectIds = mandatoryAspects;
	}

}
