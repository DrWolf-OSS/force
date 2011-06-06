package it.drwolf.slot.ruleverifier;

public class VerifierReport {

	private boolean passed;

	private VerifierMessage message;

	public boolean isPassed() {
		return passed;
	}

	public void setPassed(boolean result) {
		this.passed = result;
	}

	public VerifierMessage getMessage() {
		return message;
	}

	public void setMessage(VerifierMessage message) {
		this.message = message;
	}

}
