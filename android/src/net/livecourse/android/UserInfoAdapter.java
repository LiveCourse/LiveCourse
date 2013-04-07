package net.livecourse.android;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class UserInfoAdapter extends ArrayAdapter<String>{

	public UserInfoAdapter(Context context, int textViewResourceId, List<String> objects) 
	{
		super(context, textViewResourceId, objects);
	}


}
