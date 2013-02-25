package org.jab.control.storage.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

public class ApplicationPreference {
	
	private SharedPreferences mPrefs;
	private Context context = null;
	
	public ApplicationPreference(Context context){
		
		this.context = context;
		
		if(context != null){
			setUpPreference();
		}
	}
	
	/**
	 * Get if this is Async
	 * @return returns false if this is asynchron 
	 */
	public boolean isSync(){
		
		return mPrefs.getBoolean("syncPref", false);
	}
	
	/**
	 * Set sync
	 */
	public void setSync(){
		
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putBoolean("syncPref", true);
		editor.commit();
	}
	
	/**
	 * Set Async
	 */
	public void setAsync(){
		
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putBoolean("syncPref", false);
		editor.commit();
	}
	
	
	//Die 2 können eventuell geloescht werden
	public void setAccountService(){
		
		SharedPreferences.Editor edit = mPrefs.edit();
		edit.putBoolean("accountService", true);
		edit.commit();
	}
	
	public boolean getAccountService(){
		
		return mPrefs.getBoolean("accountService", false);
	}
	
	 /**
     * get if this is the first run
     *
     * @return returns true, if this is the first run
     */
     public boolean getFirstRun() {
        return mPrefs.getBoolean("firstRun", true);
     }
     
     /**
     * store the first run
     */
     public void setRunned() {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putBoolean("firstRun", false);
        edit.commit();
     }
     
     
     /**
     * setting up preferences storage
     */
     private void setUpPreference() {

        mPrefs = context.getSharedPreferences("myAppPrefs", 0); //0 = mode private. only this app can read these preferences
     }
	
	
}
