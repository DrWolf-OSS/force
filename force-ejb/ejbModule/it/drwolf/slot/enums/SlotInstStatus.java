package it.drwolf.slot.enums;

public enum SlotInstStatus {
	EMPTY("Vuota"), INVALID("Non valida"), VALID("Valida");
	public static SlotInstStatus fromValue(String parameter) {
		for (SlotInstStatus c : SlotInstStatus.values()) {
			if (c.value.equals(parameter)) {
				return c;
			}
		}
		throw new IllegalArgumentException(parameter);
	}

	private final String value;

	SlotInstStatus(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

}
