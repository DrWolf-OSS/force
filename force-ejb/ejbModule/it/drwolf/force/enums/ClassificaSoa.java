package it.drwolf.force.enums;

public enum ClassificaSoa {

	SOTTO_SOGLIA("Sotto Soglia"),

	PRIMA("I"),

	SECONDA("II"),

	TERZA("III"),

	QUARTA("IV"),

	QUINTA("V"),

	SESTA("VI"),

	SETTIMA("VII"),

	OTTAVA("VIII");

	private String testo;

	private ClassificaSoa(String testo) {
		this.testo = testo;
	}

	public String getNome() {
		return this.name();
	}

	public String getTesto() {
		return this.testo;
	}
}
