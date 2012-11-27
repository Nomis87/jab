package org.poke.index;

import org.jivesoftware.smack.XMPPException;
import org.poke.helper.AppFirstRun;
import org.poke.util.ApplicationConstants;
import org.poke.xmpp.XMPPConnectionHandler;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class PokeStateTask extends AsyncTask<Boolean, Integer, Boolean> {
	
	private Context context;
	private boolean check;
	private ProgressDialog waitSpinner;
	
	public PokeStateTask(Context context, boolean check){
		
		this.context = context;
		this.check = check;
	}
	
	
	protected void onPreExecute() {
		super.onPreExecute();
		
		waitSpinner = new ProgressDialog(context);
		waitSpinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		waitSpinner.setMessage("Poke wird aktiviert !!!");
		waitSpinner.show();
	}
	
	
	@Override
	protected Boolean doInBackground(Boolean... params) {
		
		XMPPConnectionHandler handler = XMPPConnectionHandler.getInstance();
		
		if(!check){
			
			SQLiteDatabase db = context.openOrCreateDatabase(ApplicationConstants.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
			Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_USER, null);		
			
			c.moveToFirst();
			String userId = c.getString(c.getColumnIndex("us_id"));
			String password = c.getString(c.getColumnIndex("us_password"));
			
			c.close();
			db.close();
				
			
			try {
				handler.login(userId, password);
				handler.pokeMessageReceiver();
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		else{
			handler.disconect();
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		waitSpinner.cancel();
	}

}
