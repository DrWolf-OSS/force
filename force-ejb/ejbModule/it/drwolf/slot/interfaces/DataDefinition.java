package it.drwolf.slot.interfaces;

import it.drwolf.slot.enums.DataType;

public interface DataDefinition {

	public String getLabel();

	public DataType getDataType();

	public boolean isRequired();

	public boolean isEditable();

}
