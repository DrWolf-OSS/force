package it.drwolf.force.entity.listeners;

import it.drwolf.force.entity.Rule;
import it.drwolf.force.ruleverifier.TimeValidity;

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
