package it.drwolf.force.enums;

public enum StatoAzienda {

	NUOVA("Nuova"),

	ATTIVA("Attiva"),

	SOPSPESA("Sospesa"),

	REVOCATA("Revocata");

	private String testo;

	private StatoAzienda(String testo) {
		this.testo = testo;
	}

	public String getNome() {
		return this.name();
	}

	public String getTesto() {
		return this.testo;
	}

}
