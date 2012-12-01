package org.poke.xmpp;

import java.util.ArrayList;
import java.util.Collection;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.packet.Presence;
import org.poke.object.RosterContact;

public class RosterStorage {
	
	private final String TAG = "RosterStorage";
	
	public static RosterStorage instance = null;
	
	public static synchronized RosterStorage getInstance(){
		
		if(instance == null){
			instance = new RosterStorage();
		}
		
		return instance;
	}
	
	public void addEntry(String jid, String nickname, XMPPConnection connection) {
		
		
		
		if((connection!=null)&&(connection.isAuthenticated())){
			
			Roster roster = connection.getRoster();
			
			// Die Subscruption auf manuell setzen und asnchließend den Listener dafür Initialisieren
			roster.setSubscriptionMode(SubscriptionMode.accept_all);
			
		    if(!roster.contains(jid)){
		    
		    	try {
		    		
		    		//Ein Eintrag in den Roster erstellen
		    		roster.createEntry(jid, nickname, null);
		    		
		    		Presence subscribe = new Presence(Presence.Type.subscribe);
		    		subscribe.setTo(jid);
		    		connection.sendPacket(subscribe);
		    		
		    		System.out.println(nickname+"  wurde hinzugefügt");
		    	} 
		    	catch (XMPPException e) {
		    
		    		System.out.println(nickname+"  wurde nicht gefunden");
		    		System.out.println(jid);
		    	}
		    }
			
		}
		
	}

	public ArrayList<RosterContact> getEntries(XMPPConnection connection) {
		
		Collection<RosterEntry> list =null;
		ArrayList<RosterContact> rcList = new ArrayList<RosterContact>();
		
		if((connection!=null)&&(connection.isAuthenticated())){
			
			connection.getRoster().setSubscriptionMode(SubscriptionMode.accept_all);
			list = connection.getRoster().getEntries();
			
		}
		
		for(RosterEntry re : list){
			
			RosterContact rc = new RosterContact();
			
			rc.setJid(re.getUser());
			rc.setUsername(re.getName());
			
			rcList.add(rc);
		}
		
		return rcList;
	}

	public RosterEntry getEntry(String userId, XMPPConnection connection) {
		
		RosterEntry entry = null;
		
		if((connection!=null)&&(connection.isAuthenticated())){
			
			connection.getRoster().setSubscriptionMode(SubscriptionMode.manual);
			entry = connection.getRoster().getEntry(userId);
		}
		
		return entry;
	}

	public Integer getEntryCount(XMPPConnection connection) {
		
		Integer count = null;
		
		if((connection!=null)&&(connection.isAuthenticated())){

			connection.getRoster().setSubscriptionMode(SubscriptionMode.manual);
			count = connection.getRoster().getEntryCount();
		}
		
		return count;
	}

	public void removeEntry(String userId, XMPPConnection connection) {
		
		RosterEntry entry = getEntry(userId, connection);
		
		if((connection!=null)&&(connection.isAuthenticated())){
			
			connection.getRoster().setSubscriptionMode(SubscriptionMode.manual);
			try {
				connection.getRoster().removeEntry(entry);
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
