package org.jab.control.xmpp.ping;

import org.jab.control.xmpp.XMPPConnectionHandler;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;

import android.util.Log;

public class PingListener implements PacketListener {
	
	private final String TAG  = "PingListener";
	
	public void processPacket(Packet packet) {
		
        if(!(packet instanceof PingExtension))
        	return;
        PingExtension p = (PingExtension) packet;

        if (p.getType() == IQ.Type.GET) {

            PingExtension pong = new PingExtension();
            pong.setType(IQ.Type.RESULT);
            pong.setTo(p.getFrom());
            pong.setPacketID(p.getPacketID());
            XMPPConnectionHandler.getInstance().getConnection().sendPacket(pong);
            Log.d(TAG, "Pong sended");

        }


	}

}
