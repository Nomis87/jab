package org.jab.control.message.timedSend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * Eine Unterklasse von BroadcastReceiver zum empfangen von gesetzten AlarmTimes.<br/>
 * Sorgt bei Aufruf dafür, das die Nachricht abgeschickt wird und eine neue als <br/>
 * aktuelle Nachricht gesetzt wird.
 * @author Tobias
 *
 */
public class TimedMessageReceiver extends BroadcastReceiver {
	
	private final String TAG = "TimedMessageReceiver";
	public static String TIMED_MESSAGE_INTENT = "org.jab.TIMED_MESSAGE_INTENT"; 
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		
        wl.acquire();
        
        Intent sendIntent = new Intent(context, SendTimedMessageActivity.class);
		sendIntent.putExtra("messageId", intent.getIntExtra("messageId", -1));
		sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(sendIntent);
		
		wl.release();
		
	}


}
