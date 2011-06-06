package it.drwolf.slot.interfaces;

import it.drwolf.slot.ruleverifier.VerifierParameterDef;
import it.drwolf.slot.ruleverifier.VerifierParameterInst;
import it.drwolf.slot.ruleverifier.VerifierReport;

import java.util.List;
import java.util.Map;

public interface IRuleVerifier {

	public VerifierReport verify(Map<String, VerifierParameterInst> params);

	public List<VerifierParameterDef> getInParams();

}