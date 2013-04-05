package net.livecourse.android;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class ClassListAdapter extends ArrayAdapter<String>{

	public ClassListAdapter(Context context, int textViewResourceId,
			List<String> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

}
