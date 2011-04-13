package it.drwolf.force.interfaces;

import it.drwolf.force.ruleverifier.VerifierParameter;

import java.util.List;
import java.util.Map;

public interface IRuleVerifier {

	public Object verify(Map<String, Object> params);

	public List<VerifierParameter> getInParams();

}
