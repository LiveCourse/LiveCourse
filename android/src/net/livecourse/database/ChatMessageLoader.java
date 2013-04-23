package net.livecourse.database;

import net.livecourse.android.chat.ChatMessageDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ChatMessageLoader extends SimpleCursorLoader
{
	private DatabaseHandler dbHandler;
	private String 			messageId;
	private int 			flag;
	
	public ChatMessageLoader(Context context, DatabaseHandler dbHandler, String sectionId, int flag) 
	{
		super(context);
		this.dbHandler 	= dbHandler;
		this.messageId 	= sectionId;
		this.flag 		= flag;
	}
	
	 @Override
     public Cursor loadInBackground() 
	 {
             SQLiteDatabase db = dbHandler.getReadableDatabase();
             String SQL_WHERE = DatabaseHandler.KEY_MESSAGE_ID + " = '" + this.messageId + "'";
             Cursor cursor = null;
             if(flag == ChatMessageDialog.DATA_FROM_CHAT_MESSAGES)
            	 cursor = db.query(DatabaseHandler.TABLE_CHAT_MESSAGES, null, SQL_WHERE, null, null, null, null);
             else if(flag == ChatMessageDialog.DATA_FROM_CHAT_HISTORY)
            	 cursor = db.query(DatabaseHandler.TABLE_HISTORY, null, SQL_WHERE, null, null, null, null);
             return cursor;
     }
}
