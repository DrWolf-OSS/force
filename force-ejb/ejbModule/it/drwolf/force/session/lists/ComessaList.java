package it.drwolf.force.session.lists;

import it.drwolf.force.entity.Commessa;
import it.drwolf.force.entity.SOA;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("commessaList")
public class ComessaList extends EntityQuery<SOA> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -165084991194423360L;

	private static final String EJBQL = "select commessa from Commessa commessa";

	private static final String[] RESTRICTIONS = { "lower(commessa.committente) like lower(concat(#{commessaList.commessa.committente},'%'))", };

	private Commessa commessa = new Commessa();

	public ComessaList() {
		this.setEjbql(ComessaList.EJBQL);
		this.setRestrictionExpressionStrings(Arrays
				.asList(ComessaList.RESTRICTIONS));

	}

	public Commessa getCommessa() {
		return this.commessa;
	}

}
