package net.livecourse.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ParticipantsLoader extends SimpleCursorLoader
{
	private DatabaseHandler dbHandler;

	public ParticipantsLoader(Context context, DatabaseHandler ParticipantsdbHandler) 
	{
		super(context);
		this.dbHandler = ParticipantsdbHandler;
	}

	 @Override
     public Cursor loadInBackground() 
	 {
             SQLiteDatabase db = dbHandler.getReadableDatabase();
             Cursor cursor = dbHandler.queryAndFormatParticipants(db);
             return cursor;
     }

}
