package org.jab.view.activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jab.control.setup.InitAccountTask;
import org.jab.control.util.HelperFunctions;
import org.jab.main.R;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Diese Activity stellt Benuterschnittstellen für den Setup der <br/>
 * App zur Verfügung. Sie ruft nach dem Drücken des Buttons die <br/>
 * SetupTask auf.
 * @author Tobias Simon
 *
 */
public class SetupAccountActivity extends Activity {
	
	//View Komponentent
	private Spinner countrySpinner;
	private TextView countryCode;
	private TextView numberErrorMessage;
	private EditText telNumber;
	private TextView passwordErrorMessage;
	private EditText password;
	private CheckBox agbCheck;
	private Button acceptButton;
	
	//Gibt die Abkürzung für das jeweilige Land
	private Context context;
	
	//asynchrone task die für die Konfiguration sorgt
	InitAccountTask ait;
	
	//Ländervorwahl
	private Document countryCodeXml;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account);
        
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
    	
        this.countryCodeXml = HelperFunctions.getInstance().parseCountryCodeXml(this);
        
    	//Initialisieren der EditTexte und Spinner
        this.countrySpinner = (Spinner) findViewById(R.id.countrySpinner);
        addItemsToSpinner();
        countrySpinnerListener();
        this.countryCode = (TextView) findViewById(R.id.countryCode);
        this.numberErrorMessage = (TextView) findViewById(R.id.activity_setup_number_error_message);
    	this.telNumber = (EditText) findViewById(R.id.telnummberEditText);
    	this.passwordErrorMessage = (TextView) findViewById(R.id.activity_setup_password_error_message);
        this.password = (EditText) findViewById(R.id.passwordEditText);
        passwordInputFilter();
        this.agbCheck = (CheckBox) findViewById(R.id.agbCheck);
        //  Hier muss noch eine Default Vlaue gesetzt werden
        //countrySpinner.setSelected(selected)
        this.acceptButton = (Button) findViewById(R.id.AgbConfirmButton);
        acceptButtonListener();
  
    }
    
    /**
     * Die Länder für den Spinner setzen
     */
    private void addItemsToSpinner(){
    	
    	int defaultPos = 1;
    	int counter = 0;
    	
    	ArrayList<String> list = new ArrayList<String>();
    	
    	for(Locale locale : Locale.getAvailableLocales()){
    		
    		if(!locale.getDisplayCountry().equals("")){
    			
    			list.add(locale.getDisplayCountry());
    			if(locale.getDisplayCountry().equals(Locale.getDefault().getDisplayCountry())){
    				
    				defaultPos = counter;
    			}
    			counter++;
    		}
    	}
    		
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	countrySpinner.setAdapter(adapter);
    	countrySpinner.setSelection(defaultPos);
    }
    
    
    /**
     * Reagieren wenn ein Element ausgewählt wurde
     */
    private void countrySpinnerListener(){
    	
   	
    	countrySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				
				((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
				((TextView) parent.getChildAt(0)).setTextSize(18);
				String country = (String) parent.getItemAtPosition(pos);
				String shortCountry = HelperFunctions.getInstance().searchCountryCode(country);	
				
				NodeList nodeList = countryCodeXml.getElementsByTagName(shortCountry);
				Node node = nodeList.item(0);
				countryCode.setText(node.getTextContent());
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
    	
    }
    
    /**
     * Input Filter zum validieren der Number
     */
    private void numberInputFilter(){
    	
    	
    	this.telNumber.setFilters(new InputFilter[]{new InputFilter() {
			
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				// TODO Auto-generated method stub
				return null;
			}
		}});
    	
    }
    
    private void passwordInputFilter(){
    	
    	
    	
    	this.password.addTextChangedListener(new TextWatcher() {
			
    		private boolean check= false;
    		
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			  
				
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			public void afterTextChanged(Editable s) {
				
				if(this.check == false){
					
					check = textValidationHelper(s);
				}
				
				
				if(s.length()<6 || !check){
					
					passwordErrorMessage.setTextColor(Color.RED);
					passwordErrorMessage.setText("not Valid");
				}
				else{
					
					passwordErrorMessage.setTextColor(Color.GREEN);
					passwordErrorMessage.setText("Valid");
				}
				
			}
		});	
    	
    }
    
    private boolean textValidationHelper(Editable s){
    	
    	boolean ret = false;
    	
    	for(int i=0; i< s.length(); i++){
    		
    		if(s.charAt(i) =='0'||s.charAt(i) =='1'||s.charAt(i) =='2'||s.charAt(i) =='3'||s.charAt(i) =='4'
    				||s.charAt(i) =='5'||s.charAt(i) =='6'||s.charAt(i) =='7'||s.charAt(i) =='8'||s.charAt(i) =='9'){
    			
    			
    			ret = true;
    		}
    		
    	}
    	
    	return ret;
    }

    /**
     * Reagiert auf den Tastendruck des Accept Buttons
     */
    private void acceptButtonListener(){
    	 ait =  new InitAccountTask(context, this);
    	
    	this.acceptButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				if(agbCheck.isChecked()){
					
					String[] arg = new String[3];
					
					arg[0] = HelperFunctions.getInstance().cleanNumber(telNumber.getText().toString());
					arg[1] = countryCode.getText().toString();
					arg[2] = password.getText().toString();
					
					ait.execute(arg);
				}
				else{
					
					agbNotCheckedToast();
				}
				
				
				
			}
		});
    }
    
    private void agbNotCheckedToast(){
    	
    	Toast.makeText(context, "Die Agbs müssen akzeptiert werden", Toast.LENGTH_LONG).show();
    }
    
}
