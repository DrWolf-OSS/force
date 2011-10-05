package it.drwolf.slot.alfresco.custom.model;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Constraint {

	public final static String LIST = "LIST";
	public final static String REGEX = "REGEX";
	public final static String LENGTH = "LENGTH";
	public final static String MINMAX = "MINMAX";

	@Attribute
	private String name;

	@Attribute
	private String type;

	// @Element
	// private Parameter parameter;

	@ElementList(inline = true, entry = "parameter")
	private List<Parameter> parameters;

	// @ElementMap(entry = "parameter", key = "name", attribute = true, inline =
	// true, value = "value")
	// private Map<String, String> map;

	// public Map<String, String> getMap() {
	// return this.map;
	// }

	public String getName() {
		return this.name;
	}

	public List<Parameter> getParameters() {
		return this.parameters;
	}

	public String getType() {
		return this.type;
	}

	public void setName(String name) {
		this.name = name;
	}

	// public void setMap(Map<String, String> map) {
	// this.map = map;
	// }

	public void setParameters(List<Parameter> parametersList) {
		this.parameters = parametersList;
	}

	public void setType(String type) {
		this.type = type;
	}

	// public Parameter getParameter() {
	// return parameter;
	// }
	//
	// public void setParameter(Parameter parameter) {
	// this.parameter = parameter;
	// }

}
