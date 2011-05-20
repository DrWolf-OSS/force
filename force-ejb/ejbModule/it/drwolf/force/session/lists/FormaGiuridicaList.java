package it.drwolf.force.session.lists;

import it.drwolf.force.entity.FormaGiuridica;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("formaGiuridicaList")
public class FormaGiuridicaList extends EntityQuery<FormaGiuridica> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8033644762869085695L;

	private static final String EJBQL = "select formaGiuridica from FormaGiuridica formaGiuridica";

	private static final String[] RESTRICTIONS = { "lower(formaGiuridica.nome) like lower(concat(#{formaGiuridicaList.formaGiuridica.nome},'%'))", };

	private FormaGiuridica formaGiuridica = new FormaGiuridica();

	public FormaGiuridicaList() {
		this.setEjbql(FormaGiuridicaList.EJBQL);
		this.setRestrictionExpressionStrings(Arrays
				.asList(FormaGiuridicaList.RESTRICTIONS));
		this.setMaxResults(null);
		this.setOrder("formaGiuridica.nome");
	}

	public FormaGiuridica getFormaGiuridica() {
		return this.formaGiuridica;
	}

}
