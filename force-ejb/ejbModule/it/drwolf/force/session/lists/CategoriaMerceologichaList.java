package it.drwolf.force.session.lists;

import it.drwolf.force.entity.CategoriaMerceologica;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

@Name("categoriaMerceologicaList")
@Scope(ScopeType.CONVERSATION)
public class CategoriaMerceologichaList {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4942397425343823929L;

	@In
	private EntityManager entityManager;

	@SuppressWarnings("unused")
	@Out(required = false)
	private List<CategoriaMerceologica> listaBeni = null;

	@SuppressWarnings("unused")
	@Out(required = false)
	private List<CategoriaMerceologica> listaServizi = null;

	@SuppressWarnings("unused")
	@Out(required = false)
	private List<CategoriaMerceologica> listaCategorieMerceologiche = null;

	@SuppressWarnings("unchecked")
	@Factory("listaBeni")
	public void getListaBeni() {
		this.listaBeni = this.entityManager
				.createQuery(
						"from CategoriaMerceologica cm where cm.type = 'BENE' order by  categoria")
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Factory("listaCategorieMerceologiche")
	public void getListaCategorieMerceologiche() {
		this.listaCategorieMerceologiche = this.entityManager.createQuery(
				"from CategoriaMerceologica order by type, categoria")
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Factory("listaServizi")
	public void getListaServizi() {
		this.listaServizi = this.entityManager
				.createQuery(
						"from CategoriaMerceologica cm where cm.type = 'SERVIZIO' order by  categoria")
				.getResultList();
	}

	public void setListaBeni(List<CategoriaMerceologica> listaBeni) {
		this.listaBeni = listaBeni;
	}

	public void setListaServizi(List<CategoriaMerceologica> listaServizi) {
		this.listaServizi = listaServizi;
	}

}
