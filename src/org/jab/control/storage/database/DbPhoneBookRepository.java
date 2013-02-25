package org.jab.control.storage.database;

import java.util.ArrayList;
import java.util.List;

import org.jab.control.util.ApplicationConstants;
import org.jab.model.contact.HandyContact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbPhoneBookRepository extends DbRepository {
	
	
	private final String TAG = "DbContactRepository";
	
	public DbPhoneBookRepository(Context context) {
		super(context);
	
	}
	
	public HandyContact getLastContact(){
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
		Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_CONTACTS, null);
		c.moveToLast();
		
		int hId = c.getInt(c.getColumnIndex("hc_id"));
		String hNumber = c.getString(c.getColumnIndex("hc_number"));
		String hName = c.getString(c.getColumnIndex("hc_name"));
		String hCountryCode = c.getString(c.getColumnIndex("hc_countryCode"));
		int hVersion = c.getInt(c.getColumnIndex("hc_version"));
		
		HandyContact hContact = new HandyContact(hId, hNumber, hName, hCountryCode, hVersion);
		
		c.close();
		db.close();
		DBUtil.closeDatabaseOpenHelper();
		
		return hContact;
	}
	
	public List<HandyContact> getAllContacts(){
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
		List<HandyContact> contacts = new ArrayList<HandyContact>();
		
		Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_CONTACTS, null);
		
		if(c.moveToFirst()){
			
			do{
				int hId = c.getInt(c.getColumnIndex("hc_id"));
				String hNumber = c.getString(c.getColumnIndex("hc_number"));
				String hName = c.getString(c.getColumnIndex("hc_name"));
				String hCountryCode = c.getString(c.getColumnIndex("hc_countryCode"));
				int hVersion = c.getInt(c.getColumnIndex("hc_version"));
				
				HandyContact hContact = new HandyContact(hId, hNumber, hName, hCountryCode, hVersion);
				
				contacts.add(hContact);	
			}		
			while(c.moveToNext());
			
		}
		
		c.close();
		db.close();
		DBUtil.closeDatabaseOpenHelper();
		
		return contacts;
 		
	}
	
	public void saveContact(HandyContact hc){
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
		ContentValues contactValue = new ContentValues();
		contactValue.put("hc_id", hc.getId());
		contactValue.put("hc_number", hc.getNumber());
		contactValue.put("hc_name", hc.getName());
		contactValue.put("hc_CountryCode", hc.getCountryCode());
		contactValue.put("hc_version", hc.getVersion());
		
		db.insert(ApplicationConstants.DB_TABLE_CONTACTS, null, contactValue);
		
		db.close();
	}
	
	public void updateContact(HandyContact hc){
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
		ContentValues contactValue = new ContentValues();
		contactValue.put("hc_number", hc.getNumber());
		contactValue.put("hc_name", hc.getName());
		contactValue.put("hc_CountryCode", hc.getCountryCode());
		contactValue.put("hc_version", hc.getVersion());
		
		db.update(ApplicationConstants.DB_TABLE_CONTACTS, contactValue, "hc_id"+"="+hc.getId(), null);
		
		db.close();
		DBUtil.closeDatabaseOpenHelper();
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
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
		db.delete(ApplicationConstants.DB_TABLE_CONTACTS, "hc_id"+"="+hc.getId(), null);
		
		db.close();
		DBUtil.closeDatabaseOpenHelper();
		
	}
	
	public void deleteContactById(int id){
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();;
		
		db.delete(ApplicationConstants.DB_TABLE_CONTACTS, "hc_id"+"="+id, null);
		
		db.close();
		DBUtil.closeDatabaseOpenHelper();
		
	}
	

}
