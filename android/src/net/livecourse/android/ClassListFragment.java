package net.livecourse.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.livecourse.R;
import net.livecourse.database.ClassEnrollLoader;
import net.livecourse.database.DatabaseHandler;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.ChatroomViewHolder;
import net.livecourse.utility.Globals;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
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
			this.switchToChat(((ChatroomViewHolder)view.getTag()).idString, ((ChatroomViewHolder)view.getTag()).className.getText().toString());
		}
	}

	/**
	 * Handles long click on item in the list view, currently starts action
	 * menu
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
	{
		Globals.mode = this.getSherlockActivity().startActionMode(this);
		
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
		// TODO Auto-generated method stub
		return false;
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
	
	public void switchToChat(String chatId, String chatName)
	{
		/**
		 * Once a class is selected it'll expand the tabs
		 */
		if(!tabsAdapter.CONTENT.equals(new String[] { "Class List", "Chat", "Participants"}))
		{
			tabsAdapter.CONTENT = new String[] { "Class List", "Chat", "Participants"};
			tabsAdapter.setCount(3);
			tabsAdapter.notifyDataSetChanged();
		}
		
		/**
		 * If it's the same room as before, do nothing, otherwise clear the database table
		 */
		if(chatId != Globals.chatId)
		{
			Globals.appDb.recreateChatMessages();		
		}
		
		/**
		 * Changes the chatId
		 */
		Globals.chatId = chatId;
		
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
		new Restful(Restful.GET_SUBSCRIBED_CHATS_PATH, Restful.GET, null, null, 0, this);
	}

	@Override
	public void onRestHandleResponseSuccess(String restCall, String response) 
	{
		JSONArray parse;
		JSONObject ob;
		
		if(restCall.equals(Restful.GET_SUBSCRIBED_CHATS_PATH))
		{
			SQLiteDatabase db = null;
			SQLiteStatement statement = null;
			try 
			{
				parse = new JSONArray(response);
				db = Globals.appDb.getWritableDatabase();
				statement = db.compileStatement(
						"INSERT INTO " 	+ DatabaseHandler.TABLE_CLASS_ENROLL +
							" ( " 		+ DatabaseHandler.KEY_CLASS_ID_STRING 		+
							", "		+ DatabaseHandler.KEY_CLASS_SUBJECT_ID 		+
							", "		+ DatabaseHandler.KEY_CLASS_COURSE_NUMBER 	+
							", "		+ DatabaseHandler.KEY_CLASS_NAME 			+
							", "		+ DatabaseHandler.KEY_CLASS_INSTITUTION_ID 	+
							", "		+ DatabaseHandler.KEY_CLASS_ROOM_ID 		+ 	
							", "		+ DatabaseHandler.KEY_CLASS_START_TIME 		+
							", "		+ DatabaseHandler.KEY_CLASS_END_TIME 		+	
							", "		+ DatabaseHandler.KEY_CLASS_START_DATE 		+
							", "		+ DatabaseHandler.KEY_CLASS_END_DATE 		+
							", "		+ DatabaseHandler.KEY_CLASS_DOW_MONDAY 		+
							", "		+ DatabaseHandler.KEY_CLASS_DOW_TUESDAY 	+	
							", "		+ DatabaseHandler.KEY_CLASS_DOW_WEDNESDAY 	+
							", "		+ DatabaseHandler.KEY_CLASS_DOW_THURSDAY 	+
							", "		+ DatabaseHandler.KEY_CLASS_DOW_FRIDAY 		+
							", "		+ DatabaseHandler.KEY_CLASS_DOW_SATURDAY 	+	
							", "		+ DatabaseHandler.KEY_CLASS_DOW_SUNDAY		+
							") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				
				db.beginTransaction();
				for(int x = 0; x < parse.length(); x++)
				{
					ob = parse.getJSONObject(x);
					
					statement.clearBindings();
					
					statement.bindString(1,  ob.getString(		"id_string"));
					statement.bindString(2,  ob.getString(		"subject_id"));
					statement.bindString(3,  ob.getString(	"course_number"));
					statement.bindString(4,  ob.getString(			"name"));
					statement.bindString(5,  ob.getString(	"institution_id"));
					statement.bindString(6,  ob.getString(		"room_id"));
					statement.bindString(7,  ob.getString(		"start_time"));
					statement.bindString(8,  ob.getString(		"end_time"));
					statement.bindString(9, ob.getString(		"start_date"));
					statement.bindString(10, ob.getString(		"end_date"));
					statement.bindString(11, ob.getString(		"dow_monday"));
					statement.bindString(12, ob.getString(	"dow_tuesday"));
					statement.bindString(13, ob.getString(	"dow_wednesday"));
					statement.bindString(14, ob.getString(	"dow_thursday"));
					statement.bindString(15, ob.getString(		"dow_friday"));
					statement.bindString(16, ob.getString(	"dow_saturday"));
					statement.bindString(17, ob.getString(		"dow_sunday"));
					
					statement.execute();
					/*
					room.setIdString(ob.getString(		"id_string"));
					room.setSubjectId(ob.getString(		"subject_id"));
					room.setCourseNumber(ob.getString(	"course_number"));
					room.setName(ob.getString(			"name"));
					room.setStartTime(ob.getString(		"start_time"));	            	
					room.setInstitutionId(ob.getString(	"institution_id"));
					room.setRoomId(ob.getString(		"room_id"));
					room.setStartTime(ob.getString(		"start_time"));
					room.setEndTime(ob.getString(		"end_time"));
					room.setStartDate(ob.getString(		"start_date"));
					room.setEndDate(ob.getString(		"end_date"));
					room.setDowMonday(ob.getString(		"dow_monday"));
					room.setDowTuesday(ob.getString(	"dow_tuesday"));
					room.setDowWednesday(ob.getString(	"dow_wednesday"));
					room.setDowThursday(ob.getString(	"dow_thursday"));
					room.setDowFriday(ob.getString(		"dow_friday"));
					room.setDowSaturday(ob.getString(	"dow_saturday"));
					room.setDowSunday(ob.getString(		"dow_sunday"));
					
					Globals.appDb.addClassEnroll(room);					
					*/
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
		}
		else if(restCall.equals(Restful.JOIN_CHAT_PATH))
		{
			if(this.updateListCalledByQR)
			{
				try 
				{
					ob = new JSONObject(response);
					Globals.chatId = ob.getString("id_string");
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
		if(restCall.equals(Restful.GET_SUBSCRIBED_CHATS_PATH))
		{
			this.getSherlockActivity().getSupportLoaderManager().restartLoader(1, null, this);	
			
			if(this.updateListCalledByQR)
			{
				//this.switchToChat(Globals.chatId, Globals.chatName);
				this.updateListCalledByQR = false;
			}
			
		}
		else if(restCall.equals(Restful.JOIN_CHAT_PATH))
		{
			this.updateList();
		}
	}

	@Override
	public void onRestPostExecutionFailed(String restCall, int code, String result) 
	{
		Log.d(this.TAG, "Rest call: " + restCall + "failed with status code: " + code);
		Log.d(this.TAG,"Result from server is:\n" + result);
		
		if(restCall.equals(Restful.GET_SUBSCRIBED_CHATS_PATH))
		{
			if(this.updateListCalledByQR)
			{
				this.updateListCalledByQR = false;
			}
		}
		else if(restCall.equals(Restful.JOIN_CHAT_PATH))
		{
			
		}
	}
}
