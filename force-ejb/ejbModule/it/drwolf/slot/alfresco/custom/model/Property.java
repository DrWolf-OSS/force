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
public class Property implements DataDefinition {

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public boolean isDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		return name + "," + type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Property))
			return false;
		Property other = (Property) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	//
	// /
	public String getLabel() {
		return this.title;
	}

	public DataType getDataType() {
		if (this.type.equals("d:text"))
			return DataType.STRING;
		else if (this.type.equals("d:int"))
			return DataType.INTEGER;
		else if (this.type.equals("d:boolean"))
			return DataType.BOOLEAN;
		else if (this.type.equals("d:date"))
			return DataType.DATE;
		else
			return null;
	}

	public boolean isRequired() {
		return this.mandatory;
	}

	public boolean isEditable() {
		return true;
	}

	public List<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}

	public List<String> getDictionaryValues() {
		if (this.constraints != null) {
			Iterator<Constraint> iterator = constraints.iterator();
			while (iterator.hasNext()) {
				Constraint constraint = iterator.next();
				if (constraint.getType().equals(Constraint.LIST)) {
					return constraint.getParameter().getList();
				}
			}
		}
		return null;
	}

}
