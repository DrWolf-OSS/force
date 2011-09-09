package it.drwolf.slot.enums;

public enum RuleType {

	DATE_COMPARISON("Comparazione date"), INSTANT_DATE_VALIDITY(
			"Validità al momento attuale");
	private final String value;

	RuleType(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	public static RuleType fromValue(String parameter) {
		for (RuleType c : RuleType.values()) {
			if (c.value.equals(parameter)) {
				return c;
			}
		}
		throw new IllegalArgumentException(parameter);
	}

}
