package org.poke.contact;


import java.util.ArrayList;
import java.util.List;

import org.poke.database.DbContactsRepository;
import org.poke.database.DbRosterRepository;
import org.poke.helper.ApplicationPreference;
import org.poke.helper.HelperFunctions;
import org.poke.object.HandyContact;
import org.poke.object.RosterContact;
import org.poke.xmpp.RosterStorage;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

public class ContactObserver extends ContentObserver {
	
	//Debug Tag
	private final String TAG = "ContactObserver";
	
	ApplicationPreference myPreference;
	
	//Konstanten zum prüfen der CUD Operationen
	public static final int CONTACT_CREATED = 0;
	public static final int CONTACT_UPDATED = 1;
	public static final int CONTACT_DELETED = 2;
	
	// Zum speichern von Contacten in die SQLite Datenbank
	private DbContactsRepository contactsRepository;
	
	//Testobjekte handy Contacts für create und id für update und delete 
	private List<HandyContact> handyContacts;
	private HandyContact handyContact;
	
	//Debug kram
	private String debug;
	
	private Context context;
	
	public ContactObserver(Handler handler, Context context) {
		super(handler);
		
		this.context = context;
		myPreference = new ApplicationPreference(context);
	}
	
	
	@Override
	public void onChange(boolean selfChange) {
		// TODO Auto-generated method stub
		super.onChange(selfChange);
		
		Log.d(TAG, "Somthing changed");
		
		contactsRepository = new DbContactsRepository(context);
		
		
		handyContacts = new ArrayList<HandyContact>();
		
		debugFunction();
		
//		if(testCreateOperation() == true){
//			
//			ArrayList<Integer> contactIdList = new ArrayList<Integer>();
//			
//			Intent intent = new Intent(ContactReceiver.CONTACT_INTENT);
//			intent.putExtra("contactOperation", CONTACT_CREATED);
//			for(HandyContact hc : handyContacts){
//				
//				contactIdList.add(hc.getId());
//				contactsRepository.saveContact(hc);
//			}
//			
//			intent.putIntegerArrayListExtra("contactIdList", contactIdList);
//			context.sendBroadcast(intent);
//		
//		}
//		
//		else if(testUpdateOperation()){
//			
//			ArrayList<Integer> contactIdListnew = new ArrayList<Integer>();
//			ArrayList<String> contactNumbers = new ArrayList<String>();
//			
//			Intent intent = new Intent(ContactReceiver.CONTACT_INTENT);
//			intent.putExtra("contactOperation", CONTACT_UPDATED);
//			
//			for(HandyContact hc : handyContacts){
//				
//				contactIdListnew.add(hc.getId());
//				contactNumbers.add(hc.getNumber());
//				contactsRepository.updateContact(hc);
//			}
//			
//			intent.putStringArrayListExtra("oldContactNumbers", contactNumbers);
//			intent.putIntegerArrayListExtra("contactIdList", contactIdListnew);
//			context.sendBroadcast(intent);
//			//Contact Receiver mit updated param und kontaktid param
//		}
//		
//		// Eventuell auch mit mehreren Nummern machen wie bei 
//		else if(testDeleteOperation()){
//			
//			Intent intent = new Intent(ContactReceiver.CONTACT_INTENT);
//			intent.putExtra("contactOperation", CONTACT_DELETED);
//			intent.putExtra("oldNumber", this.handyContact.getNumber());
//			contactsRepository.deleteContact(this.handyContact);
//			
//			context.sendBroadcast(intent);
//			//Contact Receiver mit delete param und kontaktid param
//		}
//		
//		else{
//			
//			myPreference.setSync();
//			//Log.d(TAG, name);
//			Log.d(TAG, "Else Klausel wird aufgerufen");
//		}
		

	}
	
	/**
	 * Testen ob es sich bei der Änderung um einen neuen Kontakte handelt.
	 * Wenn ja wird dieser in die Globale ContactList gespeichert. 
	 * Die Liste, da ein Kontakt mehrere Nummer enthalten kann. 
	 */
	private boolean testCreateOperation(){
		
		boolean check = false;
		
		// Normaler Cursor für dd, name und number
		Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI ,null, null, null, null);      
        cursor.moveToLast();  
        int firstId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        Log.d(TAG, "FirstId: "+firstId);
        int secondId = contactsRepository.getLastContact().getId();
        Log.d(TAG, "SecondId: "+secondId);
        
        if(firstId > secondId){
        	
        	Log.d("TAg", "Wird aufgerufen");
        	// Raw Cursor für Versionsnummer
            // Error, da die row nummer nicht lesen kann. 	
        	int version = 0;
            String[] projection = new String[] {RawContacts.VERSION};
            Cursor rawCursor = context.getContentResolver().query(RawContacts.CONTENT_URI ,projection, 
            		RawContacts.CONTACT_ID + "=" + firstId, null, null);
        			
    		//Version Lesen		
    		if (rawCursor != null && rawCursor.moveToFirst()) {
                // Just return the first one.
    			version =  rawCursor.getInt(rawCursor.getColumnIndex(RawContacts.VERSION));
            }
    		
    		rawCursor.close();
         
        	String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        	
        	Cursor phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, 
     				  ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ firstId, null, null); 
        	
     		while(phoneCursor.moveToNext()){       				
     			
     			String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
     			
     			if(number.length() >= 7){
     					
     				if(name == null){
							
     					name = number;
     				}
     				
     				HandyContact user = new HandyContact(firstId ,HelperFunctions.getInstance().cleanNumber(number), name ,version);
     				handyContacts.add(user);
     			}
     				
     		}
     		phoneCursor.close();
        	
        	check = true;   	
        }
		
        cursor.close();
        
		return check;
	}
	
	private boolean testUpdateOperation(){
		
		boolean check = false;
		
		List<HandyContact> contactListHandy = HelperFunctions.getInstance().getNumbersFromContacts(context);
		List<HandyContact> contactListDb = contactsRepository.getAllContacts();
		
		
		//Log.d(TAG, "contactListHandy Länge: "+contactListHandy.size());
		
		//Log.d(TAG, "contactListDb Länge: "+contactListDb.size());
		
		
		for(HandyContact handyContact : contactListHandy){
		
			for(HandyContact dbContact : contactListDb){
				
				if(dbContact.getId() == handyContact.getId()){
					
					if(handyContact.getVersion() != dbContact.getVersion()){
						
						Log.d(TAG, "Name 	   :"+handyContact.getName());
						Log.d(TAG, "old Version:"+dbContact.getVersion());
						Log.d(TAG, "new Version:"+handyContact.getVersion());
						
						handyContacts.add(handyContact);
						check = true;
					}
				}
				
				
			}
			
//			if(contactListHandy.get(i).getVersion() != contactListDb.get(i).getVersion()){
//				
//				handyContacts.add(contactListHandy.get(i));
//				check = true;
//			}
			
		}
		
		return check;
	}
	
	/**
	 * Testet ob ein Objekt geupdated wurde.
	 * @return true wenn geupdated wurde
	 */
	private boolean testUpdateOperation2(){
		
		boolean check = false;
		
		// Normaler Cursor für dd, name und number
		Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI ,null, null, null, null);      
		
		while(cursor.moveToNext()){
			
			int contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)) + 1;
			
			int version = 0;
			String[] projection = new String[] {RawContacts.VERSION};
	        Cursor rawCursor = context.getContentResolver().query(RawContacts.CONTENT_URI ,projection, 
	        		RawContacts.CONTACT_ID + "=" + contactId, null, null);
	        
	        //Version Lesen		
    		if (rawCursor != null && rawCursor.moveToFirst()) {
                // Just return the first one.
    			version =  rawCursor.getInt(rawCursor.getColumnIndex(RawContacts.VERSION));
            }
			rawCursor.close();
    		
			if(version != 0){
				
				HandyContact hc = contactsRepository.findContactById(contactId);
				
				if (hc != null){
					
					Log.d(TAG, "Version1: "+hc.getVersion());
					Log.d(TAG, "Version2: "+version);
					if(hc.getVersion() != version){
			        	
						String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
						Cursor phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, 
			     				  ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null); 
			        	
			     		while(phoneCursor.moveToNext()){       				
			     			
			     			String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			     			
			     			if(number.length() >= 7){
			     					
			     				if(name == null){
										
			     					name = number;
			     				}
			     				
			     				HandyContact user = new HandyContact(contactId ,HelperFunctions.getInstance().cleanNumber(number), name ,version);
			     				handyContacts.add(user);
			     			}
			     				
			     		}
			     		phoneCursor.close();
						
			        	check = true;
			        	break;
										
					}
					     		
				}
				
			}
			else{
				
				Log.d(TAG, "Version ist Null");
			}
	        
		}
		
		
		return check;
	}
	
	/**
	 * Testet ob ein Objekt geloescht wurde
	 * @return true wenn der Test erfolgreich ist.
	 */
	private boolean testDeleteOperation(){
		
		boolean check = false;
		
		// Normaler Cursor für dd, name und number
		Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI ,null, null, null, null);      
        cursor.moveToLast();  
        int firstId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        Log.d(TAG, "FirstId: "+firstId);
        int secondId = contactsRepository.getLastContact().getId();
        Log.d(TAG, "SecondId: "+secondId);
		
        if(firstId < secondId){
        	
        	this.handyContact = contactsRepository.getLastContact();
        	check = true;
        }
             
		return check;
	}
	
	private void debugFunction(){
		
		DbRosterRepository rep = new DbRosterRepository(context);
		
		List<RosterContact> test = rep.getAll();
		
		for(RosterContact rc : test){
			
			Log.d(TAG, "Username: "+rc.getUsername());
			Log.d(TAG, "Jid: "+rc.getJid());
		}
		
		
	}
	

}
