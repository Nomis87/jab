package org.poke.helper;

/**
 * Singelton Klasse<br/>
 * dient zum Setzen eines InternetStatus. Wenn connectionState = true besteht eine <br/>
 * Internetverbindung, andernfalls nicht.
 * @author Tobias
 *
 */
public class ConnectionState{
	
	private static ConnectionState instance = null;
	private boolean connectionState;

	
	
	private ConnectionState() {}
	
	public static synchronized ConnectionState getInstance(){
		
			
		if(instance == null){
			
			instance = new ConnectionState();
		}
		
		return instance;
	}
	
	/**
	 * prüfen auf Status
	 * @return True = Inet Verbindung, false = keine Intet Verbindung
	 */
	public boolean isConnectionState() {
		return connectionState;
	}
	
	/**
	 * Zum setzen des Connectionstatus
	 * @param connectionState ture = Inet Verbindung besteht, false = keine Inet Vebindung besteht
	 */
	public void setConnectionState(boolean connectionState) {
		this.connectionState = connectionState;
	}
	
}
