package org.jab.control.xmpp.ping;

import org.jivesoftware.smack.packet.IQ;

public class PingExtension extends IQ {

    public static final String NAMESPACE = "urn:xmpp:ping";
    public static final String ELEMENT = "ping";

    /**

     * Create a ping iq packet.

     */

    public PingExtension() {

    }

    @Override

    public String getChildElementXML() {

        if (getType() == IQ.Type.RESULT)

            return null;

        return "<" + ELEMENT + " xmlns=\"" + NAMESPACE + "\" />";

    }


}
