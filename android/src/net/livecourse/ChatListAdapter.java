package net.livecourse;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class ChatListAdapter<T> extends ArrayAdapter<T>{
	public ChatListAdapter(Context context, int textViewResourceId,List<T> objects) {
		super(context, textViewResourceId, objects);
	}
}
