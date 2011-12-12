package it.drwolf.slot.ruleverifier;

import it.drwolf.slot.alfresco.custom.model.Property;
import it.drwolf.slot.application.CustomModelController;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("ruleParametersResolver")
@Scope(ScopeType.CONVERSATION)
public class RuleParametersResolver {

	public Object resolveFieldDef(String field) {
		int splitIndex = field.indexOf(":");
		String clazz = field.substring(0, splitIndex);

		// if (clazz.equals("Property")) {
		if (clazz.equals(Property.class.getName())) {
			CustomModelController customModelController = (CustomModelController) org.jboss.seam.Component
					.getInstance("customModelController");
			String name = field.substring(splitIndex + 1);
			return customModelController.getProperty(name);
		} else {
			String sid = field.substring(splitIndex + 1);
			Long id = new Long(sid);

			EntityManager entityManager = (EntityManager) org.jboss.seam.Component
					.getInstance("entityManager");
			List resultList = entityManager
					.createQuery("from " + clazz + " as c where c.id=:id")
					.setParameter("id", id).getResultList();
			if (resultList != null && !resultList.isEmpty()) {
				return resultList.get(0);
			}
		}

		return null;
	}

	public Object resolveSourceDef(String source) {
		int splitIndex = source.indexOf(":");
		String clazz = source.substring(0, splitIndex);

		Long id = new Long(source.substring(splitIndex + 1));

		EntityManager entityManager = (EntityManager) org.jboss.seam.Component
				.getInstance("entityManager");
		List resultList = entityManager
				.createQuery("from " + clazz + " as c where c.id=:id")
				.setParameter("id", id).getResultList();
		if (resultList != null && !resultList.isEmpty()) {
			return resultList.get(0);
		}
		return null;
	}

}
