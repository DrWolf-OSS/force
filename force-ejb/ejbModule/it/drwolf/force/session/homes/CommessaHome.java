package it.drwolf.force.session.homes;

import it.drwolf.force.entity.Commessa;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;

@Name("commessaHome")
public class CommessaHome extends EntityHome<Commessa> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1214123840587373460L;

	@In
	EntityManager entityManager;

	@Override
	protected Commessa createInstance() {
		Commessa commessa = new Commessa();
		return commessa;
	}

	public Integer getCommessaId() {
		return (Integer) this.getId();
	}

	public Commessa getDefinedInstance() {
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

	public void setAziendaId(Integer id) {
		this.setId(id);
	}

	public void wire() {
		this.getInstance();
	}
}
