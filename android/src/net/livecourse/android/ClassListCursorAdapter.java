package net.livecourse.android;

import net.livecourse.utility.Utility;
import net.livecourse.utility.ViewHolder;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ClassListCursorAdapter extends CursorAdapter
{
	private Context mContext;
	
	public ClassListCursorAdapter(Context context, Cursor c, int flags) 
	{
		super(context, c, flags);
		mContext = context;
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) 
	{
		View view = LayoutInflater.from(mContext).inflate(R.layout.classlist_item_layout, parent, false);
		
		ViewHolder v = new ViewHolder();
		v.className = (TextView) view.findViewById(R.id.classquery_item_class_name_text_view);
		v.classTime = (TextView) view.findViewById(R.id.classquery_item_class_time_text_view);
		
		view.setTag(v);
		
		return view;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) 
	{
		ViewHolder v = (ViewHolder) view.getTag();
		
		String name = "\t" + cursor.getString(cursor.getColumnIndexOrThrow("name"));
		String time = "\t\t";
		if(cursor.getString(cursor.getColumnIndexOrThrow("dow_monday")).equals("1"))
			time += "M";
		if(cursor.getString(cursor.getColumnIndexOrThrow("dow_tuesday")).equals("1"))
			time += "T";
		if(cursor.getString(cursor.getColumnIndexOrThrow("dow_wednesday")).equals("1"))
			time += "W";
		if(cursor.getString(cursor.getColumnIndexOrThrow("dow_thursday")).equals("1"))
			time += "R";
		if(cursor.getString(cursor.getColumnIndexOrThrow("dow_friday")).equals("1"))
			time += "F";
		if(cursor.getString(cursor.getColumnIndexOrThrow("dow_saturday")).equals("1"))
			time += "S";
		if(cursor.getString(cursor.getColumnIndexOrThrow("dow_sunday")).equals("1"))
			time += "U";
		
		time += " " + Utility.convertMinutesTo24Hour(cursor.getString(cursor.getColumnIndexOrThrow("start_time"))) + " - " + Utility.convertMinutesTo24Hour(cursor.getString(cursor.getColumnIndexOrThrow("end_time")));
		
		v.className.setText(name);
		v.classTime.setText(time);
		v.idString = cursor.getString(cursor.getColumnIndexOrThrow("id_string"));
		
		view.setTag(v);
		
	}
}
