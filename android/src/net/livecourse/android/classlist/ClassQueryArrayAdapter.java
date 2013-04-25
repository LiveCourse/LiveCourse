package net.livecourse.android.classlist;

import java.util.ArrayList;

import net.livecourse.R;
import net.livecourse.utility.Utility;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ClassQueryArrayAdapter extends ArrayAdapter<Chatroom>
{
	private ArrayList<Chatroom> items;
	private Context context;
	
	public ClassQueryArrayAdapter(Context context, int textViewResourceId, ArrayList<Chatroom> items) 
	{
        super(context, textViewResourceId, items);
        this.context = context;
        this.items = items;
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
        View view = convertView;
        ChatroomViewHolder v = new ChatroomViewHolder();
        
        if (view == null) 
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.classlist_item_layout, null);
        }
        
        Chatroom item = items.get(position);
        if (item!= null) 
        {
            v.className 		= (TextView) view.findViewById(R.id.classquery_item_class_name_text_view);
            v.classTime 		= (TextView) view.findViewById(R.id.classquery_item_class_time_text_view);
            v.classType 		= (TextView) view.findViewById(R.id.classquery_item_class_type_text_view);
    		v.classInstructor 	= (TextView) view.findViewById(R.id.classquery_item_class_instructor_text_view);
    		
            
            if (v.className != null) 
            {
            	String name = item.getName();
            	v.className.setText(name);
            }
            if(v.classTime != null)
            {
            	String temp = "";
            	
            	if(item.getDowMonday().equals("1"))
            		temp += "M";
            	if(item.getDowTuesday().equals("1"))
            		temp += "T";
            	if(item.getDowWednesday().equals("1"))
            		temp += "W";
            	if(item.getDowThursday().equals("1"))
            		temp += "R";
            	if(item.getDowFriday().equals("1"))
            		temp += "F";
            	if(item.getDowSaturday().equals("1"))
            		temp += "S";
            	if(item.getDowSunday().equals("1"))
            		temp += "U";
            	temp += " " + Utility.convertMinutesTo24Hour(item.getStartTime()) + " - " + Utility.convertMinutesTo24Hour(item.getEndTime());
            	
            	v.classTime.setText(temp);
            }
            if(v.classType != null)
            {
            	v.classType.setText(item.getClassType());
            }
            if(v.classInstructor != null)
            {
            	String instructor = item.getInstructor();
            	v.classInstructor.setText(instructor);
            }
            
            v.chatIdString 		= item.getChatIdString();
            v.sectionIdString 	= item.getSectionIdString();
            view.setTag(v);
         }

        return view;
    }
	
}
