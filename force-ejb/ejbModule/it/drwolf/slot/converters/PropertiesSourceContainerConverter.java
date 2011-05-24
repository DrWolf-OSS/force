package it.drwolf.slot.converters;

import it.drwolf.slot.pagebeans.support.PropertiesSourceContainer;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Name("propertiesSourceContainerConverter")
@org.jboss.seam.annotations.faces.Converter(id = "propertiesSourceContainerConverter")
@BypassInterceptors
public class PropertiesSourceContainerConverter implements Converter {

	public Object getAsObject(FacesContext context, UIComponent component,
			String string) {

		String[] splitted = string.split(":");
		String clazz = splitted[0];
		Long id = new Long(splitted[1]);

		EntityManager entityManager = (EntityManager) org.jboss.seam.Component
				.getInstance("entityManager");
		List resultList = entityManager
				.createQuery("from " + clazz + " as c where c.id=:id")
				.setParameter("id", id).getResultList();
		if (resultList != null && !resultList.isEmpty()) {
			Object object = resultList.get(0);
			PropertiesSourceContainer propertiesSourceContainer = new PropertiesSourceContainer(
					object);
			return propertiesSourceContainer;
		}
		return null;
	}

	public String getAsString(FacesContext context, UIComponent component,
			Object object) {
		// TODO Auto-generated method stub
		return object.toString();
	}

}
