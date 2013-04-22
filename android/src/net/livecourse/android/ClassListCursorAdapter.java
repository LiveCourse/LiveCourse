package net.livecourse.android;

import net.livecourse.R;
import net.livecourse.utility.Utility;
import net.livecourse.utility.ChatroomViewHolder;
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
		
		ChatroomViewHolder v = new ChatroomViewHolder();
		v.className 		= (TextView) view.findViewById(R.id.classquery_item_class_name_text_view);
		v.classTime 		= (TextView) view.findViewById(R.id.classquery_item_class_time_text_view);
		v.classType 		= (TextView) view.findViewById(R.id.classquery_item_class_type_text_view);
		v.classInstructor 	= (TextView) view.findViewById(R.id.classquery_item_class_instructor_text_view);

		view.setTag(v);
		
		return view;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) 
	{
		ChatroomViewHolder v = (ChatroomViewHolder) view.getTag();
		
		String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
		String type = cursor.getString(cursor.getColumnIndexOrThrow("class_type"));
		String instructor = cursor.getString(cursor.getColumnIndexOrThrow("instructor"));
		
		if(name.length() > 28)
			name = name.substring(0, 25) + "...";
		if(instructor.length() > 28)
			instructor = instructor.substring(0,25) + "..."; 
			
		
		String time = "";
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
		v.classType.setText(type);
		v.classInstructor.setText(instructor);
		v.idString = cursor.getString(cursor.getColumnIndexOrThrow("id_string"));
		v.idSectionString = cursor.getString(cursor.getColumnIndexOrThrow("section_id_string"));
		
		view.setTag(v);
		
	}
}
