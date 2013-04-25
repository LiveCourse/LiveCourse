package net.livecourse.android.notes;

import net.livecourse.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class NoteDialog extends SherlockDialogFragment implements DialogInterface.OnClickListener
{
	@SuppressWarnings("unused")
	private final String	TAG = " == Notes Add Notes Dialog == ";
	
	private String 			noteId;
	private String 			message;
	
	private TextView 		noteMessage;
	
	public NoteDialog(String noteId, String message)
	{
		this.noteId = noteId;
		this.message = message;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    LayoutInflater inflater = this.getActivity().getLayoutInflater();
	    	    
	    builder.setView(inflater.inflate(R.layout.note_dialog_layout, null))
	           .setNegativeButton(R.string.notes_dialog_button_negative, this)
	           .setPositiveButton(R.string.notes_dialog_button_positive, this)
	           .setTitle("Note " + noteId);
	    
	    return builder.show();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		this.noteMessage =(TextView) this.getDialog().findViewById(R.id.note_dialog_item_message_text_view);
		this.noteMessage.setText(message);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) 
	{
		switch(which)
		{
			case DialogInterface.BUTTON_POSITIVE:
				NotesAddNotesDialog noteDialog = new NotesAddNotesDialog(noteId);
		        noteDialog.show(this.getSherlockActivity().getSupportFragmentManager(), "NoticeDialogFragment");
				break;
		}
	}
}
