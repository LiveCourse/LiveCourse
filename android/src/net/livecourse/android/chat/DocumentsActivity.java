package net.livecourse.android.chat;

import net.livecourse.R;
import net.livecourse.database.DocumentsLoader;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.Globals;
import net.livecourse.utility.Utility;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class DocumentsActivity extends SherlockFragmentActivity implements OnRestCalled, LoaderCallbacks<Cursor>
{
	private final String 	TAG	= " == Documents Activity == ";
	
	private ListView 				documentsListView;
	private DocumentsCursorAdapter	adapter;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        this.setContentView(R.layout.documents_layout);
        
        Utility.changeActivityColorBasedOnPref(this, this.getSupportActionBar());
        
        this.documentsListView = (ListView) this.findViewById(R.id.documents_list_view);
        
        this.adapter = new DocumentsCursorAdapter(this, null, 0);
        this.documentsListView.setAdapter(adapter);
        
        this.updateList();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return true;
	}

	@Override
	public void preRestExecute(String restCall) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestHandleResponseSuccess(String restCall, String response) 
	{
		Log.d(this.TAG, response);
		
		if(restCall.equals(Restful.GET_ALL_FILES_PATH))
		{
			Globals.appDb.addDocumentsFromJSON(false, response);
		}
		
	}

	@Override
	public void onRestPostExecutionSuccess(String restCall, String result) 
	{
		if(restCall.equals(Restful.GET_ALL_FILES_PATH))
		{
			this.getSupportLoaderManager().restartLoader(Globals.DOCUMENTS_LOADER, null, this);
		}
	}

	@Override
	public void onRestPostExecutionFailed(String restCall, int code, String result) 
	{
		Log.d(this.TAG, "Rest call: " + restCall + "failed with status code: " + code);
		Log.d(this.TAG,"Result from server is:\n" + result);		
	}

	@Override
	public void onRestCancelled(String restCall, String result) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) 
	{
		return new DocumentsLoader(this, Globals.appDb);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) 
	{
		long startTime = System.currentTimeMillis();
		
		adapter.notifyDataSetChanged();
		adapter.swapCursor(cursor);
		
		Log.d(this.TAG, "Documents stored into listview in " + (System.currentTimeMillis() - startTime) + "ms");
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) 
	{
		adapter.swapCursor(null);
	}
	
	public void updateList()
	{		
		Globals.appDb.recreateDocuments();
        new Restful(Restful.GET_ALL_FILES_PATH, Restful.GET, new String[]{"chat_id"}, new String[]{Globals.chatId}, 1, this);
	}
}
