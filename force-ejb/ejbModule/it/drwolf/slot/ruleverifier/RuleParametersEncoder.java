package it.drwolf.slot.ruleverifier;

import it.drwolf.slot.pagebeans.support.PropertiesSourceContainer;
import it.drwolf.slot.pagebeans.support.PropertyContainer;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("ruleParametersEncoder")
@Scope(ScopeType.CONVERSATION)
public class RuleParametersEncoder {

	public String encode(PropertiesSourceContainer sourceContainer,
			PropertyContainer propertyContainer) {
		String encodedParam = null;
		if (sourceContainer != null && propertyContainer != null) {
			encodedParam = sourceContainer.toString();
			encodedParam = encodedParam
					.concat("|" + propertyContainer.toString());
		}
		return encodedParam;
	}

}
