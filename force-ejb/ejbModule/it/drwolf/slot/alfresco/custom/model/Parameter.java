package it.drwolf.slot.alfresco.custom.model;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Parameter {

	public final static String ALLOWED_VALUES = "allowedValues";

	public final static String EXPRESSION = "expression";
	public final static String REQUIRES_MATCH = "requiresMatch";

	public final static String MIN_LENGTH = "minLength";
	public final static String MAX_LENGTH = "maxLength";

	public final static String MIN_VALUE = "minValue";
	public final static String MAX_VALUE = "maxValue";

	public final static String CASE_SENSITIVE = "caseSensitive";

	@Attribute
	private String name;

	@ElementList(required = false)
	private List<String> list;

	@Element(required = false)
	private String value;

	public List<String> getList() {
		return this.list;
	}

	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
