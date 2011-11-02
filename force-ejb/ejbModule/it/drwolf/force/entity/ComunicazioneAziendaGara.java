package it.drwolf.force.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ComunicazioneAziendaGara")
public class ComunicazioneAziendaGara implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4393200784699710776L;

	private ComunicazioneAziendaGaraId id;

	private boolean email;

	private boolean web;

	@EmbeddedId
	public ComunicazioneAziendaGaraId getId() {
		return this.id;
	}

	@Column
	public boolean isEmail() {
		return this.email;
	}

	@Column
	public boolean isWeb() {
		return this.web;
	}

	public void setEmail(boolean email) {
		this.email = email;
	}

	public void setId(ComunicazioneAziendaGaraId id) {
		this.id = id;
	}

	public void setWeb(boolean web) {
		this.web = web;
	}

}
