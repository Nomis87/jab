package org.jab.view.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jab.control.message.SendMessageActivity;
import org.jab.control.message.timedSend.TimedAlarmManager;
import org.jab.control.storage.database.DbRosterRepository;
import org.jab.control.util.ApplicationConstants;
import org.jab.main.R;
import org.jab.main.R.layout;
import org.jab.main.R.menu;
import org.jab.model.contact.RosterContact;
import org.jab.model.message.TimedOutgoingMessage;
import org.jab.view.list.TimedMessageAdapter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TimePicker;
import android.widget.Toast;

public class NewTimedMessageActivity extends NewMessageMainActivity {
	
	private Context context;
	
	private List<RosterContact> rosterContactList;
	private Spinner receiverSpinner;
	private Spinner soundSpinner;	
	private EditText pokeMessage;
	private DatePicker datePicker;
	private TimePicker timePicker;
	private Button sendButton;
	
	private RosterContact receiver = null;
	private String standardUser;
	private String sound;
	private Long timeToSend = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.context = this;
        _initLayout();
        _initControl();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_new_timed_message, menu);
        return true;
    }
    
    
    private void _initLayout(){
    	
        getWindow().setWindowAnimations(0);  
        
        RelativeLayout newMessageLayout = (RelativeLayout) findViewById(R.id.new_message_main_layout);
        View messageView = getLayoutInflater().inflate(R.layout.activity_new_timed_message, newMessageLayout, false);
        newMessageLayout.addView(messageView); 
        
    }
    
    private void _initControl(){
	 	
    	DbRosterRepository rosterRepo = new DbRosterRepository(this);
    	this.rosterContactList = rosterRepo.getAllRosterEntrys();
    	receiverSpinner = (Spinner) findViewById(R.id.tm_receiverSpinner);
    	addItemsToReceiverSpinner();
    	receiverSpinnerListener();
    	
    	soundSpinner = (Spinner) findViewById(R.id.tm_soundSpinner);
    	addItemsToSoundSpinner();
    	soundSpinnerListener();
    	
    	datePicker = (DatePicker) findViewById(R.id.tm_datePicker);
    	setCurrentDateOnView();
    	
    	timePicker = (TimePicker) findViewById(R.id.tm_timePicker);
    	timePicker.setIs24HourView(true);
    	setCurrentTimeOnView();
    	
    	pokeMessage = (EditText) findViewById(R.id.tm_pokeMessageEditText);  
    	
    	sendButton = (Button) findViewById(R.id.tm_sendButton);
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
    	
    	final Activity thisActivity = this;
    	
    	sendButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
		    	int year = datePicker.getYear();
		    	int month = datePicker.getMonth();
		    	int day = datePicker.getDayOfMonth();
		    	int hour = timePicker.getCurrentHour();
		    	int minute = timePicker.getCurrentMinute();
		    	
				timeToSend = generateTimeToSend(year, month, day, hour, minute);
				Date current = new Date();
				
				if(receiver!=null && timeToSend != null){
					
					if(timeToSend > current.getTime()){
						
						TimedOutgoingMessage message = new TimedOutgoingMessage(receiver.getJid(), sound, pokeMessage.getText().toString(), timeToSend);
						TimedAlarmManager.getInstance().setTimeToSendTimer(getApplicationContext(), message, false);
						Intent intent = new Intent(context, IndexActivity.class);
						context.startActivity(intent);
						thisActivity.finish();
					}
					else{
						
						Toast.makeText(context, "Zeit muss in der Zukunft liegen", Toast.LENGTH_LONG).show();
					}
					
				}
				
			}
		});
    }
    
    private void setCurrentDateOnView(){
    	
    	final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		datePicker.init(year, month, day, null);
    }
    
    private void setCurrentTimeOnView(){
    	
    	final Calendar c = Calendar.getInstance();
		int hours = c.get(Calendar.HOUR_OF_DAY);
		int minutes = c.get(Calendar.MINUTE);
		
		timePicker.setCurrentHour(hours);
		timePicker.setCurrentMinute(minutes);
		
    }
    
    private Long generateTimeToSend(int year, int month, int day, int hour, int minute){
    	
    	
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(year, month, day, hour, minute);
    	
    	return calendar.getTimeInMillis();
    }
}
