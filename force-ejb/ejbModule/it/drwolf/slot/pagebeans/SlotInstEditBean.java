package it.drwolf.slot.pagebeans;

import it.drwolf.slot.entity.PropertyDef;
import it.drwolf.slot.entity.PropertyInst;
import it.drwolf.slot.session.SlotDefHome;
import it.drwolf.slot.session.SlotInstHome;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("slotInstEditBean")
@Scope(ScopeType.CONVERSATION)
public class SlotInstEditBean {

	@In(create = true)
	private SlotDefHome slotDefHome;

	@In(create = true)
	private SlotInstHome slotInstHome;

	private List<PropertyInst> propertyInsts;

	public List<PropertyInst> getPropertyInsts() {
		return propertyInsts;
	}

	public void setPropertyInsts(List<PropertyInst> propertyInsts) {
		this.propertyInsts = propertyInsts;
	}

	@Create
	public void init() {
		this.propertyInsts = new ArrayList<PropertyInst>();
		for (PropertyDef propertyDef : slotDefHome.getInstance()
				.getPropertyDefs()) {
			PropertyInst propertyInst = new PropertyInst(propertyDef,
					slotInstHome.getInstance());
			propertyInsts.add(propertyInst);
		}
	}
}
