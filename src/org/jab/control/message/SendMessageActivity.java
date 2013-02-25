package org.jab.control.message;

import java.util.concurrent.ExecutionException;

import org.jab.control.xmpp.XMPPConnectionHandler;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

public class SendMessageActivity extends Activity {
	
	private Context context;
	private String receiver;
	private String sound;
	private String message;
	private Activity sendMessageActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.context = this;
		sendMessageActivity = this;
		this.receiver = getIntent().getStringExtra("receiver");
		this.sound = getIntent().getStringExtra("sound");
		this.message = getIntent().getStringExtra("message");
		
		SendMessageTask sendMessageTask = new SendMessageTask();
		sendMessageTask.execute();
		
		
	}
	
	
	private class SendMessageTask extends AsyncTask<String, Integer, Boolean> {
		
		
		XMPPConnectionHandler handler;
		
		@Override
		protected void onPreExecute() {
			
			super.onPreExecute();
			handler = XMPPConnectionHandler.getInstance();
			
		}
		
		
		@Override
		protected Boolean doInBackground(String... params) {
				
			handler.sendJabMessage(receiver, sound, message, context);
			
			
			return null;
			
			
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			sendMessageActivity.finish();
		}

	}

}
