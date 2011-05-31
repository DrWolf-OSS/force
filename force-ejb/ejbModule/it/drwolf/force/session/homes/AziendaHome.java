package it.drwolf.force.session.homes;

import it.drwolf.force.entity.Azienda;
import it.drwolf.force.enums.StatoAzienda;

import java.util.Date;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("aziendaHome")
public class AziendaHome extends EntityHome<Azienda> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1214123840587373460L;

	private String partitaIva;
	private String codiceFiscale;
	private Date dataCostituzione;

	@Override
	protected Azienda createInstance() {
		Azienda azienda = new Azienda();
		return azienda;
	}

	public Long getAziendaId() {
		return (Long) this.getId();
	}

	public String getCodiceFiscale() {
		return this.codiceFiscale;
	}

	public Date getDataCostituzione() {
		return this.dataCostituzione;
	}

	public Azienda getDefinedInstance() {
		return this.isIdDefined() ? this.getInstance() : null;
	}

	public String getPartitaIva() {
		return this.partitaIva;
	}

	public boolean isWired() {
		return true;
	}

	public void load() {
		if (this.isIdDefined()) {
			this.wire();
		}
	}

	@Override
	public String persist() {
		this.getInstance().setStato(StatoAzienda.NUOVA.toString());
		// persisto l'entity azienda
		super.persist();
		// creazione del nome del gruppo
		String groupName = this.getInstance().getRagioneSociale()
				.replaceAll("\\s", "_");
		groupName = groupName + "_" + this.getInstance().getId();

		/*
		 * a questo punto deve fare le seguenti operazioni: 1. Creazione gruppo
		 * su alfresco legato all'azienda (es: ragionesociale_id) 2. Creazione
		 * all'interno di un path predefinito (es: force/slot) di una cartella
		 * con lo stesso nome del gruppo creato al passo 1 3. Assegnazione dei
		 * permessi di owner della cartella creata al passo 2 al gruppo creato
		 * al passo 1 4. Creazione di un utente partendo da nome e cognome del
		 * referente dell'azienda 5. Assegnazione di tale utente al gruppo
		 * creato al passo 1
		 */

		return "OK";
	}

	public void setAziendaId(Long id) {
		this.setId(id);
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public void setDataCostituzione(Date dataCostituzione) {
		this.dataCostituzione = dataCostituzione;
	}

	public void setPartitaIva(String partitaIva) {
		this.partitaIva = partitaIva;
	}

	public void wire() {
		this.getInstance();
	}
}
