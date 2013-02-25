package org.jab.control.message.timedSend;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.jab.control.storage.database.DbTimedMessagesRepository;
import org.jab.main.R;
import org.jab.model.message.TimedOutgoingMessage;

import android.content.Context;
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
	
	TimedOutgoingMessage message;
	
	public TimedMessageAdapter(Context context, List<TimedOutgoingMessage> objects) {
		super(context, -1, objects);
	}
	

	public View getView(int position, View convertView, ViewGroup parent) {
		
		View returnView = convertView;
		
		if(returnView == null){
			
			returnView = LayoutInflater.from(getContext()).inflate(
					R.layout.timed_message_item, null);
			
			 returnView.setTag(new ViewHolder(returnView));
		}
		
		ViewHolder holder = (ViewHolder) returnView.getTag();
		message = getItem(position);
		
		holder.timeToSendView.setText(convertTimeToString(message.getTimeToSend()));
		holder.receiverView.setText(message.getReceiver());
		holder.messageView.setText(message.getMessage());
		holder.deleteButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				//DbTimedMessagesRepository repo = new DbTimedMessagesRepository(getContext());
				LinearLayout ll = (LinearLayout)v.getParent();
	            TextView tv = (TextView)ll.getChildAt(0);
	            Integer pos = (Integer) v.getTag();
				Log.d(TAG, "Aktuelle Pos: "+pos);
				//int aktPos = Integer.parseInt(v.getTag().toString());		
//				TimedOutgoingMessage m =  getItem(aktPos);
				
//				remove(m);
//				repo.deleteTimedMessage(m);
//				TimedAlarmManager.getInstance().setAktMessageId(null);
//				TimedAlarmManager.getInstance().setAktTimeToSend(null);
//				TimedAlarmManager.getInstance().deleteTimedMessage(getContext().getApplicationContext(), repo.getNextTimedMessage());
//				notifyDataSetChanged();
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
		
		int pos;
		TextView timeToSendView;
		TextView receiverView;
		TextView messageView;
		Button deleteButton;
		
		public ViewHolder(View contentView){
			
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
