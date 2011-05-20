package it.drwolf.force.session.lists;

import it.drwolf.force.entity.Settore;

import java.util.Arrays;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("settoreList")
public class SettoreList extends EntityQuery<Settore> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7649921355945017542L;

	private static final String EJBQL = "select settore from Settore settore";

	private static final String[] RESTRICTIONS = { "lower(settore.nome) like lower(concat(#{settoreList.settore.nome},'%'))", };

	private Settore settore = new Settore();

	public SettoreList() {
		this.setEjbql(SettoreList.EJBQL);
		this.setRestrictionExpressionStrings(Arrays
				.asList(SettoreList.RESTRICTIONS));
		this.setMaxResults(null);
		this.setOrder("settore.nome");
	}

	public Settore getSettore() {
		return this.settore;
	}

}
