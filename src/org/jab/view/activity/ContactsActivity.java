package org.jab.view.activity;

import java.util.ArrayList;
import java.util.List;

import org.jab.control.storage.database.DbRosterRepository;
import org.jab.main.R;
import org.jab.model.contact.RosterContact;
import org.jab.view.list.PersonListAdapter;
import org.jab.view.tabBuilder.ActionbarBuilder;
import org.jab.view.tabBuilder.MainTabBuilder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ContactsActivity extends Activity {
	
	private final String TAG = "ContactsActivity";
	private Context context;
	private Button addGroupButton;
	private ListView personListView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        _initLayout();
        _initPersonListView();
        _initGroupListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_contacts, menu);
        return true;
    }
    
    /**
     * Intitialiseren des Layouts
     */
    private void _initLayout(){
    	
    	getWindow().setWindowAnimations(0);
    	
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        View view = getLayoutInflater().inflate(R.layout.activity_contacts, mainLayout, false);
        mainLayout.addView(view);
        
        
        RelativeLayout actionbar = (RelativeLayout) findViewById(R.id.actionbar);
        actionbar.setBackgroundColor(Color.parseColor("#AA0B0B"));
        
        LinearLayout splitline = (LinearLayout) findViewById(R.id.actionbar_splitline);
        splitline.setBackgroundColor(Color.parseColor("#CC0000"));
        
        TextView headline = (TextView) findViewById(R.id.main_headline);
        headline.setText("Contacts");
        
        ActionbarBuilder ab = new ActionbarBuilder(this);
        ab.initTabs();
    }
    
    /**
     * Initialisieren der Gruppen anzeige
     */
    private void _initGroupListView(){
    	
    	this.addGroupButton = (Button) findViewById(R.id.activity_contacts_addGroup_button);
    	addGroupButtonListener();
    	
    	
    }
    
    /**
     * Listener f�r den AddGroup Button
     */
    private void addGroupButtonListener(){
    	
    	
    	this.addGroupButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				Intent intent = new Intent(context, CreateGroupActivity.class);
				startActivity(intent);
				
			}
		});
    	
    }
    
    /**
     * Initialisieren der Einzel Personen anzeige
     */
	private void _initPersonListView(){
    	
    	DbRosterRepository rosterRepo = new DbRosterRepository(this);
    	List<RosterContact> contacts = rosterRepo.getAllRosterEntrys();
    	
    		
    	this.personListView = (ListView) findViewById(R.id.activity_contacts_person_list);
        this.personListView.setAdapter(new PersonListAdapter(context, contacts));
        personListViewOnItemClickListener();
    	
    	
    }
	
	/**
	 * Personen Cklcik listener
	 */
	private void personListViewOnItemClickListener(){
		
		personListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				RosterContact rc = (RosterContact) parent.getAdapter().getItem(position);
				
				Intent intent = new Intent(context, NewInstantMessageActivity.class);
				intent.putExtra("userId", rc.getJid());
				
				context.startActivity(intent);
			}
		});
	}
	
	
	
    
    
}
