package org.poke.xmpp;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.poke.util.ApplicationContext;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class PokeMessageReceiver implements PacketListener {
	
	private final String TAG = "PokeMessageReceiver"; 
	
	public void processPacket(Packet packet) {
		
		Log.d(TAG, "Packet empfangen !!!");
		
		if(packet instanceof Message){
			
			Message message = (Message) packet;
			
			generatePoke(message.getFrom(), message.getBody());
			
		}

	}
	
	private void generatePoke(String from, String message){
		
		Log.d(TAG, "Packet ist eine Message");
		
		Toast.makeText(ApplicationContext.getInstance().getContext(),
				"From: ["+from+"] Message: ["+message+"]", Toast.LENGTH_LONG).show();
		
	}

}
