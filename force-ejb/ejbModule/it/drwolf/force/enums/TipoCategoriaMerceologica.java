package it.drwolf.force.enums;

public enum TipoCategoriaMerceologica {

	BENE("Bene"), SERVIZIO("Servizio");

	private String testo;

	private TipoCategoriaMerceologica(String testo) {
		this.testo = testo;
	}

	public String getNome() {
		return this.name();
	}

	public String getTesto() {
		return this.testo;
	}
}
