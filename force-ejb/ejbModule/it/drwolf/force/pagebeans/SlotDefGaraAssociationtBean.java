package it.drwolf.force.pagebeans;

import it.drwolf.force.entity.Gara;
import it.drwolf.force.session.homes.GaraHome;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.pagebeans.SlotDefEditBean;
import it.drwolf.slot.session.SlotDefHome;

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

	private void initSlotDefValues() {
		if (garaHome.getGaraId() != null && slotDefHome.getSlotDefId() == null
				&& slotDefHome.getInstance() != null) {
			slotDefHome.getInstance().setName(
					garaHome.getInstance().getOggetto().trim());
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

}
