package org.jab.view.activity;

import java.util.ArrayList;
import java.util.List;

import org.jab.control.storage.database.DbPhoneBookRepository;
import org.jab.control.storage.database.DbRosterRepository;
import org.jab.control.storage.database.DbUserRepository;
import org.jab.control.storage.preference.ApplicationPreference;
import org.jab.control.util.HelperFunctions;
import org.jab.control.xmpp.XMPPConnectionHandler;
import org.jab.control.xmpp.XMPPRosterStorage;
import org.jab.main.R;
import org.jab.main.R.layout;
import org.jab.main.R.menu;
import org.jab.model.User;
import org.jab.model.contact.HandyContact;
import org.jab.model.contact.RosterContact;
import org.jivesoftware.smack.RosterStorage;
import org.jivesoftware.smack.XMPPException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;

public class UpdateActivity extends Activity {
	
	
	private Context context;
	private UpdateActivity updateActivity;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        
        this.context = this;
        this.updateActivity = this;
        
        UpdateContactsTask uct = new UpdateContactsTask();
        uct.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_update, menu);
        return true;
    }
    
    /**
     * Task zum 
     * @author Tobias
     *
     */
    private class UpdateContactsTask extends AsyncTask<Void, Integer, Boolean>{
    	
    	private final String TAG = "UpdateContactsTask";
    	
    	private ApplicationPreference apf;
    	private ProgressDialog waitSpinner;
    	
    	//Repositorys
    	private DbPhoneBookRepository contactRepository;
    	private DbRosterRepository rosterRepository;
    	private DbUserRepository userRepository;
    	
    	//Handy User Data
    	private User user;
    	
    	
    	//Testobjekte handy Contacts für create und id für update und delete 
    	private List<HandyContact> handyContacts;
    	private HandyContact handyContact;
    	
    	//XMPP Kram 
    	private XMPPConnectionHandler handler = XMPPConnectionHandler.getInstance();
    	private XMPPRosterStorage rs = new XMPPRosterStorage();
    	
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
    		
    		apf = new ApplicationPreference(context);
    		waitSpinner = new ProgressDialog(context);
    		waitSpinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		waitSpinner.setMessage("Neue Kontakte werden geladen");
    		waitSpinner.show();
    	}
    	
    	
    	@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
    		
    		
    		_init();
    		
    		for(int i=0; i<=apf.getAsyncCounter(); i++){
    			
    			// Bei Erstellen einenes Useres
    			if(testCreateOperation() == true){
    			
    				Log.d(TAG, "Create wird aufgerufen");
    			
    			
    				for(HandyContact hc : handyContacts){
    				
    					if(hc.getName() != null){
    					
    						contactRepository.saveContact(hc);
    						createXmppContact(hc);
    					}
    				
    				}
    			
    		
    			}
    		
    			// BEim löschen eines Useres 
    			else if(testDeleteOperation()){
    			
    				Log.d(TAG, "Delete wird aufgerufen");
    			
    				if(handyContact.getCountryCode()== null){
    					handyContact.setCountryCode(user.getCountryCode());
    				}
    				contactRepository.deleteContact(this.handyContact);
    				deleteXmppContact(handyContact.getCountryCode(), handyContact.getNumber());

    			}
    		
    			//Beim ändern eines Useres
    			else if(testUpdateOperation()){
    			
    			
    				for(HandyContact hc : handyContacts){
    				
    					updateXmppContact(hc);
    					contactRepository.updateContact(hc);
    				}
    			
	
    			}
    			else {
				
   
    			}
    		}  
    		
    		return true;
    		
		}
    	
		


		@Override
    	protected void onPostExecute(Boolean result) {
    		// TODO Auto-generated method stub
    		super.onPostExecute(result);
    			
    		Intent activityIntent = new Intent(context, IndexActivity.class);
    		apf.setSync();
    		apf.resetAsyncCounter();
    		waitSpinner.cancel();
    		context.startActivity(activityIntent);
    		updateActivity.finish();
    		
    	}
		
		private void _init(){
			
			handyContacts = new ArrayList<HandyContact>();
			contactRepository = new DbPhoneBookRepository(context);
			rosterRepository = new DbRosterRepository(context);
			userRepository = new DbUserRepository(context);
			user = userRepository.readUser();
			
			
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
	        int secondId = contactRepository.getLastContact().getId();
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
	     				
	     				HandyContact user = new HandyContact(firstId ,HelperFunctions.getInstance().cleanNumber(number), name ,null,version);
	     				handyContacts.add(user);
	     			}
	     				
	     		}
	     		phoneCursor.close();
	        	
	        	check = true;   	
	        }
			
	        cursor.close();
	        
			return check;
		}
		
		private void createXmppContact(HandyContact hc) {
			
			if(hc.getCountryCode() == null){
				
				hc.setCountryCode(user.getCountryCode());
			}
			
			RosterContact rc = new RosterContact();
			rc.setJid(hc.getCountryCode(), hc.getNumber());
			rc.setUsername(hc.getName());
			
			rs.addEntry(rc, XMPPConnectionHandler.getInstance().getConnection());
			rosterRepository.createRosterEntry(rc);
		}
		
		/**
		 * Testet ob ein Objekt geupdated wurde.
		 * @return true wenn geupdated wurde
		 */
		private boolean testUpdateOperation(){
			
			boolean check = false;
			
			List<HandyContact> contactListHandy = HelperFunctions.getInstance().getNumbersFromContacts(context,null);
			List<HandyContact> contactListDb = contactRepository.getAllContacts();
			
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
				
				
			}
			
			return check;
		}
		
		/**
		 * Update Contact on XMPP Server 
		 * @param contactIdListnew
		 * @param contactNumbers
		 */
		private void updateXmppContact(HandyContact hc) {
			
				
			HandyContact hcOld = contactRepository.findContactById(hc.getId());
				
			RosterContact rcOld = new RosterContact();
			
			//Debug Methode später entfernen !!!
			if(hc.getCountryCode() == null){
				
				hc.setCountryCode(user.getCountryCode());
			}
			if(hcOld.getCountryCode() == null){
				
				hcOld.setCountryCode(user.getCountryCode());
			}
			
			rcOld.setJid(hcOld.getCountryCode(), hcOld.getNumber());
				
			RosterContact rcNew = new RosterContact();
			rcNew.setJid(hc.getCountryCode(), hc.getNumber());
			rcNew.setUsername(hc.getName());
				
			Log.d(TAG, "Update: jid "+rcNew.getJid());	
			Log.d(TAG, "Update: name "+rcNew.getUsername());
				
			if(!handler.getConnection().isAuthenticated()){
					
				try {
					handler.login(user.getUserId(), user.getPassword());
				} catch (XMPPException e) {
						
					e.printStackTrace();
				}
			}
			rs.updateEntry(rcOld, rcNew, handler.getConnection());
			rosterRepository.updateRosterEntry(rcOld, rcNew);
			
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
	        cursor.close();
	        Log.d(TAG, "FirstId: "+firstId);
	        int secondId = contactRepository.getLastContact().getId();
	        Log.d(TAG, "SecondId: "+secondId);
			
	        if(firstId < secondId){
	        	
	        	this.handyContact = contactRepository.getLastContact();
	        	check = true;
	        }
	             
			return check;
		}
		
		
		/**
		 * Delete COntact on XMPP Server
		 * @param id
		 */
		private void deleteXmppContact(String countryCode, String number) {
			
			//Debug
			Log.d(TAG, "Kontakt Deletet wird aufgerufen");
			
			RosterContact rc = new RosterContact();
			rc.setJid(countryCode, number);
			
			Log.d(TAG, "Delete: jid "+rc.getJid());
		
			rs.removeEntry(rc, handler.getConnection());
			rosterRepository.deleteRosterEntry(rc);
			
		}

    	
    }
    
    
}
