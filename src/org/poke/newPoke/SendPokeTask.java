package org.poke.newPoke;

import org.poke.object.RosterContact;
import org.poke.xmpp.XMPPConnectionHandler;

import android.os.AsyncTask;

public class SendPokeTask extends AsyncTask<String, Integer, Boolean> {
	
	
	private RosterContact receiver;
	private String sound;
	private String message;
	XMPPConnectionHandler handler;
	
	public SendPokeTask(RosterContact receiver, String sound, String message){
		
		this.receiver = receiver;
		this.sound = sound;
		this.message = message;
	}
	
	
	@Override
	protected void onPreExecute() {
		
		super.onPreExecute();
		handler = XMPPConnectionHandler.getInstance();
		
	}
	
	
	@Override
	protected Boolean doInBackground(String... params) {
			
		handler.sendPoke(receiver.getJid(), sound, message);
		
		
		return null;
		
		
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

}
