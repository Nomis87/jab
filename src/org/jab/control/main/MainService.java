package org.jab.control.main;

import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.protocol.HttpContext;
import org.jab.control.contact.ContactObserver;
import org.jab.control.util.ApplicationContext;
import org.jab.control.xmpp.XMPPService;
import org.jivesoftware.smack.ConnectionListener;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
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
	public static final String RECONNECT_INTENT = "org.jab.control.main.MainService.RECONNECT_INTENT";

	private Intent xmppService;
	private int threadTime;
	private boolean firstRun = true;

	 IBinder mBinder = new LocalBinder();

	 @Override
	 public IBinder onBind(Intent intent) {
	  return mBinder;
	 }

	 public class LocalBinder extends Binder {
		 
		 public MainService getServerInstance() {
			 
			 return MainService.this;
		 }
	 }
	
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		 
		ApplicationContext.getInstance().setContext(getApplicationContext());
		
		Log.d(TAG, "Service wird gestartet");
		xmppService = new Intent(this, XMPPService.class);
		//Register ContactObserver
		//afr.setAsync();	
		ContactObserver co = new ContactObserver(new Handler(), getBaseContext());
		getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, false, co);
	

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		
		startXmppService(1);
		//TODO Umstellen von Mobile auf WLan funktioniert noch nicht eventuell an
		//https://github.com/pfleidi/yaxim/blob/master/src/org/yaxim/androidclient/service/YaximBroadcastReceiver.java
		//orientieren.
		BroadcastReceiver internetReceiver = new BroadcastReceiver() {
			
			
			@Override
			public void onReceive(Context context, Intent intent) {
				
				
				if(!firstRun){
					
					stopXmppService();
					startXmppService(5);
				}
				

				
			}
		};	
		registerReceiver(internetReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		
		BroadcastReceiver reconnectReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				
				
				stopXmppService();
				startXmppService(5);
				
			}
		};
		registerReceiver(reconnectReceiver, new IntentFilter(RECONNECT_INTENT));
		
		return super.onStartCommand(intent, flags, startId);
	}
	


	
	private void startXmppService(int time){
		
		threadTime = time * 1000;
		
		Runnable t = new Runnable() {
			
			public void run() {
				
				try {
					Thread.sleep(threadTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(isInternetOn() && !XMPPService.XMPPServiceStatus){
					
					Log.d(TAG, "Connection TRUE");
					Log.d(TAG, "XmppService wird gestartet");
					//Startet wenn eine Verbindung besteht
					startService(xmppService);
					firstRun = false;
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
	
	public class XMPPConnectionListener implements ConnectionListener {

		public void connectionClosed() {
			
			XMPPService.XMPPServiceStatus = false;
			reconnectingIn(1);
			Log.d(TAG, "Connection CLosed");
		}

		public void connectionClosedOnError(Exception arg0) {
			
			reconnectingIn(5);
			Log.d(TAG, "Connection CLosed on Error");
		}

		public void reconnectingIn(int time) {
			
			stopXmppService();
			startXmppService(time);
			Log.d(TAG, "Reconnect in "+time+" s");
		}

		public void reconnectionFailed(Exception arg0) {
			
			reconnectingIn(5000);
			Log.d(TAG, "Reconnect in failed");
		}

		public void reconnectionSuccessful() {
			
	
		}
	}

}