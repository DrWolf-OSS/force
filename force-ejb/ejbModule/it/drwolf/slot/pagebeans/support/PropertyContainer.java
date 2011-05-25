package it.drwolf.slot.pagebeans.support;

import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.entity.PropertyDef;

public class PropertyContainer {

	private Property property;
	private PropertyDef propertyDef;

	public PropertyContainer(Object object) {
		if (object instanceof Property) {
			this.property = (Property) object;
		} else if (object instanceof PropertyDef) {
			this.propertyDef = (PropertyDef) object;
		}
	}

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
			return "Property:" + property.getName();
		} else if (propertyDef != null) {
			return "PropertyDef:" + propertyDef.getId();
		}
		return "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((property == null) ? 0 : property.hashCode());
		result = prime * result
				+ ((propertyDef == null) ? 0 : propertyDef.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PropertyContainer))
			return false;
		PropertyContainer other = (PropertyContainer) obj;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		if (propertyDef == null) {
			if (other.propertyDef != null)
				return false;
		} else if (!propertyDef.equals(other.propertyDef))
			return false;
		return true;
	}

}
