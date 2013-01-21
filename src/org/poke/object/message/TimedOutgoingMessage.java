package org.poke.object.message;

public class TimedOutgoingMessage extends OutgoingMessage {
	
	private Long timeToSend;
	
	public TimedOutgoingMessage(String receiver, String sound, String message, Long timeToSend) {
		super(receiver, sound, message);
		
		this.setTimeToSend(timeToSend);
	}

	public Long getTimeToSend() {
		return timeToSend;
	}

	public void setTimeToSend(Long timeToSend) {
		this.timeToSend = timeToSend;
	}

}
