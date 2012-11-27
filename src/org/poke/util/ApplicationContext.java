package org.poke.util;

import android.content.Context;

public class ApplicationContext {
	
	private static ApplicationContext instance = null;
	
	public static synchronized ApplicationContext getInstance(){
		
		if(instance == null){
			instance = new ApplicationContext();
		}
		
		return instance;
	}
	
	private Context context;

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	
	
}
