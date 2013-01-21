package org.poke.index;

import org.poke.main.R;
import org.poke.newPoke.NewPokeActivity;
import org.poke.newPoke.NewTimedPokeActivity;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class IndexActivity extends Activity {
	
	private final String TAG = "IndexActivity";
	
	private Context context;
	
	private SeekBar volumeFader;
	private Button myPokesButton;
	private Button newPokeButton;
	private Button newTimedPokeButton;
	
	private AudioManager audioManager = null;
//	private ToggleButton soundStateToggleButton;

	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        
        _init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_index, menu);
        return true;
    }
    
    private void _init(){
    	
    	//Init Context variable
        context = this;
    	
    	//Init Buttons + Listeners
        //serviceStateToggleButton = (ToggleButton) findViewById(R.id.toggleButtonSetService);
        volumeFader = (SeekBar) findViewById(R.id.volumeFader);
        volumeFaderListener();
		myPokesButton = (Button) findViewById(R.id.buttonMyPokes);
		myPokesButtonListener();
		newPokeButton = (Button) findViewById(R.id.buttonNewPoke);
		newPokeButtonListener();
		newTimedPokeButton = (Button) findViewById(R.id.buttonContactList);
		newTimedPokeButtonListener();
    	
    }
    
    private void volumeFaderListener(){
    	
    	this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    	
    	audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    	
    	volumeFader.setMax(audioManager
    	        .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
    	//Set the progress with current Media Volume
    	volumeFader.setProgress(audioManager
    	        .getStreamVolume(AudioManager.STREAM_MUSIC));
    	
    	volumeFader.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
			}
		});
    }
    
    private void myPokesButtonListener(){
    	
    	myPokesButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
    	
    	
    }
    
    private void newPokeButtonListener(){
    	
    	newPokeButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				Intent intent = new Intent(context, NewPokeActivity.class);
				context.startActivity(intent);
				
			}
		});
    }
    
    private void newTimedPokeButtonListener(){
    	
    	newTimedPokeButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				Intent intent = new Intent(context, NewTimedPokeActivity.class);
				context.startActivity(intent);
				
			}
		});
    }
    
   
    
    
}
