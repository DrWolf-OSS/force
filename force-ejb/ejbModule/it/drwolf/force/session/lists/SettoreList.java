package it.drwolf.force.session.lists;

import it.drwolf.force.entity.Settore;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

@Name("settoreList")
@Scope(ScopeType.CONVERSATION)
public class SettoreList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7649921355945017542L;

	@In
	private EntityManager entityManager;

	@SuppressWarnings("unused")
	@Out(required = true)
	private List<Settore> listaSettori = null;

	@SuppressWarnings("unchecked")
	@Factory("listaSettori")
	public void getListaSettori() {
		this.listaSettori = this.entityManager.createQuery("from Settore")
				.getResultList();
	}

	public void setListaSettori(List<Settore> settoreList) {
		this.listaSettori = settoreList;
	}
}
