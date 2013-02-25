package org.jab.model.message;

public abstract class AbstractMessage {
	
	protected String sound;
	protected String message;
	
	public AbstractMessage(String sound, String message){
		
		this.sound = sound;
		this.message = message;
	}

	public String getSound() {
		return sound;
	}


	public void setSound(String sound) {
		this.sound = sound;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}

}
