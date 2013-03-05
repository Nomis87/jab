package org.jab.control.main;


import org.jab.control.storage.preference.ApplicationPreference;
import org.jab.control.util.ApplicationContext;
import org.jab.view.activity.IndexActivity;
import org.jab.view.activity.SetupAccountActivity;
import org.jab.view.activity.UpdateActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
		
		ApplicationPreference afr = new ApplicationPreference(this);
		ApplicationContext.getInstance().setContext(getApplicationContext());

	
		if(afr.getFirstRun()){
			
			Log.d("Run", "This is the first run");
			Intent activityIntent = new Intent(this, SetupAccountActivity.class);
			startActivity(activityIntent);
			finish();
			
		}
		
		else{
//			DbTimedMessagesRepository repo = new DbTimedMessagesRepository();
//			repo.clearDatabase();
		
			
			if(!afr.isSync()){
				
				Intent activityIntent = new Intent(this, UpdateActivity.class);
				startActivity(activityIntent);
				Intent serviceIntent = new Intent(this, MainService.class);
				startService(serviceIntent);
				
				finish();
				
			}
			else{
				
				Log.d("Run", "This is not the first run");
				Intent activityIntent = new Intent(this, IndexActivity.class);	
				//Main Service in a Thread
				Thread t = new Thread(){
					
					public void run() {
						Intent service = new Intent(getBaseContext(), MainService.class);
						getApplicationContext().startService(service);
					};
				};
				t.start();
				startActivity(activityIntent);
				finish();
				
			}
			
				
		}
		
	}
	
	
}
