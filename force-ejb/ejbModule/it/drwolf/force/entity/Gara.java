package it.drwolf.force.entity;

import it.drwolf.force.enums.TipoGara;
import it.drwolf.slot.entity.SlotDef;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "Gara")
public class Gara implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8028102047146631398L;

	private Integer id;

	private String oggetto;

	private Date dataPubblicazione;

	private Date dataScadenza;

	private BigDecimal importo;

	private BigDecimal requisitoTecnico;

	private BigDecimal requisitoEconomico;

	private String type;

	private SlotDef slotDef;

	private Set<CategoriaMerceologica> categorieMerceologiche;

	private Set<SOA> SOA;

	private Settore settore;

	@ManyToMany
	public Set<CategoriaMerceologica> getCategorieMerceologiche() {
		return this.categorieMerceologiche;
	}

	@Transient
	public List<CategoriaMerceologica> getCategorieMerceologicheAsList() {
		return new ArrayList<CategoriaMerceologica>(
				this.getCategorieMerceologiche());

	}

	@Column
	public Date getDataPubblicazione() {
		return this.dataPubblicazione;
	}

	@Column
	public Date getDataScadenza() {
		return this.dataScadenza;
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

	@Column
	public String getOggetto() {
		return this.oggetto;
	}

	@Column
	public BigDecimal getRequisitoEconomico() {
		return this.requisitoEconomico;
	}

	@Column
	public BigDecimal getRequisitoTecnico() {
		return this.requisitoTecnico;
	}

	// deve essere inserito anche un riferimento al settore?

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	public Settore getSettore() {
		return this.settore;
	}

	@ManyToOne
	@JoinColumn(name = "SlotDef")
	public SlotDef getSlotDef() {
		return this.slotDef;
	}

	@ManyToMany
	public Set<SOA> getSOA() {
		return this.SOA;
	}

	@Transient
	public List<SOA> getSOAAsList() {
		return new ArrayList<SOA>(this.getSOA());

	}

	@Column
	public String getType() {
		return this.type;
	}

	@Transient
	public boolean isActive() {
		if (this.getType().equals(TipoGara.GESTITA.getNome())) {
			return true;
		}
		return false;
	}

	@Transient
	public boolean isNew() {
		if (this.getType().equals(TipoGara.NUOVA.getNome())) {
			return true;
		}
		return false;
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

	public void setDataPubblicazione(Date dataPubblicazione) {
		this.dataPubblicazione = dataPubblicazione;
	}

	public void setDataScadenza(Date dataScadenza) {
		this.dataScadenza = dataScadenza;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setImporto(BigDecimal importo) {
		this.importo = importo;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public void setRequisitoEconomico(BigDecimal requisitoEconomico) {
		this.requisitoEconomico = requisitoEconomico;
	}

	public void setRequisitoTecnico(BigDecimal requisitoTecnico) {
		this.requisitoTecnico = requisitoTecnico;
	}

	public void setSettore(Settore settore) {
		this.settore = settore;
	}

	public void setSlotDef(SlotDef slotDef) {
		this.slotDef = slotDef;
	}

	public void setSOA(Set<SOA> soa) {
		this.SOA = soa;
	}

	@Transient
	public void setSOAAsList(List<SOA> lista) {
		this.setSOA(new HashSet<SOA>(lista));
	}

	public void setType(String type) {
		this.type = type;
	}

}
