package it.drwolf.slot.ruleverifier;

import java.util.ArrayList;
import java.util.List;

public class VerifierReport {

	private VerifierResult result;

	private List<VerifierParameterInst> failedParams = new ArrayList<VerifierParameterInst>();

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

	// private boolean passed;

	// private VerifierMessage message;

	// public boolean isPassed() {
	// return passed;
	// }
	//
	// public void setPassed(boolean result) {
	// this.passed = result;
	// }

	// public VerifierMessage getMessage() {
	// return message;
	// }
	//
	// public void setMessage(VerifierMessage message) {
	// this.message = message;
	// }

}
