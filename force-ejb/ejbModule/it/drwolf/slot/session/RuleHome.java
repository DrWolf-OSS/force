package it.drwolf.slot.session;

import it.drwolf.slot.entity.*;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("ruleHome")
public class RuleHome extends EntityHome<Rule> {

	@In(create = true)
	SlotDefHome slotDefHome;

	public void setRuleId(Long id) {
		setId(id);
	}

	public Long getRuleId() {
		return (Long) getId();
	}

	@Override
	protected Rule createInstance() {
		Rule rule = new Rule();
		return rule;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		SlotDef slotDef = slotDefHome.getDefinedInstance();
		if (slotDef != null) {
			getInstance().setSlotDef(slotDef);
		}
	}

	public boolean isWired() {
		return true;
	}

	public Rule getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

}
