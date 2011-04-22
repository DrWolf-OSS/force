package it.drwolf.slot.prefs;

import it.drwolf.slot.entity.Preference;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("preferences")
@Scope(ScopeType.CONVERSATION)
public class Preferences {

	@In(create = true)
	private EntityManager entityManager;

	private HashMap<String, String> preferences = new HashMap<String, String>();

	@Create
	public void init() {
		@SuppressWarnings("unchecked")
		List<Preference> resultList = entityManager.createQuery(
				"from Preference").getResultList();
		if (resultList != null) {
			for (Preference p : resultList) {
				preferences.put(p.getKeyValue(), p.getStringValue());
			}
		}
	}

	public String getValue(String key) {
		return preferences.get(key);
	}

}
