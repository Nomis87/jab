package org.jab.control.storage.database;

import java.util.ArrayList;
import java.util.List;

import org.jab.control.util.ApplicationConstants;
import org.jab.model.contact.RosterContact;
import org.jab.model.contact.RosterContactGroup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbRosterGroupHelperRepository extends DbRepository {

	public DbRosterGroupHelperRepository(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void setUserToGroup(String rcId, String rcgId){
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("ro_id", rcId);
		values.put("rg_id", rcgId);
		
		db.insert(ApplicationConstants.DB_TABLE_ROSTER_GROUP_HELPER, null, values);
		db.close();
		DBUtil.closeDatabaseOpenHelper();
		
	}
	
	public void deleteUserFromGroup(String rcId, String rcgId){
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
		db.delete(ApplicationConstants.DB_TABLE_ROSTER_GROUP_HELPER, "ro_id=? AND rg_id=?", new String[]{rcId, rcgId});
		
		db.close();
		DBUtil.closeDatabaseOpenHelper();
	}
	
	public List<RosterContact> getAllContactsFromGroup(String groupId, SQLiteDatabase database){
		
		List<RosterContact> contactsList = null;
		DbRosterRepository rosterRepo = new DbRosterRepository(context);
		
		SQLiteDatabase db = database;
		Cursor c = db.rawQuery("SELECT ro_id FROM "+ApplicationConstants.DB_TABLE_ROSTER_GROUP_HELPER+
				" WHERE rg_id="+groupId, null);
		
		if(c.moveToFirst()){
			
			contactsList = new ArrayList<RosterContact>();
			
			while(c.moveToNext()){
				
				contactsList.add(rosterRepo.findRosterEntryById(c.getString(c.getColumnIndex("ro_id"))));
			}
			
		}
		
		c.close();
		
		return contactsList;
	}
	
	
	
}
