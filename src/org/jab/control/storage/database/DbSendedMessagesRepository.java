package org.jab.control.storage.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jab.control.util.ApplicationConstants;
import org.jab.model.message.OutgoingMessage;
import org.jab.model.message.TimedOutgoingMessage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Repository zum speichern, finden, aktualisieren und Löschen von Gesendeten 
 * Nachrichten in der SQLite Datenbank.
 * @author Tobias
 *
 */
public class DbSendedMessagesRepository extends DbRepository {

	public DbSendedMessagesRepository(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void createSendedMessage(OutgoingMessage message){
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		ContentValues values = new ContentValues();
		
		Date date = new Date();
		
		values.put("sm_receiver", message.getReceiver());
		values.put("sm_sound", message.getSound());
		values.put("sm_message", message.getMessage());
		values.put("sm_sendedTime", date.getTime());
		db.insert(ApplicationConstants.DB_TABLE_SENDED_MESSAGES, null, values);
		
		db.close();
		DBUtil.closeDatabaseOpenHelper();
		
	}
	
	public List<OutgoingMessage> getAllSendedMessages(){
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
		List<OutgoingMessage> messageList = new ArrayList<OutgoingMessage>();
		
		Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_SENDED_MESSAGES, null);
		
		if(c.moveToFirst()){
	    	do{
	    		
	    		Integer id = c.getInt(c.getColumnIndex("sm_id"));
	    		String receiver = c.getString(c.getColumnIndex("sm_receiver"));
	    		String sound = c.getString(c.getColumnIndex("sm_sound"));
	    		String message = c.getString(c.getColumnIndex("sm_message"));
	    		Long timeToSend = c.getLong(c.getColumnIndex("sm_timeToSend"));
	    		
	    		if(timeToSend == null){
	    			
	    			OutgoingMessage oMessage = new OutgoingMessage(receiver, sound, message);
	    			oMessage.setId(id);
		    		messageList.add(oMessage);
	    		}
	    		else{
	    			
	    			TimedOutgoingMessage oMessage = new TimedOutgoingMessage(receiver, sound, message, timeToSend);
	    			oMessage.setId(id);
		    		messageList.add(oMessage);
	    		}
	    	}
	    	while(c.moveToNext());
    	}
		
		db.close();
		DBUtil.closeDatabaseOpenHelper();
		
		return messageList;
	}
	
	public void deletSendedMessage(OutgoingMessage message){
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
		db.delete(ApplicationConstants.DB_TABLE_SENDED_MESSAGES, "sm_id=?", new String[]{message.getId().toString()});
		
		db.close();
		
		DBUtil.closeDatabaseOpenHelper();
	}

}
