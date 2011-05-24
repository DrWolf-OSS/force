package it.drwolf.slot.pagebeans.support;

import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.application.CustomModelController;
import it.drwolf.slot.entity.DocDef;
import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.entity.SlotDef;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PropertiesSourceContainer {
	private SlotDef slotDef;
	private DocDefCollection docDefCollection;
	private List<PropertyContainer> properties;

	public PropertiesSourceContainer(Object object) {
		if (object instanceof DocDefCollection) {
			this.docDefCollection = (DocDefCollection) object;
			initPropertiesAsCollection();
		} else if (object instanceof SlotDef) {
			this.slotDef = (SlotDef) object;
		}
	}

	public PropertiesSourceContainer(DocDefCollection docDefCollection) {
		super();
		this.docDefCollection = docDefCollection;
		initPropertiesAsCollection();
	}

	public PropertiesSourceContainer(SlotDef slotDef) {
		super();
		this.slotDef = slotDef;
	}

	public SlotDef getSlotDef() {
		return slotDef;
	}

	public void setSlotDef(SlotDef slotDef) {
		this.slotDef = slotDef;
	}

	public DocDefCollection getDocDefCollection() {
		return docDefCollection;
	}

	public void setDocDefCollection(DocDefCollection docDefCollection) {
		this.docDefCollection = docDefCollection;
	}

	public List<PropertyContainer> getProperties() {
		return properties;
	}

	public void setProperties(List<PropertyContainer> properties) {
		this.properties = properties;
	}

	public String getLabel() {
		if (slotDef != null) {
			return slotDef.getName();
		} else if (docDefCollection != null) {
			return docDefCollection.getName();
		}
		return "";
	}

	private void initPropertiesAsCollection() {
		CustomModelController customModelController = (CustomModelController) org.jboss.seam.Component
				.getInstance("customModelController");

		Set<Property> aspectProperties = new HashSet<Property>();
		DocDef docDef = this.docDefCollection.getDocDef();
		Set<String> aspectIds = docDef.getAspectIds();
		for (String aspectId : aspectIds) {
			aspectProperties.addAll(customModelController
					.getProperties(aspectId));
		}

		ArrayList<PropertyContainer> aspectPropertiesAsList = new ArrayList<PropertyContainer>();
		for (Property property : aspectProperties) {
			PropertyContainer container = new PropertyContainer(property);
			aspectPropertiesAsList.add(container);
		}
		this.setProperties(aspectPropertiesAsList);
	}

	@Override
	public String toString() {
		if (slotDef != null) {
			return "SlotDef:" + slotDef.getId();
		} else if (docDefCollection != null) {
			return "DocDefCollection:" + docDefCollection.getId();
		}
		return "";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((docDefCollection == null) ? 0 : docDefCollection.hashCode());
		result = prime * result + ((slotDef == null) ? 0 : slotDef.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PropertiesSourceContainer))
			return false;
		PropertiesSourceContainer other = (PropertiesSourceContainer) obj;
		if (docDefCollection == null) {
			if (other.docDefCollection != null)
				return false;
		} else if (!docDefCollection.equals(other.docDefCollection))
			return false;
		if (slotDef == null) {
			if (other.slotDef != null)
				return false;
		} else if (!slotDef.equals(other.slotDef))
			return false;
		return true;
	}

}
