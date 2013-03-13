package org.jab.control.storage.database;

import org.jab.control.util.ApplicationConstants;
import org.jab.model.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

/**
 * Repository 
 * @author Tobias
 *
 */
public class DbUserRepository extends DbRepository{
	
	//private final String TAG = "DbUserRepository";
	
	
	public DbUserRepository(Context context) {
		super(context);
		
	}

	
	public boolean createUser(User user) {
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
		//Einfügen der Informationen
		ContentValues userValues = new ContentValues();
		userValues.put("us_id", user.getUserId());
		userValues.put("us_password", user.getPassword());
		userValues.put("us_countryCode", user.getCountryCode());
		
		try{
			
			db.insert(ApplicationConstants.DB_TABLE_USER, null, userValues);
			db.close();
			DBUtil.closeDatabaseOpenHelper();
			return true;
			
		}
		catch(SQLiteException se){
			
			db.close();
			DBUtil.closeDatabaseOpenHelper();
			return false;
		}
		
	}

	

	public User readUser() {
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
		// Testen ob die datenbak erfolgreich erstellt wurde		
		Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_USER, null);
		c.moveToFirst();
		String userid = c.getString(c.getColumnIndex("us_id"));
		String password = c.getString(c.getColumnIndex("us_password"));
		String countryCode = c.getString(c.getColumnIndex("us_countryCode"));
		
		User user = new User(userid, password, countryCode);
		
		c.close();
		db.close();
		DBUtil.closeDatabaseOpenHelper();
		return user;
	}



	public void updateUser() {
		
		
	}



	public void deleteUser() {
		
		
	}
	
	public void resetDatabase(){
		
		String userTable = "CREATE TABLE IF NOT EXISTS " 
				+ ApplicationConstants.DB_TABLE_USER
				+ " (us_id VARCHAR PRIMARY KEY, us_username VARCHAR, us_password VARCHAR, us_countryCode VARCHAR);";
		
		SQLiteDatabase db = DBUtil.getDatabaseOpenHelper(context).getWritableDatabase();
		
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_USER);
		db.execSQL(userTable);
		
		db.close();
		DBUtil.closeDatabaseOpenHelper();
	}
	
	
}
