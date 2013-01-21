package org.poke.database;

import java.util.ArrayList;
import java.util.List;

import org.poke.object.contact.RosterContact;
import org.poke.util.ApplicationConstants;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbRosterRepository extends DbRepository {
	
	
	private final String TAG = "DbRosterRepository";
	
	public DbRosterRepository(Context context) {
		
		super(context);
	}
	
	
	/**
	 * Erstellt einen Eintrag in den Roster und prüft 
	 * anschließend ob dieser korrekt erstellt wurde.
	 * 
	 * @param 
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
	
	
	public RosterContact findRosterEntryById(String testId) {
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		 
        Cursor cursor = db.query(ApplicationConstants.DB_TABLE_ROSTER, new String[] { "ro_id",
                "ro_username"}, "ro_id" + " = ?",
                new String[] { String.valueOf(testId) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        RosterContact rosterContact = new RosterContact();
        rosterContact.setJid(cursor.getString(0));
        rosterContact.setUsername(cursor.getString(1));
        
        // return contact
        return rosterContact;
		
	}
	
	public void updateRosterEntry(RosterContact rcOld, RosterContact rcNew) {
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		ContentValues contactValues = new ContentValues();
		contactValues.put("ro_id", rcNew.getJid());
		contactValues.put("ro_username", rcNew.getUsername());
		
		db.update(ApplicationConstants.DB_TABLE_ROSTER, contactValues, "ro_id"+" = ?", new String[] {rcOld.getJid()});
	
		db.close();
	}
	
	public void deleteRosterEntry(RosterContact rc) {
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		db.delete(ApplicationConstants.DB_TABLE_ROSTER, "ro_id"+" = ?", new String[] {rc.getJid()});

		db.close();
	}
	
	public boolean containsRosterEntry(RosterContact rc){
		
		boolean check = false;
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_ROSTER, null);
    	
    	if(c.moveToFirst()){
	    	do{
	    		
	    		if(c.getString(c.getColumnIndex("ro_id")).equals(rc.getJid())){
	    			
	    			check = true;
	    		}
	    	}
	    	while(c.moveToNext());
    	}
		
    	db.close();
		return check;
	}
	
	public List<RosterContact> getAllRosterEntrys(){
		
		
		List<RosterContact> rosterContactList = new ArrayList<RosterContact>();
    	
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	
    	Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_ROSTER, null);
    	
    	if(c.moveToFirst()){
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
