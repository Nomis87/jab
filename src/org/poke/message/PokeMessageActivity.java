package org.poke.message;

import org.poke.main.R;
import org.poke.object.contact.RosterContact;
import org.poke.object.message.IncomingMessage;
import org.poke.xmpp.XMPPConnectionHandler;
import org.poke.xmpp.XMPPRosterStorage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Activity welche zur Darstellung der Message dient.
 * @author Tobias
 *
 */
public class PokeMessageActivity extends Activity {
	
	private IncomingMessage message;
	
	private boolean pause = false;
	
	SoundPool sp;
	Integer soundId;
	private float volume;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		
		
		Bundle extras = getIntent().getExtras();
		String pokeSender = extras.getString("pokeSender");
		String pokeSound = extras.getString("pokeSound");
		String pokeMessage = extras.getString("pokeMessage");
		
		XMPPRosterStorage rs = new XMPPRosterStorage();
		
		RosterContact rc = rs.getEntry(pokeSender.replace("/Smack", ""), XMPPConnectionHandler.getInstance().getConnection());
		
		
		if(!rc.getUsername().equals("")){
			pokeSender = rc.getUsername();
		}
		
		message = new IncomingMessage(pokeSender, pokeSound, pokeMessage);
		
	}
	
	@Override
	protected void onStart() {
		
		super.onStart();
		
		if(!message.getSound().equals("") && !pause){
			
			showDialog();
			playSound(message.getSound());
		}
	}
	
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		pause = true;
	}
	
	@SuppressLint("NewApi")
	/**
	 * Methode zum anzeigen des Dialogs
	 */
	private void showDialog(){
		
		
		final Dialog pokeDialog = new Dialog(PokeMessageActivity.this);
		
		pokeDialog.setContentView(R.layout.poke_dialog);
		pokeDialog.setCanceledOnTouchOutside(false);
		
		TextView sender = (TextView) pokeDialog.findViewById(R.id.receiveUserTextView);
		sender.setText(this.message.getSender());
		
		TextView message = (TextView) pokeDialog.findViewById(R.id.pokeMessageTextView);
		message.setText(this.message.getMessage());
		
		Button okButton = (Button) pokeDialog.findViewById(R.id.dialogOkButton);
		
		pokeDialog.show();
		
		okButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				pokeDialog.cancel();
				closeActivity();
			}
		});
		
		
		
	}
	
	/**
	 * Methode zum abspielen des Sounds
	 * @param sound Der Sound welcher abgespielt werden soll als String.
	 */
	private void playSound(String sound){
		
		sp= new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		
		soundId = null;
		
		if(sound.equals("ps:tor")){
			
			soundId = sp.load(this, R.raw.tor , 1);
			Log.d("TOR", sound);
		}
		if(sound.equals("ps:klopf")){
			
			soundId = sp.load(this, R.raw.klopfsound, 1);
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
	
	private void closeActivity(){
		
		this.finish();
	}
	
}
