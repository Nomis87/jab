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
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;


/**
 * Der Mainservice beinhaltet alle wichtigen dinge wie registriuerung von Listener usw.<br/>
 * Er beinhaltet außerdem den InernetBroadcastReceiver welcher je nach InternetZustand<br>
 * denXMPPService startet oder stoppt.
 * @author Tobias
 *
 */
public class MainService extends Service{
	

	private final String TAG = "MainService";

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
		getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, false, co);
	

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		
		//TODO Umstellen von Mobile auf WLan funktioniert noch nicht eventuell an
		//https://github.com/pfleidi/yaxim/blob/master/src/org/yaxim/androidclient/service/YaximBroadcastReceiver.java
		//orientieren.
		BroadcastReceiver internetReceiver = new BroadcastReceiver() {
			
			
			@Override
			public void onReceive(Context context, Intent intent) {
				
				
				stopXmppService();
				startXmppService();

				
			}
		};	
		registerReceiver(internetReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void startXmppService(){
		
		Runnable t = new Runnable() {
			
			public void run() {
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(isInternetOn() && !XMPPService.XMPPServiceStatus){
					
					Log.d(TAG, "Connection TRUE");
					Log.d(TAG, "XmppService wird gestartet");
					//Startet wenn eine Verbindung besteht
					startService(xmppService);
					connectionState.setConnectionState(true);
				}
			}
		};
		Thread waitThread = new Thread(t);
		waitThread.start();
		
	}
	
	private void stopXmppService(){
		
		stopService(xmppService);
	}
	
	public final boolean isInternetOn() {
		 
		 ConnectivityManager connec =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		 // ARE WE CONNECTED TO THE NET
		 if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
				 connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
				 connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
				 connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED ) {
		 // MESSAGE TO SCREEN FOR TESTING (IF REQ)
		 //Toast.makeText(this, connectionType + ” connected”, Toast.LENGTH_SHORT).show();
		 return true;
		 
		 } else if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||  connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED  ) {
		 //System.out.println(“Not Connected”);
		 return false;
		 }
		 
		 return false;
	 }
}