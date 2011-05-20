package it.drwolf.force.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.NotNull;

@Entity
@Table(name = "Settore")
public class Settore implements Serializable {

	public static enum Default {
		Edilizia(1, "Edilizia"),

		Pulizie(2, "Pulizie");

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

		public void setId(Integer id) {
			this.id = id;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 369073872190612737L;

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
