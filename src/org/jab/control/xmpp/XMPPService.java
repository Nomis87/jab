package org.jab.control.xmpp;

import org.jab.control.storage.database.DbUserRepository;
import org.jab.model.User;
import org.jab.model.message.IncomingMessage;
import org.jivesoftware.smack.XMPPException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
	private boolean threadRunning;
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
		this.threadRunning = true; 
		handler = XMPPConnectionHandler.getInstance();
		
		DbUserRepository userRepository = new DbUserRepository(this);
		user = userRepository.readUser();

		
		//Alle Registrierung von packetListener usw.
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.d(TAG, "Service Wird aufgerufen!!!!");
		
		//Alle Message Receiver registrieren
		handler.setMessageReceiver();
		
		// Thread zum testen auf eine XMPP verbinfung besteht und ob eigeloggt ist
		Runnable xmppRunnable = new Runnable() {
			
			public void run() {
				
				while(threadRunning){
					
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
						
//					}
//					else{
//						
//						//No ServerConnection Intent senden TODO BroadcastReceiver welcher diesen Intent verarbeitet.
//						//oder hier direkt verarbeite ....??
//					}
					
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		};
		
		Thread xmppThread = new Thread(xmppRunnable);
		xmppThread.start();
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		
		// Wenn Service beendet wird, wird auch der Thread beendet!!!
		this.threadRunning = false;
		handler.removeMessageReceiver();
		handler.disconect();
		
		super.onDestroy();
	}
	
	private void handleOfflineMessages(){
		
		handler.handleOfflineMessages(this);
	}

}
