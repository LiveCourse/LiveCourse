package net.livecourse.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DocumentsLoader extends SimpleCursorLoader
{
	private DatabaseHandler dbHandler;

	public DocumentsLoader(Context context, DatabaseHandler dbHandler) 
	{
		super(context);
		this.dbHandler = dbHandler;
	}

	 @Override
     public Cursor loadInBackground() 
	 {
		 long startTime = System.currentTimeMillis();
		 
         SQLiteDatabase db = dbHandler.getReadableDatabase();         
         Cursor cursor = db.query(DatabaseHandler.TABLE_DOCUMENTS, null, null, null, null, null, null);
         Log.d(" == Documents Loader == ", "Loaded from database in " + (System.currentTimeMillis() - startTime) + "ms");
         return cursor;
     }
}
