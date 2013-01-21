package org.poke.message.timedSend;

import java.util.Date;

import org.poke.database.DbTimedMessagesRepository;
import org.poke.newPoke.SendPokeTask;
import org.poke.object.message.TimedOutgoingMessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

/**
 * Eine Unterklasse von BroadcastReceiver zum empfangen von gesetzten AlarmTimes.<br/>
 * Sorgt bei Aufruf dafür, das die Nachricht abgeschickt wird und eine neue als <br/>
 * aktuelle Nachricht gesetzt wird.
 * @author Tobias
 *
 */
public class TimedMessageReceiver extends BroadcastReceiver {
	
	private final String TAG = "TimedMessageReceiver";
	public static String TIMED_MESSAGE_INTENT = "org.poke.TIMED_MESSAGE_INTENT"; 
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		
        wl.acquire();
		
		DbTimedMessagesRepository tmRepo = new DbTimedMessagesRepository(context);	
		TimedOutgoingMessage message = tmRepo.findTimedMessageById(TimedAlarmManager.getInstance().getAktMessageId());
		
		SendPokeTask poke = new SendPokeTask(message.getReceiver(), message.getSound(), message.getMessage());
		poke.execute();
		
		Log.d(TAG, "Timed message wird gesendet an: "+message.getReceiver() );
		TimedAlarmManager.getInstance().stopManager();
		//Checken ob gesendet 
		tmRepo.deleteTimedMessage(message);
		TimedAlarmManager.getInstance().setAktMessageId(null);
		TimedAlarmManager.getInstance().setAktTimeToSend(null);
		
		TimedOutgoingMessage nextTimeMessage = tmRepo.getNextTimedMessage();
		
		if(nextTimeMessage != null){
			
			Date date = new Date();
			if(nextTimeMessage.getTimeToSend() > date.getTime()){
				TimedAlarmManager.getInstance().setTimeToSendTimer(context.getApplicationContext(), tmRepo.getNextTimedMessage());
			}
			else{
				Log.d(TAG, "Message Time ist kleiner als Current Time");
				Log.d(TAG, "TimeToSend: "+nextTimeMessage.getTimeToSend());
				Log.d(TAG, "Current: "+date.getTime());
				tmRepo.deleteTimedMessage(nextTimeMessage);
			}
		}
		
		wl.release();
		
	}


}
