package it.drwolf.slot.enums;

public enum SlotDefSatus {
	VALID("Valida"), INVALID("Non valida"), UNKNOWN("Sconosciuto");
	public static SlotDefSatus fromValue(String parameter) {
		for (SlotDefSatus c : SlotDefSatus.values()) {
			if (c.value.equals(parameter)) {
				return c;
			}
		}
		throw new IllegalArgumentException(parameter);
	}

	private final String value;

	SlotDefSatus(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}
}
