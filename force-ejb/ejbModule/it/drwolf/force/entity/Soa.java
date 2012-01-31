package it.drwolf.force.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Soa")
public class Soa implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5215308627496638215L;

	private Integer id;

	private String codice;

	private String nome;

	private String descrizione;

	private String type;

	private Boolean qualifica;

	private Set<AziendaSoa> aziende;

	private Set<Gara> gare;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Soa other = (Soa) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@OneToMany(mappedBy = "soa", fetch = FetchType.EAGER)
	public Set<AziendaSoa> getAziende() {
		return this.aziende;
	}

	@Column
	public String getCodice() {
		return this.codice;
	}

	public String getDescrizione() {
		return this.descrizione;
	}

	@ManyToMany(mappedBy = "soa")
	public Set<Gara> getGare() {
		return this.gare;
	}

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
	public Boolean getQualifica() {
		return this.qualifica;
	}

	@Column
	public String getType() {
		return this.type;
	}

	@Override
	public int hashCode() {
		final int prime = 383;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	public void setAziende(Set<AziendaSoa> aziende) {
		this.aziende = aziende;
	}

	public void setCodice(String og) {
		this.codice = og;
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

	public void setQualifica(Boolean qualifica) {
		this.qualifica = qualifica;
	}

	public void setType(String type) {
		this.type = type;
	}

}
