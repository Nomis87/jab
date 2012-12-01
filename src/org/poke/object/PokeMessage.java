package org.poke.object;

import android.os.Parcel;
import android.os.Parcelable;

public class PokeMessage implements Parcelable {
	
	private String sender;
	private String sound;
	private String message;
	
	public PokeMessage(String sender, String sound, String message){
		
		this.sender = sender;
		this.sound = sound;
		this.message = message;
	}
	
	public PokeMessage(){
		
	}
	
	
	public String getSender() {
		return sender;
	}


	public void setSender(String sender) {
		this.sender = sender;
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


	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}


	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
	
}
