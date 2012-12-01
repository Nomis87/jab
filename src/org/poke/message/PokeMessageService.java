package org.poke.message;

import java.util.Vector;

import org.poke.object.PokeMessage;
import org.poke.util.ApplicationConstants;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class PokeMessageService extends Service {
	
	private final String TAG = "PokeMessageService";
	
	Vector<PokeMessage> messageQueue = new Vector<PokeMessage>();
	
	private String pokeSender;
	private String pokeSound;
	private String pokeMessage;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		
		super.onCreate();
	}
	
	@SuppressLint("NewApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		
		String[] parsedMessage = parseMessage(intent.getStringExtra("pokeMessage"));
		
		pokeSender = intent.getStringExtra("pokeSender");
		
		
		
		if(parsedMessage.length > 1){
			pokeMessage = parsedMessage[1];
			pokeSound = parsedMessage[0];
		}
		else{
			pokeMessage = parsedMessage[0];
			pokeSound = "";
		}
		
		
		PokeMessage pm = new PokeMessage(pokeSender, pokeSound, pokeMessage);
		
		messageQueue.add(pm);
		
		if(!messageQueue.isEmpty()){
			
			while(messageQueue.size()>0){
				
				PokeMessage pme = messageQueue.firstElement();				
				startDialogActivity(pme.getSender(), pme.getSound(), pme.getMessage());		
				messageQueue.remove(pme);
			}
			
		}
		
		
		//Debug kram
		Log.d(TAG, "From: ["+pokeSender+"] Message: ["+pokeMessage+"]");
		
//		Toast.makeText(this.getApplicationContext(),
//				"From: ["+pokeSender+"] Message: ["+pokeMessage+"]", Toast.LENGTH_LONG).show();
		
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
	}
	
	private void  startDialogActivity(String sender, String sound, String message){
		
		Intent pokeMessageActivity = new Intent(this, PokeMessageActivity.class);
		pokeMessageActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		pokeMessageActivity.putExtra("pokeSender", sender);
		pokeMessageActivity.putExtra("pokeSound", sound);
		pokeMessageActivity.putExtra("pokeMessage", message);
		startActivity(pokeMessageActivity);
	}
	
	
	private String[] parseMessage(String message){
		
		
		String[] messageArray =  message.split(ApplicationConstants.SEPERATOR);
		
		return messageArray;
	}

}
