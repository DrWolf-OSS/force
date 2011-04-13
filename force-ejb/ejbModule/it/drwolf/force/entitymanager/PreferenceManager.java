package it.drwolf.force.entitymanager;

import it.drwolf.force.entity.Preference;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("preferenceManager")
@Scope(ScopeType.CONVERSATION)
public class PreferenceManager {

	@In(create = true)
	EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public Preference getPreference(String preferencekey) {
		Preference preference = null;
		List<Preference> resultList = entityManager
				.createQuery(
						"from Preferences p where p.keyValue=:preferenceKey")
				.setParameter("preferenceKey", preferencekey).getResultList();
		if (!resultList.isEmpty()) {
			preference = resultList.get(0);
		}
		return preference;
	}

}
