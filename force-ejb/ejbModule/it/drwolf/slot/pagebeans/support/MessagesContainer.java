package it.drwolf.slot.pagebeans.support;

import it.drwolf.slot.ruleverifier.VerifierMessage;

import java.util.ArrayList;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("messagesContainer")
@Scope(ScopeType.CONVERSATION)
public class MessagesContainer {

	// main messages
	private ArrayList<VerifierMessage> messages = new ArrayList<VerifierMessage>();

	public ArrayList<VerifierMessage> getMessages() {
		return this.messages;
	}

	public void setMessages(ArrayList<VerifierMessage> messages) {
		this.messages = messages;
	}

}
