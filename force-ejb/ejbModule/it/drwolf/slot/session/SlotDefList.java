package it.drwolf.slot.session;

import it.drwolf.slot.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("slotDefList")
public class SlotDefList extends EntityQuery<SlotDef> {

	private static final String EJBQL = "select slotDef from SlotDef slotDef";

	private static final String[] RESTRICTIONS = { "lower(slotDef.name) like lower(concat(#{slotDefList.slotDef.name},'%'))", };

	private SlotDef slotDef = new SlotDef();

	public SlotDefList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public SlotDef getSlotDef() {
		return slotDef;
	}
}
