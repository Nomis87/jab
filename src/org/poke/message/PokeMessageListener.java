package org.poke.message;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.poke.util.ApplicationContext;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PokeMessageListener implements PacketListener {
	
	private final String TAG = "PokeMessageReceiver"; 
	private Context context;
	
	public void processPacket(Packet packet) {
		
		context = ApplicationContext.getInstance().getContext();
		
		Log.d(TAG, "Packet empfangen !!!");
		
		if(packet instanceof Message){
			
			Message message = (Message) packet;
			
			if(message.getFrom() != null){
				
				Intent service = new Intent(context,PokeMessageService.class);
				service.putExtra("pokeSender", message.getFrom());
				service.putExtra("pokeMessage", message.getBody());
				context.startService(service);
					
				Log.d(TAG, "From: ["+message.getFrom()+"] Message: ["+message.getBody()+"]");
				
			}
		}
		
	}

}
