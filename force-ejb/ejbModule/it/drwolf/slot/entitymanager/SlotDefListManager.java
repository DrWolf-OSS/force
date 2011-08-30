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

	@Factory("allPrimaryTemplates")
	public List<SlotDef> retrieveAllPrimaryTemplates() {
		return retrieve(true, SlotDefType.PRIMARY);
	}

	@Factory("allGeneralTemplates")
	public List<SlotDef> retrieveAllGeneralTemplates() {
		return retrieve(true, SlotDefType.GENERAL);
	}

	@SuppressWarnings("unchecked")
	private List<SlotDef> retrieve(boolean areTemplates, SlotDefType slotDefType) {
		List<SlotDef> resultList = entityManager
				.createQuery(
						"from SlotDef s where s.template=:areTemplates and s.type=:slotDefType")
				.setParameter("areTemplates", areTemplates)
				.setParameter("slotDefType", slotDefType).getResultList();
		return resultList;
	}

}
