package org.poke.newPoke;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.poke.main.R;
import org.poke.message.timedSend.TimedAlarmManager;
import org.poke.object.contact.RosterContact;
import org.poke.object.message.TimedOutgoingMessage;
import org.poke.util.ApplicationConstants;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TimePicker;

public class NewTimedPokeActivity extends Activity {
	
private final String TAG = "NewPokeActivity";
	
	private Context context;
	
	private ArrayList<RosterContact> rosterContactList;
	
	private Spinner receiverSpinner;
	private Spinner soundSpinner;	
	private EditText pokeMessage;
	private DatePicker datePicker;
	private TimePicker timePicker;
	private Button sendButton;
	
	private RosterContact receiver = null;
	private String sound;
	private Long timeToSend = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_timed_poke);
        
        _init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_new_poke, menu);
        return true;
    }
    
    private void _init(){
    	 	
    	context = this;
    	
    	receiverSpinner = (Spinner) findViewById(R.id.tm_receiverSpinner);
    	addItemsToReceiverSpinner();
    	receiverSpinnerListener();
    	
    	soundSpinner = (Spinner) findViewById(R.id.tm_soundSpinner);
    	addItemsToSoundSpinner();
    	soundSpinnerListener();
    	
    	pokeMessage = (EditText) findViewById(R.id.tm_pokeMessageEditText);  
    	
    	datePicker = (DatePicker) findViewById(R.id.tm_datePicker);
    	setCurrentDateOnView();
    	
    	timePicker = (TimePicker) findViewById(R.id.tm_timePicker);
    	timePicker.setIs24HourView(true);
    	setCurrentTimeOnView();
    	
    	sendButton = (Button) findViewById(R.id.tm_sendButton);
    	sendButtonListener();
    	
    }
    
    /**
     * Methode welche die 
     */
    private void addItemsToReceiverSpinner(){
    	
    	getContactsFromDatabase();
    	
    	//Dewbug Methode hier werden die User Online vom Roster gezogen
//    	getContactsFromServer();
    	  	
    	List<String> nameList = new ArrayList<String>();
    	
    	for(RosterContact rs : rosterContactList){
    		
    		Log.d(TAG, "Username: "+rs.getUsername()+" , Jid: "+rs.getJid());
    		nameList.add(rs.getUsername());
    	}
    	
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.receiver_spinner_layout, nameList);
    	
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	receiverSpinner.setAdapter(adapter);
    	
    	
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
    	
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.receiver_spinner_layout, soundList);
    	
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
						TimedAlarmManager.getInstance().setTimeToSendTimer(getApplicationContext(), message);				
						NewTimedPokeActivity.this.finish();
					}
					else{
						
						//Make a Toast SendTime Must be in Future
						Log.d(TAG, "Die Zeit muss in der Zukunft liegen");
					}
					
				}
				
			}
		});
    }
    
    private void getContactsFromDatabase(){
    	
    	rosterContactList = new ArrayList<RosterContact>();
    	
    	SQLiteDatabase db = context.openOrCreateDatabase(ApplicationConstants.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
    	
    	Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_ROSTER, null);
    	c.moveToFirst();
    	if(c.getCount() > 0){
	    	do{
	    		
	    		RosterContact rc = new RosterContact();
	    		rc.setJid(c.getString(c.getColumnIndex("ro_id")));
	    		rc.setUsername(c.getString(c.getColumnIndex("ro_username")));
	    		rosterContactList.add(rc);
	    		
	    	}
	    	while(c.moveToNext());
    	}
    	c.close();
    	db.close();
    	
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
