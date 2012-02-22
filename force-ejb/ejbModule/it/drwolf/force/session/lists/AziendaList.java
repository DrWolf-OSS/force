package it.drwolf.force.session.lists;

import it.drwolf.force.entity.Azienda;
import it.drwolf.force.enums.StatoAzienda;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

@Name("aziendaList")
@Scope(ScopeType.CONVERSATION)
public class AziendaList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1502080967471880085L;

	@In
	private EntityManager entityManager;

	@SuppressWarnings("unused")
	@Out(required = false)
	private List<Azienda> aziendeAttive = null;

	@SuppressWarnings("unused")
	@Out(required = false)
	private List<Azienda> aziendeNuove = null;

	@SuppressWarnings("unchecked")
	@Factory("aziendeAttive")
	public void getListaAziendeAttive() {
		this.aziendeAttive = this.entityManager
				.createQuery("from Azienda where stato = :stato")
				.setParameter("stato", StatoAzienda.ATTIVA).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Factory("aziendeNuove")
	public void getListaAziendeNuove() {
		this.aziendeNuove = this.entityManager
				.createQuery("from Azienda where stato = :stato")
				.setParameter("stato", StatoAzienda.NUOVA).getResultList();
		;
	}

	public void setAziendeAttive(List<Azienda> aziendeAttive) {
		this.aziendeAttive = aziendeAttive;
	}

	public void setAziendeNuove(List<Azienda> aziendeNuove) {
		this.aziendeNuove = aziendeNuove;
	}
}
