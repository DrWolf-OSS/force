package it.drwolf.force.entity;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.NotNull;

@Entity
@Table(name = "Azienda", uniqueConstraints = { @UniqueConstraint(columnNames = "AlfrescoGroupId") })
public class Azienda implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5368491460508141054L;

	private Integer id;

	private String ragioneSociale;
	// Sede legale
	private String indirizzo;
	private String localita;
	private String comune;
	private String provincia;
	private String cap;

	private String partitaIva;
	private String codiceFiscale;
	private String email;

	private String emailCertificata;

	// Referente
	private String nome;
	private String cognome;

	private String emailReferente;
	private String telefono;

	private String fax;
	private String cellulare;
	private FormaGiuridica formaGiuridica;
	private Settore settore;
	private String posizioneCNA;
	private String stato;

	// per Alfresco
	private String alfrescoGroupId;

	private Set<CategoriaMerceologica> categorieMerceologiche = new HashSet<CategoriaMerceologica>();

	private Set<SOA> SOA = new HashSet<SOA>();

	private Set<Commessa> commesse;

	private Set<ComunicazioneAziendaGara> gare = new HashSet<ComunicazioneAziendaGara>();

	public Azienda() {
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
		Azienda other = (Azienda) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public String getAlfrescoGroupId() {
		return this.alfrescoGroupId;
	}

	@Column(nullable = false)
	@NotNull
	public String getCap() {
		return this.cap;
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

	@Column(nullable = true)
	public String getCellulare() {
		return this.cellulare;
	}

	@Column
	public String getCodiceFiscale() {
		return this.codiceFiscale;
	}

	@Column(nullable = false)
	@NotNull
	public String getCognome() {
		return this.cognome;
	}

	@OneToMany(mappedBy = "azienda")
	public Set<Commessa> getCommesse() {
		return this.commesse;
	}

	@Column(nullable = false)
	@NotNull
	public String getComune() {
		return this.comune;
	}

	@Column(nullable = false)
	@NotNull
	public String getEmail() {
		return this.email;
	}

	@Column(nullable = true)
	public String getEmailCertificata() {
		return this.emailCertificata;
	}

	@Column(nullable = false)
	@NotNull
	public String getEmailReferente() {
		return this.emailReferente;
	}

	@Column(nullable = true)
	public String getFax() {
		return this.fax;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	@NotNull
	public FormaGiuridica getFormaGiuridica() {
		return this.formaGiuridica;
	}

	@OneToMany(mappedBy = "azienda")
	public Set<ComunicazioneAziendaGara> getGare() {
		return this.gare;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return this.id;
	}

	@Column(nullable = false)
	@NotNull
	public String getIndirizzo() {
		return this.indirizzo;
	}

	@Column(nullable = true)
	public String getLocalita() {
		return this.localita;
	}

	@Column(nullable = false)
	@NotNull
	public String getNome() {
		return this.nome;
	}

	@Column
	public String getPartitaIva() {
		return this.partitaIva;
	}

	@Column(nullable = false)
	public String getPosizioneCNA() {
		return this.posizioneCNA;
	}

	@Column(nullable = false)
	@NotNull
	public String getProvincia() {
		return this.provincia;
	}

	@Column(nullable = false)
	@NotNull
	public String getRagioneSociale() {
		return this.ragioneSociale;
	}

	@Transient
	public String getReferente() {
		return this.getNome() + " " + this.getCognome();
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	@NotNull
	public Settore getSettore() {
		return this.settore;
	}

	@ManyToMany
	public Set<SOA> getSOA() {
		return this.SOA;
	}

	@Transient
	public List<SOA> getSOAAsList() {
		if (this.getSOA() != null) {
			return new ArrayList<SOA>(this.getSOA());
		}
		return new ArrayList<SOA>();

	}

	@Column(nullable = false)
	@NotNull
	public String getStato() {
		return this.stato;
	}

	@Column(nullable = true)
	public String getTelefono() {
		return this.telefono;
	}

	@Override
	public int hashCode() {
		final int prime = 89;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	public void setAlfrescoGroupId(String alfrescoGroupId) {
		this.alfrescoGroupId = alfrescoGroupId;
	}

	public void setCap(String cap) {
		this.cap = cap;
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

	public void setCellulare(String cellulare) {
		this.cellulare = cellulare;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public void setCommesse(Set<Commessa> commesse) {
		this.commesse = commesse;
	}

	public void setComune(String comune) {
		this.comune = comune;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setEmailCertificata(String emailCertificata) {
		this.emailCertificata = emailCertificata;
	}

	public void setEmailReferente(String emailReferente) {
		this.emailReferente = emailReferente;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public void setFormaGiuridica(FormaGiuridica formaGiuridica) {
		this.formaGiuridica = formaGiuridica;
	}

	public void setGare(Set<ComunicazioneAziendaGara> gare) {
		this.gare = gare;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public void setLocalita(String localita) {
		this.localita = localita;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setPartitaIva(String partitaIva) {
		this.partitaIva = partitaIva;
	}

	public void setPosizioneCNA(String posizioneCNA) {
		this.posizioneCNA = posizioneCNA;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public void setSettore(Settore settore) {
		this.settore = settore;
	}

	public void setSOA(Set<SOA> categoriaOpereGenerali) {
		this.SOA = categoriaOpereGenerali;
	}

	@Transient
	public void setSOAAsList(List<SOA> lista) {
		this.setSOA(new HashSet<SOA>(lista));
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
}
