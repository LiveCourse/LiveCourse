package net.livecourse.android.notes;

import net.livecourse.R;
import net.livecourse.android.TabsFragmentAdapter;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.Globals;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class NotesFragment extends SherlockFragment implements OnRestCalled
{
	@SuppressWarnings("unused")
	private final String TAG = " == Documents Fragment == ";
	
	private static final String KEY_CONTENT = "TestFragment:Content";
	private String mContent = "???";
	private String CURRENT_CLASS = "";
	
	private View documentsLayout;
	
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
	
	public void updateList()
	{
		new Restful(Restful.GET_NOTES_PATH,Restful.GET, new String[]{"class_id_string"}, new String[]{Globals.chatId}, 1, this);		
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
		}
	}


	@Override
	public void onRestPostExecutionSuccess(String restCall, String result) 
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onRestPostExecutionFailed(String restCall, int code,String result) 
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onRestCancelled(String restCall, String result) 
	{
		// TODO Auto-generated method stub
		
	}
}
