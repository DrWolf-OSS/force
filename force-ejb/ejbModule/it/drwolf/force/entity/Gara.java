package it.drwolf.force.entity;

import it.drwolf.force.enums.TipoGara;
import it.drwolf.force.exceptions.DuplicateCoupleGaraSlotDefOwner;
import it.drwolf.slot.entity.SlotDef;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.validator.NotNull;

@Entity
@Table(name = "Gara")
public class Gara implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8028102047146631398L;

	private Integer id;

	private String oggetto;

	private String cig;

	private String link;

	private Date dataPubblicazione;

	private Date dataScadenza;

	private BigDecimal importo;

	private BigDecimal requisitoTecnico;

	private BigDecimal requisitoEconomico;

	// Con questo si mappa lo stato della Gara ai fii della segnalazione
	// Possiamo distinguere tra gare appena inserite, comunicate alle aziende,
	// etc etc
	private String stato;

	// Con questo si mappa lo stato della Gara (Nuova, Attiva, Scaduta,
	// Scartata)
	// sarebbe stato meglio chimarlo stato
	private String type;

	// Con questo si mappa la tipologia della gara (Aperta, Negoziata, etc)
	private String tipoProcedura;

	private String tipoSvolgimento;

	private Fonte fonte;

	// private SlotDef slotDef;

	private Set<SlotDef> slotDefs = new HashSet<SlotDef>();

	private Set<CategoriaMerceologica> categorieMerceologiche = new HashSet<CategoriaMerceologica>();

	private Set<SOA> SOA = new HashSet<SOA>();

	private Set<ComunicazioneAziendaGara> aziende = new HashSet<ComunicazioneAziendaGara>();

	private Settore settore;

	public Gara() {
		super();
	}

	public Gara(String oggetto, String link, String type, Fonte fonte) {
		super();
		this.oggetto = oggetto;
		this.link = link;
		this.type = type;
		this.fonte = fonte;
	}

	@Transient
	public void addSlotDef(SlotDef slotDef)
			throws DuplicateCoupleGaraSlotDefOwner {
		if (slotDef != null) {
			SlotDef previous = this
					.retrieveSlotDefByOwner(slotDef.getOwnerId());
			if (previous != null) {
				throw new DuplicateCoupleGaraSlotDefOwner(
						"La Gara \""
								+ this.getOggetto()
								+ "\" è già rappresentata da uno SlotDef di proprietà di "
								+ previous.getOwnerId());
				// System.out.println("La Gara \"" + this.getOggetto() +
				// "\" è già rappresentata da uno SlotDef di proprietà di " +
				// previous.getOwnerId());
				// this.getSlotDefs().remove(previous);
			}
			this.getSlotDefs().add(slotDef);
		}
	}

	@OneToMany(mappedBy = "gare")
	public Set<ComunicazioneAziendaGara> getAziende() {
		return this.aziende;
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

	@Column
	public String getCig() {
		return this.cig;
	}

	@Column
	public Date getDataPubblicazione() {
		return this.dataPubblicazione;
	}

	@Column
	public Date getDataScadenza() {
		return this.dataScadenza;
	}

	@ManyToOne
	@JoinColumn(name = "Fonte")
	public Fonte getFonte() {
		return this.fonte;
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
	public String getLink() {
		return this.link;
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

	// @ManyToOne
	// @JoinColumn(name = "SlotDef")
	// public SlotDef getSlotDef() {
	// return this.slotDef;
	// }

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Settore", nullable = true)
	public Settore getSettore() {
		return this.settore;
	}

	@ManyToMany
	public Set<SlotDef> getSlotDefs() {
		return this.slotDefs;
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
	public String getStato() {
		return this.stato;
	}

	@Column
	public String getTipoProcedura() {
		return this.tipoProcedura;
	}

	@Column
	public String getTipoSvolgimento() {
		return this.tipoSvolgimento;
	}

	@Column(nullable = false)
	@NotNull
	public String getType() {
		return this.type;
	}

	// deve essere inserito anche un riferimento al settore?

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

	@Transient
	public SlotDef retrieveSlotDefByOwner(String ownerId) {
		Iterator<SlotDef> iterator = this.getSlotDefs().iterator();
		while (iterator.hasNext()) {
			SlotDef slotDef = iterator.next();
			if (slotDef.getOwnerId().equals(ownerId)) {
				return slotDef;
			}
		}
		return null;
	}

	// deve essere inserito anche un riferimento al settore?

	public void setAziende(Set<ComunicazioneAziendaGara> aziende) {
		this.aziende = aziende;
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

	public void setCig(String cig) {
		this.cig = cig;
	}

	public void setDataPubblicazione(Date dataPubblicazione) {
		this.dataPubblicazione = dataPubblicazione;
	}

	public void setDataScadenza(Date dataScadenza) {
		this.dataScadenza = dataScadenza;
	}

	public void setFonte(Fonte fonte) {
		this.fonte = fonte;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setImporto(BigDecimal importo) {
		this.importo = importo;
	}

	public void setLink(String link) {
		this.link = link;
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

	// public void setSlotDef(SlotDef slotDef) {
	// this.slotDef = slotDef;
	// }

	public void setSettore(Settore settore) {
		this.settore = settore;
	}

	public void setSlotDefs(Set<SlotDef> slotDefs) {
		this.slotDefs = slotDefs;
	}

	public void setSOA(Set<SOA> soa) {
		this.SOA = soa;
	}

	@Transient
	public void setSOAAsList(List<SOA> lista) {
		this.setSOA(new HashSet<SOA>(lista));
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public void setTipoProcedura(String tipologia) {
		this.tipoProcedura = tipologia;
	}

	public void setTipoSvolgimento(String tipoSvolgimento) {
		this.tipoSvolgimento = tipoSvolgimento;
	}

	public void setType(String type) {
		this.type = type;
	}

}
