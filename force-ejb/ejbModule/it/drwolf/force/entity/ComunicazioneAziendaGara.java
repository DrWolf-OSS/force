package it.drwolf.force.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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

	public ComunicazioneAziendaGara() {

	}

	public ComunicazioneAziendaGara(ComunicazioneAziendaGaraId id,
			boolean email, boolean web, Azienda azienda, Gara gara) {
		super();
		this.id = id;
		this.email = email;
		this.web = web;
		this.azienda = azienda;
		this.gara = gara;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "aziendaId", nullable = true, insertable = false, updatable = false)
	public Azienda getAzienda() {
		return this.azienda;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "garaId", nullable = true, insertable = false, updatable = false)
	public Gara getGara() {
		return this.gara;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "garaId", column = @Column(name = "garaId", nullable = true)),
			@AttributeOverride(name = "aziendaId", column = @Column(name = "aziendaId", nullable = true)) })
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
