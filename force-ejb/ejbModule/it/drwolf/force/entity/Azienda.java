package it.drwolf.force.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
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
	private String codiceFiscale;
	private String partitaIva;

	private Integer annoCostituzione;
	private String numeroREA;

	// Sede legale
	private String indirizzo;
	private String localita;
	private String comune;
	private String provincia;
	private String cap;

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

	// per Alfresco
	private String alfrescoGroupId;

	private Azienda() {
	}

	public String getAlfrescoGroupId() {
		return this.alfrescoGroupId;
	}

	@Column(nullable = false)
	@NotNull
	public Integer getAnnoCostituzione() {
		return this.annoCostituzione;
	}

	@Column(nullable = false)
	@NotNull
	public String getCap() {
		return this.cap;
	}

	@Column(nullable = true)
	public String getCellulare() {
		return this.cellulare;
	}

	@Column(nullable = false)
	@NotNull
	public String getCodiceFiscale() {
		return this.codiceFiscale;
	}

	@Column(nullable = false)
	@NotNull
	public String getCognome() {
		return this.cognome;
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

	@Column(nullable = false)
	@NotNull
	public String getNumeroREA() {
		return this.numeroREA;
	}

	@Column(nullable = false)
	@NotNull
	public String getPartitaIva() {
		return this.partitaIva;
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

	@Column(nullable = true)
	public String getTelefono() {
		return this.telefono;
	}

	public void setAlfrescoGroupId(String alfrescoGroupId) {
		this.alfrescoGroupId = alfrescoGroupId;
	}

	public void setAnnoCostituzione(Integer annoCostituzione) {
		this.annoCostituzione = annoCostituzione;
	}

	public void setCap(String cap) {
		this.cap = cap;
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

	public void setNumeroREA(String numeroREA) {
		this.numeroREA = numeroREA;
	}

	public void setPartitaIva(String partitaIva) {
		this.partitaIva = partitaIva;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
}
