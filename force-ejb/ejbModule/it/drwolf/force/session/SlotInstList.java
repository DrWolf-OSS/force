package it.drwolf.force.session;

import it.drwolf.force.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("slotInstList")
public class SlotInstList extends EntityQuery<SlotInst> {

	private static final String EJBQL = "select slotInst from SlotInst slotInst";

	private static final String[] RESTRICTIONS = {};

	private SlotInst slotInst = new SlotInst();

	public SlotInstList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public SlotInst getSlotInst() {
		return slotInst;
	}
}
