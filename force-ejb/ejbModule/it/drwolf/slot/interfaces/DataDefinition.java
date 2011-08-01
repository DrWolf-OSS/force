package it.drwolf.slot.interfaces;

import it.drwolf.slot.enums.DataType;

import java.util.List;

public interface DataDefinition {

	public String getLabel();

	public DataType getDataType();

	public boolean isRequired();

	public boolean isEditable();

	// public boolean isMultiple();

	public List<String> getDictionaryValues();
}
