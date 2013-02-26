package org.jab.control.storage.database;

import android.content.Context;

public class DBUtil {
	
	private DBUtil(){}
	
	private static DatabaseOpenHelper dbHelper = null;
	
	public static synchronized DatabaseOpenHelper getDatabaseOpenHelper(Context context){
		
		if(dbHelper == null){
			
			dbHelper = new DatabaseOpenHelper(context);
		}
		
		return dbHelper;
	}
	
	public static synchronized void closeDatabaseOpenHelper(){
		
		if(dbHelper!= null){
			
			dbHelper.close();
			dbHelper = null;
		}
		
	}

}
