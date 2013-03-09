package org.jab.view.activity;

import org.jab.main.R;
import org.jab.view.tabBuilder.ActionbarBuilder;
import org.jab.view.tabBuilder.MainTabBuilder;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class HistoryActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        _initLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_history, menu);
        return true;
    }
    
 private void _initLayout(){
    	
        getWindow().setWindowAnimations(0);    
        
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        View view = getLayoutInflater().inflate(R.layout.activity_history, mainLayout, false);
        mainLayout.addView(view);
        
        
        RelativeLayout actionbar = (RelativeLayout) findViewById(R.id.actionbar);
        actionbar.setBackgroundColor(Color.parseColor("#1067A5"));
        
        LinearLayout splitline = (LinearLayout) findViewById(R.id.actionbar_splitline);
        splitline.setBackgroundColor(Color.parseColor("#0099CC"));
        
        TextView headline = (TextView) findViewById(R.id.main_headline);
        headline.setText("Messages");
        
        ActionbarBuilder ab = new ActionbarBuilder(this);
        ab.initTabs();
    }
}
