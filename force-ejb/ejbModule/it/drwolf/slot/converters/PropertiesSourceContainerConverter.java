package it.drwolf.slot.converters;

import it.drwolf.slot.pagebeans.support.PropertiesSourceContainer;
import it.drwolf.slot.ruleverifier.RuleParametersResolver;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Name("propertiesSourceContainerConverter")
@org.jboss.seam.annotations.faces.Converter(id = "propertiesSourceContainerConverter")
@BypassInterceptors
public class PropertiesSourceContainerConverter implements Converter {

	public Object getAsObject(FacesContext context, UIComponent component,
			String string) {

		RuleParametersResolver ruleParametersResolver = (RuleParametersResolver) org.jboss.seam.Component
				.getInstance("ruleParametersResolver");
		Object resolvedSourceDef = ruleParametersResolver
				.resolveSourceDef(string);

		PropertiesSourceContainer propertiesSourceContainer = new PropertiesSourceContainer(
				resolvedSourceDef);
		return propertiesSourceContainer;

	}

	public String getAsString(FacesContext context, UIComponent component,
			Object object) {
		// TODO Auto-generated method stub
		return object.toString();
	}

}
