package org.poke.database;

import java.util.ArrayList;
import java.util.List;

import org.poke.object.RosterContact;
import org.poke.util.ApplicationConstants;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DbRosterRepository extends DbRepository {
	
	
	private final String TAG = "DbRosterRepository";
	
	public DbRosterRepository(Context context) {
		
		super(context);
	}
	
	
	/**
	 * Erstellt einen Eintrag in den Roster und prüft 
	 * anschließend ob dieser korrekt erstellt wurde.
	 * 
	 * @param userJid
	 * @param username
	 * @return
	 */
	public boolean createRosterEntry(RosterContact rc){
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		boolean created = true;
		
		//Roster auf die Datenbank abbilden
		ContentValues userValues = new ContentValues();
		userValues.put("ro_id", rc.getJid());
		userValues.put("ro_username", rc.getUsername());
			
		db.insert(ApplicationConstants.DB_TABLE_ROSTER, null, userValues);
	
		db.close();
	
		return created;
	}
	
	public void findRosterEntry() {
		
		
	}
	
	public void updateRosterEntry(RosterContact rcOld, RosterContact rcNew) {
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		ContentValues contactValues = new ContentValues();
		contactValues.put("ro_id", rcNew.getJid());
		contactValues.put("ro_username", rcNew.getUsername());
		
		db.update(ApplicationConstants.DB_TABLE_ROSTER, contactValues, "ro_id"+" = ?", new String[] {String.valueOf(rcOld)});
	
		db.close();
	}
	
	public void deleteRosterEntry(RosterContact rc) {
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		db.delete(ApplicationConstants.DB_TABLE_ROSTER, "ro_id"+" = ?", new String[] {String.valueOf(rc.getJid())});

		db.close();
	}
	
	public List<RosterContact> getAllRosterEntrys(){
		
		
		List<RosterContact> rosterContactList = new ArrayList<RosterContact>();
    	
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	
    	Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_ROSTER, null);
    	c.moveToFirst();
    	if(c.getCount() > 0){
	    	do{
	    		
	    		RosterContact rc = new RosterContact();
	    		rc.setJid(c.getString(c.getColumnIndex("ro_id")));
	    		rc.setUsername(c.getString(c.getColumnIndex("ro_username")));
	    		rosterContactList.add(rc);
	    		
	    	}
	    	while(c.moveToNext());
    	}
    	c.close();
    	db.close();
    	
    	return rosterContactList;
	}
}
