package net.livecourse.database;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 1;
	
	/**
	 * Database name
	 */
	private static final String DATABASE_NAME 				= "livecourseDB";
	
	/**
	 * The tables we are going to use
	 */
	private static final String TABLE_CLASS_QUERY 			= "classQuery";
	private static final String TABLE_CLASS_ENROLL 			= "classEnroll";
	
	/**
	 * Fields used for the classroom object
	 */
	private static final String KEY_CLASS_ID 				= "id";
	private static final String KEY_CLASS_ID_STRING 		= "id_string";
	private static final String KEY_CLASS_SUBJECT_ID 		= "subject_id";
	private static final String KEY_CLASS_COURSE_NUMBER 	= "course_number";
	private static final String KEY_CLASS_NAME 				= "name";
	private static final String KEY_CLASS_INSTITUTION_ID 	= "institution_id";
	private static final String KEY_CLASS_ROOM_ID 			= "room_id";
	private static final String KEY_CLASS_START_TIME 		= "start_time";
	private static final String KEY_CLASS_END_TIME 			= "end_time";
	private static final String KEY_CLASS_START_DATE 		= "start_date";
	private static final String KEY_CLASS_END_DATE 			= "end_date";
	private static final String KEY_CLASS_DOW_MONDAY 		= "dow_monday";
	private static final String KEY_CLASS_DOW_TUESDAY 		= "dow_tuesday";
	private static final String KEY_CLASS_DOW_WEDNESDAY 	= "dow_wednesday";
	private static final String KEY_CLASS_DOW_THURSDAY 		= "dow_thursday";
	private static final String KEY_CLASS_DOW_FRIDAY 		= "dow_friday";
	private static final String KEY_CLASS_DOW_SATURDAY 		= "dow_saturday";
	private static final String KEY_CLASS_DOW_SUNDAY 		= "dow_sunday";
	
	public DatabaseHandler(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		/**
		 * Creates the class query table
		 */
		String CREATE_TABLE_CLASS_QUERY = 	"CREATE TABLE " 			+ TABLE_CLASS_QUERY 	+ "("
	                						+ KEY_CLASS_ID 				+ " int(11)," 
	                						+ KEY_CLASS_ID_STRING 		+ " varchar(12),"
	                						+ KEY_CLASS_SUBJECT_ID 		+ " int(11),"
	                						+ KEY_CLASS_COURSE_NUMBER 	+ " smallint(6),"
	                						+ KEY_CLASS_NAME 			+ " varchar(100),"
	                						+ KEY_CLASS_INSTITUTION_ID 	+ " int(11),"
	                						+ KEY_CLASS_ROOM_ID 		+ " int(11),"	
	                						+ KEY_CLASS_START_TIME 		+ " int(5),"
	                						+ KEY_CLASS_END_TIME 		+ " int(5),"	
	                						+ KEY_CLASS_START_DATE 		+ " date,"
	                						+ KEY_CLASS_END_DATE 		+ " date,"	
	                						+ KEY_CLASS_DOW_MONDAY 		+ " tinyint(1),"
	                						+ KEY_CLASS_DOW_TUESDAY 	+ " tinyint(1),"	
	                						+ KEY_CLASS_DOW_WEDNESDAY 	+ " tinyint(1),"
	                						+ KEY_CLASS_DOW_THURSDAY 	+ " tinyint(1),"	
	                						+ KEY_CLASS_DOW_FRIDAY 		+ " tinyint(1),"
	                						+ KEY_CLASS_DOW_SATURDAY 	+ " tinyint(1),"	
	                						+ KEY_CLASS_DOW_SUNDAY		+ " tinyint(1)"
	                						+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1";
        db.execSQL(CREATE_TABLE_CLASS_QUERY);
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		/**
		 * Drop the tables
		 */
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_QUERY);
		
		/**
		 * Recreate tables
		 */
		onCreate(db);
	}
	
	public void addClassQuery(Chatroom a)
	{
		/**
		 * Grab the DB
		 */
		SQLiteDatabase db = this.getWritableDatabase();
		
		/**
		 * Put all the values in
		 */
		ContentValues values = new ContentValues();
		values.put(KEY_CLASS_ID, 				a.getId());
		values.put(KEY_CLASS_ID_STRING, 		a.getIdString());
		values.put(KEY_CLASS_SUBJECT_ID, 		a.getSubjectId());
		values.put(KEY_CLASS_COURSE_NUMBER, 	a.getCourseNumber());
		values.put(KEY_CLASS_NAME, 				a.getName());
		values.put(KEY_CLASS_INSTITUTION_ID,	a.getInstitutionId());
		values.put(KEY_CLASS_ROOM_ID, 			a.getRoomId());
		values.put(KEY_CLASS_START_TIME, 		a.getStartTime());
		values.put(KEY_CLASS_END_TIME, 			a.getEndTime());
		values.put(KEY_CLASS_START_DATE, 		a.getStartDate());
		values.put(KEY_CLASS_END_DATE, 			a.getEndDate());
		values.put(KEY_CLASS_DOW_MONDAY, 		a.getDowMonday());
		values.put(KEY_CLASS_DOW_TUESDAY, 		a.getDowTuesday());
		values.put(KEY_CLASS_DOW_WEDNESDAY, 	a.getDowWednesday());
		values.put(KEY_CLASS_DOW_THURSDAY, 		a.getDowThursday());
		values.put(KEY_CLASS_DOW_FRIDAY, 		a.getDowFriday());
		values.put(KEY_CLASS_DOW_SATURDAY, 		a.getDowSaturday());
		values.put(KEY_CLASS_DOW_SUNDAY, 		a.getDowSunday());
		
		/**
		 * Insert the row into the table and the close the connection to the DB
		 */
		db.insert(TABLE_CLASS_QUERY, null, values);
		db.close();
	}
	/**
	 * TODO: NOT IMPLEMENTED MIGHT NOT BE USED
	 * @param id
	 * @return
	 */
	public Chatroom getClassQuery(int id)
	{
		//SQLiteDatabase db = this.getReadableDatabase();
		
		//Cursor cursor = db.query(TABLE_CLASS_QUERY, columns, selection, selectionArgs, groupBy, having, orderBy);

		/*
		 * SQLiteDatabase db = this.getReadableDatabase();
 
		    Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",new String[] { String.valueOf(id) }, null, null, null, null);
		    if (cursor != null)
		        cursor.moveToFirst();
		 
		    Contact contact = new Contact(Integer.parseInt(cursor.getString(0)),
		            cursor.getString(1), cursor.getString(2));
		    // return contact
		    return contact;
		 */
		return null;
	}
	
	/**
	 * TODO: NOT IMPLEMENTED MIGHT NOT BE USED
	 * @return
	 */
	public List<Chatroom> getAllClassQuery()
	{
		return null;
	}
	
	/**
	 * TODO: NOT IMPLEMENTED MIGHT NOT BE USED
	 * @return
	 */
	public int getClassQueryCount()
	{
		return 0;
	}
	
	/**
	 * TODO: NOT IMPLEMENTED MIGHT NOT BE USED
	 * @param a
	 * @return
	 */
	public int updateClassQuery(Chatroom a)
	{
		return 0;
	}
	/**
	 * TODO: NOT IMPLEMENTED MIGHT NOT BE USED
	 * @param a
	 */
	public void deleteClassQuery(Chatroom a)
	{
		
	}
	public SQLiteDatabase getDatabase()
	{
		return this.getReadableDatabase();
	}
}
