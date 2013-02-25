package org.jab.control.util;

import android.content.Context;

public class ApplicationContext {
	
	private static ApplicationContext instance = null;
	private Context context;
	
	public static synchronized ApplicationContext getInstance(){
		
		if(instance == null){
			instance = new ApplicationContext();
		}
		
		return instance;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	
	
}
