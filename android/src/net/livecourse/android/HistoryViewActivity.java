package net.livecourse.android;

import java.util.ArrayList;
import java.util.Arrays;
import net.livecourse.android.R;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class HistoryViewActivity extends SherlockFragmentActivity implements OnItemLongClickListener
{
	/**
	 * View used for the history list
	 */
	private ListView historyListView;
	
	/**
	 * This is the adapter used for the history list.
	 * It is incomplete.
	 */
	//private ChatListAdapter<String> adapter;
	
	/**
	 * Temporary list used to populate the history list
	 */
	String[] array = {"this","is","the","history","e","f","g"}; 
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
		//adapter = new ChatListAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1,history);
		//historyListView.setAdapter(adapter);
		
		historyListView.setOnItemLongClickListener(this);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}
}
