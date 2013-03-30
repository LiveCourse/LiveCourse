package net.livecourse;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
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
import com.actionbarsherlock.app.SherlockFragment;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ClassListFragment extends SherlockFragment implements OnItemClickListener,OnItemLongClickListener, ActionMode.Callback
{

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
	private ClassListAdapter<String> adapter;
	
	/**
	 * Temporary list of classes used, will be changed later
	 */
	String[] array = {
	        "CS252",
	        "PSY200",
	        "MA261",
	        "MA265",
	        "CS307",
	        "CS180",
	        "CS240",
	        "CS250",
	        "CS251",
	        "MA161",
	        "MA165",
	        "PSY240"
		};
	ArrayList<String> classes;


    public static ClassListFragment newInstance(String content, TabsFragmentAdapter tabsAdapter) 
    {
    	ClassListFragment fragment = new ClassListFragment();
    	fragment.tabsAdapter = tabsAdapter;
        return fragment;
    }

    private String mContent = "???";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
    	/**
		 * Settings for the fragment
		 * Allows adding stuff for the options menu
		 */
		this.setHasOptionsMenu(true);
		this.setMenuVisibility(true);
		
    	/**
		 * Initialize the temporary list
		 */
		classes = new ArrayList<String>();
		classes.addAll(Arrays.asList(array));
		
		/**
		 * Conencts the list to the XML
		 */
		classListLayout = inflater.inflate(R.layout.classlist_layout, container, false);
		classListView = (ListView) classListLayout.findViewById(R.id.class_list_view);
    	
    	
    	/** 
    	 * Create the adapter and set it to the list and populate it
    	 * **/
        adapter = new ClassListAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1,classes);
        classListView.setAdapter(adapter);
        
        /**
         * The listener for clicking on an item in the list view
         */
        classListView.setOnItemClickListener(this);
        classListView.setOnItemLongClickListener(this);
 
        return classListLayout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
    
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.classlist_fragment_menu,menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.search_class_menu_item:
				//TODO: add activity that allows searching for classes via text
				break;
			case R.id.add_class_by_qr_menu_item:
				IntentIntegrator integrator = new IntentIntegrator(this.getSherlockActivity());
				integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
				break;
		}

		return super.onOptionsItemSelected(item);		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
	{
		/**
		 * Once a class is selected it'll expand the tabs
		 */
		tabsAdapter.CONTENT = new String[] { "Class List", "Chat", "Participants"};
		tabsAdapter.setCount(3);
		tabsAdapter.notifyDataSetChanged();
		
		
		/**
		 * And direct us to the chat for the class we are in
		 */
		tabsAdapter.getPager().setCurrentItem(1);
		tabsAdapter.getActivity().setTitle(adapter.getItem(arg2));
		
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
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Runs when the contextual action mode is destroyed
	 */
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
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
	 * Handles input from QR scan
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{
		  IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		  if (scanResult != null) 
		  {
		    // handle scan result
		  }
		  // else continue with any other code you need in the method
		}
}
