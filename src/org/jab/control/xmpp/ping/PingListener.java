package org.jab.control.xmpp.ping;

import org.jab.control.xmpp.XMPPConnectionHandler;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.ping.packet.Ping;
import org.jivesoftware.smackx.ping.packet.Pong;

import android.util.Log;

public class PingListener implements PacketListener {
	
	private final String TAG  = "PingListener";
	
	public void processPacket(Packet packet) {
        	
        Log.d(TAG, "ping received");
        Pong pong = new Pong((Ping) packet);
        XMPPConnectionHandler.getInstance().getConnection().sendPacket(pong);

	}

}
