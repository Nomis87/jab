package org.poke.xmpp;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

public class SubscribePacketListener implements PacketListener {

	public void processPacket(Packet packet) {
		
		
		if(packet instanceof Presence){
			
			Presence presence = (Presence) packet;
			
			presence.getStatus() ;
		}

	}

}
