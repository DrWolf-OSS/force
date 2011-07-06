package it.drwolf.force.enums;

public enum TipoGara {

	NUOVA("Nuova"), GESTITA("Gestita"), SCADUTA("Scaduta");

	private String testo;

	private TipoGara(String testo) {
		this.testo = testo;
	}

	public String getNome() {
		return this.name();
	}

	public String getTesto() {
		return this.testo;
	}
}
