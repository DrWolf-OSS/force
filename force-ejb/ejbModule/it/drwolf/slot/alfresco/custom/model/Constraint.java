package it.drwolf.slot.alfresco.custom.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
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

	@Element
	private Parameter parameter;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Parameter getParameter() {
		return parameter;
	}

	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}

}
