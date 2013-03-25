package net.livecourse;

import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class HistoryViewActivity extends SherlockFragmentActivity
{
	/**
	 * View used for the history list
	 */
	private ListView historyListView;
	
	/**
	 * This is the adapter used for the history list.
	 * It is incomplete.
	 */
	private ChatListAdapter<String> adapter;
	
	/**
	 * Temporary list used to populate the history list
	 */
	String[] array = {"apple","banana","c","d","e","f","g"}; 
	ArrayList<String> history;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);
        
        /**
         * Connects the view to the XML
         */
        historyListView = (ListView) findViewById(R.id.history_list_view);
        
        /**
		 * Initialize the temporary list
		 */
		history = new ArrayList<String>();
		history.addAll(Arrays.asList(array));
		
		/**
		 * Adds the adapter to the list and sends the temporary list to it
		 */
		adapter = new ChatListAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1,history);
		historyListView.setAdapter(adapter);
        
	}
}
