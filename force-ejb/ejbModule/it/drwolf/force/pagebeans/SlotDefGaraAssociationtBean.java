package it.drwolf.force.pagebeans;

import it.drwolf.force.entity.Gara;
import it.drwolf.force.exceptions.DuplicateCoupleGaraSlotDefOwner;
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
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

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
		if (this.garaHome.getGaraId() != null
				&& this.slotDefHome.getSlotDefId() == null
				&& this.slotDefHome.getInstance() != null) {
			String oggetto = this.garaHome.getInstance().getOggetto().trim();
			this.slotDefHome.getInstance().setName(oggetto);
		}
	}

	@SuppressWarnings("unchecked")
	public boolean isSlotDefReferencedBySomeGara() {
		if (this.slotDefHome.getInstance() != null
				&& this.slotDefHome.getInstance().getId() != null) {
			List<SlotInst> resultList = this.slotDefHome
					.getEntityManager()
					.createQuery(
							"select id from Gara g where :slotDef in elements(g.slotDefs)")
					.setParameter("slotDef", this.slotDefHome.getInstance())
					.setMaxResults(1).getResultList();
			if (resultList != null && !resultList.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	@Transactional
	public String persistAssociation() {
		try {
			Gara gara = this.garaHome.getInstance();
			if (gara != null && this.slotDefHome.getInstance() != null) {
				String slotResult = this.slotDefEditBean.save();
				// if (slotResult.equals("valid")) {
				SlotDef slotDef = this.slotDefHome.getInstance();
				//
				// gara.setSlotDef(slotDef);
				gara.addSlotDef(slotDef);
				//
				String garaResult = this.garaHome.update();
				if (garaResult.equals("updated")) {
					return "associated";
				}
				// }
			}
		} catch (DuplicateCoupleGaraSlotDefOwner e) {
			FacesMessages.instance().add(Severity.ERROR, e.getMessage());
			e.printStackTrace();
		}
		return "failed";
	}

	public void useSlotDef(SlotDef slotDefToClone) {
		this.slotDefHome.slotDefClone(slotDefToClone);
		this.initSlotDefValues();
	}

}
