package net.livecourse.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ChatMessagesLoader extends SimpleCursorLoader
{
	private DatabaseHandler dbHandler;

	public ChatMessagesLoader(Context context, DatabaseHandler dbHandler) 
	{
		super(context);
		this.dbHandler = dbHandler;
	}

	 @Override
     public Cursor loadInBackground() 
	 {
             SQLiteDatabase db = dbHandler.getReadableDatabase();
             Cursor cursor = db.query(DatabaseHandler.TABLE_CHAT_MESSAGES, null, null, null, null, null, null);
             return cursor;
     }

}
