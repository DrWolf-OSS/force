package it.drwolf.force.enums;

public enum PosizioneCNA {

	NESSUNA("Nessuna"),

	ISCRITTA("Iscritta"),

	AMMINISTRATA("Amministrata");

	private String testo;

	private PosizioneCNA(String testo) {
		this.testo = testo;
	}

	public String getNome() {
		return this.name();
	}

	public String getTesto() {
		return this.testo;
	}
}
