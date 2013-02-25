package org.jab.control.contact;

import java.util.ArrayList;

import org.jab.control.storage.database.DbPhoneBookRepository;
import org.jab.control.storage.database.DbRosterRepository;
import org.jab.control.storage.database.DbUserRepository;
import org.jab.control.xmpp.XMPPConnectionHandler;
import org.jab.control.xmpp.XMPPRosterStorage;
import org.jab.model.User;
import org.jab.model.contact.HandyContact;
import org.jab.model.contact.RosterContact;
import org.jivesoftware.smack.XMPPException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ContactReceiver extends BroadcastReceiver {
	
	
	public static String CONTACT_INTENT = "org.jab.CONTACT_INTENT"; 
	private final String TAG = "ContactReceiver";
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		// Repositorys für Datenbankzugriff
		DbPhoneBookRepository contactRepository = new DbPhoneBookRepository(context);
		DbRosterRepository rosterRepository = new DbRosterRepository(context);
		DbUserRepository userRepository = new DbUserRepository(context);
		User user = userRepository.readUser();
		
		//XMPP Kram 
		XMPPConnectionHandler handler = XMPPConnectionHandler.getInstance();
		XMPPRosterStorage rs = new XMPPRosterStorage();
		
		int operation = intent.getIntExtra("contactOperation", 3);
		
		//Contact created
		if(operation == ContactObserver.CONTACT_CREATED){
			
			//Debug
			Log.d(TAG, "Kontakt create wird aufgerufen");
			
			ArrayList<Integer> contactIdlist = intent.getIntegerArrayListExtra("contactIdList");
			
			for(Integer id : contactIdlist){
				
				HandyContact hc = contactRepository.findContactById(id);
				
				if(handler.isRegistered(user.getCountryCode(), hc.getNumber())){
					
					rs.addEntry(user.getCountryCode(), hc.getNumber(), hc.getName(), handler.getConnection());	
					RosterContact rc = new RosterContact();
					rc.setJid(user.getCountryCode(), hc.getName());
					rosterRepository.createRosterEntry(rc);
					
					Log.d(TAG, "Create: jid "+rc.getJid());	
					Log.d(TAG, "Create: name "+rc.getUsername());
					
					//Send Added Message to user!!! 
				}
			}
			
		}
		
		else if(operation == ContactObserver.CONTACT_UPDATED){
			
			//Debug
			Log.d(TAG, "Kontakt Update wird aufgerufen");
			
			ArrayList<Integer> contactIdlistnew = intent.getIntegerArrayListExtra("contactIdList");
			ArrayList<String> odlContactNumbers = intent.getStringArrayListExtra("oldContactNumbers");
			
			for(int i = 0; i<contactIdlistnew.size(); i++){
				
				HandyContact hc = contactRepository.findContactById(contactIdlistnew.get(i));
				
				RosterContact rcOld = new RosterContact();
				rcOld.setJid(user.getCountryCode(), odlContactNumbers.get(i));
				
				RosterContact rcNew = new RosterContact();
				rcNew.setJid(user.getCountryCode(), hc.getNumber());
				rcNew.setUsername(hc.getName());
				
				Log.d(TAG, "Update: jid "+rcNew.getJid());	
				Log.d(TAG, "Update: name "+rcNew.getUsername());
				
				if(!handler.getConnection().isAuthenticated()){
					
					try {
						handler.login(user.getUserId(), user.getPassword());
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				rs.updateEntry(rcOld, rcNew, handler.getConnection());
				rosterRepository.updateRosterEntry(rcOld, rcNew);
			}
			
		}
		
		else if (operation == ContactObserver.CONTACT_DELETED){
			
			//Debug
			Log.d(TAG, "Kontakt Deletet wird aufgerufen");
		
			String oldNumber = intent.getStringExtra("oldNumber");
			
			RosterContact rc = new RosterContact();
			rc.setJid(user.getCountryCode(), oldNumber);
			
			Log.d(TAG, "Delete: jid "+rc.getJid());
		
			rs.removeEntry(rc, handler.getConnection());
			rosterRepository.deleteRosterEntry(rc);
			
			
		}
		
		else{
			
			Log.d(TAG, "Fheler bei der Übertragung der Operation");
		}
		
	}

}
