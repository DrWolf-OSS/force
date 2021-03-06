package it.drwolf.slot.pagebeans.support;

import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.entity.EmbeddedProperty;
import it.drwolf.slot.entity.PropertyDef;
import it.drwolf.slot.enums.DataType;

public class PropertyContainer {

	private Property property;
	private PropertyDef propertyDef;
	private EmbeddedProperty embeddedProperty;

	public PropertyContainer(Object object) {
		if (object instanceof Property) {
			this.property = (Property) object;
		} else if (object instanceof PropertyDef) {
			this.propertyDef = (PropertyDef) object;
		} else if (object instanceof EmbeddedProperty) {
			this.embeddedProperty = (EmbeddedProperty) object;
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

	public PropertyContainer(EmbeddedProperty embeddedProperty) {
		super();
		this.embeddedProperty = embeddedProperty;
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
		} else if (embeddedProperty != null) {
			return embeddedProperty.getName();
		}
		return "";
	}

	public String getName() {
		if (property != null) {
			return property.getName();
		} else if (propertyDef != null) {
			return propertyDef.getName();
		} else if (embeddedProperty != null) {
			return embeddedProperty.getName();
		}
		return "";
	}

	public DataType getType() {
		if (propertyDef != null) {
			return propertyDef.getDataType();
		} else if (property != null) {
			String type = property.getType();
			if (type.equals("d:text"))
				return DataType.STRING;
			else if (type.equals("d:int"))
				return DataType.INTEGER;
			else if (type.equals("d:boolean"))
				return DataType.BOOLEAN;
			else if (type.equals("d:date"))
				return DataType.DATE;
		} else if (embeddedProperty != null) {
			return embeddedProperty.getDataType();
		}
		return null;
	}

	@Override
	public String toString() {
		if (property != null) {
			return Property.class.getName() + ":" + property.getName();
		} else if (propertyDef != null) {
			return PropertyDef.class.getName() + ":" + propertyDef.getId();
		} else if (embeddedProperty != null) {
			return EmbeddedProperty.class.getName() + ":"
					+ embeddedProperty.getId();
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
		result = prime
				* result
				+ ((embeddedProperty == null) ? 0 : embeddedProperty.hashCode());
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
		if (embeddedProperty == null) {
			if (other.embeddedProperty != null)
				return false;
		} else if (!embeddedProperty.equals(other.embeddedProperty))
			return false;
		return true;
	}

	public EmbeddedProperty getEmbeddedProperty() {
		return embeddedProperty;
	}

	public void setEmbeddedProperty(EmbeddedProperty embeddedProperty) {
		this.embeddedProperty = embeddedProperty;
	}

}
