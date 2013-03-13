package org.jab.model.message;

public class OutgoingMessage extends AbstractMessage {
	
	private String receiver;
	
	public OutgoingMessage(String receiver, String sound, String message) {
		super(sound, message);
		
		this.receiver = receiver;
	}
	
	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}	
	
	
}
