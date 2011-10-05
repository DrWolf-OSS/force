package it.drwolf.slot.interfaces;

import it.drwolf.slot.enums.DataType;

import java.util.List;

public interface DataDefinition extends Definition {

	// public String getLabel();

	public DataType getDataType();

	public List<String> getDictionaryValues();

	public boolean isEditable();

	public boolean isMultiple();

	public boolean isRequired();
}
