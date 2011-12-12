package it.drwolf.force.enums;

public enum TipoFonte {

	START("S.T.A.R.T."), SITAT("S.I.T.A.T"), AVCP("AVCP");

	private String testo;

	private TipoFonte(String testo) {
		this.testo = testo;
	}

	public String getNome() {
		return this.name();
	}

	public String getTesto() {
		return this.testo;
	}
}
