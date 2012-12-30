package org.poke.main;


import org.poke.helper.AppFirstRun;
import org.poke.index.IndexActivity;
import org.poke.setup.SetupActivity;
import org.poke.util.ApplicationContext;
import org.poke.xmpp.XMPPConnectionHandler;

import android.os.Bundle;
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

	SharedPreferences mPrefs;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AppFirstRun afr = new AppFirstRun(this.getApplicationContext());
		ApplicationContext.getInstance().setContext(getApplicationContext());
		
		Intent intent;
		
		if(afr.getFirstRun()){
			
			Log.d("Run", "This is the first run");
			intent = new Intent(this, SetupActivity.class);
		}
		
		else{
			
			Log.d("Run", "This is not the first run");
			intent = new Intent(this, IndexActivity.class);			
		}
		
		startActivity(intent);
		finish();
	}
	
	
}
