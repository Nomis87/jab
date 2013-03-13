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

public class DbRosterGroupRepository extends DbRepository {

	public DbRosterGroupRepository(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void createGroup(RosterContactGroup rcg){
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("rg_id", rcg.getId());
		values.put("rg_name", rcg.getGroupName());
		values.put("rg_counter", rcg.getGroupContacts().size());
		
		db.insert(ApplicationConstants.DB_TABLE_ROSTER_GROUP, null, values);
		
		db.close();
		DBUtil.closeDatabaseOpenHelper();
		
		DbRosterGroupHelperRepository groupHelperRepo = new DbRosterGroupHelperRepository(context);
		
		for(RosterContact rc : rcg.getGroupContacts()){
			
			groupHelperRepo.setUserToGroup(rc.getJid(), rcg.getId());
		}
		
		
	}
	
	public List<RosterContactGroup> getAllGroups(){
		
		DbRosterGroupHelperRepository groupHelperRepo = new DbRosterGroupHelperRepository(context);
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		List<RosterContactGroup> groupList = new ArrayList<RosterContactGroup>();
		
		Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_ROSTER_GROUP, null);
		
		if(c.moveToFirst()){
			
			while(c.moveToNext()){
				
				RosterContactGroup rg = new RosterContactGroup(groupHelperRepo.getAllContactsFromGroup(c.getString(c.getColumnIndex("rg_id")), db));
				rg.setId(c.getString(c.getColumnIndex("rg_id")));
				rg.setGroupName(c.getString(c.getColumnIndex("rg_name")));
				groupList.add(rg);
			}
			
		}
		
		c.close();
		db.close();
		DBUtil.closeDatabaseOpenHelper();
		
		return groupList;
	}
	
	public void deleteGroup(){
		
		
		
	}
	
	

}
