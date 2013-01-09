package org.poke.main;


import org.poke.contact.ContactObserver;
import org.poke.helper.ApplicationPreference;
import org.poke.index.IndexActivity;
import org.poke.setup.SetupActivity;
import org.poke.util.ApplicationConstants;
import org.poke.util.ApplicationContext;
import org.poke.xmpp.XMPPConnectionHandler;

import android.os.Bundle;
import android.os.Handler;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Diese Activity entscheidet welche Activity zu Beginn getartet werden soll.<br/>
 * Bei einem Ersten Start wird auf SetupActivity weitergeleitet. Sobald das <br/>
 * Setup erfolgreich war wird in mPrefs false gechrieben und bei einem erneuten Start<br/>
 * wird auf IndexActivity weitergeleitet.
 * @author Tobias Simon
 *
 */
public class BootstrapActivity extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ApplicationPreference afr = new ApplicationPreference(this.getApplicationContext());
		ApplicationContext.getInstance().setContext(getApplicationContext());
		
		
			
		//Register ContactObserver
		//afr.setAsync();
		ContactObserver co = new ContactObserver(new Handler(), getBaseContext());
		getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, co);
		
		// Testen ob das Setup schon durchgeführt wurde
		Intent intent;
		if(afr.getFirstRun()){
			
			Log.d("Run", "This is the first run");
			intent = new Intent(this, SetupActivity.class);
			
		}
		
		else{
			
			Log.d("Run", "This is not the first run");
			intent = new Intent(this, IndexActivity.class);	
			
//			//Solange bis die SharedPreference auf true gesetzt wurde. 
//			getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, co);
//			while(!afr.isSync()){
//				
//				getContentResolver().notifyChange(ContactsContract.Contacts.CONTENT_URI ,co);
//			}
		}
		
		startActivity(intent);
		finish();
	}
	
	
}
