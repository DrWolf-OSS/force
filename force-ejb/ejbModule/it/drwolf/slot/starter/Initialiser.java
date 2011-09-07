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

	private void createCertDictionary(EntityManager entityManager) {
		Dictionary dictionary = new Dictionary();
		dictionary.setName("Certificazioni");
		dictionary.setDataType(DataType.STRING);
		dictionary.getValues().add("ISO 9001:08");
		dictionary.getValues().add("ISO 14001");
		dictionary.getValues().add("EMAS");
		dictionary.getValues().add("ISO 14040 LCA");
		dictionary.getValues().add("PEFC");
		dictionary.getValues().add("EPD");
		dictionary.getValues().add("Emission trading");
		dictionary.getValues().add("ISO 14064 Gas serra");
		dictionary.getValues().add("Biomasse");
		dictionary.getValues().add("EN 16001");
		dictionary.getValues().add("BS 8901");
		dictionary.getValues().add("SA 8000");
		dictionary.getValues().add("AA1000");
		dictionary.getValues().add("ISO 22000");
		dictionary.getValues().add("HACCP");
		dictionary.getValues().add("FS 22000 (FSSC)");
		dictionary.getValues().add("ISO TS 22002 e PAS220");
		dictionary.getValues().add("UNI 11311 Tappi sintetici");
		dictionary.getValues().add("BRC (GSFS) FOOD");
		dictionary.getValues().add("IFS FOOD");
		dictionary.getValues().add("GOST R e Ukr");
		dictionary.getValues().add("Halal");
		dictionary.getValues().add("Kosher");
		dictionary.getValues().add("Prodotto agroalimentare");
		dictionary.getValues().add("Filiera controllata");
		dictionary.getValues().add("ISO 22005");
		dictionary.getValues().add("GlobalG.A.P. (Eurepgap)");
		dictionary.getValues().add("GRASP");
		dictionary.getValues().add("Disegno igienico");
		dictionary.getValues().add("Leaf Marque");
		dictionary.getValues().add("QS");
		dictionary.getValues().add("Produzione integrata");
		dictionary.getValues().add("Controlado por face");
		dictionary.getValues().add("UTZ Chain of custody");
		dictionary.getValues().add("ISO 24114");
		dictionary.getValues().add("Friend of the sea");
		dictionary.getValues().add("Biodiversity Friend");
		dictionary.getValues().add("MSC");
		dictionary.getValues().add("UNI 11381");
		dictionary.getValues().add("Artigelato");
		dictionary.getValues().add("DOP");
		dictionary.getValues().add("IGP");
		dictionary.getValues().add("STG");
		dictionary.getValues().add("Etichettatura carni");
		dictionary.getValues().add("Agriqualità");
		dictionary.getValues().add("Fami-QS");
		dictionary.getValues().add("GMP+");
		dictionary.getValues().add("Codex Assalzoo");
		dictionary.getValues().add("BRC/IoP (GSPP)");
		dictionary.getValues().add("GMP FEFCO/ESBO");
		dictionary.getValues().add("UNI EN 15593");
		dictionary.getValues().add("BRC Storage & Distibution (GSSD)");
		dictionary.getValues().add("IFS logistic (ILS)");
		dictionary.getValues().add("IFS Broker");
		dictionary.getValues().add("IFS Cash&Carry");
		dictionary.getValues().add("Certificazione di servizio");
		dictionary.getValues().add("UNI 11034");
		dictionary.getValues().add("UNI 10865");
		dictionary.getValues().add("UNI 10670");
		dictionary.getValues().add("UNI 10881");
		dictionary.getValues().add("UNI 10600");
		dictionary.getValues().add("UNI 10668");
		dictionary.getValues().add("UNI EN 14804");
		dictionary.getValues().add("UNI 10891");
		dictionary.getValues().add("TQS Vending");
		dictionary.getValues().add("ISO 27001");
		dictionary.getValues().add("ISO 20000");
		dictionary.getValues().add("QWeb");
		dictionary.getValues().add("Accessibilità dei siti internet");
		dictionary.getValues().add("Gestione del rischio");
		dictionary.getValues().add("Sabotaggio (tampering)");
		dictionary.getValues().add("BS OHSAS 18001");
		dictionary.getValues().add("Attività di ispezione");
		dictionary.getValues().add("Reg. 233/2011");
		dictionary.getValues().add("ISO 22716");
		entityManager.persist(dictionary);
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

	private void createLavoroDisabileDictionary(EntityManager entityManager) {
		Dictionary dictionary = new Dictionary();
		dictionary.setName("Dizionario sui Lavoratori Disabili");
		dictionary.setDataType(DataType.STRING);
		dictionary.getValues().add("Si");
		dictionary.getValues().add("Dovrei");
		dictionary.getValues().add("No");
		entityManager.persist(dictionary);
	}

	private void createStrDictionary(EntityManager entityManager) {
		Dictionary dictionary = new Dictionary();
		dictionary.setName("Dizionare di Prova Strimga");
		dictionary.setDataType(DataType.STRING);
		dictionary.getValues().add("Stringa");
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
			}
		}
		this.checkParams(entityManager);

		//
		// this.createStrDictionary(entityManager);
		// this.createIntDictionary(entityManager);
		// this.createDateDictionary(entityManager);
		// this.createCertDictionary(entityManager);
		this.createLavoroDisabileDictionary(entityManager);
		//
		System.out.println("---> Checked");
	}

}
