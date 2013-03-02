package org.jab.control.message;

import org.jab.control.util.ApplicationContext;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SubscribeMessageListener implements PacketListener {
	
	private final String TAG = "SubscribeMessageListener"; 
	private Context context;

	public void processPacket(Packet packet) {
		
		this.context = ApplicationContext.getInstance().getContext();
		
		Log.d(TAG, "Packet empfangen !!!");
		
		if(packet instanceof Message){
			
			Message message = (Message) packet;
			if(message.getType() == Message.Type.error){
			
				if(message.getFrom() != null){
				
					Log.d(TAG,  "From: ["+message.getFrom()+"]");
				
					Intent intent = new Intent(SubscribeMessageReceiver.SUBSCRIBEMESSAGE_INTENT);
					intent.putExtra("subscribeSender", message.getFrom());
				
					context.sendBroadcast(intent);
				}
				
			
			}
		}
		
		
	}

	

}
