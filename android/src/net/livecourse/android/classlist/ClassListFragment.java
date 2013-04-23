package net.livecourse.android.classlist;

import org.json.JSONException;
import org.json.JSONObject;

import net.livecourse.R;
import net.livecourse.android.MainActivity;
import net.livecourse.android.TabsFragmentAdapter;
import net.livecourse.database.ClassEnrollLoader;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.Globals;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.app.SherlockFragment;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ClassListFragment extends SherlockFragment implements OnItemClickListener,OnItemLongClickListener, ActionMode.Callback, SearchView.OnQueryTextListener,  LoaderCallbacks<Cursor>, OnRestCalled
{
	private final String TAG = " == Class List Fragment == ";
	private static final String KEY_CONTENT = "TestFragment:Content";
	private String chatRoomToDelete = null;
	
	/**
	 * This is used to add the other tabs once a class is selected
	 */
	private TabsFragmentAdapter tabsAdapter;
	
	/**
	 * The XML views
	 */
	private View classListLayout;
	private ListView classListView;
	private MenuItem searchViewMenuItem;
	
	/**
	 * Adapter used for the class list
	 */
	private ClassListCursorAdapter adapter;
	
    private String mContent = "???";
    
    private boolean updateListCalledByQR = false;

	/**
	 * The singlton constructor, forces only one ClassListFragment to exist in the system
	 * 
	 * @param content		The content
	 * @param tabsAdapter	The Tabs Adapter
	 * @return 				The new instanced ClassListFragment
	 */
    public static ClassListFragment newInstance(String content, TabsFragmentAdapter tabsAdapter) 
    {
    	ClassListFragment fragment = new ClassListFragment();
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
    	tabsAdapter = ((MainActivity) this.getSherlockActivity()).getTabsAdapter();
    	Globals.classListFragment = this;
    	
    	/**
		 * Settings for the fragment
		 * Allows adding stuff for the options menu
		 */
		this.setHasOptionsMenu(true);
		this.setMenuVisibility(true);
		
		/**
		 * Connects the list to the XML
		 */
		classListLayout = inflater.inflate(R.layout.classlist_layout, container, false);
		classListView = (ListView) classListLayout.findViewById(R.id.classlist_list_view);
		classListView.setEmptyView(classListLayout.findViewById(R.id.class_list_empty_text_view));
    	
    	
    	/** 
    	 * Create the adapter and set it to the list and populate it
    	 * **/
		adapter = new ClassListCursorAdapter(this.getSherlockActivity(),null,0);
		classListView.setAdapter(adapter);
        
        /**
         * The listener for clicking on an item in the list view
         */
        classListView.setOnItemClickListener(this);
        classListView.setOnItemLongClickListener(this);
        
        this.updateList();
        
        return classListLayout;
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
		inflater.inflate(R.menu.classlist_fragment_menu,menu);
		
		/**
		 * Inits the search view
		 */
        SearchManager searchManager = (SearchManager) this.getSherlockActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search_class_menu_item).getActionView();
        searchViewMenuItem = (MenuItem) menu.findItem(R.id.search_class_menu_item);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getSherlockActivity().getComponentName()));
        searchView.setQueryHint("Search For a Class");
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(this);
		
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.add_class_by_qr_menu_item:
				IntentIntegrator integrator = new IntentIntegrator(this.getSherlockActivity());				
				integrator.initiateScan(IntentIntegrator.ALL_CODE_TYPES);
				break;
		}

		return super.onOptionsItemSelected(item);		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		if(parent == this.classListView)
		{
			Log.d(this.TAG, "Switch to chat 173");
			this.switchToChat(((ChatroomViewHolder)view.getTag()).idString, ((ChatroomViewHolder)view.getTag()).idSectionString, ((ChatroomViewHolder)view.getTag()).className.getText().toString());
		}
	}

	/**
	 * Handles long click on item in the list view, currently starts action
	 * menu
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) 
	{
		Globals.mode = this.getSherlockActivity().startActionMode(this);
		this.chatRoomToDelete = ( (ChatroomViewHolder)view.getTag() ).idSectionString;
		return true;
	}

	/**
	 * Handles when an contextual action mode item is clicked.
	 * For more details look at the Android information document
	 * in google docs.
	 */
	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) 
	{
		MenuInflater inflater = mode.getMenuInflater();
	    inflater.inflate(R.menu.classlist_action_menu, menu);	    
		return true;
	}

	/**
	 * Runs when the contextual action mode is created
	 */
	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) 
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Runs when the contextual action mode is destroyed
	 */
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) 
	{
		switch(item.getItemId())
		{
			case R.id.delete_class_menu_item:
			    new Restful(Restful.UNSUBSCRIBE_CHAT_PATH, Restful.POST, new String[]{"id"},new String[]{this.chatRoomToDelete}, 1, this);
			    break;
		}
		
		Globals.mode.finish();
		return true;
	}

	/**
	 * Runs when the contextual action mode gets invalidated
	 */
	@Override
	public void onDestroyActionMode(ActionMode mode) 
	{
		
	}
	
	/**
	 * This method handles the results of the QR code scan
	 */
	public void onActivityResult(int request, int result, Intent data) 
	{
		Log.d(this.TAG, "The Request Code: " + request);
		
		if(request == IntentIntegrator.REQUEST_CODE)
		{
		    IntentResult scan=IntentIntegrator.parseActivityResult(request, result, data);
		    String qrURL = scan.getContents();
		    
	    	Log.d(this.TAG, "Scanned QR code: " + qrURL);
	
		    if(qrURL == null)
		    {
		    	Log.d(this.TAG, "This could mean the user returned without scanning or there is a scan error");
		    	return;
		    }
		    int index = qrURL.lastIndexOf('/');
		    String chatRoom = qrURL.substring(index+1, qrURL.length());
		    
		    if(index == -1)
		    {
		    	Log.e(this.TAG, "Incorrect QR code format!");
		    	return;
		    }
	
		    this.updateListCalledByQR = true;
		    new Restful(Restful.JOIN_CHAT_PATH, Restful.POST, new String[]{"id"},new String[]{chatRoom}, 1, this);
		}
	}

	@Override
	public boolean onQueryTextSubmit(String query) 
	{        
		searchViewMenuItem.collapseActionView();

		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) 
	{
		return false;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) 
	{
		return new ClassEnrollLoader(this.getSherlockActivity(), Globals.appDb);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) 
	{
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) 
	{
		adapter.swapCursor(null);

	}
	
	public void switchToChat(String chatId, String sectionId, String chatName)
	{
		/**
		 * Once a class is selected it'll expand the tabs
		 */
		tabsAdapter.expand();
		
		/**
		 * If it's the same room as before, do nothing, otherwise clear the database table
		 */
		if(chatId != Globals.sectionId)
		{
			Globals.appDb.recreateChatMessages();		
		}
		
		/**
		 * Changes the sectionId
		 */
		Globals.sectionId 	= sectionId;
		Globals.chatId 		= chatId;
		Globals.chatName 	= chatName;
		
		/**
		 * Call to grab the chat messages from the server and populate them
		 */
		Globals.chatFragment.updateList();
		Globals.participantsFragment.updateList();
		
		/**
		 * And direct us to the chat for the class we are in as well as change the
		 * title to the chat's name
		 */
		tabsAdapter.getPager().setCurrentItem(1);
		this.getActivity().setTitle(chatName);
	}
	/**
	 * Re-grabs the class list and populate the class list
	 */
	public void updateList()
	{
		Globals.appDb.recreateClassEnroll();
		new Restful(Restful.GET_SUBSCRIBED_SECTIONS_PATH, Restful.GET, null, null, 0, this);
	}

	@Override
	public void onRestHandleResponseSuccess(String restCall, String response) 
	{		
		Log.d(this.TAG, response);
		if(restCall.equals(Restful.GET_SUBSCRIBED_SECTIONS_PATH))
		{	
			Globals.appDb.addClassesFromJSON(false, response);
		}
		else if(restCall.equals(Restful.JOIN_CHAT_PATH))
		{
			if(this.updateListCalledByQR)
			{
				try 
				{
					JSONObject ob = new JSONObject(response);
					Globals.sectionId = ob.getString("id_string");
					Globals.chatName = ob.getString("name");
				} 
				catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onRestPostExecutionSuccess(String restCall, String result) 
	{
		if(restCall.equals(Restful.GET_SUBSCRIBED_SECTIONS_PATH))
		{
			this.getSherlockActivity().getSupportLoaderManager().restartLoader(Globals.CLASS_LIST_LOADER, null, this);	

			if(this.updateListCalledByQR)
			{
				Log.d(this.TAG, "Switch to chat 382");
				this.switchToChat(Globals.chatId, Globals.sectionId, Globals.chatName);
				this.updateListCalledByQR = false;
			}
			
			if(Globals.sectionId != null && this.chatRoomToDelete == null)
			{
				Log.d(this.TAG, "Chat Id: " + Globals.sectionId +" Chat Name: " + Globals.chatName);
				Log.d(this.TAG, "Switch to chat 390");
				this.switchToChat(Globals.chatId, Globals.sectionId, Globals.chatName);
			}
			else if(this.chatRoomToDelete != null)
			{
				this.chatRoomToDelete = null;
			}
			
		}
		else if(restCall.equals(Restful.JOIN_CHAT_PATH))
		{
			this.updateList();
		}
		else if(restCall.equals(Restful.UNSUBSCRIBE_CHAT_PATH))
		{
			if(Globals.sectionId == null)
			{
				Globals.sectionId = null;
			}
			else if(Globals.sectionId.equals(this.chatRoomToDelete))
			{
				Globals.sectionId = null;
				Globals.chatName = null;
				this.tabsAdapter.collapse();
			}
			this.updateList();
		}
	}

	@Override
	public void onRestPostExecutionFailed(String restCall, int code, String result) 
	{
		Log.d(this.TAG, "Rest call: " + restCall + "failed with status code: " + code);
		Log.d(this.TAG,"Result from server is:\n" + result);
		
		if(restCall.equals(Restful.GET_SUBSCRIBED_SECTIONS_PATH))
		{
			if(this.updateListCalledByQR)
			{
				this.updateListCalledByQR = false;
			}
			this.chatRoomToDelete = null;
		}
		else if(restCall.equals(Restful.JOIN_CHAT_PATH))
		{
			
		}
		else if(restCall.equals(Restful.UNSUBSCRIBE_CHAT_PATH))
		{
			this.chatRoomToDelete = null;
		}
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
