package it.drwolf.slot.entity.listeners;

import it.drwolf.slot.entity.RuleParameterInst;
import it.drwolf.slot.interfaces.IRuleVerifier;
import it.drwolf.slot.ruleverifier.VerifierParameterDef;

import java.util.Iterator;

import javax.persistence.PostLoad;

public class RuleParameterInstListener {

	@PostLoad
	public void setVerifierParameterDef(RuleParameterInst parameterInst) {
		IRuleVerifier verifier = parameterInst.getRule().getVerifier();
		Iterator<VerifierParameterDef> iterator = verifier.getInParams()
				.iterator();
		while (iterator.hasNext()) {
			VerifierParameterDef parameterDef = iterator.next();
			if (parameterDef.getName().equals(parameterInst.getParameterName())) {
				parameterInst.setVerifierParameterDef(parameterDef);
				return;
			}
		}
	}

}
