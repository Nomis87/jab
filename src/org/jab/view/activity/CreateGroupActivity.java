package org.jab.view.activity;

import java.util.ArrayList;
import java.util.List;

import org.jab.control.storage.database.DbRosterRepository;
import org.jab.main.R;
import org.jab.model.contact.RosterContact;
import org.jab.view.list.PersonListAdapter;
import org.jab.view.tabBuilder.ActionbarBuilder;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CreateGroupActivity extends Activity {
	
	private final String TAG = "CreateGroupActivity";
	
	private EditText goupName;
	private AutoCompleteTextView addMember;
	private ListView memberListView;
	
	private List<RosterContact> memberList;
	private List<RosterContact> rosterList;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        _initLayout();
        _initControls();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_create_group, menu);
        return true;
    }
    
    /**
     * Intitialiseren des Layouts
     */
    private void _initLayout(){
    	
    	getWindow().setWindowAnimations(0);
    	
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        View view = getLayoutInflater().inflate(R.layout.activity_create_group, mainLayout, false);
        mainLayout.addView(view);
        
        
        RelativeLayout actionbar = (RelativeLayout) findViewById(R.id.actionbar);
        actionbar.setBackgroundColor(Color.parseColor("#AA0B0B"));
        
        LinearLayout splitline = (LinearLayout) findViewById(R.id.actionbar_splitline);
        splitline.setBackgroundColor(Color.parseColor("#CC0000"));
        
        TextView headline = (TextView) findViewById(R.id.main_headline);
        headline.setText("Create Group");
        
        ActionbarBuilder ab = new ActionbarBuilder(this);
        ab.initTabs();
    }
    
    private void _initControls(){
    	
    	this.goupName = (EditText) findViewById(R.id.activity_create_group_name);
    	this.addMember = (AutoCompleteTextView) findViewById(R.id.activity_create_group_addMember);
    	setUpAddMember();
    	this.memberListView = (ListView) findViewById(R.id.activity_create_group_meberListView);
    	setUpMember();
    }
    
    private void setUpAddMember(){
    	
    	ArrayList<String> nameList = new ArrayList<String>();
    	
    	DbRosterRepository rosterRepo = new DbRosterRepository(this);
    	this.rosterList = rosterRepo.getAllRosterEntrys();
    	for(RosterContact rc : rosterList){
    		
    		nameList.add(rc.getUsername());
    	}
    	
    	ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, nameList);
    	
    	this.addMember.setThreshold(1);
    	this.addMember.setAdapter(nameAdapter);
    	this.addMember.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				TextView textView = (TextView) view;
				for(RosterContact rc : rosterList){
					
					if(rc.getUsername().equals(textView.getText())){
						
						if(!memberList.contains(rc)){
							
							memberList.add(rc);
							memberListView.invalidateViews();
						}
						addMember.setText("");
					}
					
				}
				
			}
		});
    	
    }
    
    private void setUpMember(){
    	
    	memberList = new ArrayList<RosterContact>();
    	
    	if(memberList != null){
    		
    		this.memberListView.setAdapter(new PersonListAdapter(this, R.layout.list_contact_person_row_item, memberList));
    	}
    	
    }
}
