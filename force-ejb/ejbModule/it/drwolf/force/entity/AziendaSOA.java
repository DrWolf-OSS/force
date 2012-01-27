package it.drwolf.force.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "AziendaSOA")
public class AziendaSOA implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8472703086674783775L;

	private AziendaSOAid id;

	private String classifica;

	private Azienda azienda;

	private SOA soa;

	public AziendaSOA(AziendaSOAid id, String classifica, Azienda azienda,
			SOA soa) {
		super();
		this.id = id;
		this.classifica = classifica;
		this.azienda = azienda;
		this.soa = soa;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "aziendaId", nullable = true, insertable = false, updatable = false)
	public Azienda getAzienda() {
		return this.azienda;
	}

	public String getClassifica() {
		return this.classifica;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "soaId", column = @Column(name = "soaId", nullable = true)),
			@AttributeOverride(name = "aziendaId", column = @Column(name = "aziendaId", nullable = true)) })
	public AziendaSOAid getId() {
		return this.id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "soaId", nullable = true, insertable = false, updatable = false)
	public SOA getSoa() {
		return this.soa;
	}

	public void setAzienda(Azienda azienda) {
		this.azienda = azienda;
	}

	public void setClassifica(String classifica) {
		this.classifica = classifica;
	}

	public void setId(AziendaSOAid id) {
		this.id = id;
	}

	public void setSoa(SOA soa) {
		this.soa = soa;
	}

}
