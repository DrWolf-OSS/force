package it.drwolf.force.session.lists;

import it.drwolf.force.entity.Commessa;
import it.drwolf.force.session.homes.AziendaHome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("commessaList")
public class ComessaList extends EntityQuery<Commessa> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -165084991194423360L;

	private static final String EJBQL = "select commessa from Commessa commessa";

	@In(create = true)
	private AziendaHome aziendaHome;

	private static final String[] RESTRICTIONS = {
			"lower(commessa.committente) like lower(concat(#{commessaList.commessa.committente},'%'))",
			"lower(commessa.descrizione) like lower(concat(#{commessaList.commessa.descrizione},'%'))", };

	private Commessa commessa = new Commessa();

	public ComessaList() {
		this.setEjbql(ComessaList.EJBQL);
		this.setRestrictionExpressionStrings(Arrays
				.asList(ComessaList.RESTRICTIONS));
	}

	public Commessa getCommessa() {
		return this.commessa;
	}

	public List<Commessa> getCommesseForAzienda() {
		ArrayList<Commessa> ritorno = new ArrayList<Commessa>() {
		};
		List<Commessa> lista = super.getResultList();
		for (Commessa commessa : lista) {
			if (commessa.getAzienda().equals(this.aziendaHome.getInstance())) {
				ritorno.add(commessa);
			}
		}
		return ritorno;
	}

	@Override
	public List<Commessa> getResultList() {
		this.getCommessa().setAzienda(this.aziendaHome.getInstance());
		return super.getResultList();
	}

}
