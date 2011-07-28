package it.drwolf.slot.enums;

public enum DataType {

	STRING("String"), INTEGER("Integer"), BOOLEAN("Boolean"), DATE("Date"), LINK(
			"Link");
	private final String value;

	DataType(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	public static DataType fromValue(String parameter) {
		for (DataType c : DataType.values()) {
			if (c.value.equals(parameter)) {
				return c;
			}
		}
		throw new IllegalArgumentException(parameter);
	}

	public static DataType fromName(String parameter) {
		for (DataType c : DataType.values()) {
			if (c.name().equals(parameter)) {
				return c;
			}
		}
		throw new IllegalArgumentException(parameter);
	}

}
