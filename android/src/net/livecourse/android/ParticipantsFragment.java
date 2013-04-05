package net.livecourse.android;

import java.util.ArrayList;
import java.util.Arrays;
import net.livecourse.android.R;
import net.livecourse.database.ChatMessagesLoader;
import net.livecourse.database.DatabaseHandler;
import net.livecourse.database.ParticipantsLoader;
import net.livecourse.rest.REST;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class ParticipantsFragment extends SherlockFragment implements OnItemLongClickListener,ActionMode.Callback, LoaderCallbacks<Cursor>
{
	private static final String KEY_CONTENT = "TestFragment:Content";

	/**
	 * The XML views
	 */
	private View participantsLayout;
	private ListView participantsListView;
	private ParticipantsAdapter adapter;

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

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
    	/**
		 * Initialize the temporary list
		 */
		 //participants = new ArrayList<String>();
		 //participants.addAll(Arrays.asList(array));
		
		/**
		 * Connects the list to the XML
		 */
		participantsLayout = inflater.inflate(R.layout.classlist_layout, container, false);
		participantsListView = (ListView) participantsLayout.findViewById(R.id.class_list_view);
    	
    	
    	/** 
    	 * Create the adapter and populate the view
    	 */
        adapter = new ParticipantsAdapter(inflater.getContext(), null, 0);
        participantsListView.setAdapter(adapter);
        
        participantsListView.setOnItemLongClickListener(this);
        
		new REST(this.getSherlockActivity(),this,MainActivity.currentChatId,null,REST.PARTICIPANTS).execute();
        
        return participantsLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }

	/**
	 * Handles long click on item in the list view, currently starts action
	 * menu
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
	{
		this.getSherlockActivity().startActionMode(this);
		return true;
	}

	/**
	 * Handles when an contextual action mode item is clicked.
	 * For more details look at the Android information document
	 * in google docs.
	 */
	@Override
	public boolean onActionItemClicked(ActionMode mode,
			MenuItem item) {

		switch(item.getItemId())
		{
			case R.id.participants_fragment_participants_details_menu_item:
				Intent userInfoIntent = new Intent(this.getSherlockActivity(),UserInfoActivity.class);
				startActivity(userInfoIntent);
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
		return new ParticipantsLoader(this.getSherlockActivity(), MainActivity.getAppDb());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) 
	{
		adapter.notifyDataSetChanged();
		adapter.swapCursor(cursor);
		this.participantsListView.setStackFromBottom(true);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0)
	{
		adapter.swapCursor(null);
	}
	public void updateList()
	{
		new REST(this.getSherlockActivity(),this,MainActivity.currentChatId,null,REST.PARTICIPANTS).execute();
	}
	public void clearList()
	{
		adapter.swapCursor(null);
	}
	
}
