package it.drwolf.force.entity;

import it.drwolf.slot.entity.SlotDef;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
	private SlotDef slotDef;
	private Set<Gara> gare;

	@OneToMany(mappedBy = "settore")
	public Set<Gara> getGare() {
		return this.gare;
	}

	@Id
	public Integer getId() {
		return this.id;
	}

	@Column(name = "nome", nullable = false)
	@NotNull
	public String getNome() {
		return this.nome;
	}

	@ManyToOne
	@JoinColumn(name = "SlotDef")
	public SlotDef getSlotDef() {
		return this.slotDef;
	}

	public void setGare(Set<Gara> gare) {
		this.gare = gare;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setSlotDef(SlotDef slotDef) {
		this.slotDef = slotDef;
	}
}
