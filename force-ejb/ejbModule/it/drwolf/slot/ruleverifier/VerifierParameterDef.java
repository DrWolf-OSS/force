package it.drwolf.slot.ruleverifier;

import it.drwolf.slot.enums.DataType;

public class VerifierParameterDef {

	private String name;
	private DataType type;
	private String label;

	public VerifierParameterDef(String name, String label, DataType type) {
		super();
		this.name = name;
		this.label = label;
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

	public String getLabel() {
		if (this.label != null) {
			return label;
		}
		return name;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return name;
	}
}
