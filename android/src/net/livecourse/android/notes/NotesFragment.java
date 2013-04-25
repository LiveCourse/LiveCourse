package net.livecourse.android.notes;

import net.livecourse.R;
import net.livecourse.android.TabsFragmentAdapter;
import net.livecourse.database.NotesLoader;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.Globals;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class NotesFragment extends SherlockFragment implements OnRestCalled, ActionMode.Callback, LoaderCallbacks<Cursor>, OnItemClickListener, OnItemLongClickListener
{
	private final String TAG = " == Documents Fragment == ";
	
	private static final String KEY_CONTENT = "TestFragment:Content";
	private String mContent = "???";
	private String CURRENT_CLASS = "";
	
	private View notesLayout;
	private ListView notesListView;
	private View notesFooterView;
		
	private NotesCursorAdapter adapter;
	
	public static NotesFragment newInstance(String content, TabsFragmentAdapter tabsAdapter) 
	{
		NotesFragment fragment = new NotesFragment();
		return fragment;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) 
		{
			mContent = savedInstanceState.getString(KEY_CONTENT);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		Globals.notesFragment = this;
		
		/**
		 * Settings for the fragment
		 * Allows adding stuff for the options menu
		 */
		this.setHasOptionsMenu(true);
		
		this.notesLayout = inflater.inflate(R.layout.notes_layout, container, false);
		this.notesListView = (ListView) this.notesLayout.findViewById(R.id.notes_list_view);
		
		this.notesFooterView = this.getSherlockActivity().getLayoutInflater().inflate(R.layout.notes_footer_layout, null);
		this.notesListView.addFooterView(this.notesFooterView, null, true);
				
		this.adapter = new NotesCursorAdapter(this.getSherlockActivity(), null, 0);
		this.notesListView.setAdapter(this.adapter);
		
		this.notesListView.setOnItemClickListener(this);
		this.notesListView.setOnItemLongClickListener(this);
		
		
		return notesLayout;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.groupchat_fragment_menu,menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{

		}

		return super.onOptionsItemSelected(item);		
	}
	
	public String getCurrentClass()
	{
		return CURRENT_CLASS;
	}
	
	public void setCurrentClass(String className)
	{
		CURRENT_CLASS = className;
	}
	
	public void updateList()
	{
		Globals.appDb.recreateNotes();
		new Restful(Restful.GET_NOTES_PATH,Restful.GET, new String[]{"class_id_string"}, new String[]{Globals.chatId}, 1, this);		
	}
	
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) 
	{
		if(view.getId() == R.id.notes_list_view_footer)
		{
			NotesAddNotesDialog dialog = new NotesAddNotesDialog();
	        dialog.show(this.getSherlockActivity().getSupportFragmentManager(), "NoticeDialogFragment");
		}
		else
		{
			NoteViewHolder v = (NoteViewHolder) view.getTag();
			NoteDialog dialog = new NoteDialog(v.noteId, v.message.getText().toString());
	        dialog.show(this.getSherlockActivity().getSupportFragmentManager(), "NoticeDialogFragment");
		}
	}
	
	
	@Override
	public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) 
	{
		return new NotesLoader(this.getSherlockActivity(), Globals.appDb);
	}


	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) 
	{
		adapter.notifyDataSetChanged();
		adapter.swapCursor(cursor);
	}


	@Override
	public void onLoaderReset(Loader<Cursor> loader) 
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) 
	{
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) 
	{
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) 
	{
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void onDestroyActionMode(ActionMode mode) 
	{
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void preRestExecute(String restCall) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestHandleResponseSuccess(String restCall, String response) 
	{
		if(restCall.equals(Restful.GET_NOTES_PATH))
		{
			Log.d(this.TAG, "OnRestHandlerResponse for path GET NOTES reached with response: " + response);
			Globals.appDb.addNotesFromJSON(false, response);
		}
	}


	@Override
	public void onRestPostExecutionSuccess(String restCall, String result) 
	{
		if(restCall.equals(Restful.GET_NOTES_PATH))
		{
			this.getSherlockActivity().getSupportLoaderManager().restartLoader(Globals.NOTES_LOADER, null, this);
		}
	}


	@Override
	public void onRestPostExecutionFailed(String restCall, int code,String result) 
	{
		Log.d(this.TAG, "Rest call: " + restCall + "failed with status code: " + code);
		Log.d(this.TAG,"Result from server is:\n" + result);
		
	}


	@Override
	public void onRestCancelled(String restCall, String result) 
	{
		// TODO Auto-generated method stub
		
	}
}
