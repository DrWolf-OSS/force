package it.drwolf.force.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "CodiciCPV")
public class CodiciCPV implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4329142058956498937L;

	private String code;

	private String descrizione;

	private Set<CategoriaMerceologica> categorieMerceologiche;

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
		CodiciCPV other = (CodiciCPV) obj;
		if (this.code == null) {
			if (other.code != null) {
				return false;
			}
		} else if (!this.code.equals(other.code)) {
			return false;
		}
		return true;
	}

	@ManyToMany
	public Set<CategoriaMerceologica> getCategorieMerceologiche() {
		return this.categorieMerceologiche;
	}

	@Id
	public String getCode() {
		return this.code;
	}

	@Column
	public String getDescrizione() {
		return this.descrizione;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.code == null) ? 0 : this.code.hashCode());
		return result;
	}

	public void setCategorieMerceologiche(
			Set<CategoriaMerceologica> categorieMerceologiche) {
		this.categorieMerceologiche = categorieMerceologiche;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
}
