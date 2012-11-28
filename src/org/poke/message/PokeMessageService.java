package org.poke.message;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class PokeMessageService extends Service {
	
	private final String TAG = "PokeMessageService";
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		String pokeSender = intent.getStringExtra("pokeSender");
//		String pokeSound = intent.getStringExtra("pokeSound");
		String pokeMessage = intent.getStringExtra("pokeMessage");
		
		Log.d(TAG, "From: ["+pokeSender+"] Message: ["+pokeMessage+"]");
		
		Toast.makeText(this.getApplicationContext(),
				"From: ["+pokeSender+"] Message: ["+pokeMessage+"]", Toast.LENGTH_LONG).show();
		
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		
		super.onDestroy();
	}

}
