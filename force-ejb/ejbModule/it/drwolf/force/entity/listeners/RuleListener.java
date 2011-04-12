package it.drwolf.force.entity.listeners;

import it.drwolf.force.entity.Rule;

import javax.persistence.PostLoad;

public class RuleListener {

	@PostLoad
	public void setVerifier(Rule rule) {
		// switch su RuleType e assegnazione del verifier giusto

	}

}
