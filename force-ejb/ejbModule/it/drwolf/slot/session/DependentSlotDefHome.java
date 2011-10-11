package it.drwolf.slot.session;

import it.drwolf.slot.entity.DependentSlotDef;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("dependentSlotDefHome")
public class DependentSlotDefHome extends EntityHome<DependentSlotDef> {

	@In(create = true)
	SlotDefHome slotDefHome;

	@Override
	protected DependentSlotDef createInstance() {
		DependentSlotDef dependentSlotDef = new DependentSlotDef();
		return dependentSlotDef;
	}

	public void dependentSlotDefClone(DependentSlotDef dependentSlotDef) {

		// this.slotDefHome.slotDefClone(dependentSlotDef);
		// SlotDef cloned = this.slotDefHome.getInstance();
		// DependentSlotDef dep = (DependentSlotDef) cloned;
		//
		// System.out.println("break");
	}

	public DependentSlotDef getDefinedInstance() {
		return this.isIdDefined() ? this.getInstance() : null;
	}

	public Long getDependentSlotDefId() {
		return (Long) this.getId();
	}

	public boolean isWired() {
		return true;
	}

	public void load() {
		if (this.isIdDefined()) {
			this.wire();
		}
	}

	public void setDependentSlotDefId(Long id) {
		this.setId(id);
	}

	public void wire() {
		this.getInstance();
	}

}
