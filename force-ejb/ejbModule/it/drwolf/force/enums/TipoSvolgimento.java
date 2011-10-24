package it.drwolf.force.enums;

public enum TipoSvolgimento {

	ONLINE("Telematica (on line)"), OFFLINE("Tradizionale (off line)");

	private String testo;

	private TipoSvolgimento(String testo) {
		this.testo = testo;
	}

	public String getNome() {
		return this.name();
	}

	public String getTesto() {
		return this.testo;
	}
}
