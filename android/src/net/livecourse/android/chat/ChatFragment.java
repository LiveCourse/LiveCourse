package net.livecourse.android.chat;

import java.io.File;

import net.livecourse.R;
import net.livecourse.android.TabsFragmentAdapter;
import net.livecourse.database.ChatLoader;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.Globals;
import net.livecourse.utility.Utility;
import net.livecourse.widget.PopupMenu;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * This fragment shows the user the messages currently being displayed in the chat
 * as well as hold options to view the history and document; upload files via
 * camera, gallery, file system; and send messages
 */
public class ChatFragment extends SherlockFragment implements OnClickListener, OnItemClickListener, OnItemLongClickListener, ActionMode.Callback, LoaderCallbacks<Cursor>, OnRestCalled, PopupMenu.OnMenuItemClickListener
{
	private final String 		TAG = " == Chat Fragment == ";
	
	public Restful 				restful;

	private static final String KEY_CONTENT = "TestFragment:Content";
	private String 				CURRENT_CLASS = "";
	private String 				mContent = "???";
	
	/**
	 * This section declares all the views that this fragment handles Could
	 * probably do with better names lol
	 */
	private View 				chatLayout;
	private ListView 			messageListView;
	private ImageButton 		sendButtonView;
	private EditText 			sendMessageEditTextView;
	private ImageButton 		uploadButtonView;
	
	/**
	 * This is the adapter used for the message list.
	 */
	private ChatCursorAdapter 	adapter;
	
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
		this.chatLayout 				= inflater.inflate(R.layout.chat_layout, container, false);
		this.messageListView 			= (ListView) 	this.chatLayout.findViewById(R.id.message_list_view);
		this.sendButtonView 			= (ImageButton) this.chatLayout.findViewById(R.id.send_button_view);
		this.sendMessageEditTextView 	= (EditText) 	this.chatLayout.findViewById(R.id.send_message_edit_text_view);
		this.uploadButtonView 			= (ImageButton) this.chatLayout.findViewById(R.id.upload_button_view);
		
		/**
		 * Adds the adapter to the list and sends the temporary list to it
		 */
		this.adapter = new ChatCursorAdapter(this.getSherlockActivity(),null,0);
		this.messageListView.setAdapter(adapter);
		
		this.messageListView.setEmptyView(this.chatLayout.findViewById(R.id.message_list_empty_text_view));

		/**
		 * Sets the listeners
		 */
		this.sendButtonView.setOnClickListener(this);
		this.messageListView.setOnItemClickListener(this);
		this.messageListView.setOnItemLongClickListener(this);
		this.uploadButtonView.setOnClickListener(this);
				
		return chatLayout;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		Utility.hideKeyboard(this.getSherlockActivity());
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
			case R.id.view_history_menu_item:
				Log.d(this.TAG, "Running onOptionsItemSelected view history");
				DialogFragment newFragment = new HistoryDatePickerFragment();
			    newFragment.show(this.getSherlockActivity().getSupportFragmentManager(), "datePicker");
				break;
			case R.id.view_documents_menu_item:
				Log.d(this.TAG, "Running onOptionsItemSelected view documents");
				Intent a = new Intent(this.getSherlockActivity(), DocumentsActivity.class);
				this.getSherlockActivity().startActivity(a);
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
	
	
	@Override
	public void onClick(View v)
	{
		/**
		 * Handles the send button, will send the current message to the server,
		 * The client will not update the chat view until it recieves a push
		 * notification from the server
		 */
		if(v.getId() == R.id.send_button_view)
		{
			if(!sendMessageEditTextView.getText().toString().equals(""))
			{
				Globals.message = sendMessageEditTextView.getText().toString();
				new Restful(Restful.SEND_MESSAGE_PATH, Restful.POST, new String[]{"chat_id", "message"}, new String[]{Globals.chatId, Globals.message}, 2, this);
				
		        adapter.notifyDataSetChanged();
				sendMessageEditTextView.setText("");
			}
		}
		/**
		 * This is the upload button, clicking on it will open a menu with
		 * different upload options
		 */
		if(v.getId() == R.id.upload_button_view)
		{
			PopupMenu popup = new PopupMenu(this.getSherlockActivity(), v);
		    popup.inflate(R.menu.chat_upload_menu);
		    popup.setOnMenuItemClickListener(this);
		    popup.show();
		}
	}
	
	@Override
	public boolean onMenuItemClick(MenuItem item) 
	{
		switch(item.getItemId())
		{
			/**
			 * Uploading from camera, will open the camera application and
			 * save it the disk
			 */
			case R.id.upload_from_camera_item:
				Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(Utility.savePictureFromCamera()));
				this.getSherlockActivity().startActivityForResult(cameraIntent, Globals.CAMERA_RESULT);
				break;
			/**
			 * Uploading from gallery, will open up any application that can
			 * view images, if there are multiple such applications on the 
			 * device, the system will ask the user to pick if one is not
			 * already set as the default
			 */
			case R.id.upload_from_gallery_item:
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				this.getSherlockActivity().startActivityForResult(intent, Globals.GALLERY_RESULT);
				break;
			/**
			 * Uploading from file system, will open up any application that
			 * can view files, if there are multiple such applications on the
			 * device, the system will ask the user to pick if one is not
			 * already set as the default
			 */
			case R.id.upload_from_file_explorer_item:
				Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
			    fileIntent.setType("file/*");
			    this.getSherlockActivity().startActivityForResult(fileIntent, Globals.EXPLORER_RESULT);
				break;
		}
		return false;
	}
	
	@Override
	public void onActivityResult(int request, int result, Intent data) 
	{
		Log.d(this.TAG, "onActivityResult: " + request);
		
		if(result == Activity.RESULT_OK)
		{
			switch(request)
			{
				/**
				 * Upon finishing taking a picture form the camera, the application
				 * will attempt to upload it to the server
				 */
				case Globals.CAMERA_RESULT:
				    File image = new File(Globals.filePath);
			        Log.d(this.TAG, "The file path: " + Globals.filePath);
			        Log.d(this.TAG, "Chat Id: " + Globals.chatId);
					new Restful(Restful.SEND_MESSAGE_PATH, Restful.POST, new String[]{"chat_id", "message"}, new String[]{Globals.chatId,"Attempting to send image from camera"}, 2, image, this);
					break;
				/**
				 * Upon finishing selection an image form the gallery, the application
				 * will attempt to upload it to the server
				 */
				case Globals.GALLERY_RESULT:
					new Restful(Restful.SEND_MESSAGE_PATH, Restful.POST, new String[]{"chat_id", "message"}, new String[]{Globals.chatId,"Attempting to send file from gallery"}, 2, Utility.fileFromURI(data.getData()), this);
					break;
				/**
				 * Upon finishing selection an image form the file browser, the 
				 * application will attempt to upload it to the server
				 */
				case Globals.EXPLORER_RESULT:
					new Restful(Restful.SEND_MESSAGE_PATH, Restful.POST, new String[]{"chat_id", "message"}, new String[]{Globals.chatId,"Attempting to send file from explorer"}, 2, Utility.fileFromURI(data.getData()), this);
					break;
			}
		}
	}
	
	@Override
	/**
	 * Shows detailed information of the chat message if a chat message is clicked,
	 * links in the message will be shown here and is clickable
	 */
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) 
	{
		ChatMessageDialog dialog = new ChatMessageDialog(((ChatMessageViewHolder)view.getTag()).messageId, ChatMessageDialog.DATA_FROM_CHAT_MESSAGES);
		dialog.show(this.getSherlockActivity().getSupportFragmentManager(), "NoticeDialogFragment");
	}
	
	/**
	 * Handles long click on item in the list view and starts the action menu
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
	{
		Globals.mode = this.getSherlockActivity().startActionMode(this);
		
		return true;
	}
	
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) 
	{
		Globals.mode.finish();
		return true;
	}
	
	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) 
	{
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.chat_action_menu, menu);
		
		return true;
	}
	
	
	@Override
	public void onDestroyActionMode(ActionMode mode) 
	{
		//Do nothing
	}
	
	
	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) 
	{
		return false;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) 
	{
		return new ChatLoader(this.getSherlockActivity(), Globals.appDb);
	}

	@Override
	/**
	 * After loading chat messages from the database, the adapter will
	 * populate them and then set the view to the bottom, as the chat
	 * scrolls upwards
	 */
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
		this.getSherlockActivity().getSupportLoaderManager().restartLoader(Globals.CHAT_MESSAGES_LOADER, null, this);
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
			this.getSherlockActivity().getSupportLoaderManager().destroyLoader(Globals.CHAT_MESSAGES_LOADER);
			this.getSherlockActivity().getSupportLoaderManager().restartLoader(Globals.CHAT_MESSAGES_LOADER, null, this);
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
