package it.drwolf.slot.interfaces;

import it.drwolf.slot.enums.DataType;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

public interface DataDefinition extends Definition {

	// public String getLabel();

	public DataType getDataType();

	public List<String> getDictionaryValues();

	public boolean isEditable();

	public boolean isMultiple();

	public boolean isRequired();

	public void validate(FacesContext context, UIComponent component, Object obj)
			throws ValidatorException;
}
