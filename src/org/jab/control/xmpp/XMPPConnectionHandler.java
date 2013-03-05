package org.jab.control.xmpp;

import java.util.Iterator;
import java.util.List;

import org.jab.control.message.MessageListener;
import org.jab.control.message.SubscribeMessageListener;
import org.jab.control.storage.database.DbOfflineMessagesRepository;
import org.jab.control.util.ApplicationConstants;
import org.jab.control.util.ConnectionState;
import org.jab.control.util.HelperFunctions;
import org.jab.control.xmpp.ping.PingFilter;
import org.jab.control.xmpp.ping.PingListener;
import org.jab.model.message.OutgoingMessage;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.provider.PrivacyProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.ping.provider.PingProvider;
import org.jivesoftware.smackx.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.content.Context;
import android.util.Log;

/**
 * Singelton Klasse <br/>
 * Stellt Methoden für die benoetigen XMPP funktionalitaeten zu Verfügung.
 * 
 * @author Tobias
 *
 */
public class XMPPConnectionHandler {
		
	private final String TAG = "XMPPConnectionHandler";
	private static XMPPConnectionHandler instance = null;
	private XMPPConnection connection;
	private ConnectionConfiguration config;
	
	private PacketFilter jabFilter;
	private PacketListener jabListener;
	private PacketFilter subscribeFilter;
	private PacketListener subscribeListener;
	private PacketFilter pingFilter;
	private PacketListener pingListener;
	
	
	public XMPPConnection getConnection(){
		
		
		return this.connection;
		//addConnectionListener();
	}
	
	private XMPPConnectionHandler(){
		
		init_config();
		init_providermanager(ProviderManager.getInstance());
		init_connection();
		//addConnectionListener(ApplicationContext.getInstance().getContext());
		
	}
	
	public static synchronized XMPPConnectionHandler getInstance(){
		
		if(instance == null){
			instance = new XMPPConnectionHandler();
		}
		
		return instance;
	}
	

	private void init_config(){
		
		this.config =  new ConnectionConfiguration(ApplicationConstants.SERVER_HOST, ApplicationConstants.SERVER_PORT);
		this.config.setTruststorePath("/system/etc/security/cacerts.bks");
		this.config.setTruststorePassword("changeit");
		this.config.setTruststoreType("bks");
		
		SmackConfiguration.setKeepAliveInterval(60000);
		
	}
	
	private void init_providermanager(ProviderManager pm){
		
		configure(pm);
	}
	
	
	private void init_connection(){
		
		this.connection = new XMPPConnection(config);
		this.jabFilter = new MessageTypeFilter(Message.Type.normal);
		this.jabListener = new MessageListener();
		
		this.subscribeFilter  =  new MessageTypeFilter(Message.Type.error);
		this.subscribeListener = new SubscribeMessageListener();
		
		this.pingFilter = new PingFilter();
		this.pingListener = new PingListener();
	}

	
	public void setMessageReceiver(){
		
		connection.addPacketListener( jabListener, jabFilter);
		connection.addPacketListener(subscribeListener, subscribeFilter);
		connection.addPacketListener(pingListener, pingFilter);
	}
	
	public void removeMessageReceiver(){
		
		connection.removePacketListener( jabListener);
		connection.removePacketListener(subscribeListener);
		connection.removePacketListener(pingListener);
	}
	
	
	
	
	public void createAccount(String userId, String password ) throws XMPPException{
				
		//AccountManager wird initialisiert
		AccountManager am = new AccountManager(connection);
				
		connection.connect();
		am.createAccount(userId, HelperFunctions.getInstance().saltPassword(password));
		
	}
	
	public void deleteAccount(String userId, String password){
		
		AccountManager am = new AccountManager(connection);
		
		try {
			am.deleteAccount();
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	/**
	 * Überprüft ob ein User auf dem Server registriert ist 
	 * @param userId die userID des Benutzers
	 * @return true wenn er registriert ist false wenn nicht.
	 */
	public boolean isRegistered(String countryCode, String number){
		
		String userId = HelperFunctions.getInstance().generateUserId(number, countryCode);
		
		boolean registered = false;	
		
		UserSearchManager userSearch = new UserSearchManager(connection);
		ReportedData data = null;
		try {
			Form searchForm = userSearch.getSearchForm("search."+connection.getServiceName());
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("search", userId);
			data = userSearch.getSearchResults(answerForm, "search."+connection.getServiceName());
			
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(data != null){
			
	         Iterator<Row> rows = data.getRows();
	         while (rows.hasNext()) {
	            Row row = rows.next();
	            
	            @SuppressWarnings("unchecked")
				Iterator<String> username = row.getValues("Username");
	            while (username.hasNext()) {
	               if(!username.next().equals("userId")){
	            	   
	            	   registered = true;
	               }
	               else{
	    				
	            	   Log.d(TAG, "Der Username "+userId+" ist nicht registriert.");
	               }
	            }
	         }
		}
		else{
			
			Log.d(TAG, "Data is null");
		}
			
		return registered;
	}
	
	public void sendSubscribeMessage(String receiver){
		
		 if(connection.isAuthenticated()){
			 
			 Message message = new Message(receiver, Message.Type.error);
			 connection.sendPacket(message);
			 
			 ChatManager chatManager = connection.getChatManager();
			 Chat chat = chatManager.createChat(receiver,null);
			 
			 try {
				chat.sendMessage(message);
				Log.i(TAG, "Sending subscribe [to [" + receiver + "]");
			 } catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
			
			 
		 }
		 else{
			 
			 Log.i(TAG, "Nich eingeloggt !!!!!!");
		 }
		
	}
	
	public void handleOfflineMessages(Context context){
		
		DbOfflineMessagesRepository offRepo = new DbOfflineMessagesRepository(context);
		
		List<OutgoingMessage> messageList = offRepo.readAllOfflineMessages();
		
		if(messageList.size()>0){
			
			for(OutgoingMessage oMessage : messageList){
				
				if(ConnectionState.getInstance().isConnectionState()){
					
					sendJabMessage(oMessage.getReceiver(), oMessage.getSound(), oMessage.getMessage(), context);
					offRepo.deleteMessage(oMessage);
				}
				else{
					break;
				}
				
			}
			
		}
		
	}
	
	
	/**
	 * Methode zum senden einer Nachricht
	 * 
	 * @param receiver
	 * @param sound
	 * @param pokeMessage
	 * @param context
	 * @return
	 */
	public boolean sendJabMessage(String receiver, String sound, String pokeMessage, Context context){
		
		if(ConnectionState.getInstance().isConnectionState()){
			 Log.i("XMPPClient", "Sending text [so_" + sound + " "+pokeMessage+"] to [" + receiver + "]");
			 if(connection.isAuthenticated()){
				 
				 if(pokeMessage.equals("")){
					 
					 pokeMessage = ApplicationConstants.NO_MESSAGE;
				 }
				 
				 Message message = new Message(receiver, Message.Type.normal);
				 message.setBody(sound+ApplicationConstants.SEPERATOR+pokeMessage);
					 
			     connection.sendPacket(message);
				
				 
			 }
			 else{
				 
				 Log.i(TAG, "Nich eingeloggt !!!!!!");
			 }
		}
		else{
			
			Log.i(TAG, "Sending Offline Message");
			OutgoingMessage oMessage = new OutgoingMessage(receiver, sound, pokeMessage);
			
			DbOfflineMessagesRepository offRepo = new DbOfflineMessagesRepository(context);
			offRepo.createMessage(oMessage);
		}
		
		
		return false;
	}
	
	
	public void login(String userId, String password) throws XMPPException{
		
		if(!connection.isConnected()){
			
			if(!connection.isAuthenticated()){			
				
				connection.connect();
				SASLAuthentication.supportSASLMechanism("PLAIN", 0);
				connection.login(userId, HelperFunctions.getInstance().saltPassword(password));
			}	
		}
		else{
			
			connection.disconnect();
			connection.connect();
			SASLAuthentication.supportSASLMechanism("PLAIN", 0);
			connection.login(userId, HelperFunctions.getInstance().saltPassword(password));		
		}
	}
		
	
	public void disconect(){
		
		connection.disconnect();
	}
	
	
	private void configure(ProviderManager pm) {

		//  Private Data Storage
		pm.addIQProvider("query","jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());
	
		//  Time
		try {
		    pm.addIQProvider("query","jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
		    Log.w("TestClient", "Can't load class for org.jivesoftware.smackx.packet.Time");
		}
	
		//  Roster Exchange
		pm.addExtensionProvider("x","jabber:x:roster", new RosterExchangeProvider());
	
		//  Message Events
		pm.addExtensionProvider("x","jabber:x:event", new MessageEventProvider());
	
		//  Chat State
		pm.addExtensionProvider("active","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider()); 
		pm.addExtensionProvider("paused","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone","http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
	
		//  XHTML
		pm.addExtensionProvider("html","http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());
	
		//  Group Chat Invitations
		pm.addExtensionProvider("x","jabber:x:conference", new GroupChatInvitation.Provider());
	
		//  Service Discovery # Items    
		pm.addIQProvider("query","http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
	
		//  Service Discovery # Info
		pm.addIQProvider("query","http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
	
		//  Data Forms
		pm.addExtensionProvider("x","jabber:x:data", new DataFormProvider());
	
		//  MUC User
		pm.addExtensionProvider("x","http://jabber.org/protocol/muc#user", new MUCUserProvider());
	
		//  MUC Admin    
		pm.addIQProvider("query","http://jabber.org/protocol/muc#admin", new MUCAdminProvider());
	
		//  MUC Owner    
		pm.addIQProvider("query","http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());
	
		//  Delayed Delivery
		pm.addExtensionProvider("x","jabber:x:delay", new DelayInformationProvider());
	
		//  Version
		try {
		    pm.addIQProvider("query","jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
		    //  Not sure what's happening here.
		}
	
		//  VCard
		pm.addIQProvider("vCard","vcard-temp", new VCardProvider());
	
		//  Offline Message Requests
		pm.addIQProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());
	
		//  Offline Message Indicator
		pm.addExtensionProvider("offline","http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());
	
		//  Last Activity
		pm.addIQProvider("query","jabber:iq:last", new LastActivity.Provider());
	
		//  User Search
		pm.addIQProvider("query","jabber:iq:search", new UserSearch.Provider());
	
		//  SharedGroupsInfo
		pm.addIQProvider("sharedgroup","http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());
	
		//  JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses","http://jabber.org/protocol/address", new MultipleAddressesProvider());
	
		//   FileTransfer
		pm.addIQProvider("si","http://jabber.org/protocol/si", new StreamInitiationProvider());
	
		pm.addIQProvider("query","http://jabber.org/protocol/bytestreams", new BytestreamsProvider());
		
		// add XMPP Ping (XEP-0199)
		pm.addIQProvider("ping","urn:xmpp:ping", new PingProvider());
		
		//  Privacy
		pm.addIQProvider("query","jabber:iq:privacy", new PrivacyProvider());
		pm.addIQProvider("command", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider());
		pm.addExtensionProvider("malformed-action", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.MalformedActionError());
		pm.addExtensionProvider("bad-locale", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadLocaleError());
		pm.addExtensionProvider("bad-payload", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadPayloadError());
		pm.addExtensionProvider("bad-sessionid", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.BadSessionIDError());
		pm.addExtensionProvider("session-expired", "http://jabber.org/protocol/commands", new AdHocCommandDataProvider.SessionExpiredError());
		
	}
	
	
	
}
