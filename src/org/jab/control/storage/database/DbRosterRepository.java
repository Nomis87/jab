package org.jab.control.storage.database;

import java.util.ArrayList;
import java.util.List;

import org.jab.control.util.ApplicationConstants;
import org.jab.model.contact.RosterContact;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
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
	 * @param 
	 * @return
	 */
	public boolean createRosterEntry(RosterContact rc){
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
		boolean created = true;
		
		//Roster auf die Datenbank abbilden
		ContentValues userValues = new ContentValues();
		userValues.put("ro_id", rc.getJid());
		userValues.put("ro_username", rc.getUsername());
		
		try{
			db.insert(ApplicationConstants.DB_TABLE_ROSTER, null, userValues);
		}
		catch(SQLiteConstraintException ce){
			
			Log.d(TAG, "user bereits in DB");
		}
	
		db.close();
		
		DBUtil.closeDatabaseOpenHelper();
		
		return created;
	}
	
	
	public RosterContact findRosterEntryById(String testId) {
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		 
        Cursor cursor = db.query(ApplicationConstants.DB_TABLE_ROSTER, new String[] { "ro_id",
                "ro_username"}, "ro_id" + " = ?",
                new String[] { String.valueOf(testId) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        RosterContact rosterContact = new RosterContact();
        rosterContact.setJid(cursor.getString(0));
        rosterContact.setUsername(cursor.getString(1));
        
        db.close();
        DBUtil.closeDatabaseOpenHelper();
        
        // return contact
        return rosterContact;
		
	}
	
	public void updateRosterEntry(RosterContact rcOld, RosterContact rcNew) {
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
		ContentValues contactValues = new ContentValues();
		contactValues.put("ro_id", rcNew.getJid());
		contactValues.put("ro_username", rcNew.getUsername());
		
		db.update(ApplicationConstants.DB_TABLE_ROSTER, contactValues, "ro_id"+" = ?", new String[] {rcOld.getJid()});
	
		db.close();
		
		DBUtil.closeDatabaseOpenHelper();
	}
	
	public void deleteRosterEntry(RosterContact rc) {
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		db.delete(ApplicationConstants.DB_TABLE_ROSTER, "ro_id"+" = ?", new String[] {rc.getJid()});

		db.close();
	}
	
	public boolean containsRosterEntry(RosterContact rc){
		
		boolean check = false;
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
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
    	DBUtil.closeDatabaseOpenHelper();
    	
		return check;
	}
	
	public List<RosterContact> getAllRosterEntrys(){
		
		
		List<RosterContact> rosterContactList = new ArrayList<RosterContact>();
    	
    	SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
    	
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
    	DBUtil.closeDatabaseOpenHelper();
    	
    	return rosterContactList;
	}
}
