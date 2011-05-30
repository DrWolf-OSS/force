package it.drwolf.slot.converters;

import it.drwolf.slot.pagebeans.support.PropertyContainer;
import it.drwolf.slot.ruleverifier.RuleParametersResolver;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Name("propertyContainerConverter")
@org.jboss.seam.annotations.faces.Converter(id = "propertyContainerConverter")
@BypassInterceptors
public class PropertyContainerConverter implements Converter {

	public Object getAsObject(FacesContext context, UIComponent component,
			String string) {

		RuleParametersResolver ruleParametersResolver = (RuleParametersResolver) org.jboss.seam.Component
				.getInstance("ruleParametersResolver");

		Object resolvedFieldDef = ruleParametersResolver
				.resolveFieldDef(string);
		PropertyContainer propertyContainer = new PropertyContainer(
				resolvedFieldDef);
		return propertyContainer;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object object) {
		return object.toString();
	}

}
