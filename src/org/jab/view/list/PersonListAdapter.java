package org.jab.view.list;

import java.util.List;

import org.jab.model.contact.RosterContact;

import android.R.color;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PersonListAdapter extends ArrayAdapter<RosterContact>{


	private int resource;
	private LayoutInflater inflater;
	Context context;
	
	
	public PersonListAdapter(Context context, int textViewResourceId,
			List<RosterContact> objects) {
		super(context, textViewResourceId, objects);
		
		this.context = context;
		this.resource = textViewResourceId;
		inflater = LayoutInflater.from(context);
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		RosterContact rc = getItem(position);
		PersonListViewCache viewCache;
		
		if ( convertView == null ) {
            convertView = ( RelativeLayout ) inflater.inflate( resource, null );
            viewCache = new PersonListViewCache( convertView );
            convertView.setTag( viewCache );
		}
		else {
            convertView = ( RelativeLayout ) convertView;
            viewCache = ( PersonListViewCache ) convertView.getTag();
		}
		
		if(position%2==0){
			
			viewCache.getBackLayout(resource).setBackgroundColor(Color.parseColor("#66181818"));
		}
		
		TextView personName = viewCache.getName(resource);
		personName.setText(rc.getUsername());
		
		
		return convertView;
	}

}
