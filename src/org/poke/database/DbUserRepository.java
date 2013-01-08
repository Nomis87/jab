package org.poke.database;

import org.poke.object.User;
import org.poke.util.ApplicationConstants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DbUserRepository extends DbRepository{
	
	private final String TAG = "DbUserRepository";
	
	public DbUserRepository(Context context) {
		super(context);
		
		// Erstellen der Datenbank Tabelle für den User
		db = context.openOrCreateDatabase(ApplicationConstants.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS " 
				+ ApplicationConstants.DB_TABLE_USER
				+ " (us_id VARCHAR PRIMARY KEY, us_username VARCHAR, us_password VARCHAR, us_countryCode VARCHAR);");
		db.close();
	}

	
	


	public boolean create(String userId, String username, String password, String countryCode) {
		
		db = context.openOrCreateDatabase(ApplicationConstants.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		boolean created = false;
		
		//Einfügen der Informationen
		ContentValues userValues = new ContentValues();
		userValues.put("us_id", userId);
		userValues.put("us_username", username);
		userValues.put("us_password", password);
		userValues.put("us_countryCode", countryCode);
		db.insert(ApplicationConstants.DB_TABLE_USER, null, userValues);
		
		// Testen ob die datenbak erfolgreich erstellt wurde		
		Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_USER, null);
		c.moveToFirst();
		String testUserid = c.getString(c.getColumnIndex("us_id"));
		String testUsername = c.getString(c.getColumnIndex("us_username"));
		String testPassword = c.getString(c.getColumnIndex("us_password"));
		
		Log.d(TAG, testUsername);
		if(testUserid.equals(userId)&&testUsername.equals(username)&&testPassword.equals(password)){
					
			created = true;
		}
				
			
		//Schließen des Cursors und der Datenbank
		c.close();
		db.close();
		
		return created;
	}

	

	public User read() {
		
		// Testen ob die datenbak erfolgreich erstellt wurde		
		Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_USER, null);
		c.moveToFirst();
		String userid = c.getString(c.getColumnIndex("us_id"));
		String username = c.getString(c.getColumnIndex("us_username"));
		String password = c.getString(c.getColumnIndex("us_password"));
		String countryCode = c.getString(c.getColumnIndex("us_countryCode"));
		
		User user = new User(userid, username, password, countryCode);
		
		return user;
	}



	public void update() {
		
		
	}



	public void delete() {
		
		
	}
	
	
}
