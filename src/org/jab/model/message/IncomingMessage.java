package org.jab.model.message;

public class IncomingMessage extends AbstractMessage {
	
	private String sender;
	
	public IncomingMessage(String sender, String sound, String message) {
		super(sound, message);
		
		this.sender = sender;
		// TODO Auto-generated constructor stub
	}
	
	public String getSender() {
		return sender;
	}


	public void setSender(String sender) {
		this.sender = sender;
	}
	
	
	
}
