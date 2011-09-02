package it.drwolf.slot.enums;

public enum CollectionQuantifier {

	ANY_OR_ONE("Qualunque"), ANY_OR_MORE("Zero o uno"), ONLY_ONE(
			"Esattamente uno"), ONE_OR_MORE("Almeno uno");
	private final String value;

	CollectionQuantifier(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	public static CollectionQuantifier fromValue(String parameter) {
		for (CollectionQuantifier c : CollectionQuantifier.values()) {
			if (c.value.equals(parameter)) {
				return c;
			}
		}
		throw new IllegalArgumentException(parameter);
	}

}
