package net.livecourse.android;

import java.util.ArrayList;
import java.util.Arrays;
import net.livecourse.android.R;
import net.livecourse.database.DatabaseHandler;
import net.livecourse.rest.REST;

import android.content.Intent;
import android.os.Bundle;
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

public class ParticipantsFragment extends SherlockFragment implements OnItemLongClickListener,ActionMode.Callback
{
	private static final String KEY_CONTENT = "TestFragment:Content";
	
	/**
	 * This is used to add the other tabs once a class is selected
	 */
	//Not currently used
	//private TabsFragmentAdapter tabsAdapter;

	/**
	 * The XML views
	 */
	private View participantsLayout;
	private ListView participantsListView;
	private ParticipantsAdapter adapter;

	/**
	 * Database
	 */
	private static DatabaseHandler partDb;
	
	/**
	 * Temporary list of classes used, will be changed later
	 */
//	String[] array = {
//	        "Darren",
//	        "Hayden",
//	        "Lars",
//	        "Jermey",
//	        "Lee",
//	        "Brandon",
//	        "Android",
//	        "Google",
//	        "Person A",
//	        "Person B",
//	        "Jim",
//	        "Bob"
//		};
//	ArrayList<String> participants;

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
        
        /**
         * Init database
         */
        partDb = new DatabaseHandler(this.getSherlockActivity());
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
        
		new REST(this.getSherlockActivity(),this,null,null,null,null,null,MainActivity.currentChatId,null,REST.PARTICIPANTS).execute();
        
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

	public static DatabaseHandler getDb() {
		return partDb;
	}

	public static void setAppDb(DatabaseHandler partDb) {
		ParticipantsFragment.partDb = partDb;
	}
	
}
