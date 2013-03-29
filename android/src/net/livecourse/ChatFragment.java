package net.livecourse;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


public class ChatFragment extends SherlockFragment {

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
	String[] array = {"apple","banana","c","d","e","f","g"}; 
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
		 * Handels the send button. The button itself will most likely be
		 * replaced with a cooler looking image And there probably is a better
		 * way to implement a listener than this but I will need to look that up
		 * later
		 */
		sendButtonView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if(!sendMessageEditTextView.getText().equals(""))
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
		});
		
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
	
	
	/**
	 * This method handles when the menu button "View History" is clicked and
	 * sends the user to the history activity
	 */
	public void onViewHistoryClick(View v)
	{
		Intent historyIntent = new Intent(getActivity(), HistoryViewActivity.class);
		getActivity().startActivity(historyIntent);
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
