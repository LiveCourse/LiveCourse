package net.livecourse.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HistoryListLoader extends SimpleCursorLoader
{
	private DatabaseHandler HistoryListdbHandler;

	public HistoryListLoader(Context context, DatabaseHandler HistoryListdbHandler) 
	{
		super(context);
		this.HistoryListdbHandler = HistoryListdbHandler;
	}

	 @Override
     public Cursor loadInBackground() 
	 {
             SQLiteDatabase db = HistoryListdbHandler.getReadableDatabase();
             Cursor cursor = db.query(DatabaseHandler.TABLE_HISTORY, null, null, null, null, null, null);
             return cursor;
     }

}
