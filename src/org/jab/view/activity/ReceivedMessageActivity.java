package org.jab.view.activity;

import org.jab.control.storage.database.DbRosterRepository;
import org.jab.control.xmpp.XMPPConnectionHandler;
import org.jab.control.xmpp.XMPPRosterStorage;
import org.jab.main.R;
import org.jab.model.contact.RosterContact;
import org.jab.model.message.IncomingMessage;

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
public class ReceivedMessageActivity extends Activity {
	
	private TextView fromView;
	private Button playSoundButton;
	private TextView messageView;
	
	private SoundPool sp;
	private Integer soundId;
	private float volume;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_received_message);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		
		
		Bundle extras = getIntent().getExtras();
		String pokeSender = extras.getString("pokeSender");
		String pokeSound = extras.getString("pokeSound");
		String pokeMessage = extras.getString("pokeMessage");
		
		playSound(pokeSound);
		
		DbRosterRepository rosterRepo = new DbRosterRepository(this);
		
		for(RosterContact rc :rosterRepo.getAllRosterEntrys()){
			
			if(pokeSender.contains(rc.getJid())){
				
				pokeSender = rc.getUsername();
			}
		}
		
		this.fromView = (TextView) findViewById(R.id.activity_received_message_from_view);
		this.fromView.setText(pokeSender);
		this.playSoundButton = (Button) findViewById(R.id.activity_received_message_play_button);
		playSoundButtonListener(pokeSound);
		this.messageView = (TextView) findViewById(R.id.activity_received_message_message_view);
		this.messageView.setText(pokeMessage);
		

	}
	
	
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

	}
	
	private void playSoundButtonListener(String soundId){
		
		final String sound = soundId;
		
		this.playSoundButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				
				playSound(sound);
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
			
			soundId = sp.load(this, R.raw.standard_tor , 1);
			Log.d("TOR", sound);
		}
		if(sound.equals("ps:klopf")){
			
			soundId = sp.load(this, R.raw.standard_klopfsound, 1);
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
