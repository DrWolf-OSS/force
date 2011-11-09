package it.drwolf.force.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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

	private Azienda azienda;

	private Gara gara;

	public Azienda getAzienda() {
		return this.azienda;
	}

	public Gara getGara() {
		return this.gara;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "garaId", column = @Column(name = "id", nullable = true)),
			@AttributeOverride(name = "aziendaId", column = @Column(name = "id", nullable = true)) })
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

	public void setAzienda(Azienda azienda) {
		this.azienda = azienda;
	}

	public void setEmail(boolean email) {
		this.email = email;
	}

	public void setGara(Gara gara) {
		this.gara = gara;
	}

	public void setId(ComunicazioneAziendaGaraId id) {
		this.id = id;
	}

	public void setWeb(boolean web) {
		this.web = web;
	}

}
