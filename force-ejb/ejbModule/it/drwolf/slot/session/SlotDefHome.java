package it.drwolf.slot.session;

import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.entity.PropertyDef;
import it.drwolf.slot.entity.Rule;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.entity.SlotInst;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("slotDefHome")
public class SlotDefHome extends EntityHome<SlotDef> {

	private static final long serialVersionUID = -8721399617784074986L;

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

	@SuppressWarnings("unchecked")
	public List<SlotInst> getSlotInstsReferenced() {
		if (this.getInstance().getId() != null) {
			List<SlotInst> resultList = this.getEntityManager()
					.createQuery("from SlotInst s where s.slotDef=:slotDef")
					.setParameter("slotDef", this.getInstance())
					.getResultList();
			if (resultList != null) {
				return resultList;
			}
		}
		return new ArrayList<SlotInst>();
	}

	@SuppressWarnings("unchecked")
	public boolean isReferenced() {
		if (this.getInstance().getId() != null) {
			List<SlotInst> resultList = this
					.getEntityManager()
					.createQuery(
							"select id from SlotInst s where s.slotDef=:slotDef")
					.setParameter("slotDef", this.getInstance())
					.setMaxResults(1).getResultList();
			if (resultList != null && !resultList.isEmpty()) {
				return true;
			}
		}
		return false;
	}

}
