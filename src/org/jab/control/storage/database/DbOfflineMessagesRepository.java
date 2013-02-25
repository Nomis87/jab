package org.jab.control.storage.database;

import java.util.ArrayList;
import java.util.List;

import org.jab.control.util.ApplicationConstants;
import org.jab.control.util.ApplicationContext;
import org.jab.model.message.OutgoingMessage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Klasse welche Datenbankabfragen (CRUD + Sonstige) für OfflineMessages bereitstellt
 * 
 * @author Tobias
 *
 */
public class DbOfflineMessagesRepository extends DbRepository {
	
	
	
	public DbOfflineMessagesRepository(Context context) {
		
		super(context);
	}
	
	/**
	 * Methode zum erzeugen eines neuen Datensatzes
	 * 
	 * @param message
	 */
	public void createMessage(OutgoingMessage message){
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		//om_receiver VARCHAR, om_sound VARCHAR, om_message VARCHAR
		ContentValues values = new ContentValues();
		values.put("om_receiver", message.getReceiver());
		values.put("om_sound", message.getSound());
		values.put("om_message", message.getMessage());
		
		db.insert(ApplicationConstants.DB_TABLE_OFFLINE_MESSAGES, null, values);	
		db.close();
		
		DBUtil.closeDatabaseOpenHelper();
	}
	
	/**
	 * Methode zum Lesen eines Datensatzes
	 * 
	 * @param message
	 * @return
	 */
	public OutgoingMessage readMessage(OutgoingMessage message){
		
		return null;
	}
	
	
	/**
	 * Methode welche alle Datensätze zurück liefert.
	 * 
	 * @return
	 */
	public List<OutgoingMessage> readAllOfflineMessages(){
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
		List<OutgoingMessage> messageList = new ArrayList<OutgoingMessage>();
    	
    	
    	Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_OFFLINE_MESSAGES, null);
    	
    	if(c.moveToFirst()){
	    	do{
	    		
	    		Integer id = c.getInt(c.getColumnIndex("om_id"));
	    		String receiver = c.getString(c.getColumnIndex("om_receiver"));
	    		String sound = c.getString(c.getColumnIndex("om_sound"));
	    		String message = c.getString(c.getColumnIndex("om_message"));
	    		OutgoingMessage oMessage = new OutgoingMessage(receiver, sound, message);
	    		oMessage.setId(id);
	    		messageList.add(oMessage);
	    	}
	    	while(c.moveToNext());
    	}
    	c.close();
    	db.close();
    	
    	DBUtil.closeDatabaseOpenHelper();
    	
    	
		return messageList;
	}
	
	
	public void updateMessage(OutgoingMessage oldOm, OutgoingMessage newOm){
		
		
	}
	
	/**
	 * Methode zum löschen eines Datensatzes
	 * @param message
	 */
	public void deleteMessage(OutgoingMessage message){
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
		db.delete(ApplicationConstants.DB_TABLE_OFFLINE_MESSAGES, "om_id=?", new String[]{String.valueOf(message.getId())});
		
		db.close();
		
		DBUtil.closeDatabaseOpenHelper();
	}

}
