package it.drwolf.slot.session;

import it.drwolf.slot.entity.DependentSlotDef;
import it.drwolf.slot.entity.DocDefCollection;
import it.drwolf.slot.entity.PropertyDef;
import it.drwolf.slot.entity.Rule;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.entity.SlotInst;
import it.drwolf.slot.entitymanager.SlotDefCloner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("slotDefHome")
public class SlotDefHome extends EntityHome<SlotDef> {

	private static final long serialVersionUID = -8721399617784074986L;

	private SlotDef model;

	// @In(create = true)
	// private SlotInstHome slotInstHome;

	// @In(create = true)
	private SlotDefCloner slotDefCloner = new SlotDefCloner();

	@Override
	protected SlotDef createInstance() {
		SlotDef slotDef = new SlotDef();
		return slotDef;
	}

	public SlotDef getDefinedInstance() {
		return this.isIdDefined() ? this.getInstance() : null;
	}

	public List<DocDefCollection> getDocDefCollections() {
		return this.getInstance() == null ? null
				: new ArrayList<DocDefCollection>(this.getInstance()
						.getDocDefCollections());
	}

	public SlotDef getModel() {
		return this.model;
	}

	public List<PropertyDef> getPropertyDefs() {
		return this.getInstance() == null ? null : new ArrayList<PropertyDef>(
				this.getInstance().getPropertyDefs());
	}

	public List<Rule> getRules() {
		return this.getInstance() == null ? null : new ArrayList<Rule>(this
				.getInstance().getRules());
	}

	public SlotDefCloner getSlotDefCloner() {
		return this.slotDefCloner;
	}

	public Long getSlotDefId() {
		return (Long) this.getId();
	}

	public List<SlotInst> getSlotInstsReferenced() {
		if (this.getInstance() != null) {
			return this.getInstance().getReferencedSlotInsts();
		}
		return null;
	}

	public boolean isReferenced() {
		if (this.getInstance() != null) {
			return this.getInstance().isReferenced();
		}
		return false;
	}

	public boolean isWired() {
		return true;
	}

	public void load() {
		if (this.isIdDefined()) {
			this.wire();
		}
	}

	@Override
	public String persist() {
		//
		this.persistAddedDependentSlotDefs();
		//
		super.persist();
		this.slotDefCloner.cloneRules();

		return super.persist();
	}

	private void persistAddedDependentSlotDefs() {
		Set<SlotDefCloner> dependentSlotDefCloners = this.slotDefCloner
				.getDependentSlotDefCloners();
		EntityManager entityManager = this.getEntityManager();
		for (SlotDefCloner slotDefCloner : dependentSlotDefCloners) {
			SlotDef cloned = slotDefCloner.getCloned();
			cloned.setName(this.getInstance().getName() + " | "
					+ cloned.getName());
			entityManager.persist(cloned);
			slotDefCloner.cloneRules();
			entityManager.persist(cloned);
		}
	}

	// Il codice commentato serve per controllare in modo programmatico che lo
	// SlotDef nella home sia quello giusto (prendendolo dallo SlotInst a meno
	// che questo non sia null)
	// Nel caso scommentare anche il corrispettivo in SlotInstHome
	public void setSlotDefId(Long id) {
		// if (this.slotInstHome.isIdDefined()) {
		// this.setId(this.slotInstHome.getInstance().getSlotDef().getId());
		// } else {
		this.setId(id);
		// }
	}

	public void slotDefClone(SlotDef slotDef) {
		this.model = slotDef;

		this.slotDefCloner.setModel(slotDef);
		this.slotDefCloner.cloneModel();

		this.setInstance(this.slotDefCloner.getCloned());
	}

	public void switchPublishedStatus() {
		boolean pubblicato = this.instance.isPubblicato();
		this.instance.setPubblicato(!pubblicato);
		for (DependentSlotDef dependentSlotDef : this.instance
				.getDependentSlotDefs()) {
			dependentSlotDef.setPubblicato(!pubblicato);
		}
	}

	@Override
	public String update() {
		this.persistAddedDependentSlotDefs();
		String update = super.update();
		return update;
	}

	public void wire() {
		this.getInstance();
	}

}
