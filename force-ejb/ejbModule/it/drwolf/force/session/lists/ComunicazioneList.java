package it.drwolf.force.session.lists;

import it.drwolf.force.entity.Comunicato;
import it.drwolf.force.enums.StatoComunicato;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

@Name("comunicazioneList")
@Scope(ScopeType.CONVERSATION)
public class ComunicazioneList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9070983927173919722L;

	@In
	private EntityManager entityManager;

	@SuppressWarnings("unused")
	@Out(required = false)
	private List<Comunicato> comunicatiSpediti = null;

	@SuppressWarnings("unused")
	@Out(required = false)
	private List<Comunicato> comunicatiNonSpediti = null;

	@SuppressWarnings("unchecked")
	@Factory("comunicatiNonSpediti")
	public void getComunicatiNonSpediti() {
		this.comunicatiNonSpediti = this.entityManager
				.createQuery("from Comunicato c where c.stato IN (:s)")
				.setParameter(
						"s",
						Arrays.asList(new StatoComunicato[] {
								StatoComunicato.IN_ELABORAZIONE,
								StatoComunicato.PRONTA })).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Factory("comunicatiSpediti")
	public void getComunicatiSpediti() {
		this.comunicatiSpediti = this.entityManager
				.createQuery("from Comunicato c where c.stato = :s")
				.setParameter("s", StatoComunicato.SPEDITA).getResultList();
	}

	public void setComunicatiNonSpediti(List<Comunicato> comunicatiNonSpediti) {
		this.comunicatiNonSpediti = comunicatiNonSpediti;
	}

	public void setComunicatiSpediti(List<Comunicato> comunicatiSpediti) {
		this.comunicatiSpediti = comunicatiSpediti;
	}

}
