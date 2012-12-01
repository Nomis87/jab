package org.poke.index;

import org.poke.main.R;
import org.poke.message.PokeMessageService;
import org.poke.newPoke.NewPokeActivity;
import org.poke.xmpp.XMPPConnectionHandler;

import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

public class IndexActivity extends Activity {
	
	private final String TAG = "IndexActivity";
	
	private Context context;
	
	private Button myPokesButton;
	private Button newPokeButton;
	private Button contactListButton;
	private ToggleButton serviceStateToggleButton;
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
        serviceStateToggleButton = (ToggleButton) findViewById(R.id.toggleButtonSetService);
		myPokesButton = (Button) findViewById(R.id.buttonMyPokes);
		myPokesButtonListener();
		newPokeButton = (Button) findViewById(R.id.buttonNewPoke);
		newPokeButtonListener();
		contactListButton = (Button) findViewById(R.id.buttonContactList);
		contactListButtonListener();
		
		serviceStateToggleButtonListener();
//		soundStateToggleButton = (ToggleButton) findViewById(R.id.toggleButtonSound);
//		soundStateToggleButtonListener();
    	
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
    
    private void contactListButtonListener(){
    	
    	contactListButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
    }
    
    //TODO Empfangen von Pokes deaktivieren 
    private void serviceStateToggleButtonListener(){
    	
    	
    	if(XMPPConnectionHandler.getInstance().getConnection().isAuthenticated()){
    		
    		myPokesButton.setVisibility(View.VISIBLE);
    		newPokeButton.setVisibility(View.VISIBLE);
    		contactListButton.setVisibility(View.VISIBLE);
        	serviceStateToggleButton.setChecked(true);
    	}
    	else{
    		
    		myPokesButton.setVisibility(View.GONE);
    		newPokeButton.setVisibility(View.GONE);
    		contactListButton.setVisibility(View.GONE);
    	}
    	
    	serviceStateToggleButton.setOnClickListener(new OnClickListener() {
			
    		Intent pokeMessageService = new Intent(getApplicationContext(), PokeMessageService.class);
    
			public void onClick(View v) {
				
				
				PokeStateTask pst = new PokeStateTask(context, 
						XMPPConnectionHandler.getInstance().getConnection().isAuthenticated());
				pst.execute();
				
				if(XMPPConnectionHandler.getInstance().getConnection().isAuthenticated()){
					
					myPokesButton.setVisibility(View.GONE);
		    		newPokeButton.setVisibility(View.GONE);
		    		contactListButton.setVisibility(View.GONE);
	
				}
				else{
					
					myPokesButton.setVisibility(View.VISIBLE);
		    		newPokeButton.setVisibility(View.VISIBLE);
		    		contactListButton.setVisibility(View.VISIBLE);

				}
				
			}
		});
    	
    }
    
    
}
