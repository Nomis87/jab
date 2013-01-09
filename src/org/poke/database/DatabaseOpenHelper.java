package org.poke.database;

import org.poke.util.ApplicationConstants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
	
	String cotactsTable = "CREATE TABLE IF NOT EXISTS " 
			+ ApplicationConstants.DB_TABLE_CONTACTS
			+ " (hc_id INTEGER PRIMARY KEY, hc_number VARCHAR, hc_name VARCHAR, hc_version INTEGER);";
	
	String rosterTable = "CREATE TABLE IF NOT EXISTS " 
			+ ApplicationConstants.DB_TABLE_ROSTER
			+ " (ro_id VARCHAR PRIMARY KEY, ro_username VARCHAR);";
	
	String userTable = "CREATE TABLE IF NOT EXISTS " 
			+ ApplicationConstants.DB_TABLE_USER
			+ " (us_id VARCHAR PRIMARY KEY, us_username VARCHAR, us_password VARCHAR, us_countryCode VARCHAR);";
	
	
	
	public DatabaseOpenHelper(Context context) {
		
		super(context, ApplicationConstants.DB_NAME, null, ApplicationConstants.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(userTable);
		db.execSQL(cotactsTable);
		db.execSQL(rosterTable);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_CONTACTS);
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_ROSTER);
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_USER);
		
		onCreate(db);
	}

}
