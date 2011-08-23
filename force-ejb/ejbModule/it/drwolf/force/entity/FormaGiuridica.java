package it.drwolf.force.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.NotNull;

@Entity
@Table(name = "FormaGiuridica")
public class FormaGiuridica implements Serializable {

	public static enum Default {
		II(1, "Impresa individuale"),

		IF(2, "Impresa familiare"),

		SS(3, "Società semplice - SS"),

		SNC(4, "Società in nome collettivo - SNC"),

		SAS(5, "Società in accomandita semplice - SAS"),

		SRL(6, "Società a responsabilità limitata - SRL"),

		SAPA(7, "Società in accomandita per azioni - SAPA"),

		SPA(8, "Società per azioni - SPA"),

		SCARL(9, "Società cooperativa a responsabilità limitata - SCA.r.l."),

		SCRI(10, "Società cooperativa a responsabilità illimitata"),

		Associazione(11, "Associazione"),

		ProfessionistaStudio(12, "Professionista / Studio"),

		Altro(13, "Altro");

		private Integer id;
		private String nome;

		private Default(Integer id, String nome) {
			this.id = id;
			this.nome = nome;
		}

		public Integer getId() {
			return this.id;
		}

		public String getNome() {

			return this.nome;
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -482568088570508305L;

	private Integer id;
	private String nome;

	@Id
	public Integer getId() {
		return this.id;
	}

	@Column(name = "nome", nullable = false)
	@NotNull
	public String getNome() {
		return this.nome;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}
