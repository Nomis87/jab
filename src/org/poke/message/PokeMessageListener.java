package org.poke.message;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.poke.util.ApplicationContext;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PokeMessageListener implements PacketListener {
	
	private final String TAG = "PokeMessageListener"; 
	private Context context;
	
	public void processPacket(Packet packet) {
		
		this.context = ApplicationContext.getInstance().getContext();
		
		Log.d(TAG, "Packet empfangen !!!");
		
		if(packet instanceof Message){
			
			Message message = (Message) packet;
			
			if(message.getType() == Message.Type.normal){
				
				if(message.getFrom() != null){
				
					Intent intent = new Intent(PokeMessageReceiver.POKEMESSAGE_INTENT);
					intent.putExtra("pokeSender", message.getFrom());
					intent.putExtra("pokeMessage", message.getBody());
					context.sendBroadcast(intent);
					
					Log.d(TAG, "From: ["+message.getFrom()+"] Message: ["+message.getBody()+"]");
				
				}
			}
			
		}
		
	}

}
