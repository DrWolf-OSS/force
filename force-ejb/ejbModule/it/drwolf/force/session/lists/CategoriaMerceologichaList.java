package it.drwolf.force.session.lists;

import it.drwolf.force.entity.CategoriaMerceologica;

import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("categoriaMerceologicaList")
public class CategoriaMerceologichaList extends
		EntityQuery<CategoriaMerceologica> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4942397425343823929L;

	private static final String EJBQL = "select categoriaMerceologica from CategoriaMerceologica categoriaMerceologica";

	private static final String[] RESTRICTIONS = {
			"lower(categoriaMerceologica.categoria) like lower(concat(#{categoriaMerceologicaList.categoriaMerceologica.categoria},'%'))",
			"lower(categoriaMerceologica.type) like lower(concat(#{categoriaMerceologicaList.categoriaMerceologica.type},'%'))", };

	private CategoriaMerceologica categoriaMerceologica = new CategoriaMerceologica();

	public CategoriaMerceologichaList() {
		this.setEjbql(CategoriaMerceologichaList.EJBQL);
		this.setRestrictionExpressionStrings(Arrays
				.asList(CategoriaMerceologichaList.RESTRICTIONS));
	}

	public CategoriaMerceologica getCategoriaMerceologica() {
		return this.categoriaMerceologica;
	}

	public List<CategoriaMerceologica> getElencoBeni() {
		this.getCategoriaMerceologica().setType("BENE");
		return this.getResultList();
	}

	public List<CategoriaMerceologica> getElencoServizi() {
		this.categoriaMerceologica.setType("SERVIZO");
		return this.getResultList();
	}
}
