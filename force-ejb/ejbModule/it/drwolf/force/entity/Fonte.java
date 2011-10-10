package it.drwolf.force.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Fonte")
public class Fonte implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4815586393632496376L;

	private Integer id;

	private String nome;

	private String url;

	private String tipo;

	private boolean attiva;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	@Column
	public String getNome() {
		return this.nome;
	}

	@Column
	public String getTipo() {
		return this.tipo;
	}

	@Column
	public String getUrl() {
		return this.url;
	}

	@Column
	public boolean isAttiva() {
		return this.attiva;
	}

	public void setAttiva(boolean attiva) {
		this.attiva = attiva;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
