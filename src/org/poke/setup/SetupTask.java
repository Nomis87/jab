package org.poke.setup;

import java.util.List;
import java.util.Locale;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.poke.error.SetupErrorActivity;
import org.poke.helper.AppFirstRun;
import org.poke.helper.HelperFunctions;
import org.poke.index.IndexActivity;
import org.poke.object.HandyContact;
import org.poke.util.ApplicationConstants;
import org.poke.xmpp.RosterStorage;
import org.poke.xmpp.XMPPConnectionHandler;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class SetupTask extends AsyncTask<String, Integer, Boolean> {
	
	private Context context;
	private SetupActivity sa;
	private AppFirstRun afr;
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
		afr = new AppFirstRun(context);
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
		
		//True wenn 
		boolean created = false;
		
		SQLiteDatabase db = context.openOrCreateDatabase(ApplicationConstants.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		db.setVersion(1);
		db.setLocale(Locale.getDefault());
		db.setLockingEnabled(true); 
		
		db.execSQL("DROP TABLE IF EXISTS "+ApplicationConstants.DB_TABLE_USER);
		
		// Erstellen der Datenbank Taelle für den User
		db.execSQL("CREATE TABLE IF NOT EXISTS " 
				+ ApplicationConstants.DB_TABLE_USER
				+ " (us_id VARCHAR PRIMARY KEY, us_username VARCHAR, us_password VARCHAR );");
		
		// Einfügen der Informationen
		ContentValues userValues = new ContentValues();
		userValues.put("us_id", userId);
		userValues.put("us_username", username);
		userValues.put("us_password", password);
		db.insert(ApplicationConstants.DB_TABLE_USER, null, userValues);
		
		
		// Testen ob die datenbak erfolgreich erstellt wurde		
		Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_USER, null);
		c.moveToFirst();
		String testUserid = c.getString(c.getColumnIndex("us_id"));
		String testUsername = c.getString(c.getColumnIndex("us_username"));
		String testPassword = c.getString(c.getColumnIndex("us_password"));
		
		if(testUserid.equals(userId)&&testUsername.equals(username)&&testPassword.equals(password)){
			
			created = true;
		}
		
		
		//Schließen des Cursors und der Datenbank
		c.close();
		db.close();
		
		return created;	
		
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
		
		SQLiteDatabase db = context.openOrCreateDatabase(ApplicationConstants.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
		
		// Erstellen der Datenbank Taelle für den User
		db.execSQL("CREATE TABLE IF NOT EXISTS " 
					+ ApplicationConstants.DB_TABLE_ROSTER
					+ " (ro_id VARCHAR PRIMARY KEY, ro_username VARCHAR);");
		

		try {
			connectionHandler.login(userId, password);
			checked = true;
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(checked){
			
			List<HandyContact> contacts = HelperFunctions.getInstance().getNumbersFromContacts(context);
			
			RosterStorage rs = new RosterStorage();
						
			for(HandyContact user : contacts){
				
				if(connectionHandler.isRegistered(countryCode+"_"+user.getNumber())){
					
					String userJid = countryCode+"_"+user.getNumber()+"@"+connectionHandler.getConnection().getServiceName();
					
					//Eintrag in den Roster einfügen
					rs.addEntry(userJid, user.getName(),connectionHandler.getConnection());
					
					//Roster auf die Datenbank abbilden
					ContentValues userValues = new ContentValues();
					userValues.put("ro_id", userJid);
					userValues.put("ro_username", user.getName());
					db.insert(ApplicationConstants.DB_TABLE_ROSTER, null, userValues);
				}
		
			}
			
		}
		else checked = false;
		
		db.close();
		connectionHandler.disconect();
		return checked;
	}
	

}
