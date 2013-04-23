package net.livecourse.android.participants;

import net.livecourse.R;
import net.livecourse.utility.Globals;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ParticipantsCursorAdapter extends CursorAdapter
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
	public ParticipantsCursorAdapter(Context context, Cursor c, int flags) 
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
		View view = LayoutInflater.from(mContext).inflate(R.layout.participants_item_layout, parent, false);
		
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
		ParticipantViewHolder v = (ParticipantViewHolder) view.getTag();
				
		String name = cursor.getString(cursor.getColumnIndexOrThrow("display_name"));
				
		v.displayName.setText(name);
		v.userId = cursor.getString(cursor.getColumnIndexOrThrow("user_id"));
		
		String temp = cursor.getString(cursor.getColumnIndexOrThrow("ignored"));
		Log.d("participants cursor adapter", "Name: " + v.displayName + " Ignore: " + temp);
		v.ignore = temp;
				
		/**
		 * Sets the text to bold if it is the user's own name
		 */
		if(v.userId != null && v.userId.equals(Globals.userId))
		{
			v.displayName.setTypeface(null, Typeface.BOLD);
		}
		else if(v.ignore.equals("1"))
		{
			v.displayName.setPaintFlags(v.displayName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		}
		else
		{
			v.displayName.setTypeface(null, Typeface.NORMAL);
		}
		view.setTag(v);
	}
}
