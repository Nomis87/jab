package org.jab.control.storage.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jab.control.util.ApplicationConstants;
import org.jab.model.message.AbstractMessage;
import org.jab.model.message.IncomingMessage;
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
public class DbIOMessagesRepository extends DbRepository {
	
	public static final int TYPE_OUT = 0;
	public static final int TYPE_IN  = 1;
	
	public DbIOMessagesRepository(Context context) {
		super(context);
	}
	
	public void createMessage(AbstractMessage message){
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		ContentValues values = new ContentValues();
		
		Date date = new Date();
		
		if(message instanceof OutgoingMessage){
		
			OutgoingMessage om = (OutgoingMessage) message;
			
			values.put("iom_receiver", om.getReceiver());
			values.put("iom_sound", om.getSound());
			values.put("iom_message", om.getMessage());
			values.put("iom_time", date.getTime());
			values.put("iom_type", TYPE_OUT);
		}
		
		if(message instanceof IncomingMessage){
			
			IncomingMessage im = (IncomingMessage) message;
			
			values.put("iom_sender", im.getSender());
			values.put("iom_sound", im.getSound());
			values.put("iom_message", im.getMessage());
			values.put("iom_time", date.getTime());
			values.put("iom_type", TYPE_IN);
		}
		
		db.insert(ApplicationConstants.DB_TABLE_IO_MESSAGES, null, values);
		
		db.close();
		DBUtil.closeDatabaseOpenHelper();
		
	}
	
	public AbstractMessage findMessageById(Integer id){
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
		AbstractMessage am = null;
		
		Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_IO_MESSAGES+" WHERE iom_id="+id, null);
		
		if(c.moveToFirst()){
			
			Integer type = c.getInt(c.getColumnIndex("iom_type"));
			
			if(type == TYPE_OUT){
    			
    			Integer mId = c.getInt(c.getColumnIndex("iom_id"));
	    		String receiver = c.getString(c.getColumnIndex("iom_receiver"));
	    		String sound = c.getString(c.getColumnIndex("iom_sound"));
	    		String message = c.getString(c.getColumnIndex("iom_message"));
	    		Long time = c.getLong(c.getColumnIndex("iom_time"));
	    		
	    		am = new OutgoingMessage(receiver, sound, message);
	    		am.setId(mId);
	    		am.setTime(time);
    			
    		}
    		else if(type == TYPE_IN){
    			
    			Integer mId = c.getInt(c.getColumnIndex("iom_id"));
	    		String sender = c.getString(c.getColumnIndex("iom_sender"));
	    		String sound = c.getString(c.getColumnIndex("iom_sound"));
	    		String message = c.getString(c.getColumnIndex("iom_message"));
	    		Long time = c.getLong(c.getColumnIndex("iom_time"));
    			
    			am = new IncomingMessage(sender, sound, message);
    			am.setId(mId);
    			am.setTime(time);
    		}
			
		}
		
		c.close();
		db.close();
		DBUtil.closeDatabaseOpenHelper();
		
		return am;
	}
	
	public List<AbstractMessage> getAllMessages(){
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
		List<AbstractMessage> messageList = new ArrayList<AbstractMessage>();
		
		Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_IO_MESSAGES+" ORDER BY iom_time DESC", null);
		
		if(c.moveToFirst()){
	    	do{
	    		
	    		Integer type = c.getInt(c.getColumnIndex("iom_type"));
	    		
	    		if(type == TYPE_OUT){
	    			
	    			Integer id = c.getInt(c.getColumnIndex("iom_id"));
		    		String receiver = c.getString(c.getColumnIndex("iom_receiver"));
		    		String sound = c.getString(c.getColumnIndex("iom_sound"));
		    		String message = c.getString(c.getColumnIndex("iom_message"));
		    		Long time = c.getLong(c.getColumnIndex("iom_time"));
		    		
		    		AbstractMessage om = new OutgoingMessage(receiver, sound, message);
		    		om.setId(id);
		    		om.setTime(time);
		    		messageList.add(om);
	    			
	    		}
	    		else if(type == TYPE_IN){
	    			
	    			Integer id = c.getInt(c.getColumnIndex("iom_id"));
		    		String sender = c.getString(c.getColumnIndex("iom_sender"));
		    		String sound = c.getString(c.getColumnIndex("iom_sound"));
		    		String message = c.getString(c.getColumnIndex("iom_message"));
		    		Long time = c.getLong(c.getColumnIndex("iom_time"));
	    			
	    			AbstractMessage im = new IncomingMessage(sender, sound, message);
	    			im.setId(id);
	    			im.setTime(time);
		    		messageList.add(im);
	    		}
	    	}
	    	while(c.moveToNext());
    	}
		
		c.close();
		db.close();
		DBUtil.closeDatabaseOpenHelper();
		
		return messageList;
	}
	
	public void deleteMessage(AbstractMessage message){
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
		db.delete(ApplicationConstants.DB_TABLE_IO_MESSAGES, "iom_id=?", new String[]{message.getId().toString()});
		
		db.close();
		
		DBUtil.closeDatabaseOpenHelper();
	}
	
	public void deleteMessage(Integer id){
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
		db.delete(ApplicationConstants.DB_TABLE_IO_MESSAGES, "iom_id=?", new String[]{id.toString()});
		
		db.close();
		
		DBUtil.closeDatabaseOpenHelper();
	}

}
