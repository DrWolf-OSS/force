package it.drwolf.force.session;

import it.drwolf.force.entity.DocDefCollection;
import it.drwolf.force.entity.PropertyDef;
import it.drwolf.force.entity.Rule;
import it.drwolf.force.entity.SlotDef;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("slotDefHome")
public class SlotDefHome extends EntityHome<SlotDef> {

	public void setSlotDefId(Long id) {
		setId(id);
	}

	public Long getSlotDefId() {
		return (Long) getId();
	}

	@Override
	protected SlotDef createInstance() {
		SlotDef slotDef = new SlotDef();
		return slotDef;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
	}

	public boolean isWired() {
		return true;
	}

	public SlotDef getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<DocDefCollection> getDocDefCollections() {
		return getInstance() == null ? null : new ArrayList<DocDefCollection>(
				getInstance().getDocDefCollections());
	}

	public List<PropertyDef> getPropertyDefs() {
		return getInstance() == null ? null : new ArrayList<PropertyDef>(
				getInstance().getPropertyDefs());
	}

	public List<Rule> getRules() {
		return getInstance() == null ? null : new ArrayList<Rule>(getInstance()
				.getRules());
	}

}
