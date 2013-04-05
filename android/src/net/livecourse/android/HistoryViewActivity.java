package net.livecourse.android;

import java.util.ArrayList;
import java.util.Arrays;
import net.livecourse.android.R;
import net.livecourse.rest.REST;
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
	 * This is the same adapter used for the chat message list.
	 */
	private ChatCursorAdapter adapter;
	
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
		adapter = new ChatCursorAdapter(getBaseContext(), null, 0);
		historyListView.setAdapter(adapter);
		
		historyListView.setOnItemLongClickListener(this);
		
		//new REST(this.getSherlockActivity(),this,null,null,null,null,null,MainActivity.currentChatId,sendMessageEditTextView.getText().toString(),REST.SEND).execute();
	
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}
}
