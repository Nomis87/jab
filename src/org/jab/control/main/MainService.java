package org.jab.control.main;

import org.jab.control.contact.ContactObserver;
import org.jab.control.util.ApplicationContext;
import org.jab.control.util.ConnectionState;
import org.jab.control.xmpp.XMPPService;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;


/**
 * Der Mainservice beinhaltet alle wichtigen dinge wie registriuerung von Listener usw.<br/>
 * Er beinhaltet auﬂerdem den InernetBroadcastReceiver welcher je nach InternetZustand<br>
 * denXMPPService startet oder stoppt.
 * @author Tobias
 *
 */
public class MainService extends Service{
	

	private final String TAG = "XMPPConnectionService";
	
	private boolean xmppServiceStarted = false;
	private Intent xmppService;
	private ConnectionState connectionState;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		 
		ApplicationContext.getInstance().setContext(getApplicationContext());
		
		xmppService = new Intent(this, XMPPService.class);
		connectionState = ConnectionState.getInstance();
		//Register ContactObserver
		//afr.setAsync();	
		ContactObserver co = new ContactObserver(new Handler(), getBaseContext());
		getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, co);
	

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		
		//TODO Umstellen von Mobile auf WLan funktioniert noch nicht eventuell an
		//https://github.com/pfleidi/yaxim/blob/master/src/org/yaxim/androidclient/service/YaximBroadcastReceiver.java
		//orientieren.
		BroadcastReceiver internetReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				
				
				
				if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)){
					
					Log.d(TAG, "Connection FALSE");
					if(xmppServiceStarted){
						Log.d(TAG, "XmppService wird gestoppt");
						// Stopt den XmppService wenn keine Verbindung besteht
						stopXmppService();
						xmppServiceStarted = false;
						connectionState.setConnectionState(false);
						
					}
				}
				else{
					
					Log.d(TAG, "Connection TRUE");
					if(!xmppServiceStarted){
						Log.d(TAG, "XmppService wird gestartet");
						//Startet wenn eine Verbindung besteht
						startXmppService();
						xmppServiceStarted = true;
						connectionState.setConnectionState(true);
					
					}
				}
				
			}
		};	
		registerReceiver(internetReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void startXmppService(){
		
		startService(xmppService);
	}
	
	private void stopXmppService(){
		
		stopService(xmppService);
	}
	
	
}