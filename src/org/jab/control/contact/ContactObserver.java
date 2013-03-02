package org.jab.control.contact;

import org.jab.control.storage.preference.ApplicationPreference;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

/**
 * Klasse Zum beobachten von Aenderung an der Kontaktliste.
 * @author Tobias
 *
 */
public class ContactObserver extends ContentObserver {
	
	//Debug Tag
	private final String TAG = "ContactObserver";
	
	private long lastTimeofCall = 0;
	private long lastTimeofUpdate = 0;
	private long threshold_time = 10000;
	
	ApplicationPreference myPreference;
	
	public ContactObserver(Handler handler, Context context) {
		super(handler);

		myPreference = new ApplicationPreference(context);
	}
	
	
	@Override
	public void onChange(boolean selfChange) {
			
		lastTimeofCall = System.currentTimeMillis();	

        if(lastTimeofCall - lastTimeofUpdate > threshold_time){

        	lastTimeofUpdate = System.currentTimeMillis();
        }
        else{
        			
    		Log.d(TAG, "somthing changed");
    		myPreference.setAsync();
 
        }
			
	}
	
	
	
}
