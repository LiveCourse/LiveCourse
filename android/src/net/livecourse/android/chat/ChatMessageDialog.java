package net.livecourse.android.chat;

import java.util.Date;

import net.livecourse.R;
import net.livecourse.database.ChatMessageLoader;
import net.livecourse.database.DatabaseHandler;
import net.livecourse.utility.Globals;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

public class ChatMessageDialog extends DialogFragment implements DialogInterface.OnClickListener, LoaderCallbacks<Cursor>
{
	public static final int 	DATA_FROM_CHAT_MESSAGES = 0;
	public static final int 	DATA_FROM_CHAT_HISTORY 	= 1;
	
	private String 				messageId;
	private int					flag;
	
	private TextView 			chatMessageUser;
	private TextView 			chatMessageTime;
	private TextView 			chatMessageString;
	//private TextView 			chatMessageFile;
	
	public ChatMessageDialog(String messageId, int flag)
	{
		this.messageId 	= messageId;
		this.flag 		= flag;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    LayoutInflater inflater = this.getActivity().getLayoutInflater();
	    
	    	    
	    builder.setView(inflater.inflate(R.layout.chatmessage_dialog_layout, null))
	           .setNegativeButton(R.string.chatmessage_dialog_button_negative, this)
	           .setTitle(this.messageId);
	    
	    return builder.show();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		this.chatMessageUser 	= (TextView) this.getDialog().findViewById(R.id.chatmessage_dialog_item_user_text_view		);
	    this.chatMessageTime 	= (TextView) this.getDialog().findViewById(R.id.chatmessage_dialog_item_time_view			);
	    this.chatMessageString 	= (TextView) this.getDialog().findViewById(R.id.chatmessage_dialog_item_message_text_view	);
	    //this.chatMessageFile 	= (TextView) this.getDialog().findViewById(R.id.chatmessage_dialog_item_file_text_view		);
	    
    	this.getActivity().getSupportLoaderManager().restartLoader(Globals.CHATMESSAGE_LOADER, null, this);
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		this.getDialog().cancel();
	}
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) 
	{
		return new ChatMessageLoader(this.getActivity(), Globals.appDb, this.messageId, this.flag);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) 
	{
		this.populateDialogFromDatabase(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) 
	{
		// TODO Auto-generated method stub
		
	}
	
	private void populateDialogFromDatabase(Cursor cursor)
	{
		if(!cursor.moveToFirst())
		{
			Log.e("Chat Message Dialog", "cursor is null");
			return;
		}
		
		String messageUser		= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_USER_DISPLAY_NAME	));
		String messageTime 		= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CHAT_SEND_TIME	));
		String messageString 	= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_CHAT_MESSAGE_STRING	));
		//String chatFile		= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_	));
		
		String messageTitle 	= "Message " + this.messageId;
		
		messageTime = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date(Long.parseLong(messageTime)*1000));
		
		this.getDialog()		.setTitle(messageTitle);
		
		this.chatMessageUser	.setText(messageUser);
		this.chatMessageTime	.setText(messageTime);
		this.chatMessageString	.setText(messageString);
		//this.chatMessageFile.setText(null)
		
		Linkify.addLinks(this.chatMessageString, Linkify.ALL);
		
		cursor.close();
	}

}
