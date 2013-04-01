package net.livecourse;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * Incomplete, this is used to handle items going into the chat list,
 * should be switched over to cursor to handle SQLite later.
 * @author Darren
 *
 * @param <T>
 */
public class ChatListAdapter<T> extends ArrayAdapter<T>{
	public ChatListAdapter(Context context, int textViewResourceId,List<T> objects) {
		super(context, textViewResourceId, objects);
	}
}
