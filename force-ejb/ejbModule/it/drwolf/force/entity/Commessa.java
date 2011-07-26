package it.drwolf.force.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "Commessa")
public class Commessa implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4986969015124411014L;

	private Integer id;

	private String descrizione;

	private BigDecimal importo;

	private Date dataCommessa;

	private String committente;

	private Azienda azienda;

	private Set<CategoriaMerceologica> categorieMerceologiche = new HashSet<CategoriaMerceologica>();

	@ManyToOne
	@JoinColumn(name = "azienda_fk")
	public Azienda getAzienda() {
		return this.azienda;
	}

	@ManyToMany
	public Set<CategoriaMerceologica> getCategorieMerceologiche() {
		return this.categorieMerceologiche;
	}

	@Transient
	public List<CategoriaMerceologica> getCategorieMerceologicheAsList() {
		return new ArrayList<CategoriaMerceologica>(
				this.getCategorieMerceologiche());

	}

	@Transient
	public String getCategorieMerceologicheAsString() {
		String result = new String();
		for (CategoriaMerceologica categoriaMerceologica : this
				.getCategorieMerceologicheAsList()) {
			result += categoriaMerceologica.getCategoria() + " , ";
		}
		return result;
	}

	@Column
	public String getCommittente() {
		return this.committente;
	}

	@Column
	public Date getDataCommessa() {
		return this.dataCommessa;
	}

	@Column
	public String getDescrizione() {
		return this.descrizione;
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

	public void setAzienda(Azienda azienda) {
		this.azienda = azienda;
	}

	public void setCategorieMerceologiche(
			Set<CategoriaMerceologica> categorieMerceologiche) {
		this.categorieMerceologiche = categorieMerceologiche;
	}

	@Transient
	public void setCategorieMerceologicheAsList(
			List<CategoriaMerceologica> lista) {
		this.setCategorieMerceologiche(new HashSet<CategoriaMerceologica>(lista));
	}

	public void setCommittente(String committente) {
		this.committente = committente;
	}

	public void setDataCommessa(Date dataCommessa) {
		this.dataCommessa = dataCommessa;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setImporto(BigDecimal importo) {
		this.importo = importo;
	}
}
