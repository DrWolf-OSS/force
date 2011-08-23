package it.drwolf.slot.starter;

import it.drwolf.slot.entity.Dictionary;
import it.drwolf.slot.entity.Preference;
import it.drwolf.slot.enums.DataType;

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

	private void createDateDictionary(EntityManager entityManager) {
		Dictionary dictionary = new Dictionary();
		dictionary.setName("dizionario di date 2");
		dictionary.setDataType(DataType.DATE);
		dictionary.getValues().add("01/07/2011");
		entityManager.persist(dictionary);
	}

	private void createIntDictionary(EntityManager entityManager) {
		Dictionary dictionary = new Dictionary();
		dictionary.setDataType(DataType.INTEGER);
		dictionary.setName("dizionario di interi");
		dictionary.getValues().add("1");
		dictionary.getValues().add("2");
		dictionary.getValues().add("3");
		dictionary.getValues().add("4");
		dictionary.getValues().add("5");
		entityManager.persist(dictionary);
	}

	private void createStrDictionary(EntityManager entityManager) {
		Dictionary dictionary = new Dictionary();
		dictionary.setName("dizionario di prova");
		dictionary.setDataType(DataType.STRING);
		dictionary.getValues().add("mela");
		dictionary.getValues().add("pera");
		dictionary.getValues().add("pesca");
		dictionary.getValues().add("banana");
		dictionary.getValues().add("ananas");
		entityManager.persist(dictionary);
	}

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
		this.checkParams(entityManager);

		//
		// this.createStrDictionary(entityManager);
		// this.createIntDictionary(entityManager);
		// this.createDateDictionary(entityManager);
		//
		System.out.println("---> Checked");
	}

}
