package org.jab.control.message.timedSend;

import org.jab.control.storage.database.DbTimedMessagesRepository;
import org.jab.control.xmpp.XMPPConnectionHandler;
import org.jab.model.message.TimedOutgoingMessage;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

public class SendTimedMessageActivity extends Activity {
	
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
		
		int id = getIntent().getIntExtra("messageId", -1);
		
		if(id > 0){
			
			DbTimedMessagesRepository timedRepo = new DbTimedMessagesRepository(context);
			TimedOutgoingMessage message = timedRepo.findTimedMessageById(id);
			timedRepo.deleteTimedMessage(message);
			
			this.receiver = message.getReceiver();
			this.sound = message.getSound();
			this.message = message.getMessage();
			
			SendMessageTask sendMessageTask = new SendMessageTask();
			sendMessageTask.execute();
		}
		
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
