package it.drwolf.force.session.lists;

import it.drwolf.force.entity.Soa;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("soaList")
public class SoaList extends EntityQuery<Soa> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -165084991194423360L;

	private static final String EJBQL = "select soa from Soa soa";

	private static final String[] RESTRICTIONS = {
			"lower(soa.nome) like lower(concat(#{soaList.soa.nome},'%'))",
			"lower(soa.codice) like lower(concat(#{soaList.soa.codice},'%'))", };

	private Soa soa = new Soa();

	public SoaList() {
		this.setEjbql(SoaList.EJBQL);
		this.setRestrictionExpressionStrings(Arrays
				.asList(SoaList.RESTRICTIONS));

	}

	public Soa getSoa() {
		return this.soa;
	}

}
