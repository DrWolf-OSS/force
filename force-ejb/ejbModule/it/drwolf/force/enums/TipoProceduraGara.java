package it.drwolf.force.enums;

public enum TipoProceduraGara {

	APERTA("Aperta"), NEGOZIATA("Negoziata");

	private String testo;

	private TipoProceduraGara(String testo) {
		this.testo = testo;
	}

	public String getNome() {
		return this.name();
	}

	public String getTesto() {
		return this.testo;
	}
}
