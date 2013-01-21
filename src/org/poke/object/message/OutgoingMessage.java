package org.poke.object.message;

public class OutgoingMessage extends AbstractMessage {
	
	private String receiver;
	private Integer id;
	
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	
	
	
}
