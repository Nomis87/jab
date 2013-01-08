package org.poke.contact;


import java.util.ArrayList;
import java.util.List;

import org.poke.database.DbContactsRepository;
import org.poke.helper.HelperFunctions;
import org.poke.object.HandyContact;
import org.poke.util.ApplicationConstants;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContactsEntity;
import android.util.Log;

public class ContactObserver extends ContentObserver {
	
	//Debug Tag
	private final String TAG = "ContactObserver";
	
	//Konstanten zum prüfen der CUD Operationen
	public final int CONTACT_CREATED = 0;
	public final int CONTACT_UPDATED = 1;
	public final int CONTACT_DELETED = 2;
	
	// Zum speichern von Contacten in die SQLite Datenbank
	private DbContactsRepository contactsRepository;
	
	//Testobjekte handy Contacts für create und id für update und delete 
	private List<HandyContact> handyContacts;
	private int contactId;
	
	//Debug kram
	private String debug;
	
	private Context context;
	
	public ContactObserver(Handler handler, Context context) {
		super(handler);
		
		this.context = context;
	}
	
	@Override
	public void onChange(boolean selfChange) {
		// TODO Auto-generated method stub
		super.onChange(selfChange);
		
		Log.d(TAG, "Somthing changed");
		
		contactsRepository = new DbContactsRepository(context);
		
		
		handyContacts = new ArrayList<HandyContact>();
		
		
		if(testCreateOperation() == true){
			
			ArrayList<Integer> contactIdList = new ArrayList<Integer>();
			
			Intent intent = new Intent(ContactReceiver.CONTACT_INTENT);
			intent.putExtra("contactOperation", CONTACT_CREATED);
			for(HandyContact hc : handyContacts){
				
				contactIdList.add(hc.getId());
				contactsRepository.saveContact(hc);
			}
			
			intent.putIntegerArrayListExtra("contactIdList", contactIdList);
			context.sendBroadcast(intent);
		
		}
		
		else if(testUpdateOperation()){
			
			ArrayList<Integer> contactIdList = new ArrayList<Integer>();
			
			Intent intent = new Intent(ContactReceiver.CONTACT_INTENT);
			intent.putExtra("contactOperation", CONTACT_UPDATED);
			for(HandyContact hc : handyContacts){
				
				contactIdList.add(hc.getId());
				contactsRepository.updateContact(hc);
			}
			
			intent.putIntegerArrayListExtra("contactIdList", contactIdList);
			context.sendBroadcast(intent);
			//Contact Receiver mit updated param und kontaktid param
		}
		
		else if(testDeleteOperation()){
			
			Intent intent = new Intent(ContactReceiver.CONTACT_INTENT);
			intent.putExtra("contactOperation", CONTACT_DELETED);
			intent.putExtra("contactId", this.contactId);
			
			context.sendBroadcast(intent);
			//Contact Receiver mit delete param und kontaktid param
		}
		
		else{
			
			//Log.d(TAG, name);
			Log.d(TAG, "Else Klausel wird aufgerufen");
		}
		

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
        
        if(firstId >= secondId){
        	
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
	
	/**
	 * Testet ob ein Objekt geupdated wurde.
	 * @return
	 */
	private boolean testUpdateOperation(){
		
		boolean check = false;
		
		// Normaler Cursor für dd, name und number
		Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI ,null, null, null, null);      
		
		while(cursor.moveToNext()){
			
			int contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			
			Integer version = null;
			String[] projection = new String[] {RawContacts.VERSION};
	        Cursor rawCursor = context.getContentResolver().query(RawContacts.CONTENT_URI ,projection, 
	        		RawContacts.CONTACT_ID + "=" + contactId, null, null);
	        
	        //Version Lesen		
    		if (rawCursor != null && rawCursor.moveToFirst()) {
                // Just return the first one.
    			version =  rawCursor.getInt(rawCursor.getColumnIndex(RawContacts.VERSION));
            }
			
			if(version != null){
				
				if(contactsRepository.findContactById(contactId).getVersion() != version){
		        	
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
					
		        	check = true;
		        	break;
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
		
        if(firstId <= secondId){
        	
        	this.contactId = secondId;
        	check = true;
        }
             
		return check;
	}
	
	private void debugFunction(){
		
		
	}
	

}
