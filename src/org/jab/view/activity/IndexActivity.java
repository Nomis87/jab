package org.jab.view.activity;

import org.jab.control.xmpp.XMPPConnectionHandler;
import org.jab.control.xmpp.XMPPService;
import org.jab.main.R;
import org.jab.view.tabBuilder.MainTabBuilder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SlidingDrawer;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class IndexActivity extends Activity {
	
	
	private final String TAG = "IndexActivity";
	
	private Context context;
	
	private SeekBar volumeFader;
	private AudioManager audioManager = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        
        this.context = this;

        
        _initLayout();
        _initVolumeFader();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return false;
    }
    
    private void _initLayout(){
    	
        getWindow().setWindowAnimations(0);    
        
        
        MainTabBuilder tb = new MainTabBuilder(this);
        tb.initTabs();	
    }
    
    private void _initVolumeFader(){
    	
    	volumeFader = (SeekBar) findViewById(R.id.frequency_slider);
    	
    	this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    	audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    	volumeFader.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
    	//Set the progress with current Media Volume
    	volumeFader.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    	
    	volumeFader.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
			}
		});
    }
    

 


}
