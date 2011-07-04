package it.drwolf.force.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Comessa")
public class Commessa implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4986969015124411014L;

	private Integer id;

	private BigDecimal importo;

	private Date dataCommessa;

	private String committente;

	private Set<CategoriaMerceologica> categorieMerceologiche;

	@ManyToMany
	public Set<CategoriaMerceologica> getCategorieMerceologiche() {
		return this.categorieMerceologiche;
	}

	@Column
	public String getCommittente() {
		return this.committente;
	}

	@Column
	public Date getDataCommessa() {
		return this.dataCommessa;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	@Column
	public BigDecimal getImporto() {
		return this.importo;
	}

	public void setCategorieMerceologiche(
			Set<CategoriaMerceologica> categorieMerceologiche) {
		this.categorieMerceologiche = categorieMerceologiche;
	}

	public void setCommittente(String committente) {
		this.committente = committente;
	}

	public void setDataCommessa(Date dataCommessa) {
		this.dataCommessa = dataCommessa;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setImporto(BigDecimal importo) {
		this.importo = importo;
	}
}
