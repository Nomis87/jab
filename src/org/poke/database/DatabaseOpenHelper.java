package org.poke.database;

import org.poke.util.ApplicationConstants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
	
	private String cotactsTable = "CREATE TABLE IF NOT EXISTS " 
			+ ApplicationConstants.DB_TABLE_CONTACTS
			+ " (hc_id INTEGER PRIMARY KEY, hc_number VARCHAR, hc_name VARCHAR, hc_version INTEGER);";
	
	private String rosterTable = "CREATE TABLE IF NOT EXISTS " 
			+ ApplicationConstants.DB_TABLE_ROSTER
			+ " (ro_id VARCHAR PRIMARY KEY, ro_username VARCHAR);";
	
	private String userTable = "CREATE TABLE IF NOT EXISTS " 
			+ ApplicationConstants.DB_TABLE_USER
			+ " (us_id VARCHAR PRIMARY KEY, us_username VARCHAR, us_password VARCHAR, us_countryCode VARCHAR);";
	
	private String offlineMessagesTable = "CREATE TABLE IF NOT EXISTS " 
			+ ApplicationConstants.DB_TABLE_OFFLINE_MESSAGES
			+ " (om_id INTEGER PRIMARY KEY AUTOINCREMENT, om_receiver VARCHAR, om_sound VARCHAR, om_message VARCHAR);";
	
	private String timedMessagesTable = "CREATE TABLE IF NOT EXISTS " 
			+ ApplicationConstants.DB_TABLE_TIMED_MESSAGES
			+ " (tm_id INTEGER PRIMARY KEY AUTOINCREMENT, tm_receiver VARCHAR, tm_sound VARCHAR, tm_message VARCHAR, tm_timeToSend VARCHAR);";
	
	private String sendedMessagesTable = "CREATE TABLE IF NOT EXISTS " 
			+ ApplicationConstants.DB_TABLE_SENDED_MESSAGES
			+ " (sm_id INTEGER PRIMARY KEY AUTOINCREMENT, sm_receiver VARCHAR, sm_sound VARCHAR, sm_message VARCHAR, sm_sendedTime VARCHAR);";
	
	private String soundTable = "CREATE TABLE IF NOT EXISTS " 
			+ ApplicationConstants.DB_TABLE_MESSAGE_SOUNDS
			+ " (ms_id INTEGER PRIMARY KEY AUTOINCREMENT, ms_name VARCHAR, ms_shortcut VARCHAR, ms_category VARCHAR, ms_activated Integer);";
	
	public DatabaseOpenHelper(Context context) {
		
		super(context, ApplicationConstants.DB_NAME, null, ApplicationConstants.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(userTable);
		db.execSQL(cotactsTable);
		db.execSQL(rosterTable);
		db.execSQL(offlineMessagesTable);
		db.execSQL(timedMessagesTable);
		db.execSQL(sendedMessagesTable);
		db.execSQL(soundTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_CONTACTS);
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_ROSTER);
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_USER);
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_OFFLINE_MESSAGES);
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_TIMED_MESSAGES);
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_SENDED_MESSAGES);
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_MESSAGE_SOUNDS);
		
		onCreate(db);
	}

}
