package it.drwolf.slot.ruleverifier;

import it.drwolf.slot.entity.Dictionary;
import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.interfaces.DataDefinition;

import java.util.List;

public class VerifierParameterDef implements DataDefinition {

	private String name;
	private DataType dataType;
	private String label;
	private boolean optional;
	private boolean ruleEmbedded;
	private boolean multiple;

	private Dictionary dictionary;

	public VerifierParameterDef(String name, String label, DataType type,
			boolean optional, boolean ruleEmbedded, boolean multiple) {
		super();
		this.name = name;
		this.label = label;
		this.dataType = type;
		this.optional = optional;
		this.ruleEmbedded = ruleEmbedded;
		this.multiple = multiple;
	}

	public VerifierParameterDef(String name, String label, DataType type,
			boolean optional, boolean ruleEmbedded, boolean multiple,
			Dictionary dictionary) {
		super();
		this.name = name;
		this.label = label;
		this.dataType = type;
		this.optional = optional;
		this.ruleEmbedded = ruleEmbedded;
		this.multiple = multiple;
		this.dictionary = dictionary;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType type) {
		this.dataType = type;
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

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public boolean isRuleEmbedded() {
		return ruleEmbedded;
	}

	public void setRuleEmbedded(boolean ruleEmbedded) {
		this.ruleEmbedded = ruleEmbedded;
	}

	public boolean isRequired() {
		return !optional;
	}

	public boolean isEditable() {
		return true;
	}

	public List<String> getDictionaryValues() {
		if (this.dictionary != null) {
			return dictionary.getValues();
		}
		return null;
	}

	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	public Dictionary getDictionary() {
		return dictionary;
	}

	public void setDictionary(Dictionary dictionary) {
		this.dictionary = dictionary;
	}
}
