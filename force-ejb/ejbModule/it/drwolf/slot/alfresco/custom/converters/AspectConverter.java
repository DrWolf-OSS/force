package it.drwolf.slot.alfresco.custom.converters;

import it.drwolf.slot.alfresco.custom.model.Aspect;
import it.drwolf.slot.application.CustomModelController;

import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Name("aspectConverter")
@org.jboss.seam.annotations.faces.Converter(id = "aspectConverter")
@BypassInterceptors
public class AspectConverter implements Converter {

	public Object getAsObject(FacesContext context, UIComponent component,
			String aspectId) {
		CustomModelController customModelController = (CustomModelController) org.jboss.seam.Component
				.getInstance("customModelController");
		List<Aspect> aspectsAsObjects = customModelController.getSlotModel()
				.getAspects();
		Iterator<Aspect> iterator = aspectsAsObjects.iterator();

		while (iterator.hasNext()) {
			Aspect aspect = iterator.next();
			if (aspect.getId().equals(aspectId)) {
				return aspect;
			}
		}
		return null;
	}

	public String getAsString(FacesContext context, UIComponent component,
			Object object) {
		return ((Aspect) object).getId();
	}

}
