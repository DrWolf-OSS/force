package it.drwolf.force.starter;

import it.drwolf.force.entity.Preference;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;

@Name("initialiser")
@Scope(ScopeType.EVENT)
@Transactional
public class Initialiser {

	@Observer("org.jboss.seam.postInitialization")
	@Asynchronous
	@Transactional
	public void initialise() {
		EntityManager entityManager = null;

		while (entityManager == null) {
			try {
				Thread.sleep(1000);
				entityManager = (EntityManager) Component
						.getInstance("entityManager");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		checkParams(entityManager);
	}

	private void checkParams(EntityManager entityManager) {
		for (Preference ap : Preference.defaults) {
			try {
				if (entityManager
						.createQuery(
								"from Preference m where m.keyValue=:keyValue")
						.setParameter("keyValue", ap.getKeyValue())
						.getResultList().size() < 1) {
					entityManager.persist(ap);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
