package net.livecourse;

import java.util.ArrayList;
import java.util.Arrays;

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
	private ClassListAdapter<String> adapter;
	
	/**
	 * Temporary list of classes used, will be changed later
	 */
	String[] array = {
	        "Darren",
	        "Hayden",
	        "Lars",
	        "Jermey",
	        "Lee",
	        "Brandon",
	        "Android",
	        "Google",
	        "Person A",
	        "Person B",
	        "Jim",
	        "Bob"
		};
	ArrayList<String> participants;

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
		participants = new ArrayList<String>();
		participants.addAll(Arrays.asList(array));
		
		/**
		 * Conencts the list to the XML
		 */
		participantsLayout = inflater.inflate(R.layout.classlist_layout, container, false);
		participantsListView = (ListView) participantsLayout.findViewById(R.id.class_list_view);
    	
    	
    	/** 
    	 * Create the adapter and set it to the list and populate it
    	 * **/
        adapter = new ClassListAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1,participants);
        participantsListView.setAdapter(adapter);
        
        participantsListView.setOnItemLongClickListener(this);
        
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
		// TODO Auto-generated method stub
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
}
