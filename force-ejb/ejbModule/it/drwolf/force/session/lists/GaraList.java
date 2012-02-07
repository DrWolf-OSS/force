package it.drwolf.force.session.lists;

import it.drwolf.force.entity.Gara;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;

@Name("garaList")
public class GaraList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -165084991194423360L;

	@In
	private EntityManager entityManager;

	@SuppressWarnings("unused")
	@Out(required = false)
	private List<Gara> listaGareNuove = null; // gare non gestire
												// automaticamente dal sistema

	@SuppressWarnings("unused")
	@Out(required = false)
	private List<Gara> listaGareGestite = null; // gare gestite automaticamente.
												// Il Crawler è riuscito ad
												// individuare le categorie
												// merceologihe o le SOA
												// associate

	@SuppressWarnings("unused")
	@Out(required = false)
	private List<Gara> listaGareAttive = null; // gare che hanno una busta
												// definita da admin

	@SuppressWarnings("unchecked")
	@Factory("listaGareAttive")
	public void getListaGareAttive() {
		this.listaGareAttive = this.entityManager
				.createQuery(
						"select g from Gara g left join g.slotDefs sd where g.type = 'GESTITA' and sd.ownerId='ADMIN'  order by g.dataScadenza desc")
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Factory("listaGareGestite")
	public void getListaGareGestire() {
		this.listaGareGestite = this.entityManager
				.createQuery(
						"from Gara g where g.type = 'GESTITA' order by g.dataScadenza desc")
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Factory("listaGareNuove")
	public void getListaGareNuove() {
		this.listaGareNuove = this.entityManager
				.createQuery(
						"from Gara g where g.type = 'NUOVA'  order by g.dataScadenza desc")
				.getResultList();
	}

	public void setListaGareAttive(List<Gara> listaGareAttive) {
		this.listaGareAttive = listaGareAttive;
	}

	public void setListaGareGestite(List<Gara> listaGareGestite) {
		this.listaGareGestite = listaGareGestite;
	}

	public void setListaGareNuove(List<Gara> listaGareNuove) {
		this.listaGareNuove = listaGareNuove;
	}
}
