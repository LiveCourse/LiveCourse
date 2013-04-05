package net.livecourse.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ParticipantsLoader extends SimpleCursorLoader
{
	private DatabaseHandler ParticipantsdbHandler;

	public ParticipantsLoader(Context context, DatabaseHandler ParticipantsdbHandler) 
	{
		super(context);
		this.ParticipantsdbHandler = ParticipantsdbHandler;
	}

	 @Override
     public Cursor loadInBackground() 
	 {
             SQLiteDatabase db = ParticipantsdbHandler.getReadableDatabase();
             Cursor cursor = db.query(DatabaseHandler.TABLE_CHAT_MESSAGES, null, null, null, null, null, null);
             return cursor;
     }

}
