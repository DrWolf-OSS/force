package it.drwolf.slot.enums;

public enum DefStatus {
	VALID("Valida"), INVALID("Non valida");
	public static DefStatus fromValue(String parameter) {
		for (DefStatus c : DefStatus.values()) {
			if (c.value.equals(parameter)) {
				return c;
			}
		}
		throw new IllegalArgumentException(parameter);
	}

	private final String value;

	DefStatus(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

}
