package org.jab.control.xmpp;

import java.util.List;

import org.jab.control.message.SendMessageActivity;
import org.jab.control.storage.database.DbOfflineMessagesRepository;
import org.jab.control.storage.database.DbUserRepository;
import org.jab.control.util.ConnectionState;
import org.jab.model.User;
import org.jab.model.message.IncomingMessage;
import org.jab.model.message.OutgoingMessage;
import org.jivesoftware.smack.XMPPException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

/**
 * Service welcher alle XMPP Aktivitäten managed.
 * Beinhaltet einen Thread welcher stetig auf eine Connection zum XMPP Server prueft.
 * @author Tobias
 *
 */
public class XMPPService extends Service {
	
	private final String TAG = "XMPPService";
	
	private static final String RECONNECT_ALARM = "org.yaxim.androidclient.RECONNECT_ALARM";
	private Intent mAlarmIntent = new Intent(RECONNECT_ALARM);
	private PendingIntent mPAlarmIntent;
	private BroadcastReceiver mAlarmReceiver = new ReconnectAlarmReceiver();
	private XMPPConnectionHandler handler;
	private User user;
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();	
		
		handler = XMPPConnectionHandler.getInstance();	
		DbUserRepository userRepository = new DbUserRepository(this);
		user = userRepository.readUser();

		
		mPAlarmIntent = PendingIntent.getBroadcast(this, 0, mAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		registerReceiver(mAlarmReceiver, new IntentFilter(RECONNECT_ALARM));

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.d(TAG, "Service Wird aufgerufen!!!!");
		
		//Alle Message Receiver registrieren
		handler.setMessageReceiver();
		
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, mPAlarmIntent);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		
		// Wenn Service beendet wird, wird auch der Thread beendet!!!
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.cancel(mPAlarmIntent);
		unregisterReceiver(mAlarmReceiver);
		handler.removeMessageReceiver();
		handler.disconect();
		
		super.onDestroy();
	}
	
	private void handleOfflineMessages(){
		
		DbOfflineMessagesRepository offRepo = new DbOfflineMessagesRepository(this);
		
		List<OutgoingMessage> messageList = offRepo.readAllOfflineMessages();
		
		if(messageList.size()>0){
			
			for(OutgoingMessage oMessage : messageList){
				
				if(ConnectionState.getInstance().isConnectionState()){
					
					Intent intent = new Intent(this, SendMessageActivity.class);
					intent.putExtra("receiver", oMessage.getReceiver());
					intent.putExtra("sound", oMessage.getSound());
					intent.putExtra("message", oMessage.getMessage());
					
					startActivity(intent);
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
			
			if (handler.getConnection().isConnected()) {
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
				
					
					//Testen ob verbindung zum server besteht!!
//					if(handler.getConnection().isConnected()){
						Log.d(TAG, "Thread laeuft !!");
						if(!handler.getConnection().isAuthenticated()){
							
							try {
								Log.d(TAG, "Neu Einloggen");
								handler.disconect();
								handler.login(user.getUserId(), user.getPassword());
								handleOfflineMessages();

							} catch (XMPPException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else{
							Log.d(TAG, "Verbindung besteht");
						}
						
//				}
//					else{
//						
//						//No ServerConnection Intent senden TODO BroadcastReceiver welcher diesen Intent verarbeitet.
//						//oder hier direkt verarbeite ....??
//					}
					
				}
				
		};
		
		Thread xmppThread = new Thread(xmppRunnable);
		xmppThread.start();
	}

}
