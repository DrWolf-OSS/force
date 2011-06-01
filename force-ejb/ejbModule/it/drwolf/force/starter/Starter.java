package it.drwolf.force.starter;

import it.drwolf.force.entity.FormaGiuridica;
import it.drwolf.force.entity.Settore;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;

@Name("starter")
public class Starter {

	@Observer("org.jboss.seam.postInitialization")
	@Asynchronous
	@Transactional
	public void start() {
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

		for (FormaGiuridica.Default d : FormaGiuridica.Default.values()) {
			if (entityManager.find(FormaGiuridica.class, d.getId()) == null) {
				FormaGiuridica fg = new FormaGiuridica();
				fg.setId(d.getId());
				fg.setNome(d.getNome());
				entityManager.persist(fg);
			}

		}

		for (Settore.Default d : Settore.Default.values()) {
			if (entityManager.find(Settore.class, d.getId()) == null) {
				Settore s = new Settore();
				s.setId(d.getId());
				s.setNome(d.getNome());
				entityManager.persist(s);
			}
		}
		entityManager.flush();
		entityManager.clear();
	}
}