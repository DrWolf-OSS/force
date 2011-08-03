package it.drwolf.slot.entity;

import it.drwolf.slot.entity.listeners.RuleParameterInstListener;
import it.drwolf.slot.enums.DataType;
import it.drwolf.slot.interfaces.DataDefinition;
import it.drwolf.slot.interfaces.DataInstance;
import it.drwolf.slot.ruleverifier.VerifierParameterDef;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@EntityListeners(value = RuleParameterInstListener.class)
public class RuleParameterInst implements DataInstance {

	private Long id;

	private String parameterName;

	private VerifierParameterDef verifierParameterDef;

	private Rule rule;

	private String stringValue;

	private Integer integerValue;

	private Boolean booleanValue;

	private Date dateValue;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Integer getIntegerValue() {
		return integerValue;
	}

	public void setIntegerValue(Integer integerValue) {
		this.integerValue = integerValue;
	}

	public Boolean getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	@Temporal(TemporalType.DATE)
	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	@ManyToOne
	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	@Transient
	public VerifierParameterDef getVerifierParameterDef() {
		return verifierParameterDef;
	}

	public void setVerifierParameterDef(
			VerifierParameterDef verifierParameterDef) {
		this.verifierParameterDef = verifierParameterDef;
	}

	@Transient
	public Object getValue() {
		if (this.getVerifierParameterDef().getDataType().equals(DataType.STRING))
			return this.getStringValue();
		else if (this.getVerifierParameterDef().getDataType()
				.equals(DataType.INTEGER))
			return this.getIntegerValue();
		else if (this.getVerifierParameterDef().getDataType()
				.equals(DataType.BOOLEAN))
			return this.getBooleanValue();
		else if (this.getVerifierParameterDef().getDataType().equals(DataType.DATE))
			return this.getDateValue();
		else
			return null;
	}

	//
	@Transient
	public DataDefinition getDataDefinition() {
		return this.getVerifierParameterDef();
	}

}
