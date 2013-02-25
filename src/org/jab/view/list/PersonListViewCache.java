package org.jab.view.list;

import org.jab.main.R;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PersonListViewCache {
	
	private View baseView;
	private TextView name;
	private RelativeLayout backLayout;
	
	public PersonListViewCache(View baseView) {
		this.setBaseView(baseView);
	}

	public View getBaseView() {
		return baseView;
	}

	public void setBaseView(View baseView) {
		this.baseView = baseView;
	}

	public TextView getName(int resource) {
			
		if ( name == null ) {
            name = ( TextView ) baseView.findViewById(R.id.contact_person_name_view);
      }
		
		return name;
	}
	
	public RelativeLayout getBackLayout(int resource){
		
		if(backLayout == null){
			
			backLayout = (RelativeLayout) baseView.findViewById(R.id.person_row_item_background);
		}
		
		return backLayout;
	}

	
	

}
