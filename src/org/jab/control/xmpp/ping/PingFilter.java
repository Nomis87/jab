package org.jab.control.xmpp.ping;

import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.ping.packet.Ping;

public class PingFilter implements PacketFilter {

	public boolean accept(Packet packet) {
		
		if (packet instanceof Ping) {


			return true;
        }

		return false;
	}

}
