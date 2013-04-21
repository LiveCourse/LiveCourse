package net.livecourse.android;

import net.livecourse.R;
import net.livecourse.database.ChatMessagesLoader;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.Globals;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class ChatFragment extends SherlockFragment implements OnClickListener, OnItemLongClickListener, ActionMode.Callback, LoaderCallbacks<Cursor>, OnRestCalled
{
	private final String TAG = " == Chat Fragment == ";
	
	public Restful restful;

	private static final String KEY_CONTENT = "TestFragment:Content";
	private String CURRENT_CLASS = "";
	private String mContent = "???";
	
	/**
	 * This section declares all the views that this fragment handles Could
	 * probably do with better names lol
	 */
	private View chatLayout;
	private ListView messageListView;
	private ImageButton sendButtonView;
	private EditText sendMessageEditTextView;
	
	/**
	 * This is the adapter used for the message list.
	 */
	private ChatCursorAdapter adapter;
	
	public static ChatFragment newInstance(String content, TabsFragmentAdapter tabsAdapter) 
	{
		ChatFragment fragment = new ChatFragment();
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
		Log.d(this.TAG, "Created");
		
		Globals.chatFragment = this;
		/**
		 * Settings for the fragment
		 * Allows adding stuff for the options menu
		 */
		this.setHasOptionsMenu(true);
		this.setMenuVisibility(true);
		
		/**
		 * Connects the views to their XML equivalent
		 */
		chatLayout = inflater.inflate(R.layout.chat_layout, container, false);
		messageListView = (ListView) chatLayout.findViewById(R.id.message_list_view);
		sendButtonView = (ImageButton) chatLayout.findViewById(R.id.send_button_view);
		sendMessageEditTextView = (EditText) chatLayout.findViewById(R.id.send_message_edit_text_view);
		
		/**
		 * Adds the adapter to the list and sends the temporary list to it
		 */
		adapter = new ChatCursorAdapter(this.getSherlockActivity(),null,0);
		messageListView.setAdapter(adapter);
		

		/**
		 * Sets the listeners
		 */
		sendButtonView.setOnClickListener(this);
		messageListView.setOnItemLongClickListener(this);
				
		return chatLayout;
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
		inflater.inflate(R.menu.chat_fragment_menu,menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.view_history_item:
				Log.d(this.TAG, "Running onOptionsItemSelected view history");
				DialogFragment newFragment = new HistoryDatePickerFragment();
			    newFragment.show(this.getSherlockActivity().getSupportFragmentManager(), "datePicker");
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
				
				Globals.message = sendMessageEditTextView.getText().toString();
				new Restful(Restful.SEND_MESSAGE_PATH, Restful.POST, new String[]{"chat_id", "message"}, new String[]{Globals.chatId, Globals.message}, 2, this);
				
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
		Globals.mode = this.getSherlockActivity().startActionMode(this);
		
		return true;
	}

	/**
	 * Handles when an contextual action mode item is clicked.
	 * For more details look at the Android information document
	 * in google docs.
	 */
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) 
	{
		// TODO Auto-generated method stub
		
		Globals.mode.finish();
		return true;
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
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) 
	{
		return new ChatMessagesLoader(this.getSherlockActivity(), Globals.appDb);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) 
	{
		long startTime = System.currentTimeMillis();
		
		adapter.notifyDataSetChanged();
		adapter.swapCursor(cursor);
		this.messageListView.setStackFromBottom(true);
		
		Log.d(this.TAG, "Messages stored into listview in " + (System.currentTimeMillis() - startTime) + "ms");
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) 
	{
		adapter.swapCursor(null);
		
	}
	public void updateList()
	{		
		Globals.appDb.recreateChatMessages();
		restful = new Restful(Restful.GET_RECENT_MESSAGES_PATH,Restful.GET, new String[]{"chat_id", "num_messages"}, new String[]{Globals.chatId, Restful.MAX_MESSAGE_SIZE}, 2, this);
		
	}
	
	public void updateListNoRRecreate()
	{
		Globals.mainActivity.getSupportLoaderManager().restartLoader(Globals.CHAT_LOADER, null, this);
	}

	@Override
	public void onRestHandleResponseSuccess(String restCall, String response) 
	{		
		
		if(restCall.equals(Restful.GET_RECENT_MESSAGES_PATH))
		{
			Log.d(this.TAG, "OnRestHandlerResponse for path GET RECENT MESSAGES reached with response: " + response);
			Globals.appDb.addChatMessagesFromJSON(false, response);
		}
		else if(restCall.equals(Restful.SEND_MESSAGE_PATH))
		{
			
		}
	}

	@Override
	public void onRestPostExecutionSuccess(String restCall, String result) 
	{
		if(restCall.equals(Restful.GET_RECENT_MESSAGES_PATH))
		{
			Globals.mainActivity.getSupportLoaderManager().restartLoader(2, null, this);
		}
		else if(restCall.equals(Restful.SEND_MESSAGE_PATH))
		{
			
		}
	}

	@Override
	public void onRestPostExecutionFailed(String restCall, int code, String result) 
	{
		Log.d(this.TAG, "Rest call: " + restCall + "failed with status code: " + code);
		Log.d(this.TAG,"Result from server is:\n" + result);
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
