package it.drwolf.slot.entity.listeners;

import it.drwolf.slot.entity.Rule;
import it.drwolf.slot.ruleverifier.TimeValidity;

import javax.persistence.PostLoad;

public class RuleListener {

	@PostLoad
	public void setVerifier(Rule rule) {
		// switch su RuleType e assegnazione del verifier giusto
		switch (rule.getType()) {
		case TIME_VALIDITY:
			rule.setVerifier(new TimeValidity());
			break;

		default:
			break;
		}

	}

}