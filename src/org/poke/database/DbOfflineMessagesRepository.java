package org.poke.database;

import java.util.ArrayList;
import java.util.List;

import org.poke.object.message.OutgoingMessage;
import org.poke.util.ApplicationConstants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbOfflineMessagesRepository extends DbRepository {

	public DbOfflineMessagesRepository(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public void createMessage(OutgoingMessage message){
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		//om_receiver VARCHAR, om_sound VARCHAR, om_message VARCHAR
		ContentValues values = new ContentValues();
		values.put("om_receiver", message.getReceiver());
		values.put("om_sound", message.getSound());
		values.put("om_message", message.getMessage());
		
		db.insert(ApplicationConstants.DB_TABLE_OFFLINE_MESSAGES, null, values);	
		db.close();
		
	}
	
	public List<OutgoingMessage> getAllOfflineMessages(){
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
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
    	
    	
		return messageList;
	}
	
	public void deleteOfflineMessage(OutgoingMessage message){
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		db.delete(ApplicationConstants.DB_TABLE_OFFLINE_MESSAGES, "om_id=?", new String[]{String.valueOf(message.getId())});
		
		db.close();
	}

}
