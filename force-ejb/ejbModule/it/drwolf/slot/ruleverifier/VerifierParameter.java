package it.drwolf.slot.ruleverifier;

import it.drwolf.slot.enums.DataType;

public class VerifierParameter {

	private String name;
	private DataType type;

	public VerifierParameter(String name, DataType type) {
		super();
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return name;
	}

}
