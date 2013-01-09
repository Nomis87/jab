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

import android.util.Log;

public class RosterStorage {
	
	private final String TAG = "RosterStorage";
	
	public static RosterStorage instance = null;
	
	public static synchronized RosterStorage getInstance(){
		
		if(instance == null){
			instance = new RosterStorage();
		}
		
		return instance;
	}
	
	public void addEntry(String countryCode, String number, String nickname, XMPPConnection connection) {
		
		String jid = countryCode+"_"+number+"@"+connection.getServiceName();
		
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
	
	
	public void updateEntry(RosterContact rcOld, RosterContact rcNew, XMPPConnection connection){
		
		if((connection!=null)&&(connection.isAuthenticated())){
			
			Roster roster = connection.getRoster();
			
			// Die Subscruption auf manuell setzen und asnchließend den Listener dafür Initialisieren
			roster.setSubscriptionMode(SubscriptionMode.accept_all);
			
		    if(roster.contains(rcOld.getJid())){
		    
		    	try {
		    		
		    		removeEntry(rcOld, connection);
		    		
		    		//Ein Eintrag in den Roster erstellen
		    		roster.createEntry(rcNew.getJid(), rcNew.getUsername(), null);
		    		
		    		Presence subscribe = new Presence(Presence.Type.subscribe);
		    		subscribe.setTo(rcNew.getJid());
		    		connection.sendPacket(subscribe);
		    		
		    		System.out.println(rcNew.getUsername()+"  wurde geupdated");
		    	} 
		    	catch (XMPPException e) {
		    
		    		System.out.println(rcNew.getUsername()+"  wurde nicht gefunden");
		    		System.out.println(rcNew.getJid());
		    	}
		    }
			
		}
		
	}

	/**
	 * Liefert 
	 * @param userId
	 * @param connection
	 * @return
	 */
	public RosterContact getEntry(String userId, XMPPConnection connection) {
		
		ArrayList<RosterContact> contacList = getEntries(connection);
		RosterContact entry = null;
		
		
		for(RosterContact contact : contacList ){
			
			if(contact.getJid().equals(userId)){
				
				entry = contact;
			}
		}
		
		return entry;
	}
	
	/**
	 * Zählt wieviele Kontakte sich im Roster befinden
	 * @param connection XmppConection
	 * @return die Anzahl der Kontakte die sich im Roster befinden
	 */
	public Integer getEntryCount(XMPPConnection connection) {
		
		Integer count = null;
		
		if((connection!=null)&&(connection.isAuthenticated())){

			connection.getRoster().setSubscriptionMode(SubscriptionMode.manual);
			count = connection.getRoster().getEntryCount();
		}
		
		return count;
	}
	
	
	/**
	 * Metode zum löschen eines Contacts aus dem roster
	 * @param rc RosterContact beinhaltet JID und name
	 * @param connection XmppConnection 
	 */
	public void removeEntry(RosterContact rc, XMPPConnection connection) {
		
		
		if((connection!=null)&&(connection.isAuthenticated())){
			
			Roster roster = connection.getRoster();
					
			try {
				
				if(roster.contains(rc.getJid())){
					
					roster.removeEntry(roster.getEntry(rc.getJid()));
				}
				else{
					
					Log.d(TAG, rc.getUsername()+" befindet sich nicht im Roster");
				}
				
				
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
