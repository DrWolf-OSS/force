package it.drwolf.force.entity;

import it.drwolf.force.enums.TipoGara;
import it.drwolf.force.exceptions.DuplicateCoupleGaraSlotDefOwner;
import it.drwolf.force.session.UserSession;
import it.drwolf.slot.alfresco.AlfrescoUserIdentity;
import it.drwolf.slot.entity.SlotDef;
import it.drwolf.slot.entity.SlotInst;

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
import javax.persistence.EntityManager;
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

	private Set<Soa> soa = new HashSet<Soa>();

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

	@Transient
	@SuppressWarnings("unchecked")
	public List<SlotInst> getAllAssociatedSlotInsts() {
		EntityManager entityManager = (EntityManager) org.jboss.seam.Component
				.getInstance("entityManager");
		List<SlotInst> resultList = entityManager
				.createQuery(
						"select si from Gara g, SlotInst si inner join g.slotDefs sd where si.slotDef = sd and g=:gara")
				.setParameter("gara", this).getResultList();
		return resultList;
	}

	@Transient
	public SlotDef getAssociatedSlotDef() {
		AlfrescoUserIdentity alfrescoUserIdentity = (AlfrescoUserIdentity) org.jboss.seam.Component
				.getInstance("alfrescoUserIdentity");
		String ownerId = alfrescoUserIdentity.getMyOwnerId();
		if (!ownerId.equals("ADMIN")) {
			// EntityManager entityManager = (EntityManager)
			// org.jboss.seam.Component
			// .getInstance("entityManager");
			UserSession userSession = (UserSession) org.jboss.seam.Component
					.getInstance("userSession");
			String tipologiaAbbonamento = userSession.getAzienda()
					.getTipologiaAbbonamento();
			if (tipologiaAbbonamento != null
					&& tipologiaAbbonamento.equals("PREMIUM")) {
				ownerId = "ADMIN";
			}
		}
		Iterator<SlotDef> iterator = this.getSlotDefs().iterator();

		while (iterator.hasNext()) {
			SlotDef slotDef = iterator.next();
			if (slotDef.getOwnerId().equals(ownerId)) {
				return slotDef;
			}
		}
		return null;
	}

	@OneToMany(mappedBy = "gara", fetch = FetchType.EAGER)
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

	@Column(length = 500)
	public String getOggetto() {
		return this.oggetto;
	}

	@Column
	public BigDecimal getRequisitoEconomico() {
		return this.requisitoEconomico;
	}

	// @ManyToOne
	// @JoinColumn(name = "SlotDef")
	// public SlotDef getSlotDef() {
	// return this.slotDef;
	// }

	@Column
	public BigDecimal getRequisitoTecnico() {
		return this.requisitoTecnico;
	}

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
	public Set<Soa> getSoa() {
		return this.soa;
	}

	@Transient
	public List<Soa> getSoaAsList() {
		return new ArrayList<Soa>(this.getSoa());

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

	public void setSoa(Set<Soa> soa) {
		this.soa = soa;
	}

	@Transient
	public void setSoaAsList(List<Soa> lista) {
		this.setSoa(new HashSet<Soa>(lista));
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
