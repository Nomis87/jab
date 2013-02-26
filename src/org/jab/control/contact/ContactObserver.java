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
	
	ApplicationPreference myPreference;
	
	public ContactObserver(Handler handler, Context context) {
		super(handler);

		myPreference = new ApplicationPreference(context);
	}
	
	
	@Override
	public void onChange(boolean selfChange) {
		
		if(!myPreference.getContactObserverPref()){
			
			Log.d(TAG, "somthing changed");
			myPreference.setAsync();
			myPreference.incrementAsyncCounter();
			myPreference.setContactObserverPref(true);
		}
		else{
			myPreference.setContactObserverPref(false);
		}
			
	}
	
	
	
}
