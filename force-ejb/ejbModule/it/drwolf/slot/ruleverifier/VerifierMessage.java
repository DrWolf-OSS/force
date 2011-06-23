package it.drwolf.slot.ruleverifier;

public class VerifierMessage {

	// private Long id;
	private String text;
	private VerifierMessageType type;

	public VerifierMessage() {
	}

	public VerifierMessage(String text, VerifierMessageType type) {
		this.text = text;
		this.type = type;
	}

	// @Id
	// @GeneratedValue
	// public Long getId() {
	// return id;
	// }
	//
	// public void setId(Long id) {
	// this.id = id;
	// }

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	// @Enumerated(EnumType.STRING)
	public VerifierMessageType getType() {
		return type;
	}

	public void setType(VerifierMessageType type) {
		this.type = type;
	}

}
