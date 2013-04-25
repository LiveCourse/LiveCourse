package net.livecourse.android.chat;

import net.livecourse.R;
import net.livecourse.database.DocumentsLoader;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.Globals;
import net.livecourse.utility.Utility;
import android.app.DownloadManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

public class DocumentsActivity extends SherlockFragmentActivity implements OnRestCalled, LoaderCallbacks<Cursor>, OnItemClickListener, OnItemLongClickListener
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
        this.documentsListView.setEmptyView(this.findViewById(R.id.documents_list_empty_text_view));
        
        this.adapter = new DocumentsCursorAdapter(this, null, 0);
        this.documentsListView.setAdapter(adapter);
        this.documentsListView.setOnItemClickListener(this);
        this.documentsListView.setOnItemLongClickListener(this);
        
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
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) 
	{
		DocumentViewHolder v = (DocumentViewHolder) view.getTag();
		this.downloadFile(v.documentName.getText().toString(), "http://livecourse.s3.amazonaws.com/" + v.documentFile);
		
		//http://livecourse.s3.amazonaws.com/UIQPBr2Og7TELzvW.mp3
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		return false;
	}
	
	private void downloadFile(String fileName, String location)
	{
		DownloadManager dm = (DownloadManager) this.getSystemService(DOWNLOAD_SERVICE);
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(location));
		request.setTitle("Downloading " + fileName);
		request.allowScanningByMediaScanner();
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + "/LiveCourse", fileName);
		dm.enqueue(request);
	}

}
