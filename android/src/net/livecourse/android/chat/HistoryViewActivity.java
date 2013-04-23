package net.livecourse.android.chat;

import net.livecourse.R;
import net.livecourse.database.HistoryListLoader;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.Globals;
import net.livecourse.utility.Utility;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class HistoryViewActivity extends SherlockFragmentActivity implements OnItemClickListener, OnItemLongClickListener, LoaderCallbacks<Cursor>, OnRestCalled
{
	private final String TAG = " == History View Activity == ";
	
	private long time;
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
		Log.d(this.TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);
        
        Utility.changeActivityColorBasedOnPref(this, this.getSupportActionBar());
        this.setTitle(this.getIntent().getStringExtra("date"));
        this.time = this.getIntent().getLongExtra("time", 0);
        
        /**
         * Connects the view to the XML
         */
        historyListView = (ListView) findViewById(R.id.history_list_view);
		
		/**
		 * Adds the adapter to the list and sends the temporary list to it
		 */
		adapter = new ChatCursorAdapter(getBaseContext(), null, 0);
		historyListView.setAdapter(adapter);
		historyListView.setOnItemClickListener(this);
		historyListView.setOnItemLongClickListener(this);
		
		this.updateList();
	}
	
	@Override
	protected void onNewIntent (Intent intent)
	{
		Log.d(this.TAG, "onNewIntent");
        this.setTitle(intent.getStringExtra("date"));
		this.time = intent.getLongExtra("time", 0);
		this.updateList();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater=getSupportMenuInflater();
		inflater.inflate(R.menu.history_activity_menu,menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.change_history_date_item:
			Log.d(this.TAG, "Running onOptionsItemSelected view history");
			DialogFragment newFragment = new HistoryDatePickerFragment();
		    newFragment.show(this.getSupportFragmentManager(), "datePicker");
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) 
	{
		ChatMessageDialog dialog = new ChatMessageDialog(((ChatMessageViewHolder)view.getTag()).messageId, ChatMessageDialog.DATA_FROM_CHAT_HISTORY);
        dialog.show(this.getSupportFragmentManager(), "NoticeDialogFragment");		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
	{
		return false;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1)
	{
		return new HistoryListLoader(this, Globals.appDb);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) 
	{
		adapter.notifyDataSetChanged();
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0)
	{
		adapter.swapCursor(null);
	}
	public void updateList()
	{
		Globals.appDb.recreateHistory();
		new Restful(Restful.GET_CHAT_HISTORY_PATH, Restful.GET,new String[] {"chat_id","start_epoch"},new String[] {Globals.chatId, "" + this.time},2,this);
	}
	public void clearList()
	{
		adapter.swapCursor(null);
	}

	@Override
	public void onRestHandleResponseSuccess(String restCall, String response) 
	{
		Log.d(this.TAG, "Result: " + response);
		
		/**
		 * This section of code is executed in the Rest background thread after grabbing
		 * data from the Rest call to the server.
		 * 
		 * Does different actions based on the type of Rest call
		 * 
		 * If the call is to Fetch_Day...
		 */
		if(restCall.equals(Restful.GET_CHAT_HISTORY_PATH))
		{
			Globals.appDb.addHistoryMessagesFromJSON(false, response);
		}
	}

	@Override
	public void onRestPostExecutionSuccess(String restCall, String result) 
	{
		if(restCall.equals(Restful.GET_CHAT_HISTORY_PATH))
		{
			this.getSupportLoaderManager().restartLoader(Globals.HISTORY_LOADER, null, this);
		}
	}

	@Override
	public void onRestPostExecutionFailed(String restCall, int code, String result) 
	{
		Log.d(this.TAG, "Rest call: " + restCall + "failed with status code: " + code);
		Log.d(this.TAG,"Result from server is:\n" + result);		
	}

	@Override
	public void preRestExecute(String restCall) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestCancelled(String restCall, String result) {
		// TODO Auto-generated method stub
		
	}
}
