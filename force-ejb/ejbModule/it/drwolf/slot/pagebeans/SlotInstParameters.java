package it.drwolf.slot.pagebeans;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("slotInstParameters")
@Scope(ScopeType.CONVERSATION)
public class SlotInstParameters {

	public static final String UPDATED = "updated";
	public static final String SAVED = "saved";

	private String outcome;

	public String getOutcome() {
		return this.outcome;
	}

	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}
}
