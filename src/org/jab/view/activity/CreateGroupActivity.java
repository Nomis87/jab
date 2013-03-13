package org.jab.view.activity;

import java.util.ArrayList;
import java.util.List;

import org.jab.control.storage.database.DbRosterGroupRepository;
import org.jab.control.storage.database.DbRosterRepository;
import org.jab.control.storage.database.DbUserRepository;
import org.jab.control.xmpp.XMPPConnectionHandler;
import org.jab.control.xmpp.XMPPRosterStorage;
import org.jab.main.R;
import org.jab.model.contact.RosterContact;
import org.jab.model.contact.RosterContactGroup;
import org.jab.view.list.PersonListAdapter;
import org.jab.view.tabBuilder.ActionbarBuilder;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CreateGroupActivity extends Activity {
	
	private final String TAG = "CreateGroupActivity";
	
	private Context context;
	private boolean isGroupName = false;
	private EditText groupName;
	private TextView groupNameCounter;
	private AutoCompleteTextView addMember;
	
	private boolean isMember = false;
	private TextView groupMemberCount;
	private ListView memberListView;
	private Button createGroup;
	
	private List<RosterContact> memberList;
	private List<RosterContact> rosterList;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.context = this;
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
    	
    	this.groupName = (EditText) findViewById(R.id.activity_create_group_name);
    	this.groupNameCounter = (TextView) findViewById(R.id.activity_create_group_nameCounter);
    	groupNameCounter.setText("0/20");
    	setUpGroupNameCounter();
    	
    	this.addMember = (AutoCompleteTextView) findViewById(R.id.activity_create_group_addMember);
    	setUpAddMember();
    	
    	this.groupMemberCount = (TextView) findViewById(R.id.activity_create_group_groupMemberCount);
    	this.memberListView = (ListView) findViewById(R.id.activity_create_group_meberListView);
    	setUpMember();
    	
    	this.createGroup = (Button) findViewById(R.id.activity_create_group_createButton);
    	createGroupButtonListener();
    }
    
    private void setUpGroupNameCounter(){
    	
    	
    	this.groupName.addTextChangedListener(new TextWatcher() {
			
    		
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				

				groupNameCounter.setText(count+"/20");	
				
				if(count>0)
					isGroupName = true;

				else
					isGroupName = false;
				
			}

			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
		});
    	
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
							groupMemberCount.setText("Anzahl: "+memberList.size());
							
							if(memberList.size()>1)
								isMember = true;
							else
								isMember = false;
							
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
    		
    		this.memberListView.setAdapter(new PersonListAdapter(this, memberList));
    	}
    	
    }
    
    private void createGroupButtonListener(){
    	
    	this.createGroup.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				groupName.setHintTextColor(Color.GRAY);
				addMember.setHintTextColor(Color.GRAY);
				
				if(isMember && isGroupName){
					
					
					// In ne Task auslagern
					DbUserRepository userRepo = new DbUserRepository(context);
					
					RosterContactGroup group = new RosterContactGroup(memberList);
					group.setId(userRepo.readUser().getUserId()+"_"+groupName.getText());
					group.setGroupName(""+groupName.getText());
					DbRosterGroupRepository groupRepo = new DbRosterGroupRepository(context);		
					
					groupRepo.createGroup(group);
					
					XMPPRosterStorage.getInstance().addGroup(group, XMPPConnectionHandler.getInstance().getConnection());

					finish();
				}
				
				if(!isGroupName)
					groupName.setHintTextColor(Color.RED);
					
				if(!isMember)
					addMember.setHintTextColor(Color.RED);
				
			}
		});
    }
}
