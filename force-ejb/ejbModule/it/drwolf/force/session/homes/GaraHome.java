package it.drwolf.force.session.homes;

import it.drwolf.force.entity.Gara;
import it.drwolf.force.enums.TipoGara;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("garaHome")
public class GaraHome extends EntityHome<Gara> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1214123840587373460L;

	@In
	EntityManager entityManager;

	public String attivaGara() {
		this.getInstance().setType(TipoGara.GESTITA.getNome());
		this.persist();
		return "OK";
	}

	@Override
	protected Gara createInstance() {
		Gara gara = new Gara();
		return gara;
	}

	public Gara getDefinedInstance() {
		return this.isIdDefined() ? this.getInstance() : null;
	}

	public Integer getGaraId() {
		return (Integer) this.getId();
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
		return super.persist();
	}

	public void setGaraId(Integer id) {
		this.setId(id);
	}

	public void wire() {
		this.getInstance();
	}
}
