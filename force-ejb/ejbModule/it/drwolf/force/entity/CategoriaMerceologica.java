package it.drwolf.force.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "CategoriaMerceologica")
public class CategoriaMerceologica implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3573580041171154090L;

	private Integer id;

	private String categoria;

	private String type;

	private Set<Azienda> aziende;

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
		CategoriaMerceologica other = (CategoriaMerceologica) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@ManyToMany(mappedBy = "categorieMerceologiche")
	public Set<Azienda> getAziende() {
		return this.aziende;
	}

	@Column
	public String getCategoria() {
		return this.categoria;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	@Column
	public String getType() {
		return this.type;
	}

	@Override
	public int hashCode() {
		final int prime = 113;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	public void setAziende(Set<Azienda> aziende) {
		this.aziende = aziende;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

}
