package it.drwolf.force.session.lists;

import it.drwolf.force.entity.Gara;
import it.drwolf.force.enums.TipoGara;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("garaList")
public class GaraList extends EntityQuery<Gara> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -165084991194423360L;

	private static final String EJBQL = "select gara from Gara gara";

	private static final String[] RESTRICTIONS = { "lower(gara.oggetto) like lower(concat(#{garaList.gara.oggetto},'%'))", };

	private Gara gara = new Gara();

	public GaraList() {
		this.setEjbql(GaraList.EJBQL);
		this.setOrder("dataScadenza desc");
		this.setRestrictionExpressionStrings(Arrays
				.asList(GaraList.RESTRICTIONS));
	}

	public Gara getGara() {
		return this.gara;
	}

	public List<Gara> getGaraAttive(Integer maxRes) {
		ArrayList<Gara> ritorno = new ArrayList<Gara>();
		for (Gara gara : this.getResultList()) {
			if (gara.getType().equals(TipoGara.GESTITA.getNome())) {
				ritorno.add(gara);
			}
			// se maxRes è 0 vuol dire che le voglio tutte
			if (maxRes > 0) {
				if (ritorno.size() == maxRes) {
					break;
				}
			}
		}
		return ritorno;
	}

	public List<Gara> getGaraNuove() {
		ArrayList<Gara> ritorno = new ArrayList<Gara>();
		for (Gara gara : this.getResultList()) {
			if (gara.getType().equals(TipoGara.NUOVA.getNome())) {
				ritorno.add(gara);
			}
		}
		return ritorno;
	}
}
