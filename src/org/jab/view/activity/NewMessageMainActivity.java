package org.jab.view.activity;

import org.jab.main.R;
import org.jab.view.tabBuilder.ActionbarBuilder;
import org.jab.view.tabBuilder.MainTabBuilder;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewMessageMainActivity extends Activity {
	
	private final String TAG = "NewMessageMainActivity";
	
	private Context context;
	
	private Button instantMessage;
	private Button timedMessage;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.context = this;
        
        _initLayout();
        _initControl();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_new_message, menu);
        return true;
    }
    
    private void _initLayout(){
    	
        getWindow().setWindowAnimations(0);  
        
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        View mainView = getLayoutInflater().inflate(R.layout.activity_new_message_main, mainLayout, false);
        mainLayout.addView(mainView);
        
        
        RelativeLayout actionbar = (RelativeLayout) findViewById(R.id.actionbar);
        actionbar.setBackgroundColor(Color.parseColor("#181818"));
        
        LinearLayout splitline = (LinearLayout) findViewById(R.id.actionbar_splitline);
        splitline.setBackgroundColor(Color.parseColor("#5D5D5D"));
        
        TextView headline = (TextView) findViewById(R.id.main_headline);
        headline.setText("New Message");
        
        ActionbarBuilder ab = new ActionbarBuilder(this);
        ab.initTabs();
    }
    
    
    private void _initControl(){
	 	
    	this.instantMessage = (Button) findViewById(R.id.instantMessageButton);
    	instantMessageButtonListener();
    	this.timedMessage = (Button) findViewById(R.id.timedMessageButton);
    	timedMessageButtonListener();
    	
    }
    
    private void instantMessageButtonListener(){
    	
    	this.instantMessage.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				Intent intent = new Intent(context, NewInstantMessageActivity.class);
				context.startActivity(intent);
				
			}
		});
    	
    }
    
    private void timedMessageButtonListener(){
    	
    	this.timedMessage.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				Intent intent = new Intent(context, NewTimedMessageActivity.class);
				context.startActivity(intent);
				
			}
		});
    }
    
    
    
}
