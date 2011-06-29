package it.drwolf.slot.ruleverifier;

import it.drwolf.slot.interfaces.DataDefinition;

public class ParameterCoordinates {

	private Object sourceDef;

	private DataDefinition fieldDef;

	public ParameterCoordinates(Object sourceDef, DataDefinition fieldDef) {
		super();
		this.sourceDef = sourceDef;
		this.fieldDef = fieldDef;
	}

	public Object getSourceDef() {
		return sourceDef;
	}

	public void setSourceDef(Object sourceDef) {
		this.sourceDef = sourceDef;
	}

	public DataDefinition getFieldDef() {
		return fieldDef;
	}

	public void setFieldDef(DataDefinition fieldDef) {
		this.fieldDef = fieldDef;
	}

}
