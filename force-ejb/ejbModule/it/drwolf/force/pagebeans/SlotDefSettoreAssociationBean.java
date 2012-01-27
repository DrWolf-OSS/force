package it.drwolf.force.pagebeans;

import it.drwolf.force.entity.Settore;
import it.drwolf.force.session.homes.SettoreHome;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.pagebeans.SlotDefEditBean;
import it.drwolf.slot.session.SlotDefHome;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

@Name("slotDefSettoreAssociationBean")
@Scope(ScopeType.CONVERSATION)
public class SlotDefSettoreAssociationBean {

	@In(create = true)
	private SlotDefHome slotDefHome;

	@In(create = true)
	private SettoreHome settoreHome;

	@In(create = true)
	private SlotDefEditBean slotDefEditBean;

	@Transactional
	public String persistAssociation() {
		Settore settore = this.settoreHome.getInstance();
		this.slotDefEditBean.save();
		SlotDef slotDef = this.slotDefHome.getInstance();
		settore.setSlotDef(slotDef);
		String settoreResult = this.settoreHome.update();
		if (settoreResult.equals("updated")) {
			return "associated";
		}

		return "failed";
	}

	public void useSlotDef(SlotDef slotDefToClone) {
		this.slotDefHome.slotDefClone(slotDefToClone);
	}

}
