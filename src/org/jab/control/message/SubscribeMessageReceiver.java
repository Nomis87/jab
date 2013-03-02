package org.jab.control.message;

import java.util.List;

import org.jab.control.storage.database.DbPhoneBookRepository;
import org.jab.control.storage.database.DbRosterRepository;
import org.jab.control.util.ApplicationConstants;
import org.jab.control.util.HelperFunctions;
import org.jab.control.xmpp.XMPPConnectionHandler;
import org.jab.control.xmpp.XMPPRosterStorage;
import org.jab.model.contact.HandyContact;
import org.jab.model.contact.RosterContact;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class SubscribeMessageReceiver extends BroadcastReceiver{
	
	private final String TAG = "SubscribeMessageReceiver";
	public static final String SUBSCRIBEMESSAGE_INTENT = "org.jab.SUBSCRIBEMESSAGE_INTENT";
	private Context context;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		this.context = context;
		
		String sender = intent.getStringExtra("subscribeSender");
		DbPhoneBookRepository contactRepository = new DbPhoneBookRepository(context);
		
		List<HandyContact> handyContactList = contactRepository.getAllContacts();
		for(HandyContact hc : handyContactList){
			
			if(sender.contains(HelperFunctions.getInstance().cleanNumber(hc.getNumber()))){
				
				addToRoster(sender, hc.getName());
				showNotification(hc);		
				break;
			}
			else{
				
				Log.d(TAG, sender+" nicht in der Kontaktliste.");
			}
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
		if(!rosterRepository.containsRosterEntry(rc)){
			
			rosterRepository.createRosterEntry(rc);
		}
		
	}
		
}
