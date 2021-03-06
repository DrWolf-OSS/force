package it.drwolf.force.session.lists;

import it.drwolf.force.entity.Azienda;

import java.util.Arrays;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("revokedAziendaList")
public class RevokedAziendaList extends EntityQuery<Azienda> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5798978382991605299L;

	private static final String EJBQL = "select azienda from Azienda azienda";

	private static final String[] RESTRICTIONS = {
			"lower(azienda.alfrescoGroupId) like lower(concat(#{aziendaList.azienda.alfrescoGroupId},'%'))",
			"lower(azienda.cap) like lower(concat(#{aziendaList.azienda.cap},'%'))",
			"lower(azienda.cellulare) like lower(concat(#{aziendaList.azienda.cellulare},'%'))",
			"lower(azienda.cognome) like lower(concat(#{aziendaList.azienda.cognome},'%'))",
			"lower(azienda.comune) like lower(concat(#{aziendaList.azienda.comune},'%'))",
			"lower(azienda.email) like lower(concat(#{aziendaList.azienda.email},'%'))",
			"lower(azienda.emailCertificata) like lower(concat(#{aziendaList.azienda.emailCertificata},'%'))",
			"lower(azienda.emailReferente) like lower(concat(#{aziendaList.azienda.emailReferente},'%'))",
			"lower(azienda.fax) like lower(concat(#{aziendaList.azienda.fax},'%'))",
			"lower(azienda.indirizzo) like lower(concat(#{aziendaList.azienda.indirizzo},'%'))",
			"lower(azienda.localita) like lower(concat(#{aziendaList.azienda.localita},'%'))",
			"lower(azienda.nome) like lower(concat(#{aziendaList.azienda.nome},'%'))",
			"lower(azienda.provincia) like lower(concat(#{aziendaList.azienda.provincia},'%'))",
			"lower(azienda.ragioneSociale) like lower(concat(#{aziendaList.azienda.ragioneSociale},'%'))",
			"lower(azienda.stato) like lower(concat(#{'REVOCATA'},'%'))",
			"lower(azienda.telefono) like lower(concat(#{aziendaList.azienda.telefono},'%'))", };

	private Azienda azienda = new Azienda();

	public RevokedAziendaList() {
		this.setEjbql(RevokedAziendaList.EJBQL);
		this.setRestrictionExpressionStrings(Arrays
				.asList(RevokedAziendaList.RESTRICTIONS));
		this.setMaxResults(25);
	}

	public Azienda getAzienda() {
		return this.azienda;
	}

	@Override
	public List<Azienda> getResultList() {
		return super.getResultList();
	}
}
