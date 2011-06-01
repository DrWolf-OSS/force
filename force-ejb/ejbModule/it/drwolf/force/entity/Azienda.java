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
	private Settore settore;

	private String stato;

	// per Alfresco
	private String alfrescoGroupId;

	public Azienda() {
	}

	public String getAlfrescoGroupId() {
		return this.alfrescoGroupId;
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
	public String getProvincia() {
		return this.provincia;
	}

	@Column(nullable = false)
	@NotNull
	public String getRagioneSociale() {
		return this.ragioneSociale;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	@NotNull
	public Settore getSettore() {
		return this.settore;
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

	public void setAlfrescoGroupId(String alfrescoGroupId) {
		this.alfrescoGroupId = alfrescoGroupId;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public void setCellulare(String cellulare) {
		this.cellulare = cellulare;
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

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = ragioneSociale;
	}

	public void setSettore(Settore settore) {
		this.settore = settore;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
}
