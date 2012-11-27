package org.poke.newPoke;

import java.util.ArrayList;
import java.util.List;

import org.poke.main.R;
import org.poke.main.R.layout;
import org.poke.main.R.menu;
import org.poke.object.RosterContact;
import org.poke.util.ApplicationConstants;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class NewPokeActivity extends Activity {
	
	private Context context;
	
	private Spinner receiverSpinner;
	private Spinner soundSpinner;	
	private EditText pokeMessage;
	private Button sendButton;
	
	private RosterContact receiver = null;
	
	private ArrayList<RosterContact> rosterContactList;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_poke);
        
        _init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_new_poke, menu);
        return true;
    }
    
    private void _init(){
    	 	
    	context = this;
    	
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
    	
    	rosterContactList = new ArrayList<RosterContact>();
    	
    	SQLiteDatabase db = context.openOrCreateDatabase(ApplicationConstants.DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
    	
    	Cursor c = db.rawQuery("SELECT * FROM "+ApplicationConstants.DB_TABLE_ROSTER, null);
    	c.moveToFirst();
    	do{
    		
    		RosterContact rc = new RosterContact();
    		rc.setJid(c.getString(c.getColumnIndex("ro_id")));
    		rc.setUsername(c.getString(c.getColumnIndex("ro_username")));
    		rosterContactList.add(rc);
    		
    	}
    	while(c.moveToNext());
    	
    	c.close();
    	db.close();
    	
    	List<String> nameList = new ArrayList<String>();
    	
    	for(RosterContact rs : rosterContactList){
    		
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
    	
    }
    
    private void soundSpinnerListener(){
    	
    }
    
    private void sendButtonListener(){
    	
    	sendButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				if(receiver!=null){
					
					SendPokeTask poke = new SendPokeTask(receiver, "test", pokeMessage.getText().toString());
					poke.execute();
					
				}
				
			}
		});
    }
}
