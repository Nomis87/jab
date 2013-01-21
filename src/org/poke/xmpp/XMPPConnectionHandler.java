package org.poke.xmpp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
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
import org.poke.database.DbOfflineMessagesRepository;
import org.poke.helper.ConnectionState;
import org.poke.helper.HelperFunctions;
import org.poke.message.PokeMessageListener;
import org.poke.message.SubscribeMessageListener;
import org.poke.object.message.OutgoingMessage;
import org.poke.util.ApplicationConstants;
import org.poke.util.ApplicationContext;

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
	
	private PacketFilter pokeFilter;
	private PacketListener pokeListener;
	private PacketFilter subscribeFilter;
	private PacketListener subscribeListener;
	
	
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
		
		this.pokeFilter = new MessageTypeFilter(Message.Type.normal);
		this.pokeListener = new PokeMessageListener();
		
		this.subscribeFilter  =  new MessageTypeFilter(Message.Type.error);
		this.subscribeListener = new SubscribeMessageListener();
	}

	
	public void setMessageReceiver(){
		
		Log.d(TAG, "Message Receiver wird aufgerufen");
		connection.addPacketListener( pokeListener, pokeFilter);
		connection.addPacketListener(subscribeListener, subscribeFilter);
	}
	
	public void removeMessageReceiver(){
		
		connection.addPacketListener( pokeListener, pokeFilter);
		connection.addPacketListener(subscribeListener, subscribeFilter);
	}
	
	public void createAccount(String userId, String username, String password ) throws XMPPException{
		
		//Hier wird das zusätzliches Atttribute Username gesetzt
		HashMap<String,String> attributes = new HashMap<String, String>();
		attributes.put("name", username);
				
		//AccountManager wird initialisiert
		AccountManager am = new AccountManager(connection);
				
		connection.connect();
		am.createAccount(userId, HelperFunctions.getInstance().saltPassword(password), attributes);
		
	}
	
	/**
	 * Überprüft ob ein User auf dem Server registriert ist 
	 * @param userId die userID des Benutzers
	 * @return true wenn er registriert ist false wenn nicht.
	 */
	public boolean isRegistered(String countryCode, String number){
		
		String userId = countryCode+"_"+number;
		
		boolean registered = false;
		
		
		UserSearchManager userSearch = new UserSearchManager(connection);
		ReportedData data = null;
		try {
			Form searchForm = userSearch.getSearchForm("search."+connection.getServiceName());
			Form answerForm  = searchForm.createAnswerForm();
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
	
	public void handleOfflineMessages(){
		
		DbOfflineMessagesRepository offRepo = new DbOfflineMessagesRepository(ApplicationContext.getInstance().getContext());
		
		List<OutgoingMessage> messageList = offRepo.getAllOfflineMessages();
		
		if(messageList.size()>0){
			
			for(OutgoingMessage oMessage : messageList){
				
				if(ConnectionState.getInstance().isConnectionState()){
					
					sendPokeMessage(oMessage.getReceiver(), oMessage.getSound(), oMessage.getMessage());
					offRepo.deleteOfflineMessage(oMessage);
				}
				else{
					break;
				}
				
			}
			
		}
		
	}
	
	public boolean sendPokeMessage(String receiver, String sound, String pokeMessage){
		
		if(ConnectionState.getInstance().isConnectionState()){
			 Log.i("XMPPClient", "Sending text [so_" + sound + " "+pokeMessage+"] to [" + receiver + "]");
			 if(connection.isAuthenticated()){
				 
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
			
			DbOfflineMessagesRepository offRepo = new DbOfflineMessagesRepository(ApplicationContext.getInstance().getContext());
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
