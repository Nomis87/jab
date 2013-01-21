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
		
	}

	
	public boolean createUser(User user) {
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		boolean created = false;
		
		//Einfügen der Informationen
		ContentValues userValues = new ContentValues();
		userValues.put("us_id", user.getUserId());
		userValues.put("us_username", user.getUsername());
		userValues.put("us_password", user.getPassword());
		userValues.put("us_countryCode", user.getCountryCode());
		db.insert(ApplicationConstants.DB_TABLE_USER, null, userValues);
		
		// Testen ob die datenbak erfolgreich erstellt wurde		
		Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_USER, null);
		c.moveToFirst();
		String testUserid = c.getString(c.getColumnIndex("us_id"));
		String testUsername = c.getString(c.getColumnIndex("us_username"));
		String testPassword = c.getString(c.getColumnIndex("us_password"));
		
		Log.d(TAG, testUsername);
		if(testUserid.equals(user.getUserId())&&testUsername.equals(user.getUsername())&&testPassword.equals(user.getPassword())){
					
			created = true;
		}
				
			
		//Schließen des Cursors und der Datenbank
		c.close();
		db.close();
		
		return created;
	}

	

	public User readUser() {
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		// Testen ob die datenbak erfolgreich erstellt wurde		
		Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_USER, null);
		c.moveToFirst();
		String userid = c.getString(c.getColumnIndex("us_id"));
		String username = c.getString(c.getColumnIndex("us_username"));
		String password = c.getString(c.getColumnIndex("us_password"));
		String countryCode = c.getString(c.getColumnIndex("us_countryCode"));
		
		User user = new User(userid, username, password, countryCode);
		
		c.close();
		db.close();
		return user;
	}



	public void updateUser() {
		
		
	}



	public void deleteUser() {
		
		
	}
	
	
}
