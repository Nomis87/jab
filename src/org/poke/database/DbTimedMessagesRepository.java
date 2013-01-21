package org.poke.database;

import java.util.ArrayList;
import java.util.List;

import org.poke.object.message.TimedOutgoingMessage;
import org.poke.util.ApplicationConstants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Repository für die TimedMessages
 * Enthält Funktionen für den Zugriff auf die Datenbank.
 * @author Tobias
 *
 */
public class DbTimedMessagesRepository extends DbRepository {
	
	private final String TAG = "DbTimedMessagesRepository";
	
	
	public DbTimedMessagesRepository(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public int createTimedMessage(TimedOutgoingMessage message){
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		//(tm_id INTEGER PRIMARY KEY AUTOINCREMENT, tm_receiver VARCHAR, tm_sound VARCHAR, tm_message VARCHAR, tm_timeToSend INTEGER);"
		ContentValues values = new ContentValues();
		values.put("tm_receiver", message.getReceiver());
		values.put("tm_sound", message.getSound());
		values.put("tm_message", message.getMessage());
		values.put("tm_timeToSend", message.getTimeToSend());
		
		String stringId = String.valueOf(db.insert(ApplicationConstants.DB_TABLE_TIMED_MESSAGES, null, values));
		db.close();
		
		return Integer.valueOf(stringId);
	}
	
	public List<TimedOutgoingMessage> getAllTimedMessages(){
			
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<TimedOutgoingMessage> messageList = new ArrayList<TimedOutgoingMessage>();
    	  	
    	Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_TIMED_MESSAGES, null);
    	
    	if(c.moveToFirst()){
	    	do{
	    		
	    		Integer id = c.getInt(c.getColumnIndex("tm_id"));
	    		String receiver = c.getString(c.getColumnIndex("tm_receiver"));
	    		String sound = c.getString(c.getColumnIndex("tm_sound"));
	    		String message = c.getString(c.getColumnIndex("tm_message"));
	    		Long timeToSend = c.getLong(c.getColumnIndex("tm_timeToSend"));
	    		TimedOutgoingMessage oMessage = new TimedOutgoingMessage(receiver, sound, message, timeToSend);
	    		oMessage.setId(id);
	    		messageList.add(oMessage);
	    	}
	    	while(c.moveToNext());
    	}
    	c.close();
    	db.close();
    	
    	return messageList;
		
	}
	
	public TimedOutgoingMessage findTimedMessageById(int id){
		
		TimedOutgoingMessage oMessage = null;
		
		List<TimedOutgoingMessage> messageList = getAllTimedMessages();
		
		for(TimedOutgoingMessage message : messageList){
			
			if(message.getId() == id){
				
				return message;
			}
			
		}
		
		return oMessage;
	}
	
	public TimedOutgoingMessage getNextTimedMessage(){
		
		TimedOutgoingMessage returnMessage = null;
		
		List<TimedOutgoingMessage> messageList = getAllTimedMessages();
		
		for(TimedOutgoingMessage message : messageList){
			
			if(returnMessage == null){
				
				returnMessage = message;
			}
			else if(message.getTimeToSend()<returnMessage.getTimeToSend()){
				
				returnMessage = message;
			}
				
		}
		
		return returnMessage;
		
	}
	
	public void deleteTimedMessage(TimedOutgoingMessage message){
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		db.delete(ApplicationConstants.DB_TABLE_TIMED_MESSAGES, "tm_id=?", new String[]{String.valueOf(message.getId())});
		
		for(TimedOutgoingMessage m : getAllTimedMessages()){
			
			Log.d(TAG, "ID: "+m.getId());
			Log.d(TAG, "TTS: "+m.getTimeToSend());
		}
		
		db.close();
	}
	
	public void clearDatabase(){
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		String timedMessagesTable = "CREATE TABLE IF NOT EXISTS " 
				+ ApplicationConstants.DB_TABLE_TIMED_MESSAGES
				+ " (tm_id INTEGER PRIMARY KEY AUTOINCREMENT, tm_receiver VARCHAR, tm_sound VARCHAR, tm_message VARCHAR, tm_timeToSend VARCHAR);";
		
		//db.delete(ApplicationConstants.DB_TABLE_TIMED_MESSAGES, null, null);
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_TIMED_MESSAGES);
		db.execSQL(timedMessagesTable);
		
		db.close();
	}

}
