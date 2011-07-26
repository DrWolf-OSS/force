package it.drwolf.force.enums;

public enum TipoSOA {

	CATEGORIA_GENERALE("Categoria Generale"), CATEGORIA_SPECIALIZZATA(
			"Categoria Specializzata");

	private String testo;

	private TipoSOA(String testo) {
		this.testo = testo;
	}

	public String getNome() {
		return this.name();
	}

	public String getTesto() {
		return this.testo;
	}
}
