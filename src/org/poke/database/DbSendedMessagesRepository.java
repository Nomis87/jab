package org.poke.database;

import java.util.Date;

import org.poke.object.message.OutgoingMessage;
import org.poke.util.ApplicationConstants;

import android.content.ContentValues;
import android.content.Context;
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
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		Date date = new Date();
		
		values.put("sm_receiver", message.getReceiver());
		values.put("sm_sound", message.getSound());
		values.put("sm_message", message.getMessage());
		values.put("sm_sendedTime", date.getTime());
		db.insert(ApplicationConstants.DB_TABLE_SENDED_MESSAGES, null, values);
		
		db.close();
		
	}

}
