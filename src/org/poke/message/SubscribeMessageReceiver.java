package org.poke.message;

import java.util.List;

import org.poke.database.DbContactsRepository;
import org.poke.database.DbRosterRepository;
import org.poke.object.contact.HandyContact;
import org.poke.object.contact.RosterContact;
import org.poke.util.ApplicationConstants;
import org.poke.xmpp.XMPPRosterStorage;
import org.poke.xmpp.XMPPConnectionHandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class SubscribeMessageReceiver extends BroadcastReceiver{
	
	private final String TAG = "SubscribeMessageReceiver";
	public static final String SUBSCRIBEMESSAGE_INTENT = "org.poke.SUBSCRIBEMESSAGE_INTENT";
	private Context context;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		this.context = context;
		
		String sender = intent.getStringExtra("subscribeSender");
		boolean check = false;
		DbContactsRepository contactRepository = new DbContactsRepository(context);
		
		HandyContact handyContact = null;
		List<HandyContact> handyContactList = contactRepository.getAllContacts();
		for(HandyContact hc : handyContactList){
			
			if(sender.contains(hc.getNumber())){
				
				check = true;
				handyContact = hc;
				break;
			}
		}
		
		if(check){
			
			
			addToRoster(sender, handyContact.getName());
			showNotification(handyContact);
		}
		else{
			
			Log.d(TAG, sender+" nicht in der Kontaktliste.");
		}
	}
	
	/**
	 * Notification wid erzeugt und angezeigt
	 * @param context
	 * @param hc
	 */
	private void showNotification(HandyContact hc){
		
		
		Context context = this.context.getApplicationContext();
		CharSequence text = hc.getName()+" benutzt nun auch "+ApplicationConstants.APP_NAME;
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		
		// Notification muss noch implementiert werden!!!!
		Log.d(TAG, "Notification muss noch implementiert werden");
		
	}
	
	private void addToRoster(String userJid, String username){
		
		RosterContact rc = new RosterContact();
		rc.setJid(userJid);
		rc.setUsername(username);
		
		//Add Online to Roster
		XMPPRosterStorage rs = new XMPPRosterStorage();
		rs.addEntry(rc, XMPPConnectionHandler.getInstance().getConnection());
		
		//Add to Roster DB
		DbRosterRepository rosterRepository = new DbRosterRepository(context);
		if(rosterRepository.containsRosterEntry(rc)){
			
			rosterRepository.createRosterEntry(rc);
		}
		
	}
		
}
