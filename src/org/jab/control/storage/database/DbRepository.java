package org.jab.control.storage.database;

import android.content.Context;

public abstract class DbRepository {
	
	protected Context context;
	protected DatabaseOpenHelper dbHelper;
	
	public DbRepository(Context context){
		
		this.context = context;
	}
	
		
}
