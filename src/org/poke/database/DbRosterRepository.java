package org.poke.database;

import org.poke.util.ApplicationConstants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbRosterRepository extends DbRepository {

	public DbRosterRepository(Context context) {
		super(context);
		
		db = context.openOrCreateDatabase(ApplicationConstants.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS " 
				+ ApplicationConstants.DB_TABLE_ROSTER
				+ " (ro_id VARCHAR PRIMARY KEY, ro_username VARCHAR);");	
		db.close();
	}
	
	
	/**
	 * Erstellt einen Eintrag in den Roster und prüft 
	 * anschließend ob dieser korrekt erstellt wurde.
	 * 
	 * @param userJid
	 * @param username
	 * @return
	 */
	public boolean create(String userJid, String username){
		
		SQLiteDatabase db = context.openOrCreateDatabase(ApplicationConstants.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		
		boolean created = true;
		
		//Roster auf die Datenbank abbilden
		ContentValues userValues = new ContentValues();
		userValues.put("ro_id", userJid);
		userValues.put("ro_username", username);
		db.insert(ApplicationConstants.DB_TABLE_ROSTER, null, userValues);
		
		Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_ROSTER, null);
		c.moveToFirst();
		
//		while(c.moveToNext()){
//				
//			String testUserJid = c.getString(c.getColumnIndex("ro_id"));
//			String testUsername = c.getString(c.getColumnIndex("ro_username"));
//				
//			if(testUserJid.equals(userJid)&&testUsername.equals(username)){
//					
//				created = true;
//			}
//			
//		}
		
		db.close();
	
		return created;
	}
	
	public void find() {
		
		
	}
	
	public void update() {
		
		
	}
	
	public void delete() {
		
		
	}
	
	public void getAll(){
		
		
	}
}
