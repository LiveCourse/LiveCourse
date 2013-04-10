package net.livecourse.android;

import java.util.Date;

import net.livecourse.R;
import net.livecourse.utility.ChatMessageViewHolder;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This is the adapter for the chat messages.  It will update the chat messages with information fed in
 * from the database via ChatMessageLoader.
 * 
 * @author Darren Cheng
 */
public class ChatCursorAdapter extends CursorAdapter
{
	/**
	 * The context
	 */
	private Context mContext;
	
	/**
	 * The constructor, it takes in the current context, the cursor that is used to populate the list view,
	 * and the flag for setting the mode of this adapter.
	 * 
	 * @param context	The current context
	 * @param c			The cursor used to populate the list view
	 * @param flags		The mode for the adapter
	 */
	public ChatCursorAdapter(Context context, Cursor c, int flags) 
	{
		super(context, c, flags);
		mContext = context;
	}
	
	@Override
	/**
	 * Makes a new view to hold the data pointed to by cursor.
	 * 
	 * @param context	Interface to application's global information
	 * @param cursor	The cursor from which to get the data. The cursor is already moved to the correct position.
	 * @param parent	The parent to which the new view is attached to
	 */
	public View newView(Context context, Cursor cursor, ViewGroup parent) 
	{
		View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_layout, parent, false);
		
		ChatMessageViewHolder v = new ChatMessageViewHolder();
		
		v.displayName 	= (TextView) view.findViewById(R.id.chat_item_display_name_text_view);
		v.time 			= (TextView) view.findViewById(R.id.chat_item_time_text_view);
		v.message 		= (TextView) view.findViewById(R.id.chat_item_class_message_text_view);
		
		view.setTag(v);
		
		return view;	
	}
	
	@Override
	/**
	 * Bind an existing view to the data pointed to by cursor
	 * 
	 * @param view		Existing view, returned earlier by newView
	 * @param context	Interface to application's global information
	 * @param cursor	The cursor from which to get the data. The cursor is already moved to the correct position.
	 */
	public void bindView(View view, Context context, Cursor cursor) 
	{
		ChatMessageViewHolder v = (ChatMessageViewHolder) view.getTag();
				
		String name = cursor.getString(cursor.getColumnIndexOrThrow("display_name"));
		String time = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("send_time")))*1000));
		String message = cursor.getString(cursor.getColumnIndexOrThrow("message_string"));
				
		v.displayName.setText(name);
		v.time.setText(time);
		v.message.setText(message);
		//Linkify.addLinks(v.message, Linkify.WEB_URLS);
		
		v.messageId = cursor.getString(cursor.getColumnIndexOrThrow("chat_id"));
		v.userId = cursor.getString(cursor.getColumnIndexOrThrow("chat_id"));

		view.setTag(v);
	}
}
