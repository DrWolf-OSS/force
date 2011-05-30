package it.drwolf.slot.interfaces;

import it.drwolf.slot.ruleverifier.VerifierParameter;

import java.util.List;
import java.util.Map;

public interface IRuleVerifier {

	public Boolean verify(Map<String, Object> params);

	public List<VerifierParameter> getInParams();

}