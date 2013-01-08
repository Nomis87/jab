package org.poke.message;

import org.jivesoftware.smack.RosterEntry;
import org.poke.main.R;
import org.poke.object.PokeMessage;
import org.poke.object.RosterContact;
import org.poke.util.ApplicationConstants;
import org.poke.xmpp.RosterStorage;
import org.poke.xmpp.XMPPConnectionHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PokeMessageActivity extends Activity {
	
	private String pokeSender;
	private String pokeSound;
	private String pokeMessage;
	
	private boolean pause = false;
	
	SoundPool sp;
	Integer soundId;
	private float volume;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		
		Bundle extras = getIntent().getExtras();
		pokeSender = extras.getString("pokeSender");
		pokeSound = extras.getString("pokeSound");
		pokeMessage = extras.getString("pokeMessage");
		
		RosterStorage rs = new RosterStorage();
		
		RosterContact rc = rs.getEntry(pokeSender.replace("/Smack", ""), XMPPConnectionHandler.getInstance().getConnection());
		
		if(rc != null){
			pokeSender = rc.getUsername();
		}
		
		
	}
	
	@Override
	protected void onStart() {
		
		super.onStart();
		
		if(!pokeSound.equals("") && !pause){
			
			showDialog();
			playSound(pokeSound);
		}
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		pause = true;
	}
	
	@SuppressLint("NewApi")
	private void showDialog(){
		
		
		Log.d("help", pokeSender);
		Log.d("help", pokeMessage);
		
//		FragmentManager fm = getFragmentManager();
//		DialogFragment pokeDialog = new DialogFragment(){
//			
//			@Override
//			public Dialog onCreateDialog(Bundle savedInstanceState) {
//				
//				AlertDialog.Builder builder = new AlertDialog.Builder(PokeMessageActivity.this);
//				
//				builder.setMessage("From: ["+pokeSender+"], Message: ["+pokeMessage+"]")
//					.setPositiveButton("ok", new DialogInterface.OnClickListener() {
//						
//						public void onClick(DialogInterface dialog, int which) {
//							
//							sp = null;
//							PokeMessageActivity.this.finish();
//						}
//					});
//				
//				
//				
//				return builder.create();
//			}
//			
//		};
//		pokeDialog.show(fm, "Dialog");
		
		Dialog pokeDialog = new Dialog(PokeMessageActivity.this);
		
		pokeDialog.setContentView(R.layout.poke_dialog);
		
		TextView sender = (TextView) pokeDialog.findViewById(R.id.receiveUserTextView);
		sender.setText(pokeSender);
		
		TextView message = (TextView) pokeDialog.findViewById(R.id.pokeMessageTextView);
		message.setText(pokeMessage);
		
		Button okButton = (Button) pokeDialog.findViewById(R.id.dialogOkButton);
		
		pokeDialog.show();
		
		okButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				PokeMessageActivity.this.finish();
			}
		});
		
		
		
	}
	
	private void playSound(String sound){
		
		sp= new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		
		soundId = null;
		
		if(sound.equals("ps:tor")){
			
			soundId = sp.load(this, R.raw.tor , 1);
			Log.d("TOR", sound);
		}
		
		if(soundId != null){
			
			Log.d("SoundID", "Soundid != null");
			
			AudioManager am =(AudioManager) this.getSystemService(AUDIO_SERVICE);
			
			float actVolume = (float) am.getStreamVolume(AudioManager.STREAM_MUSIC);
			float macVolume = (float) am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			volume = actVolume/macVolume;	
			
			sp.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
					
					
					sp.play(soundId, volume, volume, 1, 0, 1f);
					
				}
			});
			
		}
	}
}
