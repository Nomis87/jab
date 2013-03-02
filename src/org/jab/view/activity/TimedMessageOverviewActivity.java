package org.jab.view.activity;

import java.util.Collections;
import java.util.List;

import org.jab.control.storage.database.DbTimedMessagesRepository;
import org.jab.main.R;
import org.jab.model.message.TimedOutgoingMessage;
import org.jab.view.list.TimedMessageAdapter;
import org.jab.view.tabBuilder.MainTabBuilder;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TimedMessageOverviewActivity extends Activity {
	
	private ListView listView;
	private TimedMessageAdapter adapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        _initLayout();
        _initControl();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_timed_message_overview, menu);
        return true;
    }
    
 private void _initLayout(){
    	
        getWindow().setWindowAnimations(0);    
        
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        View view = getLayoutInflater().inflate(R.layout.activity_timed_message_overview, mainLayout, false);
        mainLayout.addView(view);
        
        TextView tv = (TextView) findViewById(R.id.main_headline);
        tv.setText("Übersicht");
        
        LinearLayout lyLeft = (LinearLayout) findViewById(R.id.left_yellow_bar_button_layout);
        lyLeft.setBackgroundResource(R.drawable.tabgroup_yellow_bar_repeat_activated);
        
        LinearLayout lyRight = (LinearLayout) findViewById(R.id.right_yellow_bar_button_layout);
        lyRight.setBackgroundResource(R.drawable.tabgroup_yellow_bar_repeat_activated);
        
        MainTabBuilder tb = new MainTabBuilder(this);
        tb.initTabs();	
    }
 
 
 	private void _initControl(){
 	
 		DbTimedMessagesRepository repo = new DbTimedMessagesRepository(this);
 		this.listView = (ListView) findViewById(R.id.timedMessagesList);
 		List<TimedOutgoingMessage> objects = repo.getAllTimedMessages();
 		//sort(objects);
 		adapter = new TimedMessageAdapter(this, objects);
 		this.listView.setAdapter(adapter);
 		this.listView.setFocusable(false);
 		this.listView.setFocusableInTouchMode(false);
 		this.listView.setClickable(false);
 	
	}
 	
 	private void sort(List<TimedOutgoingMessage> objects){
		
		boolean sort;
		
		do{
			
			sort = true;
			
			for(int i=1; i<objects.size(); i++){
				
				if(objects.get(i).getTimeToSend()>objects.get(i+1).getTimeToSend()){
					
					Collections.swap(objects, i, i+1);
					sort = false;
				}
			}
				
		}
		while(!sort);
	}
 	

}
