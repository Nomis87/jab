package org.jab.control.xmpp;

import java.util.List;

import org.jab.control.main.MainService;
import org.jab.control.main.MainService.LocalBinder;
import org.jab.control.message.SendMessageActivity;
import org.jab.control.storage.database.DbOfflineMessagesRepository;
import org.jab.control.storage.database.DbUserRepository;
import org.jab.model.User;
import org.jab.model.message.OutgoingMessage;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

/**
 * Service welcher alle XMPP Aktivitäten managed.
 * Beinhaltet einen Thread welcher stetig auf eine Connection zum XMPP Server prueft.
 * @author Tobias
 *
 */
public class XMPPService extends Service {
	
	private final String TAG = "XMPPService";
	
	public static boolean XMPPServiceStatus = false;
	private Integer reconnectCounter = 0;
	
	private static final String RECONNECT_ALARM = "org.yaxim.androidclient.RECONNECT_ALARM";
	private Intent mAlarmIntent = new Intent(RECONNECT_ALARM);
	private PendingIntent mPAlarmIntent;
	private BroadcastReceiver mAlarmReceiver = new ReconnectAlarmReceiver();
	private XMPPConnectionHandler handler;
	private User user;
	
	boolean bounded = false;
	private ServiceConnection sConnection;
	private MainService mainService;
	
	private ConnectionListener connectionListener;
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();	
		
		XMPPServiceStatus = true;
		
		handler = XMPPConnectionHandler.getInstance();	
		DbUserRepository userRepository = new DbUserRepository(this);
		user = userRepository.readUser();

		
		mPAlarmIntent = PendingIntent.getBroadcast(this, 0, mAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		registerReceiver(mAlarmReceiver, new IntentFilter(RECONNECT_ALARM));
		
		
		
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.d(TAG, "Service Wird aufgerufen!!!!");
		
		Intent bindIntent = new Intent(this, MainService.class);
		sConnection = new ServiceConnection() {
			
			public void onServiceDisconnected(ComponentName name) {
				
				mainService = null;
				bounded = false;
			}
			
			public void onServiceConnected(ComponentName name, IBinder service) {
				
				LocalBinder mLocalBinder = (LocalBinder)service;
				mainService = mLocalBinder.getServerInstance();
				bounded = true;
			}
		};
		
		bindService(bindIntent, sConnection, BIND_AUTO_CREATE);
		
		if(bounded){
			
			this.connectionListener = mainService.new XMPPConnectionListener();
		}
			
		//Alle Message Receiver registrieren
		handler.setMessageReceiver();
		
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 30000, mPAlarmIntent);
		
		
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	@Override
	public void onDestroy() {
		
		// Wenn Service beendet wird, wird auch der Thread beendet!!!
		Log.d(TAG, "Sevice wird destroyed");
		
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.cancel(mPAlarmIntent);
		unregisterReceiver(mAlarmReceiver);
		handler.disconect();
		handler.removeMessageReceiver();
		handler.reoveConnectionListener(connectionListener);
		
		unbindService(sConnection);
		
		XMPPServiceStatus = false;;
		super.onDestroy();
	}
	
	private void handleOfflineMessages(){
		
		DbOfflineMessagesRepository offRepo = new DbOfflineMessagesRepository(this);
		
		List<OutgoingMessage> messageList = offRepo.readAllOfflineMessages();
		
		if(messageList.size()>0){
			
			for(OutgoingMessage oMessage : messageList){
				
				if(XMPPService.XMPPServiceStatus){
					
					Intent intent = new Intent(this, SendMessageActivity.class);
					intent.putExtra("receiver", oMessage.getReceiver());
					intent.putExtra("sound", oMessage.getSound());
					intent.putExtra("message", oMessage.getMessage());
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
					startActivity(intent);
					offRepo.deleteMessage(oMessage);
				}
				else{
					break;
				}
				
			}
			
		}
	}
	
	private class ReconnectAlarmReceiver extends BroadcastReceiver {
		
		private final String TAG = "ReconnectAlarmReceiver";
		
		public void onReceive(Context ctx, Intent i) {
			
			
			Log.d(TAG, TAG+" wird aufgerufen");
			Log.d(TAG, "isCOnnected: "+handler.getConnection().isConnected());
			Log.d(TAG, "isAUthentikaded: "+handler.getConnection().isAuthenticated());
			Log.d(TAG, "ConnectionId: "+handler.getConnection().getConnectionID());
			if (handler.getConnection().isAuthenticated()) {
				return;
			}
			
			else{
				
				connectWithThread();
			}
		}
	}
	
	private void connectWithThread(){
		
		// Thread zum testen auf eine XMPP verbinfung besteht und ob eigeloggt ist
		Runnable xmppRunnable = new Runnable() {
			
			public void run() {
				
					
				Log.d(TAG, "Thread laeuft !!");
				if(!handler.getConnection().isAuthenticated()){
					
					try {
						
							if(reconnectCounter <=2){
								Log.d(TAG, "Neu Einloggen");
								handler.getConnection().connect();
								handler.setConnectionListener(connectionListener);
								handler.login(user.getUserId(), user.getPassword());
								handleOfflineMessages();
								reconnectCounter++;
							}
							else{
								
								Intent intent = new Intent(MainService.RECONNECT_INTENT);
								sendBroadcast(intent);
							}

					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else{
					Log.d(TAG, "Verbindung besteht");
				}
					
			}
				
		};
		
		Thread xmppThread = new Thread(xmppRunnable);
		xmppThread.start();
	}

}
