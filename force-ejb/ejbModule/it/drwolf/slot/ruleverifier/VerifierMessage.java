package it.drwolf.slot.ruleverifier;

public class VerifierMessage {

	private String text;
	private VerifierMessageType type;

	public VerifierMessage(String text, VerifierMessageType type) {
		this.text = text;
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public VerifierMessageType getType() {
		return type;
	}

	public void setType(VerifierMessageType type) {
		this.type = type;
	}

}
