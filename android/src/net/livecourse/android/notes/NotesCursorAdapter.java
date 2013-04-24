package net.livecourse.android.notes;

import net.livecourse.R;
import net.livecourse.database.DatabaseHandler;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NotesCursorAdapter extends CursorAdapter
{
	private Context mContext;
	
	public NotesCursorAdapter(Context context, Cursor c, int flags) 
	{
		super(context, c, flags);
		mContext = context;
	}
	
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) 
	{
		View view = LayoutInflater.from(mContext).inflate(R.layout.note_item_layout, parent, false);
		
		NoteViewHolder v = new NoteViewHolder();
		
		v.message = (TextView) view.findViewById(R.id.note_item_message_text_view);
		
		view.setTag(v);
		
		return view;
	}
	
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) 
	{
		NoteViewHolder v 	= (NoteViewHolder) view.getTag();
		
		String message 		= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_NOTES_MESSAGE));
		int depth			= cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_NOTES_DEPTH));
		
		v.message.setText(message);
		
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)v.message.getLayoutParams();
		params.setMargins(20*depth, 5, 10, 5);
		v.message.setLayoutParams(params);
		
		if(depth == 0)
			v.message.setTextAppearance(context, android.R.style.TextAppearance_Large);
		else if(depth == 1)
			v.message.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		else
			v.message.setTextAppearance(context, android.R.style.TextAppearance_Small);
		
		view.setTag(v);
	}
}
