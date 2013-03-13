package org.jab.control.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jab.model.contact.HandyContact;
import org.jivesoftware.smack.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

/**
 * Singelton Class<br/>
 * Enthält diverse Hiflsfunktion welche in der Apllication genutzt werden.
 * @author Tobias
 *
 */
public class HelperFunctions {
	
	private static HelperFunctions instance = null;
	
	private static String TAG = "HelperFunctions";
	
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
	public List<HandyContact> getNumbersFromContacts(Context context, String standardCountry){
		
		// Hier muss das Telefonbuch nach den Nummer gesucht werden
		
		List<HandyContact> list = new ArrayList<HandyContact>();
		
		 // Run query

		Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI ,null, null, null, null);
		
		String prevNumber = "first";
		
        if(cursor.getCount()>0){
        	while(cursor.moveToNext()){
        		
        		// Only if the Contact has a TelNumber
        		Integer hasPhone = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER)));
           		if(hasPhone == 1){
           			
           			String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            		String number = null;
            		
           			int userId = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
                    
                    // Version holen
                    int version = 0;
                    String[] projection = new String[] {RawContacts.VERSION};
                    Cursor rawCursor = context.getContentResolver().query(RawContacts.CONTENT_URI ,projection, 
                    		RawContacts.CONTACT_ID + "=" + userId, null, null);
            		//Version Lesen		
            		if (rawCursor != null && rawCursor.moveToFirst()) {
                        // Just return the first one.
            			version =  rawCursor.getInt(rawCursor.getColumnIndex(RawContacts.VERSION));
                    }
            		
            		rawCursor.close();
           			
            		// Phone Number holen
            		Cursor phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, 
             				  ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ userId, null, null); 
           			while(phoneCursor.moveToNext()){       				
           					
           				int phoneType = phoneCursor.getInt(phoneCursor.getColumnIndex(Phone.TYPE));
           				if(Phone.TYPE_MOBILE == phoneType){
           					
           					number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
           					
           					
           					if(number.length() >= 7){
           						
           						if(!cleanNumber(number).equals(prevNumber)){
           							
           							if(name == null){
    							
           								name = number;
           							}
           							
//           							Log.d(TAG, "Name: "+name);
//           							Log.d(TAG, "Number: "+cleanNumber(number));
//           							Log.d(TAG, "PrevNumber: "+cleanNumber(prevNumber));
           							prevNumber = number;
           							
           							//Country Setzen
           							String country;
           							String testCountry = stripCountryNumber(number, context);
           							if(testCountry != null) country = testCountry;
           							else country = standardCountry;
           							
           							//Für den Roster
           							//HandyContact user = new HandyContact(cleanNumber(number), name);
           							HandyContact user = new HandyContact(userId, cleanNumber(number), name, country, version);

           							list.add(user);
           							break;
           						}
           					}
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
		cleandNumber = cleandNumber.replaceFirst("^49", "");
		
		//Führende null entfernen
		cleandNumber = cleandNumber.replaceFirst("^0", "");

		return cleandNumber;
	}
	
	/**
	 * Pruefen ob zur zeit ein Telefonat stattfindet. 
	 * @param context
	 * @return
	 */
	public static boolean isCallActive(Context context){
		   AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		   if(manager.getMode()==AudioManager.MODE_IN_CALL){
		         return true;
		   }
		   else{
		       return false;
		   }
	}
	
	public Document parseCountryCodeXml(Context context){
    		
		Document countryCodeXml = null;
		
    	AssetManager am = context.getAssets();
		InputStream is = null;
		try {
			is = am.open("country_code.xml");
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    	
    	try{
    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    		dbf.setNamespaceAware(false);
    		dbf.setValidating(false);
    		DocumentBuilder db = dbf.newDocumentBuilder();
    		countryCodeXml = db.parse(is);
    	}
    	catch(Exception e){
    		
    	}	
    	
    	return countryCodeXml;
    }
    
	/**
	 * Methode welche das Ländervorwahl prüft falls die Nummer eine Besitztz
	 * @param number
	 * @param context
	 * @return
	 */
	public String stripCountryNumber(String number, Context context){
		
		String returnCountry = null;
		Document coundtryCode = parseCountryCodeXml(context);
		
		NodeList nodeList = coundtryCode.getElementsByTagName("*");
		
		for(int i=0; i<nodeList.getLength(); i++){
			
			Node node = nodeList.item(i);
			
			if(node.getTextContent().length()==1){
				
				if(number.substring(0,0).equals(node.getTextContent())) return node.getTextContent();
			}
			
			if(node.getTextContent().length()==2){
				
				if(number.substring(0,1).equals(node.getTextContent())) return node.getTextContent();
			}
			
			if(node.getTextContent().length()==3){
				
				if(number.substring(0,2).equals(node.getTextContent())) return node.getTextContent();
			}
			
			if(node.getTextContent().length()==4){
				
				if(number.substring(0,3).equals(node.getTextContent())) return node.getTextContent();
			}
		}
		
		return returnCountry;
	}
	
}

