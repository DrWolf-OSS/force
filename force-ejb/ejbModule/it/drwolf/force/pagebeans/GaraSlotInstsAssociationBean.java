package it.drwolf.force.pagebeans;

import it.drwolf.force.session.homes.GaraHome;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("garaSlotInstsAssociationBean")
@Scope(ScopeType.CONVERSATION)
public class GaraSlotInstsAssociationBean {

	@In(create = true)
	GaraHome garaHome;

	@In(create = true)
	EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Factory("allAssociatedSlotInstsAsObjects")
	public List<Object[]> getAllAssociatedSlotInstsAsObjects() {
		List<Object[]> resultList = this.entityManager
				.createQuery(
						"select si.id, si.ownerId, a.ragioneSociale, si.slotDef.id, si.slotDef.ownerId, si.status from Gara g, SlotInst si, Azienda a inner join g.slotDefs sd where si.slotDef = sd and g.id=:garaId and a.alfrescoGroupId = si.ownerId")
				.setParameter("garaId", this.garaHome.getGaraId())
				.getResultList();
		return resultList;
	}

}
