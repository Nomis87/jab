package org.jab.view.list;

import java.util.List;

import org.jab.control.xmpp.XMPPConnectionHandler;
import org.jab.main.R;
import org.jab.model.contact.RosterContact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PersonListAdapter extends ArrayAdapter<RosterContact>{


	
	public PersonListAdapter(Context context, List<RosterContact> objects) {
		super(context, 0, objects);
		
	
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View returnView = convertView;
		
		if(returnView == null){
			
			returnView = LayoutInflater.from(getContext()).inflate(
					R.layout.list_contact_person_row_item, null);
			
			 returnView.setTag(new ViewHolder(returnView));
		}
		
		ViewHolder holder = (ViewHolder) returnView.getTag();
		
		RosterContact rc = getItem(position);
		
		if(XMPPConnectionHandler.getInstance().isUserOnline(rc.getJid())){
			
			holder.personImage.setImageResource(R.drawable.person_online);
		}
		else{
			
			holder.personImage.setImageResource(R.drawable.person_offline);
		}
		
		holder.personName.setText(rc.getUsername());
		
		
		return returnView;
	}
	
	private class ViewHolder{
		
		ImageView personImage;
		TextView personName;
		
		public ViewHolder(View baseView) {
			
			this.personImage = (ImageView) baseView.findViewById(R.id.contact_person_image);
			this.personName = (TextView) baseView.findViewById(R.id.contact_person_name_view);
		}
		
	}

}
