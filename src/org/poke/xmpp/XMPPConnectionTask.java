package org.poke.xmpp;

import org.jivesoftware.smack.XMPPException;
import org.poke.database.DbUserRepository;
import org.poke.object.User;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class XMPPConnectionTask extends AsyncTask<Boolean, Integer, Boolean> {
	
	private final String TAG = "PokeStateTask";
	
	public static final int SERVER_CONNECT = 1;
	public static final int SERVER_DISCONNECT = 2;
	
	private Context context;
	
	private int status;
	
//	private ProgressDialog waitSpinner;
	
	public XMPPConnectionTask(Context context, int status){
		
		this.context = context;
		this.status = status;
	}
	
	
	protected void onPreExecute() {
		super.onPreExecute();
		
//		waitSpinner = new ProgressDialog(context);
//		waitSpinner.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		waitSpinner.setMessage("Poke wird aktiviert !!!");
//		waitSpinner.show();
	}
	
	
	@Override
	protected Boolean doInBackground(Boolean... params) {
		
		XMPPConnectionHandler handler = XMPPConnectionHandler.getInstance();
		
		if(this.status == SERVER_CONNECT){
			
			if(!handler.getConnection().isAuthenticated()){
						
				DbUserRepository userRepository = new DbUserRepository(context);
				
				User user = userRepository.readUser();
			
				try {
					handler.login(user.getUserId(), user.getPassword());
					handler.setMessageReceiver();
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					Log.d(TAG, "Bereits eingeloggt");
				}
			}
			else{
				
				Log.d(TAG, "Bereits eingeloggt");
			}
		}
		
		else if(this.status == SERVER_DISCONNECT){
			
			if(handler.getConnection().isAuthenticated()){
				
				handler.disconect();
			}
			else{
				
				Log.d(TAG, "Bereits ausgeloggt");
			}
			
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		//waitSpinner.cancel();
	}

}
