package it.drwolf.force.enums;

public enum TipoSoa {

	CATEGORIA_GENERALE("Categoria Generale"), CATEGORIA_SPECIALIZZATA(
			"Categoria Specializzata");

	private String testo;

	private TipoSoa(String testo) {
		this.testo = testo;
	}

	public String getNome() {
		return this.name();
	}

	public String getTesto() {
		return this.testo;
	}
}
