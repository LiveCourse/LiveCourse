package net.livecourse;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;


/**
 * This adapter is used to handle the list view of the class list fragment.
 * @author Darren
 * @param <T>
 *
 * @param <String>
 */
public class ClassListAdapter<T> extends ArrayAdapter<T>{

	public ClassListAdapter(Context context, int textViewResourceId,List<T> objects) {
		super(context, textViewResourceId, objects);
	}
}
