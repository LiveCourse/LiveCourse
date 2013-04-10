package net.livecourse.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.livecourse.R;
import net.livecourse.database.DatabaseHandler;
import net.livecourse.database.HistoryListLoader;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.Globals;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
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
		return new HistoryListLoader(this, Globals.appDb);
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
		new Restful("chats/fetch_day",0,new String[] {"chat_id","start_epoch"},new String[] {"sproga","1365307201"},2,this);
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
		 * Used to parse the response into String objects
		 */
		JSONArray parse = null;
		JSONObject ob = null;
		
		/**
		 * This section of code is executed in the Rest background thread after grabbing
		 * data from the Rest call to the server.
		 * 
		 * Does different actions based on the type of Rest call
		 * 
		 * If the call is to Fetch_Day...
		 */
		if(restCall.equals(Restful.FETCH_DAY))
		{
			SQLiteDatabase db = null;
			SQLiteStatement statement = null;
			try 
			{
				parse = new JSONArray(response);
				db = Globals.appDb.getWritableDatabase();
				statement = db.compileStatement(
						"INSERT INTO " 	+ DatabaseHandler.TABLE_HISTORY + 
							" ( " 		+ DatabaseHandler.KEY_CHAT_ID + 
							", "		+ DatabaseHandler.KEY_USER_ID +
							", " 		+ DatabaseHandler.KEY_CHAT_SEND_TIME + 
							", " 		+ DatabaseHandler.KEY_CHAT_MESSAGE_STRING + 
							", " 		+ DatabaseHandler.KEY_USER_EMAIL + 
							", " 		+ DatabaseHandler.KEY_USER_DISPLAY_NAME + 
							") VALUES (?, ?, ?, ?, ?, ?)");

				db.beginTransaction();
				for(int x = 0;x<parse.length();x++)
		        {
		        	ob = parse.getJSONObject(x);
		   		        	
		        	//statement.clearBindings();
		        	
		        	statement.bindString(1, ob.getString("id"));
		        	statement.bindString(2, ob.getString("user_id"));
		        	statement.bindString(3, ob.getString("send_time"));
		        	statement.bindString(4, ob.getString("message_string"));
		        	statement.bindString(5, ob.getString("email"));
		        	statement.bindString(6, ob.getString("display_name"));
		        	
		        	statement.execute();
		        }
				db.setTransactionSuccessful();	
			}
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			finally
			{
				db.endTransaction();

			}
			statement.close();
			db.close();
			
			//Log.d(this.TAG, parse.length() + " messages stored in database in " + (System.currentTimeMillis() - startTime) + "ms");
		}
	}

	@Override
	public void onRestPostExecutionSuccess(String restCall, String result) 
	{
		if(restCall.equals(Restful.FETCH_DAY))
		{
			this.getSupportLoaderManager().restartLoader(4, null, this);
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
