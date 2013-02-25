package org.jab.control.util;

/**
 * Enthält alle wichtigen Applicationsinformationen.
 * @author Tobias
 *
 */
public class ApplicationConstants {
	
	//App Name
	public static final String APP_NAME = "jab";
	
	//Server Informations
	public static final String SERVER_HOST = "codeinjection.dyndns.org";
	public static final String SERVER_NAME = "codeinjection";
	public static final int SERVER_PORT = 5222;
	
	//DB Informationen
	public static final String DB_NAME = "jab.db"; 
	public static final String DB_TABLE_USER = SERVER_NAME+"_user";
	public static final String DB_TABLE_ROSTER = SERVER_NAME+"_roster";
	public static final String DB_TABLE_ROSTER_GROUP_HELPER = SERVER_NAME+"_rosterGroup_helper";
	public static final String DB_TABLE_ROSTER_GROUP = SERVER_NAME+"_rosterGroup";
	public static final String DB_TABLE_CONTACTS = SERVER_NAME+"_handyContacts";
	public static final String DB_TABLE_OFFLINE_MESSAGES = SERVER_NAME+"_outgoingMessages";
	public static final String DB_TABLE_TIMED_MESSAGES = SERVER_NAME+"_timedMessages";
	public static final String DB_TABLE_SENDED_MESSAGES = SERVER_NAME+"_sendedMessages";
	public static final String DB_TABLE_SOUND_PACKAGES = SERVER_NAME+"_soundPackages";
	public static final String DB_TABLE_SOUNDS = SERVER_NAME+"_sounds";
	public static final int DB_VERSION = 1;
		
	// Salt password 
	public final static String PASSWORD_SALT_VALUE = "45Ad2L56T";
	
	//Message Constants
	public final static String SEPERATOR = "%sep%";
	public final static String NO_MESSAGE = "#8080";
}
