package org.poke.newPoke;

import org.poke.xmpp.XMPPConnectionHandler;

import android.os.AsyncTask;

public class SendPokeTask extends AsyncTask<String, Integer, Boolean> {
	
	
	private String receiver;
	private String sound;
	private String message;
	XMPPConnectionHandler handler;
	
	public SendPokeTask(String receiver, String sound, String message){
		
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
			
		handler.sendPokeMessage(receiver, sound, message);
		
		
		return null;
		
		
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

}
