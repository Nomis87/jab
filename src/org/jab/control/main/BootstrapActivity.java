package org.jab.control.main;


import org.jab.control.contact.ContactObserver;
import org.jab.control.storage.database.DbTimedMessagesRepository;
import org.jab.control.storage.preference.ApplicationPreference;
import org.jab.control.util.ApplicationContext;
import org.jab.view.activity.IndexActivity;
import org.jab.view.activity.SetupAccountActivity;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
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

		
		// Testen ob das Setup schon durchgeführt wurde
		Intent intent;
		if(afr.getFirstRun()){
			
			Log.d("Run", "This is the first run");
			intent = new Intent(this, SetupAccountActivity.class);
			
		}
		
		else{
//			DbTimedMessagesRepository repo = new DbTimedMessagesRepository();
//			repo.clearDatabase();
			
			Log.d("Run", "This is not the first run");
			intent = new Intent(this, IndexActivity.class);	
			
			//Main Service
			Intent serviceIntent = new Intent(this, MainService.class);
			startService(serviceIntent);
			
			//XMPPConnectionHandler.getInstance().addConnectionListener(getApplicationContext());
			
		}
		
		startActivity(intent);
		finish();
	}
	
	
}
