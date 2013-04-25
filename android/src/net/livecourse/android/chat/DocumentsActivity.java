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
import android.os.Build;
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

/**
 * This activity handles the viewing and downloading of documents for the chat
 * the user is currently in.
 */
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
	/**
	 * Upong clicking on a file, the system will attempt to download it
	 */
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) 
	{
		DocumentViewHolder v = (DocumentViewHolder) view.getTag();
		if(Globals.currentDownloadLocation == null)
		{
			String saveLocation = Environment.DIRECTORY_DOWNLOADS + "/LiveCourse";
			String fileName = v.documentName.getText().toString(); 
			String location = "http://livecourse.s3.amazonaws.com/" + v.documentFile;
			this.downloadFile(fileName, location, saveLocation);
			Globals.currentDownloadLocation = saveLocation;
			Globals.currentDownloadName = fileName;
		}
		else
		{
			//alert only one download at a time
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		return false;
	}
	
	@SuppressWarnings("deprecation")
	/**
	 * This method will call Android's download manager and attempt to download
	 * the file selected by the user
	 * 
	 * @param fileName		The file name to be downloaded
	 * @param location		The location of the file on the web
	 * @param saveLocation	The location on the device to be saved
	 */
	private void downloadFile(String fileName, String location, String saveLocation)
	{
		DownloadManager dm = (DownloadManager) this.getSystemService(DOWNLOAD_SERVICE);
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(location));
		request.setTitle(fileName);
		request.setDescription(location);
		request.allowScanningByMediaScanner();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
		else
			request.setShowRunningNotification(true);
		
		request.setDestinationInExternalPublicDir(saveLocation, fileName);
		dm.enqueue(request);
	}

}
