package net.livecourse.android;

import net.livecourse.utility.ChatMessageViewHolder;
import net.livecourse.utility.ParticipantViewHolder;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ParticipantsAdapter extends CursorAdapter
{
	/**
	 * The context
	 */
	private Context mContext;
	
	/**
	 * The constructor, it takes in the current context, the cursor that is used to populate the participants view,
	 * and the flag for setting the mode of this adapter.
	 * 
	 * @param context	The current context
	 * @param c			The cursor used to populate the view
	 * @param flags		The mode for the adapter
	 */
	public ParticipantsAdapter(Context context, Cursor c, int flags) 
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
		View view = LayoutInflater.from(mContext).inflate(R.layout.participants_layout, parent, false);
		
		ParticipantViewHolder v = new ParticipantViewHolder();
		
		v.displayName = (TextView) view.findViewById(R.id.participant_display_name_text_view);
		
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
				
		v.displayName.setText(name);
		
		v.messageId = cursor.getString(cursor.getColumnIndexOrThrow("chat_id"));

		view.setTag(v);
	}
}