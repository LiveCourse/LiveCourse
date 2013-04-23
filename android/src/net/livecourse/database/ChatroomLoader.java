package net.livecourse.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ChatroomLoader extends SimpleCursorLoader
{
	private DatabaseHandler dbHandler;
	private String sectionId;

	public ChatroomLoader(Context context, DatabaseHandler dbHandler, String sectionId) 
	{
		super(context);
		this.dbHandler = dbHandler;
		this.sectionId = sectionId;
	}

	 @Override
     public Cursor loadInBackground() 
	 {
             SQLiteDatabase db = dbHandler.getReadableDatabase();
             String SQL_WHERE = DatabaseHandler.KEY_SECTION_ID_STRING + " = '" + this.sectionId + "'";
             Cursor cursor = db.query(DatabaseHandler.TABLE_CLASS_ENROLL, null, SQL_WHERE, null, null, null, null);
             return cursor;
     }
}
