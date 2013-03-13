package org.jab.model.message;

public abstract class AbstractMessage {
	
	protected Integer id;
	protected String sound;
	protected String message;
	protected long time;
	
	public AbstractMessage( String sound, String message){
		
		this.sound = sound;
		this.message = message;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
