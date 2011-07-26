package it.drwolf.force.session.lists;

import it.drwolf.force.entity.SOA;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("soaList")
public class SOAList extends EntityQuery<SOA> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -165084991194423360L;

	private static final String EJBQL = "select soa from SOA soa";

	private static final String[] RESTRICTIONS = {
			"lower(soa.nome) like lower(concat(#{soaList.soa.nome},'%'))",
			"lower(soa.codice) like lower(concat(#{soaList.soa.codice},'%'))", };

	private SOA soa = new SOA();

	public SOAList() {
		this.setEjbql(SOAList.EJBQL);
		this.setRestrictionExpressionStrings(Arrays
				.asList(SOAList.RESTRICTIONS));

	}

	public SOA getSoa() {
		return this.soa;
	}

}
