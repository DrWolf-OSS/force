package it.drwolf.slot.ruleverifier;

import java.util.ArrayList;
import java.util.List;

public class VerifierReport {

	private VerifierResult result;

	private List<VerifierParameterInst> failedParams = new ArrayList<VerifierParameterInst>();

	private List<VerifierParameterInst> warningParams = new ArrayList<VerifierParameterInst>();

	public VerifierResult getResult() {
		return result;
	}

	public void setResult(VerifierResult result) {
		this.result = result;
	}

	public List<VerifierParameterInst> getFailedParams() {
		return failedParams;
	}

	public void setFailedParams(List<VerifierParameterInst> failedParams) {
		this.failedParams = failedParams;
	}

	public List<VerifierParameterInst> getWarningParams() {
		return warningParams;
	}

	public void setWarningParams(List<VerifierParameterInst> warningParas) {
		this.warningParams = warningParas;
	}

}
