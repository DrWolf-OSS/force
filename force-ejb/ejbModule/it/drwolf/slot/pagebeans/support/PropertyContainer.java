package it.drwolf.slot.pagebeans.support;

import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.entity.PropertyDef;

public class PropertyContainer {

	private Property property;
	private PropertyDef propertyDef;

	public PropertyContainer(PropertyDef propertyDef) {
		super();
		this.propertyDef = propertyDef;
	}

	public PropertyContainer(Property property) {
		super();
		this.property = property;
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public PropertyDef getPropertyDef() {
		return propertyDef;
	}

	public void setPropertyDef(PropertyDef propertyDef) {
		this.propertyDef = propertyDef;
	}

	public String getLabel() {
		if (property != null) {
			return property.getTitle();
		} else if (propertyDef != null) {
			return propertyDef.getName();
		}
		return "";
	}

	@Override
	public String toString() {
		if (property != null) {
			return "Property:" + property.getTitle();
		} else if (propertyDef != null) {
			return "PropertyDef:" + propertyDef.getName();
		}
		return "";
	}

}
