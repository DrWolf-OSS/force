package it.drwolf.slot.alfresco.custom.model;

import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.interfaces.DataDefinition;

import java.util.Iterator;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Property implements DataDefinition, Comparable<Property> {

	@Attribute
	private String name;

	@Element
	private String title;

	@Element
	private String type;

	@Element(required = false)
	private boolean mandatory;

	@Element(required = false)
	private boolean multiple;

	@Element(required = false, name = "default")
	private boolean defaultValue;

	@ElementList(required = false)
	private List<Constraint> constraints;

	private int position;

	public int compareTo(Property o) {
		if (o.getPosition() < this.getPosition()) {
			return 1;
		} else if (o.getPosition() > this.getPosition()) {
			return -1;
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Property)) {
			return false;
		}
		Property other = (Property) obj;
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	public List<Constraint> getConstraints() {
		return this.constraints;
	}

	public DataType getDataType() {
		if (this.type.equals("d:text")) {
			return DataType.STRING;
		} else if (this.type.equals("d:int")) {
			return DataType.INTEGER;
		} else if (this.type.equals("d:boolean")) {
			return DataType.BOOLEAN;
		} else if (this.type.equals("d:date")) {
			return DataType.DATE;
		} else {
			return null;
		}
	}

	public List<String> getDictionaryValues() {
		if (this.constraints != null) {
			Iterator<Constraint> iterator = this.constraints.iterator();
			while (iterator.hasNext()) {
				Constraint constraint = iterator.next();
				if (constraint.getType().equals(Constraint.LIST)) {
					List<Parameter> parameters = constraint.getParameters();
					Iterator<Parameter> iterator2 = parameters.iterator();
					while (iterator2.hasNext()) {
						Parameter parameter = iterator2.next();
						if (parameter.getName()
								.equals(Parameter.ALLOWED_VALUES)) {
							return parameter.getList();
						}
					}
				}
			}
		}
		return null;
	}

	//
	// /
	public String getLabel() {
		return this.title;
	}

	public String getName() {
		return this.name;
	}

	public int getPosition() {
		return this.position;
	}

	public String getTitle() {
		return this.title;
	}

	public String getType() {
		return this.type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	public boolean isDefaultValue() {
		return this.defaultValue;
	}

	public boolean isEditable() {
		return true;
	}

	public boolean isMandatory() {
		return this.mandatory;
	}

	public boolean isMultiple() {
		return this.multiple;
	}

	public boolean isRequired() {
		return this.mandatory;
	}

	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}

	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return this.name + "," + this.type;
	}

}
