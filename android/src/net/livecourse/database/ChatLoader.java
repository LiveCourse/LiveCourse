package net.livecourse.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ChatLoader extends SimpleCursorLoader
{
	private DatabaseHandler dbHandler;

	public ChatLoader(Context context, DatabaseHandler dbHandler) 
	{
		super(context);
		this.dbHandler = dbHandler;
	}

	 @Override
     public Cursor loadInBackground() 
	 {
		 long startTime = System.currentTimeMillis();
		 
         SQLiteDatabase db = dbHandler.getReadableDatabase();         
         Cursor cursor = db.query(DatabaseHandler.TABLE_CHAT_MESSAGES, null, null, null, null, null, DatabaseHandler.KEY_MESSAGE_ID);
         Log.d(" == Chat Message Loader == ", "Loaded from database in " + (System.currentTimeMillis() - startTime) + "ms");
         return cursor;
     }

}
