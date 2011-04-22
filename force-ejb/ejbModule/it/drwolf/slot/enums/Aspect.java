package it.drwolf.slot.enums;

public enum Aspect {

	SIGNED("P:slot:signed");
	private final String value;

	Aspect(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	public static Aspect fromValue(String parameter) {
		for (Aspect c : Aspect.values()) {
			if (c.value.equals(parameter)) {
				return c;
			}
		}
		throw new IllegalArgumentException(parameter);
	}

}
