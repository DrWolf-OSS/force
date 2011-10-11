package it.drwolf.slot.entitymanager;

import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.enums.SlotDefType;
import it.drwolf.slot.pagebeans.SlotDefParameters;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
@Name("slotDefListManager")
public class SlotDefListManager {

	@In(create = true)
	private EntityManager entityManager;

	// private boolean model = Boolean.FALSE;
	// private String mode = SlotDefType.GENERAL.name();

	@In(create = true)
	private SlotDefParameters slotDefParameters;

	@SuppressWarnings("unchecked")
	private List<SlotDef> retrieve(Boolean areTemplates, SlotDefType slotDefType) {
		int count = 0;
		String templates = "";
		if (areTemplates != null) {
			templates = "s.template=:areTemplates";
			count++;
		}

		String type = "";
		if (slotDefType != null) {
			type = "s.type=:slotDefType";
			count++;
		}

		String where = "";
		String and = "";
		if (count > 0) {
			where = "where";
		}
		if (count > 1) {
			and = "and";
		}

		String query = "from SlotDef s " + where + " " + templates + " " + and
				+ " " + type;

		List<SlotDef> resultList = null;

		if (areTemplates != null && slotDefType != null) {
			resultList = this.entityManager.createQuery(query)
					.setParameter("areTemplates", areTemplates)
					.setParameter("slotDefType", slotDefType).getResultList();
		} else if (areTemplates != null && slotDefType == null) {
			resultList = this.entityManager.createQuery(query)
					.setParameter("areTemplates", areTemplates).getResultList();
		} else if (areTemplates == null && slotDefType != null) {
			resultList = this.entityManager.createQuery(query)
					.setParameter("slotDefType", slotDefType).getResultList();
		} else if (areTemplates == null && slotDefType == null) {
			resultList = this.entityManager.createQuery(query).getResultList();
		}
		return resultList;
	}

	@Factory("allGeneralTemplates")
	public List<SlotDef> retrieveAllGeneralTemplates() {
		return this.retrieve(true, SlotDefType.GENERAL);
	}

	@Factory("allPrimaryTemplates")
	public List<SlotDef> retrieveAllPrimaryTemplates() {
		return this.retrieve(true, SlotDefType.PRIMARY);
	}

	@Factory("slotDefsByParams")
	public List<SlotDef> retrieveByParams() {
		if (this.slotDefParameters.getMode() == null
				|| this.slotDefParameters.getMode().equals("")) {
			return this.retrieve(this.slotDefParameters.isModel(), null);
		} else if (this.slotDefParameters.getMode().equals(
				SlotDefType.PRIMARY.name())) {
			return this.retrieve(this.slotDefParameters.isModel(),
					SlotDefType.PRIMARY);
		} else if (this.slotDefParameters.getMode().equals(
				SlotDefType.GENERAL.name())) {
			return this.retrieve(this.slotDefParameters.isModel(),
					SlotDefType.GENERAL);
		} else if (this.slotDefParameters.getMode().equals(
				SlotDefType.DEPENDENT.name())) {
			return this.retrieve(this.slotDefParameters.isModel(),
					SlotDefType.DEPENDENT);
		}
		return null;
	}

	public List<SlotDef> retrieveByType(Boolean areTemplates, String slotDefType) {
		SlotDefType type = SlotDefType.valueOf(slotDefType);
		return this.retrieve(areTemplates, type);
	}

	// public boolean isModel() {
	// return model;
	// }
	//
	// public void setModel(boolean model) {
	// this.model = model;
	// }
	//
	// public String getMode() {
	// return mode;
	// }
	//
	// public void setMode(String mode) {
	// this.mode = mode;
	// }

}
