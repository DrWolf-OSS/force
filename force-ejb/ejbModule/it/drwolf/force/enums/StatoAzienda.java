package it.drwolf.force.enums;

public enum StatoAzienda {

	NUOVA("Nuova"), ATTIVA("Attiva"), SOSPESA("Sospesa"), REVOCATA("Revocata");
	public static StatoAzienda fromName(String parameter) {
		for (StatoAzienda c : StatoAzienda.values()) {
			if (c.name().equals(parameter)) {
				return c;
			}
		}
		throw new IllegalArgumentException(parameter);
	}

	public static StatoAzienda fromValue(String parameter) {
		for (StatoAzienda c : StatoAzienda.values()) {
			if (c.value.equals(parameter)) {
				return c;
			}
		}
		throw new IllegalArgumentException(parameter);
	}

	private final String value;

	StatoAzienda(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

}
