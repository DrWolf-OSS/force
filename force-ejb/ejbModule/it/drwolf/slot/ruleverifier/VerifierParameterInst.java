package it.drwolf.slot.ruleverifier;

import it.drwolf.slot.interfaces.DataDefinition;
import it.drwolf.slot.interfaces.DataInstance;

public class VerifierParameterInst implements DataInstance {

	private VerifierParameterDef verifierParameterDef;

	private Object value;

	private boolean fallible = true;

	private ParameterCoordinates parameterCoordinates;

	public VerifierParameterInst(VerifierParameterDef verifierParameterDef,
			Object value) {
		super();
		this.verifierParameterDef = verifierParameterDef;
		this.value = value;
	}

	public VerifierParameterInst(VerifierParameterDef verifierParameterDef,
			Object value, boolean fallible) {
		super();
		this.verifierParameterDef = verifierParameterDef;
		this.value = value;
		this.fallible = fallible;
	}

	public VerifierParameterDef getVerifierParameterDef() {
		return verifierParameterDef;
	}

	public void setVerifierParameterDef(
			VerifierParameterDef verifierParameterDef) {
		this.verifierParameterDef = verifierParameterDef;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isFallible() {
		return fallible;
	}

	public void setFallible(boolean fallible) {
		this.fallible = fallible;
	}

	@Override
	public String toString() {
		return value.toString();
	}

	public ParameterCoordinates getParameterCoordinates() {
		return parameterCoordinates;
	}

	public void setParameterCoordinates(
			ParameterCoordinates parameterCoordinates) {
		this.parameterCoordinates = parameterCoordinates;
	}

	public DataDefinition getDataDefinition() {
		return this.verifierParameterDef;
	}

}
