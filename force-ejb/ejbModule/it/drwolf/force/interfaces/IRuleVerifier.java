package it.drwolf.force.interfaces;

import java.util.List;
import java.util.Map;

public interface IRuleVerifier {

	public Object verify(Map<String, Object> params);

	public List<String> getInParams();

}
