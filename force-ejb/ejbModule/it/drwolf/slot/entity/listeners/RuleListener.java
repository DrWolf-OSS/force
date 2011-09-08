package it.drwolf.slot.entity.listeners;

import it.drwolf.slot.entity.Rule;
import it.drwolf.slot.ruleverifier.InstantDateValidity;
import it.drwolf.slot.ruleverifier.DateComparison;

import javax.persistence.PostLoad;

public class RuleListener {

	@PostLoad
	public void setVerifier(Rule rule) {
		if (rule.getType() != null) {
			// switch su RuleType e assegnazione del verifier giusto
			switch (rule.getType()) {
			case DATE_COMPARISON:
				rule.setVerifier(new DateComparison());
				break;

			case INSTANT_DATE_VALIDITY:
				rule.setVerifier(new InstantDateValidity());
				break;

			default:
				rule.setVerifier(null);
				break;
			}
		} else {
			rule.setVerifier(null);
		}
	}

}
