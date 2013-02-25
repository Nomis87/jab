package org.jab.view.activity;

import org.jab.main.R;

import android.os.Bundle;
import android.app.Activity;
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
        
        TextView tv = (TextView) findViewById(R.id.main_headline);
        tv.setText("History");
        
        LinearLayout lyLeft = (LinearLayout) findViewById(R.id.left_blue_bar_button_layout);
        lyLeft.setBackgroundResource(R.drawable.tabgroup_blue_bar_repeat_activated);
        
        LinearLayout lyRight = (LinearLayout) findViewById(R.id.right_blue_bar_button_layout);
        lyRight.setBackgroundResource(R.drawable.tabgroup_blue_bar_repeat_activated);
        
        TabBuilder tb = new TabBuilder(this);
        tb.initTabs();	
    }
}
