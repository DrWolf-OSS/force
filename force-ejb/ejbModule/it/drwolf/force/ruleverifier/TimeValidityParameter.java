package it.drwolf.force.ruleverifier;

import it.drwolf.force.enums.DataType;

public enum TimeValidityParameter {
	EXPIRATION_DATE("ExpirationDate", DataType.DATE), DATE_TO_VERIFY(
			"ExpirationDate", DataType.DATE);
	private final String paramName;
	private final DataType paramType;

	private TimeValidityParameter(String name, DataType type) {
		this.paramName = name;
		this.paramType = type;
	}

	public DataType type() {
		return this.paramType;
	}

	public String paramName() {
		return this.paramName;
	}

}
