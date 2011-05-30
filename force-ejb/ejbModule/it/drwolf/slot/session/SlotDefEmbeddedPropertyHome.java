package it.drwolf.slot.session;

import it.drwolf.slot.entity.SlotDefEmbeddedProperty;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("slotDefEmbeddedPropertyHome")
public class SlotDefEmbeddedPropertyHome extends
		EntityHome<SlotDefEmbeddedProperty> {

	private static final long serialVersionUID = -1501111099811991189L;

	public void setSlotDefEmbeddedPropertyId(Long id) {
		setId(id);
	}

	public Long getSlotDefEmbeddedPropertyId() {
		return (Long) getId();
	}

	@Override
	protected SlotDefEmbeddedProperty createInstance() {
		SlotDefEmbeddedProperty slotDefEmbeddedProperty = new SlotDefEmbeddedProperty();
		return slotDefEmbeddedProperty;
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

	public SlotDefEmbeddedProperty getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
