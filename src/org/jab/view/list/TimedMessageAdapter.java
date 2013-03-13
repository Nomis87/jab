package org.jab.view.list;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.jab.control.message.timedSend.TimedAlarmManager;
import org.jab.control.storage.database.DbRosterRepository;
import org.jab.control.storage.database.DbTimedMessagesRepository;
import org.jab.main.R;
import org.jab.model.message.TimedOutgoingMessage;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TimedMessageAdapter extends ArrayAdapter<TimedOutgoingMessage> {
	
	private final String TAG = "TimedMessageAdapter";
	private TimedMessageAdapter adapter;
	private TimedOutgoingMessage message;
	
	public TimedMessageAdapter(Context context,  List<TimedOutgoingMessage> objects) {
		super(context, -1, objects);
		
		this.adapter = this;
	}
	

	public View getView(int position, View convertView, ViewGroup parent) {
		
		View returnView = convertView;
		
		if(returnView == null){
			
			returnView = LayoutInflater.from(getContext()).inflate(
					R.layout.timed_message_item, null);
			
			 returnView.setTag(new ViewHolder(returnView));
		}
		
		
		ViewHolder holder = (ViewHolder) returnView.getTag();
		
		if(position%2==0){
			
			holder.background.setBackgroundColor(Color.parseColor("#66181818"));
		}
		message = getItem(position);
		
		DbRosterRepository rosterRepo = new DbRosterRepository(getContext());
		
		holder.id.setText(String.valueOf(message.getId()));
		holder.timeToSendView.setText(convertTimeToString(message.getTimeToSend()));
		holder.receiverView.setText(rosterRepo.findRosterEntryById(message.getReceiver()).getUsername());
		holder.messageView.setText(message.getMessage());
		holder.deleteButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				DbTimedMessagesRepository repo = new DbTimedMessagesRepository(getContext());
				LinearLayout llb = (LinearLayout) v.getParent();
				LinearLayout llo = (LinearLayout) llb.getParent();
				LinearLayout llt = (LinearLayout) llo.getChildAt(0);
	            TextView tv = (TextView)llt.getChildAt(0);
	            int id = Integer.valueOf(tv.getText().toString());
	            TimedOutgoingMessage message = repo.findTimedMessageById(id);
	            repo.deleteTimedMessage(message);
	            adapter.notifyDataSetChanged();//or
	            
	            TimedAlarmManager.getInstance().deleteTimer(getContext(), message);
				Log.d(TAG, "Aktuelle Pos: "+tv.getText());
				
				
			}
		});
		
		return returnView;
	}
	
	/**
	 * View Hodler Pattern to optimize the Performence
	 * @author Tobias
	 *
	 */
	private static class ViewHolder{
		
		LinearLayout background;
		TextView id;
		TextView timeToSendView;
		TextView receiverView;
		TextView messageView;
		Button deleteButton;
		
		public ViewHolder(View contentView){
			
			background = (LinearLayout) contentView.findViewById(R.id.timed_message_list_background);
			id = (TextView) contentView.findViewById(R.id.timedMessageList_id);
			timeToSendView = (TextView) contentView.findViewById(R.id.timedMessageList_TimeToSend);
			receiverView = (TextView) contentView.findViewById(R.id.timedMessageList_Receiver);
			messageView = (TextView) contentView.findViewById(R.id.timedMessageList_Message);
			deleteButton = (Button) contentView.findViewById(R.id.timedMessageList_deleteButton);
		}
	}
	
	private String convertTimeToString(long timestamp){
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
		return sdf.format(calendar.getTime());
	}
	
}
