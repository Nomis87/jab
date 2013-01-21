package org.poke.message.timedSend;

import org.poke.database.DbTimedMessagesRepository;
import org.poke.object.message.TimedOutgoingMessage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Singelton Klasse.
 * Beinhaltet den AlarmManager welcher zum setzten des Alarms für die 
 * TimedMessages zuständig ist.
 * @author Tobias
 *
 */
public class TimedAlarmManager {
	
	private static TimedAlarmManager instance = null;
	private final String TAG = "TimedAlarmManager";
	private AlarmManager am;
	private Long aktTimeToSend;
	private Integer  aktMessageId;
	
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
	
	
	
	//Getter and Setters
	public Long getAktTimeToSend() {
		return aktTimeToSend;
	}

	public void setAktTimeToSend(Long aktTimeToSend) {
		this.aktTimeToSend = aktTimeToSend;
	}

	public Integer getAktMessageId() {
		return aktMessageId;
	}

	public void setAktMessageId(Integer aktMessageId) {
		this.aktMessageId = aktMessageId;
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
	public void setTimeToSendTimer(Context context, TimedOutgoingMessage message){
		
		if(aktTimeToSend == null && aktMessageId==null){
			
			aktTimeToSend = message.getTimeToSend();
			aktMessageId = saveInDb(context, message);
			
			am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(TimedMessageReceiver.TIMED_MESSAGE_INTENT);
			pi = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 0);
			am.set(AlarmManager.RTC_WAKEUP, message.getTimeToSend(), pi);
			
			Log.d(TAG, "Null: "+message.getReceiver());
		}
		else if(aktTimeToSend > message.getTimeToSend()){
			
			aktTimeToSend = message.getTimeToSend();
			aktMessageId = saveInDb(context, message);

			Intent intent = new Intent(TimedMessageReceiver.TIMED_MESSAGE_INTENT);
			pi = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 0);
			am.set(AlarmManager.RTC_WAKEUP, message.getTimeToSend(), pi);
			 
		}
		else{
			
			saveInDb(context.getApplicationContext(), message);
		}
		
	}
	
	/**
	 * Speichert eine Timednachricht über dessen Repository in die Datenbank.
	 * @param context Context
	 * @param message TimedOutgoingMessage
	 * @return
	 */
	private int saveInDb(Context context, TimedOutgoingMessage message){
		
		DbTimedMessagesRepository tmRepo = new DbTimedMessagesRepository(context);
		
		return tmRepo.createTimedMessage(message);
	}
	
	public void stopManager(){
		
		am.cancel(pi);
	}

}
