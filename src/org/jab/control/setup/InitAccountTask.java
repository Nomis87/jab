package org.jab.control.setup;

import java.util.List;

import org.jab.control.main.BootstrapActivity;
import org.jab.control.storage.database.DbPhoneBookRepository;
import org.jab.control.storage.database.DbRosterRepository;
import org.jab.control.storage.database.DbUserRepository;
import org.jab.control.storage.preference.ApplicationPreference;
import org.jab.control.util.HelperFunctions;
import org.jab.control.xmpp.XMPPConnectionHandler;
import org.jab.control.xmpp.XMPPRosterStorage;
import org.jab.model.User;
import org.jab.model.contact.HandyContact;
import org.jab.model.contact.RosterContact;
import org.jab.view.activity.SetupAccountActivity;
import org.jab.view.activity.SetupErrorActivity;
import org.jivesoftware.smack.XMPPException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Task für die Installation der Applikation
 * @author Tobias
 *
 */
public class InitAccountTask extends AsyncTask<String, Integer, Boolean> {
	
	private Context context;
	private SetupAccountActivity sa;
	private ApplicationPreference afr;
	private XMPPConnectionHandler connectionHandler;
	private ProgressDialog waitSpinner;
	
	private int contactCounter = 0;
	
	
	private final String TAG = "SetupTaskDebug";
	
	private String countryCode;
	
	public InitAccountTask(Context context, SetupAccountActivity sa){
		
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
		waitSpinner.setMessage("Account wird erstellt");
		waitSpinner.show();
	}
	
	@Override
	protected Boolean doInBackground(String... arg) {
		
		boolean check = false;
		
		countryCode = arg[1].toLowerCase();
		
		String userId = HelperFunctions.getInstance().generateUserId(arg[0], arg[1]);
		String password = arg[2];
		
		//Initialisieren der Datenbank, Account und Contact List
		if(init_Database(userId, password)&&
		   init_Account(userId, password)&&
		   init_Contactlist(userId, password)){
			
			Log.d(TAG, "Der init teil wird aufgerufen");
			check = true;
		}
		
		return check;
	}
	
	
	/**
	 * Erstellt eine Datenbank auf dem Mobilen Endgerät und befüllt <br/>
	 * diese mit Daten
	 * @return true wenn es erfolgreich war sonst false
	 */
	private boolean init_Database(String userId, String password){
		

		DbUserRepository userRepository = new DbUserRepository(context);
		User user = new User(userId, password, countryCode);
		
		
		return userRepository.createUser(user);
	}
	
	/**
	 * Erstellt einen Account auf dem Openfire Server
	 * @return true wenn es erfolgreich war sonst false
	 */
	private boolean init_Account(String userId, String password){
		
		boolean checked = false;
		
		try {
			connectionHandler.createAccount(userId, password);
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
		
		//connectionHandler.setMessageReceiver();
		
		if(checked){
			
			Log.d(TAG, "InitContactList wird asugefuehrt");
			
			List<HandyContact> contacts = HelperFunctions.getInstance().getNumbersFromContacts(context, countryCode);
			//Kontaktlist abbildung auf Datenbank
			DbPhoneBookRepository contactsRepository = new DbPhoneBookRepository(context);
			
			XMPPRosterStorage rs = new XMPPRosterStorage();
						
			for(HandyContact handyContact : contacts){
				
				contactsRepository.saveContact(handyContact);
				
				Log.d(TAG, handyContact.getName());
				System.out.println(connectionHandler.isRegistered(countryCode,handyContact.getNumber()));
				if(connectionHandler.isRegistered(countryCode,handyContact.getNumber())){
					
					//Subscribe Message to user
					String userJid = handyContact.getCountryCode()+"_"+handyContact.getNumber()+"@"+connectionHandler.getConnection().getServiceName();
					connectionHandler.sendSubscribeMessage(userJid);
					
					//Eintrag in den Roster einfügen
					rs.addEntry(handyContact.getCountryCode(), handyContact.getNumber(), handyContact.getName(),connectionHandler.getConnection());
					
					Log.d(TAG, userJid);
					
					RosterContact rc = new RosterContact();
					rc.setJid(handyContact.getCountryCode(), handyContact.getNumber());
					rc.setUsername(handyContact.getName());
					
					//Roster auf Datenbank abbilden
					checked = rosterRepository.createRosterEntry(rc);
					
					contactCounter++;
				}
		
			}
			
		}
		else checked = false;
		
		connectionHandler.disconect();
		
		
		return checked;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		Intent intent;

		if(result == true){
			
			Toast toast = Toast.makeText(context, "Es Wurden "+contactCounter+" hinzugefügt", Toast.LENGTH_SHORT);
			toast.show();
		
			intent = new Intent(context, BootstrapActivity.class);
			afr.setRunned();

		}
		else{
			intent = new Intent(context, SetupErrorActivity.class);
		}
		waitSpinner.cancel();
		context.startActivity(intent);
		sa.finish();
	}
	
}
