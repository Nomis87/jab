package org.jab.view.tabBuilder;

import org.jab.main.R;
import org.jab.view.activity.IndexActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActionbarBuilder {
	
	private Activity activity;
	private Button returnButton;
	
	public ActionbarBuilder(Activity activity) {
		
		this.activity = activity;
	}
	
	public void initTabs(){
		
		this.returnButton = (Button) activity.findViewById(R.id.actionButtonReturn);
		returnButtonListener();
		
	}
	
	private void returnButtonListener(){
		
		
		this.returnButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
					
//				Intent intent = new Intent(activity.getBaseContext(), IndexActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				activity.startActivity(intent);
				
				activity.finish();
			}
		});
		
	}

}
