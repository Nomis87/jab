package org.jab.control.xmpp.ping;

import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

public class PingFilter implements PacketFilter {

	public boolean accept(Packet packet) {
		
		if (packet instanceof Presence) {

            Presence pres = (Presence) packet;

            if (pres.getType() == Presence.Type.subscribe)

                return true;

        }

		return false;
	}

}
