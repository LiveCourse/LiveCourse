package net.livecourse.android;

import net.livecourse.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class GroupNotesFragment extends SherlockFragment
{
	@SuppressWarnings("unused")
	private final String TAG = " == Documents Fragment == ";
	
	private static final String KEY_CONTENT = "TestFragment:Content";
	private String mContent = "???";
	private String CURRENT_CLASS = "";
	
	private View documentsLayout;
	
	public static GroupNotesFragment newInstance(String content, TabsFragmentAdapter tabsAdapter) 
	{
		GroupNotesFragment fragment = new GroupNotesFragment();
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
		/**
		 * Settings for the fragment
		 * Allows adding stuff for the options menu
		 */
		this.setHasOptionsMenu(true);
		
		documentsLayout = inflater.inflate(R.layout.documents_layout, container, false);
		
		
		return documentsLayout;
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
}
