package org.jab.view.tabBuilder;

import org.jab.main.R;
import org.jab.main.R.drawable;
import org.jab.view.activity.ContactsActivity;
import org.jab.view.activity.HistoryActivity;
import org.jab.view.activity.IndexActivity;
import org.jab.view.activity.NewInstantMessageActivity;
import org.jab.view.activity.NewMessageMainActivity;
import org.jab.view.activity.SoundsActivity;
import org.jab.view.activity.TimedMessageOverviewActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainTabBuilder {
	
	private Activity activity;
	private Button newMessageButton;
	
	private Button contactsButton;
	private LinearLayout leftRedBar;
	private LinearLayout rightRedBar;
	
	private Button historyButton;
	private LinearLayout leftBlueBar;
	private LinearLayout rightBlueBar;
	
	private Button soundsButton;
	private LinearLayout leftGreenBar;
	private LinearLayout rightGreenBar;
	
	private Button timedMessagesButton;
	private LinearLayout leftYellowBar;
	private LinearLayout rightYellowBar;
	
	public MainTabBuilder(Activity activity){
		
		this.activity = activity;
	}
	
	public void initTabs(){
		
    	this.newMessageButton = (Button) activity.findViewById(R.id.tabButtonNewMessage);
    	newMessageButtonListener();
    	this.contactsButton = (Button) activity.findViewById(R.id.tabButtonContacts);
    	this.leftRedBar = (LinearLayout) activity.findViewById(R.id.left_red_bar_button_layout);
    	this.rightRedBar = (LinearLayout) activity.findViewById(R.id.right_red_bar_button_layout);
    	contactButtonListener();
    	this.historyButton = (Button) activity.findViewById(R.id.tabButtonHistory);
    	this.leftBlueBar = (LinearLayout) activity.findViewById(R.id.left_blue_bar_button_layout);
    	this.rightBlueBar = (LinearLayout) activity.findViewById(R.id.right_blue_bar_button_layout);
    	historyButtonListener();
    	this.soundsButton = (Button) activity.findViewById(R.id.tabButtonSounds);
    	this.leftGreenBar = (LinearLayout) activity.findViewById(R.id.left_green_bar_button_layout);
    	this.rightGreenBar = (LinearLayout) activity.findViewById(R.id.right_green_bar_button_layout);
    	soundButtonListener();
    	this.timedMessagesButton = (Button) activity.findViewById(R.id.tabButtonTimedMessages);
    	this.leftYellowBar = (LinearLayout) activity.findViewById(R.id.left_yellow_bar_button_layout);
    	this.rightYellowBar = (LinearLayout) activity.findViewById(R.id.right_yellow_bar_button_layout);
    	timedMessagesButtonListener();
		
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
    	
    	this.contactsButton.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN:
	            	
	            	leftRedBar.setBackgroundResource(drawable.tabgroup_red_bar_repeat_activated);
	            	rightRedBar.setBackgroundResource(drawable.tabgroup_red_bar_repeat_activated);
	            	return false;

	            case MotionEvent.ACTION_UP:
	                
	            	leftRedBar.setBackgroundResource(drawable.tabgroup_red_bar_repeat);
	            	rightRedBar.setBackgroundResource(drawable.tabgroup_red_bar_repeat);
	            	return false;

	            }
				return false;
			}
		});
    	
    	this.contactsButton.setOnClickListener(new OnClickListener() {
    		
			public void onClick(View v) {
				
				Intent intent = new Intent(activity.getBaseContext(), ContactsActivity.class);
				activity.startActivity(intent);
				
			}
		});
    	
    }
    
    private void historyButtonListener(){
    	
    	this.historyButton.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN:
	            	
	            	leftBlueBar.setBackgroundResource(drawable.tabgroup_blue_bar_repeat_activated);
	            	rightBlueBar.setBackgroundResource(drawable.tabgroup_blue_bar_repeat_activated);
	            	return false;
	            	
	            case MotionEvent.ACTION_UP:
	                
	            	leftBlueBar.setBackgroundResource(drawable.tabgroup_blue_bar_repeat);
	            	rightBlueBar.setBackgroundResource(drawable.tabgroup_blue_bar_repeat);
	            	return false;
	            	
	            }
	            return false;
			}
		});	
    	
    	this.historyButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				Intent intent = new Intent(activity.getBaseContext(), HistoryActivity.class);
				activity.startActivity(intent);
			}
		});
    	
    }
    
    private void soundButtonListener(){
    	
    	this.soundsButton.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN:
	            	
	            	leftGreenBar.setBackgroundResource(drawable.tabgroup_green_bar_repeat_activated);
	            	rightGreenBar.setBackgroundResource(drawable.tabgroup_green_bar_repeat_activated);
	            	return false;
	            	
	            case MotionEvent.ACTION_UP:
	                
	            	leftGreenBar.setBackgroundResource(drawable.tabgroup_green_bar_repeat);
	            	rightGreenBar.setBackgroundResource(drawable.tabgroup_green_bar_repeat);
	            	return false;
	            	
	            }
	            return false;
			}
		});
    	
    	this.soundsButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				Intent intent = new Intent(activity.getBaseContext(), SoundsActivity.class);
				activity.startActivity(intent);
			}
		});
    	
    }
    
	private void timedMessagesButtonListener(){
		
		this.timedMessagesButton.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()) {
				
				case MotionEvent.ACTION_DOWN:
	            	
	            	leftYellowBar.setBackgroundResource(drawable.tabgroup_yellow_bar_repeat_activated);
	            	rightYellowBar.setBackgroundResource(drawable.tabgroup_yellow_bar_repeat_activated);
	            	return false;
	            	
	            case MotionEvent.ACTION_UP:
	                
	            	leftYellowBar.setBackgroundResource(drawable.tabgroup_yellow_bar_repeat);
	            	rightYellowBar.setBackgroundResource(drawable.tabgroup_yellow_bar_repeat);
	            	return false;
	            	
	            }
	            return false;
			}
		});
		
		this.timedMessagesButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				Intent intent = new Intent(activity.getBaseContext(), TimedMessageOverviewActivity.class);
				activity.startActivity(intent);
			}
		});
    	
    }
	
	
}
