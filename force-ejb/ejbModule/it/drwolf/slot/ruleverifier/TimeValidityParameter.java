package it.drwolf.slot.ruleverifier;

import it.drwolf.slot.enums.DataType;

public enum TimeValidityParameter {
	EXPIRATION_DATE("ExpirationDate", DataType.DATE), DATE_TO_VERIFY(
			"DateToVerify", DataType.DATE);
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
