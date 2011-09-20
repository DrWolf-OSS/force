package it.drwolf.force.pagebeans;

import it.drwolf.force.entity.Gara;
import it.drwolf.force.session.homes.GaraHome;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.entity.SlotInst;
import it.drwolf.slot.pagebeans.SlotDefEditBean;
import it.drwolf.slot.session.SlotDefHome;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

@Name("slotDefGaraAssociationtBean")
@Scope(ScopeType.CONVERSATION)
public class SlotDefGaraAssociationtBean {

	@In(create = true)
	private SlotDefHome slotDefHome;

	@In(create = true)
	private SlotDefEditBean slotDefEditBean;

	@In(create = true)
	private GaraHome garaHome;

	public void initSlotDefValues() {
		if (garaHome.getGaraId() != null && slotDefHome.getSlotDefId() == null
				&& slotDefHome.getInstance() != null) {
			String oggetto = garaHome.getInstance().getOggetto().trim();
			slotDefHome.getInstance().setName(oggetto);
		}
	}

	@Transactional
	public String persistAssociation() {
		Gara gara = garaHome.getInstance();
		if (gara != null && slotDefHome.getInstance() != null) {
			String slotResult = slotDefEditBean.save();
			if (slotResult.equals("persisted")) {
				SlotDef slotDef = slotDefHome.getInstance();
				gara.setSlotDef(slotDef);
				String garaResult = garaHome.update();
				if (garaResult.equals("updated")) {
					return "associated";
				}
			}
		}
		return "failed";
	}

	public void useSlotDef(SlotDef slotDefToClone) {
		slotDefHome.slotDefClone(slotDefToClone);
		initSlotDefValues();
	}

	@SuppressWarnings("unchecked")
	public boolean isSlotDefReferencedBySomeGara() {
		if (this.slotDefHome.getInstance() != null
				&& this.slotDefHome.getInstance().getId() != null) {
			List<SlotInst> resultList = this.slotDefHome
					.getEntityManager()
					.createQuery(
							"select id from Gara g where g.slotDef=:slotDef")
					.setParameter("slotDef", this.slotDefHome.getInstance())
					.setMaxResults(1).getResultList();
			if (resultList != null && !resultList.isEmpty()) {
				return true;
			}
		}
		return false;
	}

}
