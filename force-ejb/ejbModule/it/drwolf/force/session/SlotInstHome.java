package it.drwolf.force.session;

import it.drwolf.force.entity.*;
import java.util.ArrayList;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("slotInstHome")
public class SlotInstHome extends EntityHome<SlotInst> {

	@In(create = true)
	SlotDefHome slotDefHome;

	public void setSlotInstId(Long id) {
		setId(id);
	}

	public Long getSlotInstId() {
		return (Long) getId();
	}

	@Override
	protected SlotInst createInstance() {
		SlotInst slotInst = new SlotInst();
		return slotInst;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		SlotDef slotDef = slotDefHome.getDefinedInstance();
		if (slotDef != null) {
			getInstance().setSlotDef(slotDef);
		}
	}

	public boolean isWired() {
		return true;
	}

	public SlotInst getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<DocInstCollection> getDocInstCollections() {
		return getInstance() == null ? null : new ArrayList<DocInstCollection>(
				getInstance().getDocInstCollections());
	}

}
