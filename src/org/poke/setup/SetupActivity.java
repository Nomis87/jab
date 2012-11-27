package org.poke.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.poke.helper.HelperFunctions;
import org.poke.main.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Diese Activity stellt Benuterschnittstellen für den Setup der <br/>
 * App zur Verfügung. Sie ruft nach dem Drücken des Buttons die <br/>
 * SetupTask auf.
 * @author Tobias Simon
 *
 */
public class SetupActivity extends Activity {
	
	//View Komponentent
	private Spinner countrySpinner;
	private EditText telNumber;
	private EditText username;
	private EditText password;
	private Button acceptButton;
	
	//Gibt die Abkürzung für das jeweilige Land
	private String shortCountry;	
	private Context context;
	
	//asynchrone task die für die Konfiguration sorgt
	SetupTask ait;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        
        _init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_setup, menu);
        return true;
    }
    
    /**
     * Initialisiert die View Komponentent und den Context.
     */
    private void _init(){
  
        context = this;
    	
    	//Initialisieren der EditTexte und Spinner
    	this.telNumber = (EditText) findViewById(R.id.telnummberEditText);
        this.username = (EditText) findViewById(R.id.usernameEditText);
        this.password = (EditText) findViewById(R.id.passwordEditText);
        this.countrySpinner = (Spinner) findViewById(R.id.countrySpinner);
        addItemsToSpinner();
        countrySpinnerListener();
        //  Hier muss noch eine Default Vlaue gesetzt werden
        //countrySpinner.setSelected(selected)
        this.acceptButton = (Button) findViewById(R.id.AgbConfirmButton);
        acceptButtonListener();
  
    }
    
    /**
     * Dynamisch die Länder für den Spinner setzen
     */
    private void addItemsToSpinner(){
    	
    	List<String> list = new ArrayList<String>();
    	
    	for(Locale locale : Locale.getAvailableLocales()){
    		
    		if(!locale.getDisplayCountry().equals("")){
    			
    			list.add(locale.getDisplayCountry());
    		}
    	}
    		
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	countrySpinner.setAdapter(adapter);
    	
    }
    
    
    /**
     * Reagieren wenn ein Element ausgewählt wurde
     */
    private void countrySpinnerListener(){
    	
   	
    	countrySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

				String country = (String) parent.getItemAtPosition(pos);
				shortCountry = HelperFunctions.getInstance().searchCountryCode(country);		
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
    	
    }

    /**
     * Reagiert auf den Tastendruck des Accept Buttons
     */
    private void acceptButtonListener(){
    	 ait =  new SetupTask(context, this);
    	
    	this.acceptButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				String[] arg = new String[4];
				
				arg[0] = telNumber.getText().toString();
				arg[1] = shortCountry;
				arg[2] = username.getText().toString();
				arg[3] = password.getText().toString();
				
				
				ait.execute(arg);
				
			}
		});
    }
    
}
