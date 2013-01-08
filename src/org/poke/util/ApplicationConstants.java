package org.poke.util;

public class ApplicationConstants {
	
	//Server Informations
	public static final String SERVER_HOST = "codeinjection.dyndns.org";
	public static final String SERVER_NAME = "poke";
	public static final int SERVER_PORT = 5222;
	
	//DB Informationen
	public static final String DB_NAME = "poke.db"; 
	public static final String DB_TABLE_USER = SERVER_NAME+"_user";
	public static final String DB_TABLE_ROSTER = SERVER_NAME+"_roster";
	public static final String DB_TABLE_CONTACTS = SERVER_NAME+"_handyContacts";
	public static final int DB_VERSION = 1;
		
	// Salt password 
	public final static String PASSWORD_SALT_VALUE = "45Ad2L56T";
	
	//Message Constants
	public final static String SEPERATOR = "%sep%";

}
