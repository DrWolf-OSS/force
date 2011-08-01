package it.drwolf.slot.enums;

public enum DataInstanceMultiplicity {

	SINGLE("Single"), MULTIPLE("Multiple");
	private final String value;

	DataInstanceMultiplicity(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	public static DataInstanceMultiplicity fromValue(String parameter) {
		for (DataInstanceMultiplicity c : DataInstanceMultiplicity.values()) {
			if (c.value.equals(parameter)) {
				return c;
			}
		}
		throw new IllegalArgumentException(parameter);
	}

	public static DataInstanceMultiplicity fromName(String parameter) {
		for (DataInstanceMultiplicity c : DataInstanceMultiplicity.values()) {
			if (c.name().equals(parameter)) {
				return c;
			}
		}
		throw new IllegalArgumentException(parameter);
	}

}
