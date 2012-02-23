package it.drwolf.force.enums;

public enum TipologiaAbbonamento {

	BASE("Base"), CALL_CENTER("Call Center"), PREMIUM("All inclusive");
	public static TipologiaAbbonamento fromName(String parameter) {
		for (TipologiaAbbonamento c : TipologiaAbbonamento.values()) {
			if (c.name().equals(parameter)) {
				return c;
			}
		}
		throw new IllegalArgumentException(parameter);
	}

	public static TipologiaAbbonamento fromValue(String parameter) {
		for (TipologiaAbbonamento c : TipologiaAbbonamento.values()) {
			if (c.value.equals(parameter)) {
				return c;
			}
		}
		throw new IllegalArgumentException(parameter);
	}

	private final String value;

	TipologiaAbbonamento(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

}
