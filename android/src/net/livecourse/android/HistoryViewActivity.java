package net.livecourse.android;

import java.util.ArrayList;
import java.util.Arrays;
import net.livecourse.android.R;
import net.livecourse.database.ParticipantsLoader;
import net.livecourse.rest.REST;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;

public class HistoryViewActivity extends SherlockFragmentActivity implements OnItemLongClickListener, LoaderCallbacks<Cursor>
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
	//String[] array = {"this","is","the","history","e","f","g"}; 
	//ArrayList<String> history;
	
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
		//history = new ArrayList<String>();
		//history.addAll(Arrays.asList(array));
		
		/**
		 * Adds the adapter to the list and sends the temporary list to it
		 */
		adapter = new ChatCursorAdapter(getBaseContext(), null, 0);
		historyListView.setAdapter(adapter);
		
		historyListView.setOnItemLongClickListener(this);
		
		//new REST(this,null,MainActivity.currentChatId,null/* start_epoch */,REST.HISTORY).execute();
	
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1)
	{
		return new ParticipantsLoader(this, MainActivity.getAppDb());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) 
	{
		adapter.notifyDataSetChanged();
		adapter.swapCursor(cursor);
		this.historyListView.setStackFromBottom(true);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0)
	{
		adapter.swapCursor(null);
	}
	public void updateList()
	{
		new REST(this,null,MainActivity.currentChatId,null/*epoch*/,REST.HISTORY).execute();
	}
	public void clearList()
	{
		adapter.swapCursor(null);
	}
}
