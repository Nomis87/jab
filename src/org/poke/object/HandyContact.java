package org.poke.object;

import android.os.Parcel;
import android.os.Parcelable;

public class HandyContact implements Parcelable{
	
	private int id;
	private String number;
	private String name;
	private int version;
	
	public HandyContact(){}
	
	public HandyContact(String number, String name){
		
		this.number = number;
		this.name = name;
	}
	
	public HandyContact(int id, String number, String name, int version ){
		
		this.id = id;
		this.number = number;
		this.name = name;
		this.version = version;
	}
	
	public HandyContact(int id, String number, int version ){
		
		this.id = id;
		this.number = number;
		this.version = version;
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id ;
	}
	
	public String getNumber() {
		return number;
	}

	public void setNumber(String number){
		this.number = number;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
		
	public int getVersion(){
		return version;
	}
	
	public void setVersion(int version){
		this.version = version;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeInt(id);
		dest.writeString(number);
		dest.writeString(name);
		dest.writeInt(version);
		
	}
	
}
