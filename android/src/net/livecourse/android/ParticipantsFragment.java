package net.livecourse.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.livecourse.R;
import net.livecourse.database.DatabaseHandler;
import net.livecourse.database.ParticipantsLoader;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.Globals;
import net.livecourse.utility.ParticipantViewHolder;

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

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ParticipantsFragment extends SherlockFragment implements OnItemLongClickListener,ActionMode.Callback, LoaderCallbacks<Cursor>, OnRestCalled, OnItemClickListener
{
	private final String TAG = " == Participants Fragment == ";
	private static final String KEY_CONTENT = "TestFragment:Content";

	/**
	 * The XML views
	 */
	private View clickedView;
	private View participantsLayout;
	private ListView participantsListView;
	private ParticipantsCursorAdapter adapter;

    public static ParticipantsFragment newInstance(String content, TabsFragmentAdapter tabsAdapater) 
    {
    	ParticipantsFragment fragment = new ParticipantsFragment();
        return fragment;
    }

    private String mContent = "???";

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
    	Globals.participantsFragment = this;
		Globals.appDb.recreateParticipants();
		/**
		 * Connects the list to the XML
		 */
		participantsLayout = inflater.inflate(R.layout.participants_layout, container, false);
		participantsListView = (ListView) participantsLayout.findViewById(R.id.participants_list_view);
    	
    	
    	/** 
    	 * Create the adapter and populate the view
    	 */
        adapter = new ParticipantsCursorAdapter(inflater.getContext(), null, 0);
        participantsListView.setAdapter(adapter);
        
        participantsListView.setOnItemLongClickListener(this);
                
        return participantsLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) 
    {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
    
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	{
		if(parent == this.participantsListView)
		{
			this.startUserInfo(view);
		}
	}
	

	/**
	 * Handles long click on item in the list view, currently starts action
	 * menu
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) 
	{
		this.clickedView = view;
		Globals.mode = this.getSherlockActivity().startActionMode(this);
		return true;
	}

	/**
	 * Handles when an contextual action mode item is clicked.
	 * For more details look at the Android information document
	 * in google docs.
	 */
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

		switch(item.getItemId())
		{
			case R.id.participants_fragment_participants_details_menu_item:
				this.startUserInfo(clickedView);
				break;
		}
		return false;
	}

	/**
	 * Runs when the contextual action mode is created
	 */
	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) 
	{
		MenuInflater inflater = mode.getMenuInflater();
	    inflater.inflate(R.menu.participants_action_menu, menu);
	    	    
		return true;
	}
	
	/**
	 * Runs when the contextual action mode is destroyed
	 */
	@Override
	public void onDestroyActionMode(ActionMode mode) 
	{
		
	}
	
	/**
	 * Runs when the contextual action mode gets invalidated
	 */
	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1)
	{
		return new ParticipantsLoader(this.getSherlockActivity(), Globals.appDb);
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
	
	public void startUserInfo(View v)
	{
		Intent userInfoIntent = new Intent(this.getSherlockActivity(),UserInfoActivity.class);
		userInfoIntent.putExtra("userId", ((ParticipantViewHolder) v.getTag()).userId);
		
		this.getSherlockActivity().startActivity(userInfoIntent);
	}
	
	public void updateList()
	{
		Globals.appDb.recreateParticipants();
		new Restful(Restful.GET_PARTICIPANTS_PATH, Restful.GET, new String[]{"id"}, new String[]{Globals.chatId}, 1, this);
	}
	
	public void clearList()
	{
		adapter.swapCursor(null);
	}

	@Override
	public void onRestHandleResponseSuccess(String restCall, String response) 
	{
		JSONArray parse;
		JSONObject ob;
		
		if(restCall.equals(Restful.GET_PARTICIPANTS_PATH))
		{
			SQLiteDatabase db = null;
			SQLiteStatement statement = null;
			
			try 
			{
				parse = new JSONArray(response);
				db = Globals.appDb.getWritableDatabase();
				statement = db.compileStatement(
						"INSERT INTO " 	+ DatabaseHandler.TABLE_PARTICIPANTS + 
							" ( " 		+ DatabaseHandler.KEY_PART_USER_ID + 
							", " 		+ DatabaseHandler.KEY_CHAT_DISPLAY_NAME + 
							", " 		+ DatabaseHandler.KEY_CHAT_EMAIL + 
							", " 		+ DatabaseHandler.KEY_PART_TIME_LASTFOCUS + 
							", " 		+ DatabaseHandler.KEY_PART_TIME_LASTREQUEST + 
							") VALUES (?, ?, ?, ?, ?)");
				
				db.beginTransaction();
				for(int x = 0; x < parse.length(); x++)
				{
					ob = parse.getJSONObject(x);
					
					statement.bindString(1, ob.getString("id"));
					statement.bindString(2, ob.getString("display_name"));
					statement.bindString(3, ob.getString("email"));
					statement.bindString(4, ob.getString("time_lastfocus"));
					statement.bindString(5, ob.getString("time_lastrequest"));
					
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
		}
	}

	@Override
	public void onRestPostExecutionSuccess(String restCall, String result) 
	{
		if(restCall.equals(Restful.GET_PARTICIPANTS_PATH))
		{
			this.getSherlockActivity().getSupportLoaderManager().restartLoader(3, null, this);
		}
	}

	@Override
	public void onRestPostExecutionFailed(String restCall, int code, String result) 
	{
		Log.d(this.TAG, "Rest call: " + restCall + "failed with status code: " + code);
		Log.d(this.TAG,"Result from server is:\n" + result);		
	}


}
