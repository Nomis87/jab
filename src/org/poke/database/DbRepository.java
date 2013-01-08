package org.poke.database;

import java.util.Locale;

import org.poke.util.ApplicationConstants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class DbRepository {
	
	protected Context context;
	protected SQLiteDatabase db;
	
	public DbRepository(Context context){
		
		this.context = context;
		init_db();
	}
	
	private void init_db(){
		
		db = context.openOrCreateDatabase(ApplicationConstants.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		db.setVersion(ApplicationConstants.DB_VERSION);
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true);
		db.close();
	}
	
	
}
