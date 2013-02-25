package org.jab.model;

public class User {
	
	private String userId;
	private String password;
	private String countryCode;
	
	
	public User(String userId, String password, String countryCode){
		
		this.userId = userId;
		this.password = password;
		this.setCountryCode(countryCode);
			
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}
	

	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getCountryCode() {
		return countryCode;
	}


	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}	
	
}
