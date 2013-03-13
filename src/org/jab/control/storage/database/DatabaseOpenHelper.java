package org.jab.control.storage.database;

import org.jab.control.util.ApplicationConstants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
	
	private String userTable = "CREATE TABLE IF NOT EXISTS " 
			+ ApplicationConstants.DB_TABLE_USER
			+ " (us_id VARCHAR PRIMARY KEY, us_username VARCHAR, us_password VARCHAR, us_countryCode VARCHAR);";
	
	private String cotactsTable = "CREATE TABLE IF NOT EXISTS " 
			+ ApplicationConstants.DB_TABLE_CONTACTS
			+ " (hc_id INTEGER PRIMARY KEY, hc_number VARCHAR, hc_name VARCHAR, hc_countryCode VARCHAR, hc_version INTEGER);";
	
	private String rosterTable = "CREATE TABLE IF NOT EXISTS "
			+ ApplicationConstants.DB_TABLE_ROSTER
			+ " (ro_id VARCHAR PRIMARY KEY, ro_username VARCHAR);";
	
	private String rosterGroupHelperTable = "CREATE TABLE IF NOT EXISTS " 
			+ ApplicationConstants.DB_TABLE_ROSTER_GROUP_HELPER
			+ " (rgh_id INTEGER PRIMARY KEY AUTOINCREMENT, ro_id VARCHAR NOT NULL, rg_id VARCHAR NOT NULL);"
			+ "	FOREIGN KEY (ro_id) REFERENCES "+ApplicationConstants.DB_TABLE_ROSTER+" (ro_id) ON DELETE CASCADE);"
			+ "	FOREIGN KEY (rg_id) REFERENCES "+ApplicationConstants.DB_TABLE_ROSTER_GROUP+" (rg_id) ON DELETE CASCADE);";
	
	private String rosterGroupTable = "CREATE TABLE IF NOT EXISTS " 
			+ ApplicationConstants.DB_TABLE_ROSTER_GROUP
			+ " (rg_id VARCHAR PRIMARY KEY, rg_name VARCHAR NOT NULL, rg_counter INTEGER);";
	
	private String offlineMessagesTable = "CREATE TABLE IF NOT EXISTS " 
			+ ApplicationConstants.DB_TABLE_OFFLINE_MESSAGES
			+ " (om_id INTEGER PRIMARY KEY AUTOINCREMENT, om_receiver VARCHAR, om_sound VARCHAR, om_message VARCHAR);";
	
	private String timedMessagesTable = "CREATE TABLE IF NOT EXISTS " 
			+ ApplicationConstants.DB_TABLE_TIMED_MESSAGES
			+ " (tm_id INTEGER PRIMARY KEY AUTOINCREMENT, tm_receiver VARCHAR, tm_sound VARCHAR, tm_message VARCHAR, tm_timeToSend VARCHAR);";
	
	private String ioMessagesTable = "CREATE TABLE IF NOT EXISTS " 
			+ ApplicationConstants.DB_TABLE_IO_MESSAGES
			+ " (iom_id INTEGER PRIMARY KEY AUTOINCREMENT, iom_receiver VARCHAR, iom_sender VARCHAR, iom_sound VARCHAR, iom_message VARCHAR, iom_time VARCHAR, iom_type INTEGER);";
	
	private String soundPackageTable = "CREATE TABLE IF NOT EXISTS " 
			+ ApplicationConstants.DB_TABLE_SOUND_PACKAGES
			+ " (sp_id INTEGER PRIMARY KEY AUTOINCREMENT,sp_shortcut VARCHAR, sp_name VARCHAR, sp_picture VARCHAR, sp_activated Integer);";
	
	private String soundTable = "CREATE TABLE IF NOT EXISTS " 
			+ ApplicationConstants.DB_TABLE_SOUNDS
			+ " (so_id INTEGER PRIMARY KEY AUTOINCREMENT, so_name VARCHAR, so_shortcut VARCHAR, so_picture VARCHAR, sp_id INTEGER NOT NULL," 
			+ "	FOREIGN KEY (sp_id) REFERENCES "+ApplicationConstants.DB_TABLE_SOUND_PACKAGES+" (sp_id));";
	
	public DatabaseOpenHelper(Context context) {
		
		super(context, ApplicationConstants.DB_NAME, null, ApplicationConstants.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(userTable);
		db.execSQL(cotactsTable);
		db.execSQL(rosterTable);
		db.execSQL(rosterGroupHelperTable);
		db.execSQL(rosterGroupTable);
		db.execSQL(offlineMessagesTable);
		db.execSQL(timedMessagesTable);
		db.execSQL(ioMessagesTable);
		db.execSQL(soundPackageTable);
		db.execSQL(soundTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_CONTACTS);
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_ROSTER);
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_USER);
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_OFFLINE_MESSAGES);
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_TIMED_MESSAGES);
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_IO_MESSAGES);
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_SOUNDS);
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_SOUND_PACKAGES);
		
		onCreate(db);
	}

}
