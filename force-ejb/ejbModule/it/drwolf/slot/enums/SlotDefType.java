package it.drwolf.slot.enums;

public enum SlotDefType {
	PRIMARY("Busta di Riferimento"), GENERAL("Busta Amministrativa"), DEPENDENT(
			"Sottobusta");
	public static SlotDefType fromValue(String parameter) {
		for (SlotDefType c : SlotDefType.values()) {
			if (c.value.equals(parameter)) {
				return c;
			}
		}
		throw new IllegalArgumentException(parameter);
	}

	private final String value;

	SlotDefType(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

}
