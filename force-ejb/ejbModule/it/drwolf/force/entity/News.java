package it.drwolf.force.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "News")
public class News implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3014013228558061718L;

	private Integer id;

	private String titolo;

	private String testo;

	private boolean pubblicata = false;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	public String getTesto() {
		return this.testo;
	}

	public String getTitolo() {
		return this.titolo;
	}

	@Column
	public boolean isPubblicata() {
		return this.pubblicata;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setPubblicata(boolean pubblicata) {
		this.pubblicata = pubblicata;
	}

	public void setTesto(String testo) {
		this.testo = testo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

}
