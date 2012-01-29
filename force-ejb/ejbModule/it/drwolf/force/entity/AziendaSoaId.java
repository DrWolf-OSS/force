package it.drwolf.force.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AziendaSoaId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8372776792331706417L;

	private Integer aziendaId;

	private Integer soaId;

	public AziendaSoaId() {
		super();
	}

	public AziendaSoaId(Integer aziendaId, Integer soaId) {
		super();
		this.aziendaId = aziendaId;
		this.soaId = soaId;
	}

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
		AziendaSoaId other = (AziendaSoaId) obj;
		if (this.aziendaId == null) {
			if (other.aziendaId != null) {
				return false;
			}
		} else if (!this.aziendaId.equals(other.aziendaId)) {
			return false;
		}
		if (this.soaId == null) {
			if (other.soaId != null) {
				return false;
			}
		} else if (!this.soaId.equals(other.soaId)) {
			return false;
		}
		return true;
	}

	@Column(nullable = false)
	public Integer getAziendaId() {
		return this.aziendaId;
	}

	@Column(nullable = false)
	public Integer getSoaId() {
		return this.soaId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.aziendaId == null) ? 0 : this.aziendaId.hashCode());
		result = prime * result
				+ ((this.soaId == null) ? 0 : this.soaId.hashCode());
		return result;
	}

	public void setAziendaId(Integer aziendaId) {
		this.aziendaId = aziendaId;
	}

	public void setSoaId(Integer soaId) {
		this.soaId = soaId;
	}

}
