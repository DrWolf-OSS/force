package it.drwolf.force.session.homes;

import it.drwolf.force.entity.Azienda;
import it.drwolf.force.enums.StatoAzienda;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("aziendaHome")
public class AziendaHome extends EntityHome<Azienda> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1214123840587373460L;

	@Override
	protected Azienda createInstance() {
		Azienda azienda = new Azienda();
		return azienda;
	}

	public Long getAziendaId() {
		return (Long) this.getId();
	}

	public Azienda getDefinedInstance() {
		return this.isIdDefined() ? this.getInstance() : null;
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
		return super.persist();
	}

	public void setAziendaId(Long id) {
		this.setId(id);
	}

	public void wire() {
		this.getInstance();
	}
}
