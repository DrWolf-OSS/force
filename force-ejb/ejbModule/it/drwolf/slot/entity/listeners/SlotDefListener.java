package it.drwolf.slot.entity.listeners;

import it.drwolf.slot.entity.PropertyDef;
import it.drwolf.slot.entity.SlotDef;

import java.util.List;

import javax.persistence.PostLoad;

public class SlotDefListener {

	@PostLoad
	public void setPropertyDefsPosition(SlotDef slotDef) {
		List<PropertyDef> list = slotDef.getPropertyDefsAsList();
		for (PropertyDef propertyDef : list) {
			if (propertyDef.getPosition() == null) {
				int index = list.indexOf(propertyDef);
				propertyDef.setPosition(index);
			}
		}
	}

}
