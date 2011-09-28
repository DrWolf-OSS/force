package it.drwolf.slot.starter;

import it.drwolf.slot.entity.Dictionary;
import it.drwolf.slot.entity.Preference;
import it.drwolf.slot.enums.DataType;

import java.util.List;

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

	// private void createDateDictionary(EntityManager entityManager) {
	// Dictionary dictionary = new Dictionary();
	// dictionary.setName("dizionario di date 2");
	// dictionary.setDataType(DataType.DATE);
	// dictionary.getValues().add("01/07/2011");
	// entityManager.persist(dictionary);
	// }

	private void createLavoroDisabileDictionary(EntityManager entityManager) {
		List res = entityManager
				.createQuery("from Dictionary d where d.name = :name")
				.setParameter("name", "Dizionario sui Lavoratori Disabili")
				.getResultList();

		if (res.size() == 0) {
			Dictionary dictionary = new Dictionary();
			dictionary.setName("Dizionario sui Lavoratori Disabili");
			dictionary.setDataType(DataType.STRING);
			dictionary.getValues().add("Si");
			dictionary.getValues().add("Sotto 15 dipendenti");
			dictionary.getValues().add("No");
			entityManager.persist(dictionary);
		}
	}

	private void createTipoPartecipazioneDictonary(EntityManager entityManager) {
		List res = entityManager
				.createQuery("from Dictionary d where d.name = :name")
				.setParameter("name",
						"Dizionario sulle Tipologie di Partecipazione")
				.getResultList();

		if (res.size() == 0) {
			Dictionary dictionary = new Dictionary();
			dictionary.setName("Dizionario sulle Tipologie di Partecipazione");
			dictionary.setDataType(DataType.STRING);
			dictionary.getValues().add("Singola");
			dictionary.getValues().add("ATI Costituenda");
			dictionary.getValues().add("ATI Costituita");
			dictionary.getValues().add("Consorzio");
			dictionary.getValues().add("G.E.I.E.");
			entityManager.persist(dictionary);
		}
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
			}
		}
		this.checkParams(entityManager);

		this.createLavoroDisabileDictionary(entityManager);
		this.createTipoPartecipazioneDictonary(entityManager);
		System.out.println("---> Checked");
	}

}
