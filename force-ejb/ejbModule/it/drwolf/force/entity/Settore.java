package it.drwolf.force.entity;

import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
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
import javax.persistence.Transient;

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
	private String descrizione;
	private SlotDef slotDef;

	private Set<Gara> gare;

	// Questo metodo dovrà essere modificato per tener conto del fatto che gli
	// utenti "paganti" avranno a disposizione gli SlotDef definiti dal sistema
	@Transient
	public SlotDef getAssociatedSlotDef() {
		AlfrescoUserIdentity alfrescoUserIdentity = (AlfrescoUserIdentity) org.jboss.seam.Component
				.getInstance("alfrescoUserIdentity");
		SlotDef slotDef = this.slotDef;
		String ownerId = alfrescoUserIdentity.getMyOwnerId();
		if ((slotDef != null) && (slotDef.getOwnerId().equals(ownerId))) {
			return slotDef;
		}
		return null;
	}

	@Column(name = "descrizione", nullable = true)
	public String getDescrizione() {
		return this.descrizione;
	}

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

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
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
