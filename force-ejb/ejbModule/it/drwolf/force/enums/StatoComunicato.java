package it.drwolf.force.enums;

public enum StatoComunicato {

	IN_ELABORAZIONE("In Elaboraizone"), PRONTA("Pronta"), SPEDITA("Spedita");

	private String value;

	StatoComunicato(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}
}
