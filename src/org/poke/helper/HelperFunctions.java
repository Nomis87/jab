package org.poke.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.poke.object.HandyContact;
import org.poke.util.ApplicationConstants;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * Singelton Class<br/>
 * Enthält diverse Hiflsfunktion welche in der Apllication genutzt werden.
 * @author Tobias
 *
 */
public class HelperFunctions {
	
	private static HelperFunctions instance = null;
	
	public static synchronized HelperFunctions getInstance(){
		
		if(instance == null){
			
			instance = new HelperFunctions();
		}
		
		return instance;
	}
	
	/**
	 * Sucht zu dem gegebenen Land das zugehörige Länderkürzel.
	 * @param country Ein Ländername
	 * @return Das zugehörige Länderkürzel
	 */
	public String searchCountryCode(String country){
			
		String shortCountry = null;
	    	
	    for(Locale locale : Locale.getAvailableLocales()){
	    		
	    	if(locale.getDisplayCountry().equals(country)){
	    			
	    		shortCountry = locale.getCountry();
	    	}
	   	}   	
	    	
	   	return shortCountry;
	}
	
	/**
	 * Generiert aus der Telefonnummer und dem Länderkürzel eine <br/>
	 * eindeutige User Id
	 * @param telNumber Die Telefonnummer
	 * @param shortCountry Das Länderkürzel
	 * @return
	 */
	public String generateUserId(String telNumber, String shortCountry){
			
		return shortCountry+"_"+telNumber;
	}
	
	/**
	 * Generiert aus einem klartext Passwort ein hashed salted Passwort.
	 * @param passwort Das Passwort im Klartext 
	 * @return hashed salted Passwort
	 */
	public String saltPassword(String passwort){
		
		String saltedPassword = passwort+""+ApplicationConstants.PASSWORD_SALT_VALUE;
		
		return saltedPassword;
	}
	
	/**
	 * Erstellt aus dem Adressbuch des Mobiltelefons eine Liste mit Contact User
	 * @param context Einen Context
	 * @return Eine Liste mit ContactUser
	 */
	public List<HandyContact> getNumbersFromContacts(Context context){
		
		// Hier muss das Telefonbuch nach den Nummer gesucht werden
		
		List<HandyContact> list = new ArrayList<HandyContact>();
		
		 // Run query

		Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI ,null, null, null, null); 
        
        cursor.moveToFirst();
        System.out.println(cursor.getCount());
        if(cursor.getCount()>0){
        	while(cursor.moveToNext()){
        		
        		String userId;
        		String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        		String number = null;
        		Integer hasPhone = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER)));
           		if(name != null && hasPhone == 1){
            		
           			userId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
           			Cursor phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, 
           				  ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ userId, null, null); 

           			while(phoneCursor.moveToNext()){       				
           					
           				number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
           				
           				if(number.length() >= 7){
           					
           					HandyContact user = new HandyContact(cleanNumber(number), name);
           					
           					list.add(user);
           				}
           				
           			}
           			phoneCursor.close();
           		}
           	}    	
        	cursor.close();
        }
              
		return list;
	}
	
	/**
	 * Reinigt eine Nummer in dem die Sonderzeichen und die Laendervorwahl entfernt <br/>
	 * werden
	 * @param number eine Handynummer als String
	 * @return die gesaeuberte Handynummer
	 */
	public String cleanNumber(String number){
		
		String cleandNumber = number;
		
		//Alle Sonderzeichen entfernen
		cleandNumber = number.replaceAll("[^0-9]", "");
		
		//Ländervorwahl entfernen 
		cleandNumber = cleandNumber.replaceFirst("^49", "0");	


		return cleandNumber;
	}
}
