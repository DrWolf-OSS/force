package it.drwolf.slot.ruleverifier;

public class VerifierParameterInst {

	private VerifierParameterDef verifierParameterDef;

	private Object value;

	private String lable;

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

	public String getLable() {
		return lable;
	}

	public void setLable(String lable) {
		this.lable = lable;
	}

}
