package net.livecourse;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ChatFragment extends SherlockFragment implements OnClickListener, OnItemLongClickListener, ActionMode.Callback
{

	private static final String KEY_CONTENT = "TestFragment:Content";
	
	/**
	 * This is the current class we are
	 */
	private String CURRENT_CLASS = "";
	
	/**
	 * This section declares all the views that this fragment handles Could
	 * probably do with better names lol
	 */
	private View chatLayout;
	private ListView messageListView;
	private Button sendButtonView;
	private EditText sendMessageEditTextView;
	
	/**
	 * This is the adapter used for the message list.
	 * It is incomplete.
	 */
	private ChatListAdapter<String> adapter;
	
	/**
	 * Temporary list used to populate the message list
	 */
	String[] array = {"Brandon","is","amazing","d","e","f","g"}; 
	ArrayList<String> messages;
	
	public static ChatFragment newInstance(String content, TabsFragmentAdapter tabsAdapter) 
	{
		ChatFragment fragment = new ChatFragment();
		return fragment;
	}

	private String mContent = "???";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ((savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT)) {
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
		this.setMenuVisibility(false);
		
		/**
		 * Initialize the temporary list
		 */
		messages = new ArrayList<String>();
		messages.addAll(Arrays.asList(array));
		
		/**
		 * Connects the views to their XML equivalent
		 */
		chatLayout = inflater.inflate(R.layout.chat_layout, container, false);
		messageListView = (ListView) chatLayout.findViewById(R.id.message_list_view);
		sendButtonView = (Button) chatLayout.findViewById(R.id.send_button_view);
		sendMessageEditTextView = (EditText) chatLayout.findViewById(R.id.send_message_edit_text_view);
		
		/**
		 * Adds the adapter to the list and sends the temporary list to it
		 */
		adapter = new ChatListAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1,messages);
		messageListView.setAdapter(adapter);
		

		/**
		 * Sets the listeners
		 */
		sendButtonView.setOnClickListener(this);
		messageListView.setOnItemLongClickListener(this);
		
		return chatLayout;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_CONTENT, mContent);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		inflater.inflate(R.menu.chat_fragment_menu,menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.view_history_item:
				Intent historyIntent = new Intent(this.getActivity(), HistoryViewActivity.class);
				this.startActivity(historyIntent);
				break;
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
	
	/**
	 * Handles the send button. The button itself will most likely be
	 * replaced with a cooler looking image And there probably is a better
	 * way to implement a listener than this but I will need to look that up
	 * later
	 */
	@Override
	public void onClick(View v)
	{
		/**
		 * This is the Send Button
		 */
		if(v.getId() == R.id.send_button_view)
		{
			/**
			 * Checks to see if there are any text in the send message box, if there are no
			 * text, don't send any
			 */
			if(!sendMessageEditTextView.getText().toString().equals(""))
			{
				/**
				 * Update the list and sends update the adapter, then change
				 * EditText back to blank
				 */
				messages.add(sendMessageEditTextView.getText().toString());
		        adapter.notifyDataSetChanged();
				sendMessageEditTextView.setText("");
			}
		}
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
	    inflater.inflate(R.menu.chat_action_menu, menu);
	    
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
