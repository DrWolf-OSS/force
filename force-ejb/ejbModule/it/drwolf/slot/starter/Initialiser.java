package it.drwolf.slot.starter;

import it.drwolf.slot.entity.Preference;

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
				entityManager = (EntityManager) Component
						.getInstance("entityManager");
				System.out.println("---> Sleep!");
				Thread.sleep(2000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		checkParams(entityManager);
		System.out.println("---> Checked");
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
