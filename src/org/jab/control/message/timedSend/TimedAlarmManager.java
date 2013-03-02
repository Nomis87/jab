package org.jab.control.message.timedSend;

import org.jab.control.storage.database.DbTimedMessagesRepository;
import org.jab.model.message.TimedOutgoingMessage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Singelton Klasse.
 * Beinhaltet den AlarmManager welcher zum setzten des Alarms für die 
 * TimedMessages zuständig ist.
 * @author Tobias
 *
 */
public class TimedAlarmManager {
	
	private final String TAG = "TimedAlarmManager";
	private static TimedAlarmManager instance = null;
	private AlarmManager am;
	private PendingIntent pi;
	
	private TimedAlarmManager(){}
	
	/**
	 * SINGELTON METHODE<br/>
	 * Wenn instance = null wird eine neue Instanz von TimedAlarmManager erzeugt und zurückgeliefert.<br/>
	 * Dies sorgt dafür das es in Der Application nur eine Instanz von diesem Objekt geben kann.
	 * 
	 * @return TimedAlarmManager
	 */
	public synchronized static TimedAlarmManager getInstance(){
		
		if(instance == null){
			
			instance = new TimedAlarmManager();
		}
		
		return instance;
	}

	
	/**
	 * Methode zum setzen der nächsten benachrichtigungszeit.<br/>
	 * Wenn noch keine aktive Message vorhanden wird die neue als aktuelle gesetzt.<br/>
	 * Wenn schon eine Message gesetzt ist wird verglichen ob die neue aktueller als die 
	 * gesetzte ist und ggf. die neue als aktuelle gesetzt.<br/>
	 * Treffen beide nicht zu wird die nachricht nur in die Db eingetragen.
	 * @param context Context 
	 * @param message TimedOutgoingMessage
	 */
	public void setTimeToSendTimer(Context context, TimedOutgoingMessage message, boolean isSaved){
		
			
		DbTimedMessagesRepository tmRepo = new DbTimedMessagesRepository(context);
		int messageId = tmRepo.createTimedMessage(message);	
		
		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(TimedMessageReceiver.TIMED_MESSAGE_INTENT);
		intent.putExtra("messageId", messageId);
		pi = PendingIntent.getBroadcast(context, messageId, intent, PendingIntent.FLAG_ONE_SHOT);
		am.set(AlarmManager.RTC_WAKEUP, message.getTimeToSend(), pi);	
	}
	
	public void deleteTimer(Context context, TimedOutgoingMessage message){
		
		DbTimedMessagesRepository tmRepo = new DbTimedMessagesRepository(context);
		tmRepo.deleteTimedMessage(message);
		
		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(TimedMessageReceiver.TIMED_MESSAGE_INTENT);
		intent.putExtra("messageId", message.getId());
		pi = PendingIntent.getBroadcast(context, message.getId(), intent, PendingIntent.FLAG_ONE_SHOT);
		am.cancel(pi);
		
	}

}
