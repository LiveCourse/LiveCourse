package net.livecourse.android.notes;

import net.livecourse.R;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.Globals;
import net.livecourse.utility.Utility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class NotesAddNotesDialog extends SherlockDialogFragment implements OnRestCalled, DialogInterface.OnClickListener
{
	private final String	TAG					= " == Notes Add Notes Dialog == ";
	private final int 		ADD_NOTE_SECTION 	= 0;
	private final int 		ADD_NOTE_ITEM 		= 1;
	private final String	NOTE_SECTION_TITLE	= "Add New Section";
	private final String	NOTE_ITEM_TITLE		= "Add New Item";
	
	private int 			flag;
	private String 			parentId;
	private String 			message;
	
	private EditText notesAdd;
	
	public NotesAddNotesDialog()
	{
		this.flag 		= this.ADD_NOTE_SECTION;
		this.parentId	= "0";
		this.message	= "";
	}
	
	public NotesAddNotesDialog(String parentId)
	{
		this.flag 		= this.ADD_NOTE_ITEM;
		this.parentId 	= parentId;
		this.message	= "";
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    LayoutInflater inflater = this.getActivity().getLayoutInflater();
	    	    
	    builder.setView(inflater.inflate(R.layout.notes_add_notes_dialog_layout, null))
	           .setNegativeButton(R.string.notes_add_dialog_button_negative, this)
	           .setPositiveButton(R.string.notes_add_dialog_button_positive, this);
	    
	    if(this.flag == this.ADD_NOTE_ITEM)
	    	builder.setTitle(this.NOTE_ITEM_TITLE);
	    else if(this.flag == this.ADD_NOTE_SECTION)
	    	builder.setTitle(this.NOTE_SECTION_TITLE);
	    
	    return builder.show();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		this.notesAdd =(EditText) this.getDialog().findViewById(R.id.notes_add_notes_dialog_edit_text_view);
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) 
	{
		Log.d(this.TAG, "Which: " + which);
		switch(which)
		{
			case AlertDialog.BUTTON_POSITIVE:
				addNote();
				break;
			case AlertDialog.BUTTON_NEGATIVE:
				break;
		}
	}
	
	private void addNote()
	{
		this.message = this.notesAdd.getText().toString();
		new Restful(Restful.ADD_NOTE_PATH, Restful.POST, new String[]{"class_id_string", "parent_note_id", "text"}, new String[]{Globals.chatId, this.parentId, this.message}, 3, this);
	}

	
	@Override
	public void preRestExecute(String restCall) 
	{
		if(restCall.equals(Restful.ADD_NOTE_PATH))
		{
			Utility.startDialog(this.getSherlockActivity(), "Adding Note...", "Adding...");
		}
	}
	@Override
	public void onRestHandleResponseSuccess(String restCall, String response) 
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRestPostExecutionSuccess(String restCall, String result) 
	{
		if(restCall.equals(Restful.ADD_NOTE_PATH))
		{
			Utility.stopDialog();
			Globals.notesFragment.updateList();
		}
	}
	@Override
	public void onRestPostExecutionFailed(String restCall, int code, String result) 
	{
		Log.d(this.TAG, "Rest call: " + restCall + "failed with status code: " + code);
		Log.d(this.TAG,"Result from server is:\n" + result);
		
		if(restCall.equals(Restful.ADD_NOTE_PATH))
		{
			Utility.stopDialog();
		}
	}
	@Override
	public void onRestCancelled(String restCall, String result) 
	{
		// TODO Auto-generated method stub
		
	}
}
