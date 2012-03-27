package it.drwolf.force.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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

	private boolean inviata = false;

	@Column
	public String getBody() {
		return this.body;
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

	public boolean isInviata() {
		return this.inviata;
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

	public void setId(Integer id) {
		this.id = id;
	}

	public void setInviata(boolean inviata) {
		this.inviata = inviata;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
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
