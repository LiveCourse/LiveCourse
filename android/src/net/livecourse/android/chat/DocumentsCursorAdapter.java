package net.livecourse.android.chat;

import java.util.Date;

import net.livecourse.R;
import net.livecourse.database.DatabaseHandler;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * This is the adapter for the documnts.  It will update the documnts with information fed in
 * from the database via ChatMessageLoader.
 * 
 * @author Darren Cheng
 */
public class DocumentsCursorAdapter extends CursorAdapter
{
	/**
	 * The context
	 */
	private Context mContext;
	
	/**
	 * The constructor, it takes in the current context, the cursor that is used to populate the list view,
	 * and the flag for setting the mode of this adapter.
	 * 
	 * @param context	The current context
	 * @param c			The cursor used to populate the list view
	 * @param flags		The mode for the adapter
	 */
	public DocumentsCursorAdapter(Context context, Cursor c, int flags) 
	{
		super(context, c, flags);
		mContext = context;
	}
	
	@Override
	/**
	 * Makes a new view to hold the data pointed to by cursor.
	 * 
	 * @param context	Interface to application's global information
	 * @param cursor	The cursor from which to get the data. The cursor is already moved to the correct position.
	 * @param parent	The parent to which the new view is attached to
	 */
	public View newView(Context context, Cursor cursor, ViewGroup parent) 
	{
		View view 					= LayoutInflater.from(mContext).inflate(R.layout.document_item_view, parent, false);
		
		DocumentViewHolder v 		= new DocumentViewHolder();
		
		v.documentName				= (TextView) view.findViewById(R.id.documents_item_document_name_text_view);
		v.documentSize				= (TextView) view.findViewById(R.id.documents_item_document_size_text_view);
		v.documentUploadTime		= (TextView) view.findViewById(R.id.documents_item_upload_time_text_view);
		v.documentUploader			= (TextView) view.findViewById(R.id.documents_item_uploader_text_view);
		
		view.setTag(v);
		
		return view;	
	}
	
	@Override
	/**
	 * Bind an existing view to the data pointed to by cursor
	 * 
	 * @param view		Existing view, returned earlier by newView
	 * @param context	Interface to application's global information
	 * @param cursor	The cursor from which to get the data. The cursor is already moved to the correct position.
	 */
	public void bindView(View view, Context context, Cursor cursor) 
	{
		DocumentViewHolder v 		= (DocumentViewHolder) view.getTag();
				
		String documentName 		= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DOCS_ORI_FILENAME));
		String documentSize	 		= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DOCS_SIZE));
		String documentUploadTime	= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DOCS_UPLOAD_TIME));
		String documentUploader		= "";
		
		documentUploadTime 			= new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date(Long.parseLong(documentUploadTime)));
		documentSize				= documentSize + " bytes";
		
		if(documentName.length() > 28)
			documentName = documentName.substring(0, 25) + "...";
		
		v.documentName				.setText(documentName		);
		v.documentSize				.setText(documentSize		);
		v.documentUploadTime		.setText(documentUploadTime	);
		v.documentUploader			.setText(documentUploader	);
		
		v.messageId 				= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_MESSAGE_ID));
		v.documentFile 				= cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_DOCS_FILENAME));
		view.setTag(v);
	}
}
