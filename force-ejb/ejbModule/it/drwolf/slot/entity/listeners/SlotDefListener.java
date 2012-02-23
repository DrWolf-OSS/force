package it.drwolf.slot.entity.listeners;

import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.entity.EmbeddedProperty;
import it.drwolf.slot.entity.PropertyDef;
import it.drwolf.slot.entity.SlotDef;

import java.util.List;

import javax.persistence.PostLoad;

public class SlotDefListener {

	@PostLoad
	public void setPositions(SlotDef slotDef) {
		List<PropertyDef> pdList = slotDef.getPropertyDefsAsList();
		for (PropertyDef propertyDef : pdList) {
			if (propertyDef.getPosition() == null) {
				int index = pdList.indexOf(propertyDef);
				propertyDef.setPosition(index);
			}
		}

		List<DocDefCollection> ddcList = slotDef.getDocDefCollectionsAsList();
		for (DocDefCollection docDefCollection : ddcList) {
			if (docDefCollection.getPosition() == null) {
				int index = ddcList.indexOf(docDefCollection);
				docDefCollection.setPosition(index);
			}
		}

		List<EmbeddedProperty> epList = slotDef.getEmbeddedPropertiesAsList();
		for (EmbeddedProperty embeddedProperty : epList) {
			if (embeddedProperty.getPosition() == null) {
				int index = epList.indexOf(embeddedProperty);
				embeddedProperty.setPosition(index);
			}
		}

	}
}
