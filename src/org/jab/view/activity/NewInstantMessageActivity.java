package org.jab.view.activity;

import java.util.ArrayList;
import java.util.List;

import org.jab.control.message.SendMessageActivity;
import org.jab.control.storage.database.DbRosterRepository;
import org.jab.main.R;
import org.jab.model.contact.RosterContact;
import org.jab.view.tabBuilder.MainTabBuilder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class NewInstantMessageActivity extends NewMessageMainActivity {
	
	
	private final String TAG = "NewInstantMessageActivity";
	
	private Context context;
	
	private List<RosterContact> rosterContactList;
	
	private Spinner receiverSpinner;
	private Spinner soundSpinner;	
	private EditText pokeMessage;
	private Button sendButton;
	
	private RosterContact receiver = null;
	private String sound;
	private String standardUser;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.context = this;
        Intent intent = getIntent();
        standardUser = intent.getStringExtra("userId");
		
		_initLayout();
		_initControl();
	}
	
	
	private void _initLayout(){
    	
        getWindow().setWindowAnimations(0);  
        
        RelativeLayout newMessageLayout = (RelativeLayout) findViewById(R.id.new_message_main_layout);
        View messageView = getLayoutInflater().inflate(R.layout.activity_new_message, newMessageLayout, false);
        newMessageLayout.addView(messageView);
        
        
    }
	
	 private void _initControl(){
		 	
	    	DbRosterRepository rosterRepo = new DbRosterRepository(this);
	    	this.rosterContactList = rosterRepo.getAllRosterEntrys();
	    	receiverSpinner = (Spinner) findViewById(R.id.receiverSpinner);
	    	addItemsToReceiverSpinner();
	    	receiverSpinnerListener();
	    	
	    	soundSpinner = (Spinner) findViewById(R.id.soundSpinner);
	    	addItemsToSoundSpinner();
	    	soundSpinnerListener();
	    	
	    	pokeMessage = (EditText) findViewById(R.id.pokeMessageEditText);  
	    	
	    	sendButton = (Button) findViewById(R.id.sendButton);
	    	sendButtonListener();
	    	
	 }
	 
	 private void addItemsToReceiverSpinner(){
	    	
	    	int defaultPos = 0;
	    	int counter = 0;
	    	  	
	    	List<String> nameList = new ArrayList<String>();
	    	
	    	for(RosterContact rs : this.rosterContactList){
	    		
	    		nameList.add(rs.getUsername());
	    		
	    		if(standardUser != null){
	    			
	    			if(rs.getJid().equals(this.standardUser)){
	    				
	    				defaultPos = counter;
	    			}
	    			counter ++;
	    		}
	    		Log.d(TAG, "Username: "+rs.getUsername()+" , Jid: "+rs.getJid());
	    		
	    	}
	    	
	    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.receiver_spinner_layout, nameList);
	    	
	    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	
	    	receiverSpinner.setAdapter(adapter);
	    	receiverSpinner.setSelection(defaultPos);
	    	
	    	
	    }
	    
	    private void receiverSpinnerListener(){
	    	
	    	receiverSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				public void onItemSelected(AdapterView<?> parent, View arg1,
						int pos, long arg3) {
					
					for(RosterContact cu : rosterContactList){
						
						if(cu.getUsername().equals((String) parent.getItemAtPosition(pos))){
							
							receiver = cu;
						}
					}
					
				}

				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});
	    	
	    }
	    
	    private void addItemsToSoundSpinner(){
	    	
	    	List<String> soundList = new ArrayList<String>();
	    	soundList.add("TOR");
	    	soundList.add("KlopfKlopf");
	    	
	    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.receiver_spinner_layout, soundList);
	    	
	    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    	
	    	soundSpinner.setAdapter(adapter);
	    	
	    }
	    
	    private void soundSpinnerListener(){
	    	
	    	soundSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				public void onItemSelected(AdapterView<?> parent, View arg1,
						int pos, long arg3) {
					
					if(parent.getItemAtPosition(pos).equals("TOR")){
						
						sound = "ps:tor";
					}
					
					if(parent.getItemAtPosition(pos).equals("KlopfKlopf")){
						
						sound = "ps:klopf";
					}
					
				}

				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
			});
	    	
	    }
	    
	    private void sendButtonListener(){
	    	
	    	final Activity newMessageActivity = this;
	    	
	    	sendButton.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					
					if(receiver!=null){
						
						
						Intent intent = new Intent(context, SendMessageActivity.class);
						intent.putExtra("receiver", receiver.getJid());
						intent.putExtra("sound", sound);
						intent.putExtra("message", pokeMessage.getText().toString());
						
						startActivity(intent);
						
						newMessageActivity.finish();
					}
					
				}
			});
	    }

}
