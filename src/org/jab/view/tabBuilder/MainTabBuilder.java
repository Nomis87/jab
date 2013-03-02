package org.jab.view.tabBuilder;

import org.jab.main.R;
import org.jab.view.activity.ContactsActivity;
import org.jab.view.activity.HistoryActivity;
import org.jab.view.activity.IndexActivity;
import org.jab.view.activity.NewInstantMessageActivity;
import org.jab.view.activity.NewMessageMainActivity;
import org.jab.view.activity.SoundsActivity;
import org.jab.view.activity.TimedMessageOverviewActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainTabBuilder {
	
	private Activity activity;
	private Button homeButton;
	private Button newMessageButton;
	private Button contactsButton;
	private Button historyButton;
	private Button soundsButton;
	private Button timedMessagesButton;
	
	public MainTabBuilder(Activity activity){
		
		this.activity = activity;
	}
	
	public void initTabs(){
		
		this.homeButton = (Button) activity.findViewById(R.id.tabButtonHome);
    	homeButtonListener();
    	this.newMessageButton = (Button) activity.findViewById(R.id.tabButtonNewMessage);
    	newMessageButtonListener();
    	this.contactsButton = (Button) activity.findViewById(R.id.tabButtonContacts);
    	contactButtonListener();
    	this.historyButton = (Button) activity.findViewById(R.id.tabButtonHistory);
    	historyButtonListener();
    	this.soundsButton = (Button) activity.findViewById(R.id.tabButtonSounds);
    	soundButtonListener();
    	this.timedMessagesButton = (Button) activity.findViewById(R.id.tabButtonTimedMessages);
    	timedMessagesButtonListener();
		
	}
	
	
	
	private void homeButtonListener(){
    	
    	this.homeButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				Intent intent = new Intent(activity.getBaseContext(), IndexActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				activity.startActivity(intent);
			}
		});
    	
    }
    
    private void newMessageButtonListener(){
    	
    	this.newMessageButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				Intent intent = new Intent(activity.getBaseContext(), NewInstantMessageActivity.class);
				activity.startActivity(intent);
			}
		});
    }
    
    private void contactButtonListener(){
    	
    	this.contactsButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				Intent intent = new Intent(activity.getBaseContext(), ContactsActivity.class);
				activity.startActivity(intent);
				
			}
		});
    	
    }
    
    private void historyButtonListener(){
    	
    	this.historyButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				Intent intent = new Intent(activity.getBaseContext(), HistoryActivity.class);
				activity.startActivity(intent);
			}
		});
    	
    }
    
    private void soundButtonListener(){
    	
    	this.soundsButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				Intent intent = new Intent(activity.getBaseContext(), SoundsActivity.class);
				activity.startActivity(intent);
			}
		});
    	
    }
    
	private void timedMessagesButtonListener(){
		
		this.timedMessagesButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				Intent intent = new Intent(activity.getBaseContext(), TimedMessageOverviewActivity.class);
				activity.startActivity(intent);
			}
		});
    	
    }
	
	
}
