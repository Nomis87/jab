package org.poke.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

public class AppFirstRun implements Parcelable {
	
	private SharedPreferences mPrefs;
	private Context context = null;
	
	public AppFirstRun(Context context){
		
		this.context = context;
		
		if(context != null){
			firstRunPreferences();
		}
	}
	
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
     private void firstRunPreferences() {

        mPrefs = context.getSharedPreferences("myAppPrefs", 0); //0 = mode private. only this app can read these preferences
     }

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
