package it.drwolf.force.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EntePubblico")
public class EntePubblico implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4886850752667336231L;

	private Integer id;

	private String ente;

	private String codiceFiscale;

	private String linkSezioneGare;

	private boolean attivo;

	@Column
	public String getCodiceFiscale() {
		return this.codiceFiscale;
	}

	@Column
	public String getEnte() {
		return this.ente;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	@Column
	public String getLinkSezioneGare() {
		return this.linkSezioneGare;
	}

	@Column
	public boolean isAttivo() {
		return this.attivo;
	}

	public void setAttivo(boolean attivo) {
		this.attivo = attivo;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public void setEnte(String ente) {
		this.ente = ente;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setLinkSezioneGare(String linkSezioneGare) {
		this.linkSezioneGare = linkSezioneGare;
	}
}
