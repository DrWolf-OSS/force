package it.drwolf.force.entity;

import it.drwolf.force.enums.StatoComunicato;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.validator.NotNull;

@Entity
@Table(name = "Comunicato")
public class Comunicato implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2043917299098809187L;

	private Integer id;

	private String oggetto;

	private String body;

	private boolean toLavori = false;

	private boolean toBeni = false;

	private boolean toServizi = false;

	private StatoComunicato stato;

	private Date dataCreazione;

	private Date dataSpedizione;

	private Date dataUltimaModifica;

	@Column
	public String getBody() {
		return this.body;
	}

	@Transient
	public String getBodySnippet() {
		if (this.getBody().length() > 19) {
			return this.getBody().substring(0, 20) + "...";
		}
		return this.getBody();
	}

	@Column
	public Date getDataCreazione() {
		return this.dataCreazione;
	}

	@Column
	public Date getDataSpedizione() {
		return this.dataSpedizione;
	}

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataUltimaModifica() {
		return this.dataUltimaModifica;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	@Column
	public String getOggetto() {
		return this.oggetto;
	}

	@Enumerated(EnumType.STRING)
	@NotNull
	public StatoComunicato getStato() {
		return this.stato;
	}

	@Column
	public boolean isToBeni() {
		return this.toBeni;
	}

	@Column
	public boolean isToLavori() {
		return this.toLavori;
	}

	@Column
	public boolean isToServizi() {
		return this.toServizi;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setDataCreazione(Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public void setDataSpedizione(Date dataSpedizione) {
		this.dataSpedizione = dataSpedizione;
	}

	public void setDataUltimaModifica(Date dataUltimaModifica) {
		this.dataUltimaModifica = dataUltimaModifica;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public void setStato(StatoComunicato stato) {
		this.stato = stato;
	}

	public void setToBeni(boolean toBeni) {
		this.toBeni = toBeni;
	}

	public void setToLavori(boolean toLavori) {
		this.toLavori = toLavori;
	}

	public void setToServizi(boolean toServizi) {
		this.toServizi = toServizi;
	}

}
