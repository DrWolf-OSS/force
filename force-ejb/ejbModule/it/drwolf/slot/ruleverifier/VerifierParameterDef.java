package it.drwolf.slot.ruleverifier;

import it.drwolf.slot.entity.Dictionary;
import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.interfaces.DataDefinition;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

public class VerifierParameterDef implements DataDefinition {

	private String name;
	private DataType dataType;
	private String label;
	private boolean optional;
	private boolean ruleEmbedded;
	private boolean multiple;
	private String description;

	private Dictionary dictionary;

	public VerifierParameterDef(String name, String label, DataType type,
			boolean optional, boolean ruleEmbedded, boolean multiple,
			Dictionary dictionary, String description) {
		super();
		this.name = name;
		this.label = label;
		this.dataType = type;
		this.optional = optional;
		this.ruleEmbedded = ruleEmbedded;
		this.multiple = multiple;
		this.dictionary = dictionary;
		this.description = description;
	}

	public VerifierParameterDef(String name, String label, DataType type,
			boolean optional, boolean ruleEmbedded, boolean multiple,
			String description) {
		super();
		this.name = name;
		this.label = label;
		this.dataType = type;
		this.optional = optional;
		this.ruleEmbedded = ruleEmbedded;
		this.multiple = multiple;
		this.description = description;
	}

	public DataType getDataType() {
		return this.dataType;
	}

	public String getDescription() {
		return this.description;
	}

	public Dictionary getDictionary() {
		return this.dictionary;
	}

	public List<String> getDictionaryValues() {
		if (this.dictionary != null) {
			return this.dictionary.getValues();
		}
		return null;
	}

	public String getLabel() {
		if (this.label != null) {
			return this.label;
		}
		return this.name;
	}

	public String getName() {
		return this.name;
	}

	public boolean isEditable() {
		return true;
	}

	public boolean isMultiple() {
		return this.multiple;
	}

	public boolean isOptional() {
		return this.optional;
	}

	public boolean isRequired() {
		return !this.optional;
	}

	public boolean isRuleEmbedded() {
		return this.ruleEmbedded;
	}

	public void setDataType(DataType type) {
		this.dataType = type;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public void setRuleEmbedded(boolean ruleEmbedded) {
		this.ruleEmbedded = ruleEmbedded;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public void validate(FacesContext context, UIComponent component, Object obj)
			throws ValidatorException {
		// TODO Auto-generated method stub

	}
}
