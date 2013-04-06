package net.livecourse.android;

import net.livecourse.R;
import net.livecourse.database.ParticipantsLoader;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.utility.Globals;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class HistoryViewActivity extends SherlockFragmentActivity implements OnItemLongClickListener, LoaderCallbacks<Cursor>, OnRestCalled
{
	private final String TAG = " == History View Activity == ";
	/**
	 * View used for the history list
	 */
	private ListView historyListView;
	
	/**
	 * This is the same adapter used for the chat message list.
	 */
	private ChatCursorAdapter adapter;
	
	
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
		 * Adds the adapter to the list and sends the temporary list to it
		 */
		adapter = new ChatCursorAdapter(getBaseContext(), null, 0);
		historyListView.setAdapter(adapter);
		
		historyListView.setOnItemLongClickListener(this);
		
		this.updateList();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
	{
		return false;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1)
	{
		return new ParticipantsLoader(this, Globals.appDb);
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
		//TODO: Change this to the new Restful call
		//new REST(this,null,Globals.chatId,null/*epoch*/,REST.HISTORY).execute();
	}
	public void clearList()
	{
		adapter.swapCursor(null);
	}

	@Override
	public void onRestHandleResponseSuccess(String restCall, String response) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestPostExecutionSuccess(String restCall, String result) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestPostExecutionFailed(String restCall, int code, String result) 
	{
		Log.d(this.TAG, "Rest call: " + restCall + "failed with status code: " + code);
		Log.d(this.TAG,"Result from server is:\n" + result);		
	}
}
