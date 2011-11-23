package it.drwolf.slot.entity.listeners;

import it.drwolf.slot.entity.SlotInst;

import javax.persistence.PostLoad;

public class SlotInstListener {

	@PostLoad
	public void setTransientStatus(SlotInst slotInst) {
		slotInst.setTransientStatus(slotInst.getStatus());
	}

}
