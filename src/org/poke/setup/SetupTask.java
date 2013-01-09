package org.poke.setup;

import java.util.List;
import org.jivesoftware.smack.XMPPException;
import org.poke.database.DbContactsRepository;
import org.poke.database.DbRosterRepository;
import org.poke.database.DbUserRepository;
import org.poke.error.SetupErrorActivity;
import org.poke.helper.ApplicationPreference;
import org.poke.helper.HelperFunctions;
import org.poke.index.IndexActivity;
import org.poke.object.HandyContact;
import org.poke.object.RosterContact;
import org.poke.xmpp.RosterStorage;
import org.poke.xmpp.XMPPConnectionHandler;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Task for first Setup
 * @author Tobias
 *
 */
public class SetupTask extends AsyncTask<String, Integer, Boolean> {
	
	private Context context;
	private SetupActivity sa;
	private ApplicationPreference afr;
	private XMPPConnectionHandler connectionHandler;
	private ProgressDialog waitSpinner;
	
	
	private final String TAG = "SetupTaskDebug";
	
	private String countryCode;
	
	public SetupTask(Context context, SetupActivity sa){
		
		this.context = context;
		this.sa = sa;
	}
	
	
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		afr = new ApplicationPreference(context);
		connectionHandler = XMPPConnectionHandler.getInstance();
		
		waitSpinner = new ProgressDialog(context);
		waitSpinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		waitSpinner.setMessage("Bitte warten.... Poke wird konfiguriert");
		waitSpinner.show();
	}
	
	@Override
	protected Boolean doInBackground(String... arg) {
		
		boolean check = false;
		
		countryCode = arg[1].toLowerCase();
		
		String userId = HelperFunctions.getInstance().generateUserId(arg[0], arg[1]);
		String username = arg[2];
		String password = arg[3];
		
		//Initialisieren der Datenbank, Account und Contact List
		if(init_Database(userId, username, password)&&
		   init_Account(userId, password, username)&&
		   init_Contactlist(userId, password)){
			
			Log.d(TAG, "Der init teil wird aufgerufen");
			check = true;
		}
		
		return check;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		Intent intent;
		
		if(result == true){
			intent = new Intent(context, IndexActivity.class);
			afr.setRunned();
			
		}
		else{
			intent = new Intent(context, SetupErrorActivity.class);
		}
		waitSpinner.cancel();
		context.startActivity(intent);
		sa.finish();
	}
	
	
	/**
	 * Erstellt eine Datenbank auf dem Mobilen Endgerät und befüllt <br/>
	 * diese mit Daten
	 * @return true wenn es erfolgreich war sonst false
	 */
	private boolean init_Database(String userId, String username, String password){
		
		DbUserRepository repository = new DbUserRepository(context);
			
		return repository.create(userId, username, password, countryCode);	
		
	}
	
	/**
	 * Erstellt einen Account auf dem Openfire Server
	 * @return true wenn es erfolgreich war sonst false
	 */
	private boolean init_Account(String userId, String password, String username){
		
		boolean checked = false;
		
		try {
			connectionHandler.createAccount(userId, username, password);
			connectionHandler.disconect();
			checked = true;
			Log.d("init_Account", "Der Account wurde erstellt.");
		} catch (XMPPException e) {
			
			Log.d("init_Account", "Der Account konnte nicht erstellt werden.");
		}
	
		
		return checked;
	}
	
	/**
	 * Erstellt die Kontaktliste
	 * @return true wenn es erfolgreich war sonst false
	 */
	private boolean init_Contactlist(String userId, String password){
		
		boolean checked = false;
		DbRosterRepository rosterRepository = new DbRosterRepository(context);

		try {
			connectionHandler.login(userId, password);
			checked = true;
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(checked){
			
			List<HandyContact> contacts = HelperFunctions.getInstance().getNumbersFromContacts(context);
			//Kontaktlist abbildung auf Datenbank
			DbContactsRepository contactsRepository = new DbContactsRepository(context);
			
			RosterStorage rs = new RosterStorage();
						
			for(HandyContact handyContact : contacts){
				
				contactsRepository.saveContact(handyContact);
				
				if(connectionHandler.isRegistered(countryCode,handyContact.getNumber())){
					
					String userJid = countryCode+"_"+handyContact.getNumber()+"@"+connectionHandler.getConnection().getServiceName();
					
					//Eintrag in den Roster einfügen
					rs.addEntry(countryCode, handyContact.getNumber(), handyContact.getName(),connectionHandler.getConnection());
					
					Log.d(TAG, userJid);
					
					RosterContact rc = new RosterContact();
					rc.setJid(countryCode, handyContact.getNumber());
					rc.setUsername(handyContact.getName());
					
					//Roster auf Datenbank abbilden
					checked = rosterRepository.createRosterEntry(rc);
					
				}
		
			}
			
		}
		else checked = false;
		
		connectionHandler.disconect();
		
		
		return checked;
	}
	

}
