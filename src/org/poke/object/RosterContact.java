package org.poke.object;

import org.poke.util.ApplicationConstants;

public class RosterContact {
	
	private String jid;
	private String username;
	
	public RosterContact(){
			
	}

	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}
	
	public void setJid(String countryCode, String number){
		
		
		this.jid = countryCode+"_"+number+"@"+ApplicationConstants.SERVER_NAME;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
