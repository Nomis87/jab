package org.jab.view.list;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.jab.control.storage.database.DbIOMessagesRepository;
import org.jab.control.storage.database.DbRosterRepository;
import org.jab.main.R;
import org.jab.main.R.drawable;
import org.jab.model.message.AbstractMessage;
import org.jab.model.message.IncomingMessage;
import org.jab.model.message.OutgoingMessage;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class IOMessageListAdapter extends ArrayAdapter<AbstractMessage> {
	
	private final String TAG = "IOMessageListAdapter";
	private AbstractMessage aMessage;
	private String dateHolder = "";
	private SoundPool sp;
	private Integer soundId;
	private float volume;
	private ArrayAdapter<AbstractMessage> adapter;
	List<AbstractMessage> objects;
	
	
	public IOMessageListAdapter(Context context, List<AbstractMessage> objects) {
		super(context, 0, objects);
		this.objects = objects;
		adapter = new ArrayAdapter<AbstractMessage>(context, 0, objects);
	}
	
	public boolean areAllItemsEnabled() 
    { 
            return false; 
    } 
    public boolean isEnabled(int position) 
    { 
            return false; 
    } 
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View returnView = convertView;
		
		if(returnView == null){
			
			returnView = LayoutInflater.from(getContext()).inflate(
					R.layout.list_messages_row_item, null);
			
			 returnView.setTag(new ViewHolder(returnView));
		}
		
		ViewHolder holder = (ViewHolder) returnView.getTag();
		aMessage = getItem(position);
			
		Calendar c = convertDateTime(aMessage.getTime());
		DateFormat dfDate = new SimpleDateFormat("dd/MM/yy");
		
		if(!dateHolder.equals(dfDate.format(c.getTime()))){
			
			holder.selector.setVisibility(View.VISIBLE);
			holder.selector.setBackgroundColor(Color.parseColor("#181818"));
			this.dateHolder = dfDate.format(c.getTime());
			holder.date.setText(dateHolder);
			holder.date.setVisibility(View.VISIBLE);
			Log.d(TAG, "Date: "+dateHolder);
		}

		
		DateFormat dfTime = new SimpleDateFormat("HH:mm");
		DbRosterRepository rosterRepo = new DbRosterRepository(getContext());
		
		if(aMessage instanceof OutgoingMessage){
			
			OutgoingMessage oMessage = (OutgoingMessage) aMessage;
			
			holder.background.setBackgroundResource(drawable.jabs_out_message_bg_two);
			holder.time.setText(dfTime.format(c.getTime())+" Uhr");
			holder.rsContact.setText("TO: "+rosterRepo.findRosterEntryById(oMessage.getReceiver()).getUsername());
			holder.message.setText(oMessage.getMessage());
		}
		
		else if(aMessage instanceof IncomingMessage){
			
			IncomingMessage iMessage = (IncomingMessage) aMessage;
			
			holder.background.setBackgroundResource(drawable.jabs_in_message_bg_two);
			holder.time.setText(dfTime.format(c.getTime())+" Uhr");
			holder.rsContact.setText("FROM: "+iMessage.getSender());
			holder.message.setText(iMessage.getMessage());
		}
		
		holder.id.setText(String.valueOf(aMessage.getId()));
		
		holder.playButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				playSound(aMessage.getSound());
			}
		});
		
		holder.deleteButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				DbIOMessagesRepository repo = new DbIOMessagesRepository(getContext());
				LinearLayout llb = (LinearLayout) v.getParent();
				TextView tv = (TextView) llb.getChildAt(0);
				Integer id = Integer.valueOf(tv.getText().toString());
				Log.d(TAG, "id: "+id);
				
				repo.deleteMessage(id);

				clear();
				for(AbstractMessage a : repo.getAllMessages()){
					
					add(a);
				}
				dateHolder = "";
				adapter.notifyDataSetChanged();
			}
		});
		
		return returnView;
	}
	
	private Calendar convertDateTime(long timestamp){
		
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timestamp);
		
		return c;
	}
	
	
	/**
	 * View Hodler Pattern to optimize the Performence
	 * @author Tobias
	 *
	 */
	private class ViewHolder{
		
		TextView id;
		RelativeLayout selector;
		TextView date;
		LinearLayout background;
		TextView rsContact;
		TextView time;
		TextView message;
		Button playButton;
		Button deleteButton;
		
		public ViewHolder(View baseView) {
			
			id = (TextView) baseView.findViewById(R.id.message_row_item_idView);
			selector = (RelativeLayout) baseView.findViewById(R.id.message_row_item_selector);
			date = (TextView) baseView.findViewById(R.id.message_row_item_selector_date);
			background = (LinearLayout) baseView.findViewById(R.id.message_row_item_item);
			rsContact = (TextView) baseView.findViewById(R.id.message_row_item_rs_contact);
			time = (TextView) baseView.findViewById(R.id.message_row_item_time);
			message = (TextView) baseView.findViewById(R.id.message_row_item_message);
			playButton = (Button) baseView.findViewById(R.id.message_row_item_playButton);
			deleteButton = (Button) baseView.findViewById(R.id.message_row_item_deleteButton);
		}
			
	}
	
	private void playSound(String sound){
	
		sp= new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		
		soundId = null;
		
		if(sound.equals("ps:tor")){
			
			soundId = sp.load(getContext(), R.raw.standard_tor , 1);
			Log.d("TOR", sound);
		}
		if(sound.equals("ps:klopf")){
			
			soundId = sp.load(getContext(), R.raw.standard_klopfsound, 1);
		}
		
		if(soundId != null){
			
			Log.d("SoundID", "Soundid != null");
			AudioManager am =(AudioManager) getContext().getSystemService(getContext().AUDIO_SERVICE);
			
			
			float actVolume = (float) am.getStreamVolume(AudioManager.STREAM_MUSIC);
			float macVolume = (float) am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			
			
			volume = actVolume/macVolume;	
			
			sp.setOnLoadCompleteListener(new OnLoadCompleteListener() {
				
				public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
					
					
					sp.play(soundId, volume, volume, 1, 0, 1f);
				}
				
			});
			
			
		}
	}

}
