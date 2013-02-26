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
     * setting up preferences storage
     */
     private void setUpPreference() {

        mPrefs = context.getSharedPreferences("myAppPrefs", 0); //0 = mode private. only this app can read these preferences
     }
     
     public void setContactObserverPref(boolean set){
    	 
    	SharedPreferences.Editor editor = mPrefs.edit();
 		editor.putBoolean("ContactObserver", set);
 		editor.commit();
     }
     
     public boolean getContactObserverPref(){
    	 
    	 return mPrefs.getBoolean("ContactObserver", false); 
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
	
	public void incrementAsyncCounter(){
		
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putInt("syncCounterPref", mPrefs.getInt("syncCounterPref", 0)+1);
		editor.commit();
	}
	
	public int getAsyncCounter(){
		
		return mPrefs.getInt("syncCounterPref", 0);
	}
	
	public void resetAsyncCounter(){
		
		SharedPreferences.Editor editor = mPrefs.edit();
		editor.putInt("syncCounterPref", 0);
		editor.commit();
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
     
     
     
	
	
}
