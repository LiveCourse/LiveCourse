package net.livecourse.android;

import java.util.Date;

import net.livecourse.utility.ChatMessageViewHolder;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ChatCursorAdapter extends CursorAdapter
{
	private Context mContext;
	public ChatCursorAdapter(Context context, Cursor c, int flags) 
	{
		super(context, c, flags);
		mContext = context;
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) 
	{
		
		View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_layout, parent, false);
		
		//ChatroomViewHolder v = new ChatroomViewHolder();
		ChatMessageViewHolder v = new ChatMessageViewHolder();
		v.displayName 	= (TextView) view.findViewById(R.id.chat_item_display_name_text_view);
		v.time 			= (TextView) view.findViewById(R.id.chat_item_time_text_view);
		v.message 		= (TextView) view.findViewById(R.id.chat_item_class_message_text_view);
		
		view.setTag(v);
		
		return view;	
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) 
	{
		ChatMessageViewHolder v = (ChatMessageViewHolder) view.getTag();
		
		String date = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date (Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("send_time")))*1000));
		
		String name = cursor.getString(cursor.getColumnIndexOrThrow("display_name"));
		String time = date + "\t";
		String message = cursor.getString(cursor.getColumnIndexOrThrow("message_string"));
				
		v.displayName.setText(name);
		v.time.setText(time);
		v.message.setText(message);
		
		v.messageId = cursor.getString(cursor.getColumnIndexOrThrow("chat_id"));

		view.setTag(v);
	}


}
