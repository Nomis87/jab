package org.poke.message;

import java.util.LinkedList;
import java.util.Queue;

import org.poke.object.message.IncomingMessage;
import org.poke.util.ApplicationConstants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PokeMessageReceiver extends BroadcastReceiver {
	
	private final String TAG = "PokeMessageReceiver";	
	public static String POKEMESSAGE_INTENT = "org.poke.POKEMESSAGE_INTENT"; 	
	private Context context;
	
	Queue<IncomingMessage> messageQueue = new LinkedList<IncomingMessage>();
	private String pokeSender;
	private String pokeSound;
	private String pokeMessage;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		this.context = context;
		//ReceivedMessageQueue messageQueue =  ReceivedMessageQueue.getInstance();
		
		String[] parsedMessage = parseMessage(intent.getStringExtra("pokeMessage"));
		
		pokeSender = intent.getStringExtra("pokeSender");
			
		// Hier Muss geprüft werden
		
		if(parsedMessage.length > 1){
			
			pokeMessage = parsedMessage[1];
			pokeSound = parsedMessage[0];
		}
		else{
			pokeMessage = parsedMessage[0];
			pokeSound = "";
		}
		
		
		IncomingMessage pm = new IncomingMessage(pokeSender, pokeSound, pokeMessage);
		
		messageQueue.add(pm);
		
		// observer pattern zum if isReading false notify zu senden.
		while(messageQueue.peek() != null){
			
			
			IncomingMessage pme = messageQueue.poll();				
			startMessageActivity(pme.getSender(), pme.getSound(), pme.getMessage());		
		}
			
		
		//Debug kram
		Log.d(TAG, "From: ["+pokeSender+"] Message: ["+pokeMessage+"]");

	}
	
	private void  startMessageActivity(String sender, String sound, String message){
		
		Intent pokeMessageActivity = new Intent(context, PokeMessageActivity.class);
		pokeMessageActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		pokeMessageActivity.putExtra("pokeSender", sender);
		pokeMessageActivity.putExtra("pokeSound", sound);
		pokeMessageActivity.putExtra("pokeMessage", message);
		context.startActivity(pokeMessageActivity);
	}
	
	
	private String[] parseMessage(String message){
		
		
		String[] messageArray =  message.split(ApplicationConstants.SEPERATOR);
		
		return messageArray;
	}

}
