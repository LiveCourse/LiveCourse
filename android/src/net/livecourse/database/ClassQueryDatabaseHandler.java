package net.livecourse.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ClassQueryDatabaseHandler extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_NAME = "ClassQueryManager";
	public ClassQueryDatabaseHandler(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
