package it.drwolf.slot.converters;

import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.application.CustomModelController;
import it.drwolf.slot.pagebeans.support.PropertyContainer;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Name("propertyContainerConverter")
@org.jboss.seam.annotations.faces.Converter(id = "propertyContainerConverter")
@BypassInterceptors
public class PropertyContainerConverter implements Converter {

	public Object getAsObject(FacesContext context, UIComponent component,
			String string) {
		int splitIndex = string.indexOf(":");
		String clazz = string.substring(0, splitIndex);

		// String[] splitted = string.split(":");
		// String clazz = splitted[0];

		if (clazz.equals("PropertyDef")) {
			String sid = string.substring(splitIndex + 1);
			Long id = new Long(sid);
			// Long id = new Long(splitted[1]);

			EntityManager entityManager = (EntityManager) org.jboss.seam.Component
					.getInstance("entityManager");
			List resultList = entityManager
					.createQuery("from " + clazz + " as c where c.id=:id")
					.setParameter("id", id).getResultList();
			if (resultList != null && !resultList.isEmpty()) {
				Object object = resultList.get(0);
				PropertyContainer propertyContainer = new PropertyContainer(
						object);
				return propertyContainer;
			}
		} else if (clazz.equals("Property")) {
			CustomModelController customModelController = (CustomModelController) org.jboss.seam.Component
					.getInstance("customModelController");
			// String name = splitted[1];
			String name = string.substring(splitIndex + 1);
			Property property = customModelController.getProperty(name);
			PropertyContainer propertyContainer = new PropertyContainer(
					property);
			return propertyContainer;
		}
		return null;
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object object) {
		return object.toString();
	}

}
