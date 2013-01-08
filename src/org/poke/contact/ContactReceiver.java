package org.poke.contact;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.XMPPException;
import org.poke.database.DbRosterRepository;
import org.poke.database.DbUserRepository;
import org.poke.helper.HelperFunctions;
import org.poke.object.HandyContact;
import org.poke.object.User;
import org.poke.xmpp.RosterStorage;
import org.poke.xmpp.XMPPConnectionHandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

public class ContactReceiver extends BroadcastReceiver {
	
	
	public static String CONTACT_INTENT = "org.poke.CONTACT_INTENT"; 
	private final String TAG = "ContactReceiver";
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		List<HandyContact> contactList = getContact(context);
		addToRoster(context, contactList);
		
	}
	
	
	//Sucht nach dem geänderten oder neuerstellten Kontakt
	private List<HandyContact> getContact(Context context){
		
		List<HandyContact> contactList = new ArrayList<HandyContact>();
		
		Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI ,null, null, null, null);
		cursor.moveToLast();
		
		if(cursor.moveToFirst()){
			
			String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			String userId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
			Integer hasPhone = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER)));
			
			//Prüfen ob der Kontakt eine Telefonnummer besitzt 
			if(hasPhone==1){
				
				//Da ein Kontakt mehrere Nummern beinhalten kann mussen man einen zusätzlichen Cursor erzeugen
				Cursor contactCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, 
	     				  ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ userId, null, null);
				
				if(contactCursor.moveToFirst()){
					
					while(contactCursor.moveToNext()){
						
						String number = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						
						if(number.length() > 7){
							
							if(name == null){
								
								name = number;
							}
							
							HandyContact handyContact = new HandyContact(HelperFunctions.getInstance().cleanNumber(number), name);
							contactList.add(handyContact);
						}
					}
				}
			}
		}
		
		return contactList;	
	}
	
	private void addToRoster(Context context, List<HandyContact> contacts){
		
		//Zum einloggen
		DbUserRepository userRepository = new DbUserRepository(context);
		User user = userRepository.read();
		
		DbRosterRepository rosterRepository = new DbRosterRepository(context);
		
		XMPPConnectionHandler connectionHandler = XMPPConnectionHandler.getInstance();
		RosterStorage rs = new RosterStorage();
		
		try {
			connectionHandler.login(user.getUserId(), user.getPassword());
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(HandyContact contact : contacts){
			
			Log.d(TAG, "Name: "+contact.getName()+" Number: "+contact.getNumber());
			
			if(connectionHandler.isRegistered(user.getCountryCode()+"_"+contact.getNumber())){
				
				String userJid = user.getCountryCode()+"_"+contact.getNumber()+"@"+connectionHandler.getConnection().getServiceName();
				
				rs.addEntry(userJid, contact.getName(),connectionHandler.getConnection());
				
				rosterRepository.create(userJid, contact.getName());
				
			}
		}
		
	}

}
