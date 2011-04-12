package it.drwolf.force.enums;

public enum DataType {

	STRING("String"), INTEGER("Integer"), BOOLEAN("Boolean"), DATE("Date");
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

}
