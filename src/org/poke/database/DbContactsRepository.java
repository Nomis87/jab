package org.poke.database;

import java.util.ArrayList;
import java.util.List;

import org.poke.object.HandyContact;
import org.poke.util.ApplicationConstants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DbContactsRepository extends DbRepository {
	
	
	private final String TAG = "DbContactRepository";
	
	public DbContactsRepository(Context context) {
		super(context);
		
		// Erstellen der Datenbank Tabelle für den User
		db = context.openOrCreateDatabase(ApplicationConstants.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS " 
				+ ApplicationConstants.DB_TABLE_CONTACTS
				+ " (hc_id INTEGER PRIMARY KEY, hc_number VARCHAR, hc_name VARCHAR, hc_version INTEGER);");
		db.close();
	}
	
	public HandyContact getLastContact(){
		
		db = context.openOrCreateDatabase(ApplicationConstants.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		
		Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_CONTACTS, null);
		c.moveToLast();
		
		int hId = c.getInt(c.getColumnIndex("hc_id"));
		String hNumber = c.getString(c.getColumnIndex("hc_number"));
		String hName = c.getString(c.getColumnIndex("hc_name"));
		int hVersion = c.getInt(c.getColumnIndex("hc_version"));
		
		HandyContact hContact = new HandyContact(hId, hNumber, hName, hVersion);
		
		c.close();
		db.close();
		
		return hContact;
	}
	
	public List<HandyContact> getAllContacts(){
		
		db = context.openOrCreateDatabase(ApplicationConstants.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		
		List<HandyContact> contacts = new ArrayList<HandyContact>();
		
		Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_CONTACTS, null);
		c.moveToFirst();
		
		while(c.moveToNext()){
			
			int hId = c.getInt(c.getColumnIndex("hc_id"));
			String hNumber = c.getString(c.getColumnIndex("hc_number"));
			String hName = c.getString(c.getColumnIndex("hc_name"));
			int hVersion = c.getInt(c.getColumnIndex("hc_version"));
			
			HandyContact hContact = new HandyContact(hId, hNumber, hName, hVersion);
			
			contacts.add(hContact);			
		}
		
		db.close();
		
		return contacts;
 		
	}
	
	public void saveContact(HandyContact hc){
		
		db = context.openOrCreateDatabase(ApplicationConstants.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		
		Log.d(TAG, "ID: "+hc.getId());
		Log.d(TAG, "Number: "+hc.getNumber());
		Log.d(TAG, "Version: "+hc.getVersion());
		Log.d(TAG, "Name: "+hc.getName());
		
		ContentValues contactValue = new ContentValues();
		contactValue.put("hc_id", hc.getId());
		contactValue.put("hc_number", hc.getNumber());
		contactValue.put("hc_name", hc.getName());
		contactValue.put("hc_version", hc.getVersion());
		
		db.insert(ApplicationConstants.DB_TABLE_CONTACTS, null, contactValue);
		
		db.close();
	}
	
	public void updateContact(HandyContact hc){
		
		db = context.openOrCreateDatabase(ApplicationConstants.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		
		ContentValues contactValue = new ContentValues();
		contactValue.put("hc_number", hc.getNumber());
		contactValue.put("hc_name", hc.getName());
		contactValue.put("hc_version", hc.getVersion());
		
		db.update(ApplicationConstants.DB_TABLE_CONTACTS, contactValue, "hc_id"+"="+hc.getId(), null);
		
		db.close();
	}
	
	public HandyContact findContactById(int id){
		
		HandyContact hContact = null;
		List<HandyContact> contactList = getAllContacts();
		
		for(HandyContact contact : contactList){
			
			if(contact.getId() == id){
				
				return contact;
			}
			
		}
		
		return hContact;
	}
	
	public void deleteContact(HandyContact hc){
		
		db = context.openOrCreateDatabase(ApplicationConstants.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		
		db.delete(ApplicationConstants.DB_TABLE_CONTACTS, "hc_id"+"="+hc.getId(), null);
		
		db.close();
		
	}
	
	public void deleteContactById(int id){
		
		db = context.openOrCreateDatabase(ApplicationConstants.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		
		db.delete(ApplicationConstants.DB_TABLE_CONTACTS, "hc_id"+"="+id, null);
		
		db.close();
		
	}

}
