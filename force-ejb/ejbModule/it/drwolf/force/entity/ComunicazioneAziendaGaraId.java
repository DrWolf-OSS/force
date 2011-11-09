package it.drwolf.force.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ComunicazioneAziendaGaraId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7936216953400903452L;

	private Integer aziendaId;
	private Integer garaId;

	public ComunicazioneAziendaGaraId() {
		super();
	}

	public ComunicazioneAziendaGaraId(Integer aziendaId, Integer garaId) {
		super();
		this.aziendaId = aziendaId;
		this.garaId = garaId;
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
		ComunicazioneAziendaGaraId other = (ComunicazioneAziendaGaraId) obj;
		if (this.aziendaId == null) {
			if (other.aziendaId != null) {
				return false;
			}
		} else if (!this.aziendaId.equals(other.aziendaId)) {
			return false;
		}
		if (this.garaId == null) {
			if (other.garaId != null) {
				return false;
			}
		} else if (!this.garaId.equals(other.garaId)) {
			return false;
		}
		return true;
	}

	@Column(nullable = false)
	public Integer getAziendaId() {
		return this.aziendaId;
	}

	@Column(nullable = false)
	public Integer getGaraId() {
		return this.garaId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.aziendaId == null) ? 0 : this.aziendaId.hashCode());
		result = prime * result
				+ ((this.garaId == null) ? 0 : this.garaId.hashCode());
		return result;
	}

	public void setAziendaId(Integer aziendaId) {
		this.aziendaId = aziendaId;
	}

	public void setGaraId(Integer garaId) {
		this.garaId = garaId;
	}

}
