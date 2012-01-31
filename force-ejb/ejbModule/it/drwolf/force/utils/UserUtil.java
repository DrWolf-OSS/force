package it.drwolf.force.utils;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("userUtil")
@AutoCreate
@Scope(ScopeType.CONVERSATION)
public class UserUtil implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5699721558566989659L;

	@In
	EntityManager entityManager;

	private String email = null;

	private String statoRecuperoEmail = null;

	public String getEmail() {
		return this.email;
	}

	public String getStatoRecuperoEmail() {
		return this.statoRecuperoEmail;
	}

	public String recuperaPassword() {
		try {
			this.entityManager
					.createQuery(
							"from Azienda a where a.emailReferente = :email")
					.setParameter("email", this.email).getSingleResult();
			this.setStatoRecuperoEmail("OK");
			return "OK";
		} catch (NoResultException e) {
			this.setStatoRecuperoEmail("NO_RESULT");
			return "NO_RESULT";
		}
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setStatoRecuperoEmail(String statoRecuperoEmail) {
		this.statoRecuperoEmail = statoRecuperoEmail;
	}

}
