package it.drwolf.slot.entitymanager;

import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.enums.SlotDefType;

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

	private boolean model = Boolean.FALSE;
	private String mode = SlotDefType.GENERAL.value();

	@Factory("allPrimaryTemplates")
	public List<SlotDef> retrieveAllPrimaryTemplates() {
		return retrieve(true, SlotDefType.PRIMARY);
	}

	@Factory("allGeneralTemplates")
	public List<SlotDef> retrieveAllGeneralTemplates() {
		return retrieve(true, SlotDefType.GENERAL);
	}

	@SuppressWarnings("unchecked")
	private List<SlotDef> retrieve(Boolean areTemplates, SlotDefType slotDefType) {
		int count = 0;
		String templates = "";
		if (slotDefType != null) {
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

		List<SlotDef> resultList = entityManager.createQuery(query)
				.setParameter("areTemplates", areTemplates)
				.setParameter("slotDefType", slotDefType).getResultList();
		return resultList;
	}

	@Factory("slotDefsByParams")
	public List<SlotDef> retrieveByParams() {
		if (mode == null || mode.equals(""))
			return retrieve(model, null);
		else if (mode.equals(SlotDefType.PRIMARY.value()))
			return retrieve(model, SlotDefType.PRIMARY);
		else if (mode.equals(SlotDefType.GENERAL.value()))
			return retrieve(model, SlotDefType.GENERAL);
		return null;
	}

	public boolean isModel() {
		return model;
	}

	public void setModel(boolean model) {
		this.model = model;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}
