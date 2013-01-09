package org.poke.database;

import android.content.Context;

public abstract class DbRepository {
	
	protected Context context;
	protected DatabaseOpenHelper dbHelper;
	
	public DbRepository(Context context){
		
		this.context = context;
		init_db();
	}
	
	private void init_db(){
		
		dbHelper = new DatabaseOpenHelper(context);	
	}
		
}
