package net.livecourse.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class is responsible for the managing the SQLite Database that the LiveCourse Android App uses.
 * 
 * @author Darren Cheng
 */
public class DatabaseHandler extends SQLiteOpenHelper
{
	private final String TAG = " == DatabaseHandler == ";
	
	private static final int DATABASE_VERSION = 25;
	
	/**
	 * Database name
	 */
	private static final String DATABASE_NAME 				= "livecourseDB";
	
	/**
	 * The tables we are going to use
	 */
	public static final String TABLE_CLASS_ENROLL 			= "classEnroll";
	public static final String TABLE_CHAT_MESSAGES			= "chatMessages";
	public static final String TABLE_PARTICIPANTS			= "participants";
	
	/**
	 * Fields used for the classroom object
	 */
	public static final String KEY_ID						= "_id";
	public static final String KEY_CLASS_ID_STRING 			= "id_string";
	public static final String KEY_CLASS_SUBJECT_ID 		= "subject_id";
	public static final String KEY_CLASS_COURSE_NUMBER 		= "course_number";
	public static final String KEY_CLASS_NAME 				= "name";
	public static final String KEY_CLASS_INSTITUTION_ID 	= "institution_id";
	public static final String KEY_CLASS_ROOM_ID 			= "room_id";
	public static final String KEY_CLASS_START_TIME 		= "start_time";
	public static final String KEY_CLASS_END_TIME 			= "end_time";
	public static final String KEY_CLASS_START_DATE 		= "start_date";
	public static final String KEY_CLASS_END_DATE 			= "end_date";
	public static final String KEY_CLASS_DOW_MONDAY 		= "dow_monday";
	public static final String KEY_CLASS_DOW_TUESDAY 		= "dow_tuesday";
	public static final String KEY_CLASS_DOW_WEDNESDAY 		= "dow_wednesday";
	public static final String KEY_CLASS_DOW_THURSDAY 		= "dow_thursday";
	public static final String KEY_CLASS_DOW_FRIDAY 		= "dow_friday";
	public static final String KEY_CLASS_DOW_SATURDAY 		= "dow_saturday";
	public static final String KEY_CLASS_DOW_SUNDAY 		= "dow_sunday";
	
	/**
	 * Fields used for the message object
	 */
	public static final String KEY_CHAT_ID					= "chat_id";
	public static final String KEY_CHAT_USER_ID				= "user_id";
	public static final String KEY_CHAT_SEND_TIME			= "send_time";
	public static final String KEY_CHAT_MESSAGE_STRING		= "message_string";
	public static final String KEY_CHAT_EMAIL				= "email";
	public static final String KEY_CHAT_DISPLAY_NAME		= "display_name";
	
	/**
	 * Fields used for the participant object
	 */
	public static final String KEY_PART_USER_ID				= "user_id";
	public static final String KEY_PART_TIME_LASTFOCUS		= "time_lastfocus";
	public static final String KEY_PART_TIME_LASTREQUEST	= "time_lastrequest";
	
	/**
	 * The constructor of the database, pass it the context.
	 * 
	 * @param context The context
	 */
	public DatabaseHandler(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	/**
	 * Creates all the tables in the database.
	 * 
	 * @param db The SQLiteDatabase to be created
	 */
	public void onCreate(SQLiteDatabase db) {
		/**
		 * Creates the class query table
		 */
		String CREATE_TABLE_CLASS_QUERY = 	"CREATE TABLE " 			+ TABLE_CLASS_ENROLL 	+ "( "
											+ KEY_ID					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
	                						+ KEY_CLASS_ID_STRING 		+ " varchar(12) UNIQUE, "
	                						+ KEY_CLASS_SUBJECT_ID 		+ " int(11), "
	                						+ KEY_CLASS_COURSE_NUMBER 	+ " smallint(6), "
	                						+ KEY_CLASS_NAME 			+ " varchar(100), "
	                						+ KEY_CLASS_INSTITUTION_ID 	+ " int(11), "
	                						+ KEY_CLASS_ROOM_ID 		+ " int(11), "	
	                						+ KEY_CLASS_START_TIME 		+ " int(5), "
	                						+ KEY_CLASS_END_TIME 		+ " int(5), "	
	                						+ KEY_CLASS_START_DATE 		+ " date, "
	                						+ KEY_CLASS_END_DATE 		+ " date, "	
	                						+ KEY_CLASS_DOW_MONDAY 		+ " tinyint(1), "
	                						+ KEY_CLASS_DOW_TUESDAY 	+ " tinyint(1), "	
	                						+ KEY_CLASS_DOW_WEDNESDAY 	+ " tinyint(1), "
	                						+ KEY_CLASS_DOW_THURSDAY 	+ " tinyint(1), "	
	                						+ KEY_CLASS_DOW_FRIDAY 		+ " tinyint(1), "
	                						+ KEY_CLASS_DOW_SATURDAY 	+ " tinyint(1), "	
	                						+ KEY_CLASS_DOW_SUNDAY		+ " tinyint(1) "
	                						+ ")";
		
		String CREATE_TABLE_CHAT_MESSAGES = "CREATE TABLE " 			+ TABLE_CHAT_MESSAGES 	+ "( "
											+ KEY_ID					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
											+ KEY_CHAT_USER_ID			+ " int(11),"
											+ KEY_CHAT_ID 				+ " int(11) UNIQUE, "
											+ KEY_CHAT_SEND_TIME		+ " int(11), "
											+ KEY_CHAT_MESSAGE_STRING 	+ " varchar(2048), "
											+ KEY_CHAT_EMAIL 			+ " varchar(255), "
											+ KEY_CHAT_DISPLAY_NAME 	+ " varchar(255) "
											+ ")";
		
		String CREATE_TABLE_PARTICIPANTS  = "CREATE TABLE " 			+ TABLE_PARTICIPANTS 	+ "( "
											+ KEY_ID					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
											+ KEY_PART_USER_ID			+ " int(11) UNIQUE, "
											+ KEY_CHAT_DISPLAY_NAME 	+ " int(255), "
											+ KEY_CHAT_EMAIL 			+ " varchar(255), "
											+ KEY_PART_TIME_LASTFOCUS	+ " int(11), "
											+ KEY_PART_TIME_LASTREQUEST + " int(11) "
											+ ")";
		
        db.execSQL(CREATE_TABLE_CLASS_QUERY);
        db.execSQL(CREATE_TABLE_CHAT_MESSAGES);
        db.execSQL(CREATE_TABLE_PARTICIPANTS);
	}

	@Override
	/**
	 * This method resets the tables and the SQL database if it detects an upgraded version,
	 * you can set the version with the database version number.
	 * 
	 * @param db The SQLiteDatabase
	 * @param oldVersion The old version number of the database
	 * @param newVersion The new version number of the database
	 */
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		/**
		 * Drop the tables
		 */
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_ENROLL);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_MESSAGES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANTS);
		
		/**
		 * Recreate tables
		 */
		onCreate(db);
		
		Log.d(this.TAG, "Recreated all database tables");
	}
	
	/**
	 * Adds a Chatroom object to the TABLE_CLASS_ENROLL table.
	 * 
	 * @param a The chatroom to be added
	 */
	public void addClassEnroll(Chatroom a)
	{
		/**
		 * Grab the DB
		 */
		SQLiteDatabase db = this.getWritableDatabase();
		
		/**
		 * Put all the values in
		 */
		ContentValues values = new ContentValues();
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
		
		long row = db.insertWithOnConflict(TABLE_CLASS_ENROLL, null, values, SQLiteDatabase.CONFLICT_IGNORE);
		db.close();
		
		Log.d(this.TAG, "Added Chatroom to row " + row + " with name: " + a.getName());
	}
	
	/**
	 * Adds a Message object to the TABLE_CHAT_MESSAGES table.
	 * 
	 * @param a The message to be added
	 */
	public void addChatMessage(ChatMessage a)
	{
		/**
		 * Grab the DB
		 */
		SQLiteDatabase db = this.getWritableDatabase();
		
		/**
		 * Put all the values in
		 */
		ContentValues values = new ContentValues();
		values.put(KEY_CHAT_ID,					a.getChatId());
		values.put(KEY_CHAT_SEND_TIME,			a.getSendTime());
		values.put(KEY_CHAT_MESSAGE_STRING,		a.getMessageString());
		values.put(KEY_CHAT_EMAIL, 				a.getEmail());
		values.put(KEY_CHAT_DISPLAY_NAME,		a.getDisplayName());
		
		/**
		 * Insert the row into the table and the close the connection to the DB
		 */
		db.insertWithOnConflict(TABLE_CHAT_MESSAGES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		db.close();
		
		//Log.d(this.TAG, "Added Chat Message to row " + row + " with message: " + a.getMessageString());
	}
	
	/**
	 * Does the same as the normal addChatMessages but does not open a
	 * SQL transaction.  You need to open it in the calling method, can be used for
	 * bulk add
	 * 
	 * @param a The message
	 * @param db The database
	 */
	public void addChatMessageWithoutSQL(ChatMessage a, SQLiteDatabase db)
	{	
		/**
		 * Put all the values in
		 */
		ContentValues values = new ContentValues();
		values.put(KEY_CHAT_ID,					a.getChatId());
		values.put(KEY_CHAT_SEND_TIME,			a.getSendTime());
		values.put(KEY_CHAT_MESSAGE_STRING,		a.getMessageString());
		values.put(KEY_CHAT_EMAIL, 				a.getEmail());
		values.put(KEY_CHAT_DISPLAY_NAME,		a.getDisplayName());
		
		/**
		 * Insert the row into the table and the close the connection to the DB
		 */
		db.insertWithOnConflict(TABLE_CHAT_MESSAGES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		
		//Log.d(this.TAG, "Added Chat Message to row " + row + " with message: " + a.getMessageString());
	}
	
	/**
	 * Adds a Participant object to the TABLE_PARTICIPANTS table.
	 * 
	 * @param a The participant to be added
	 */
	public void addParticipant(Participant a)
	{
		/**
		 * Grab the DB
		 */
		SQLiteDatabase db = this.getWritableDatabase();
		
		/**
		 * Put all the values in
		 */
		ContentValues values = new ContentValues();
		values.put(KEY_PART_USER_ID,			a.getChatId());
		values.put(KEY_CHAT_DISPLAY_NAME,		a.getDisplayName());
		values.put(KEY_CHAT_EMAIL, 				a.getEmail());
		values.put(KEY_PART_TIME_LASTFOCUS, 	a.getTime_lastfocus());
		values.put(KEY_PART_TIME_LASTREQUEST, 	a.getTime_lastrequest());
		
		/**
		 * Insert the row into the table and the close the connection to the DB
		 */
		db.insertWithOnConflict(TABLE_PARTICIPANTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
		db.close();
		
		//Log.d(this.TAG, "Added Participant to row " + row + " with name: " + a.getDisplayName());
	}
	
	/**
	 * This method recreates the table TABLE_CLASS_ENROLL.
	 */
	public void recreateClassEnroll()
	{
		//System.out.println("Attempting to recreate table: " + TABLE_CLASS_ENROLL);
		SQLiteDatabase db = this.getWritableDatabase();
		db = this.getDatabase();

		/**
		 * Drop the tables
		 */
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_ENROLL);
		
		/**
		 * Recreate tables
		 */
		/**
		 * Creates the class query table
		 */
		String CREATE_TABLE_CLASS_QUERY = 	"CREATE TABLE " 			+ TABLE_CLASS_ENROLL 	+ "( "
											+ KEY_ID					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
	                						+ KEY_CLASS_ID_STRING 		+ " varchar(12), "
	                						+ KEY_CLASS_SUBJECT_ID 		+ " int(11), "
	                						+ KEY_CLASS_COURSE_NUMBER 	+ " smallint(6), "
	                						+ KEY_CLASS_NAME 			+ " varchar(100), "
	                						+ KEY_CLASS_INSTITUTION_ID 	+ " int(11), "
	                						+ KEY_CLASS_ROOM_ID 		+ " int(11), "	
	                						+ KEY_CLASS_START_TIME 		+ " int(5), "
	                						+ KEY_CLASS_END_TIME 		+ " int(5), "	
	                						+ KEY_CLASS_START_DATE 		+ " date, "
	                						+ KEY_CLASS_END_DATE 		+ " date, "	
	                						+ KEY_CLASS_DOW_MONDAY 		+ " tinyint(1), "
	                						+ KEY_CLASS_DOW_TUESDAY 	+ " tinyint(1), "	
	                						+ KEY_CLASS_DOW_WEDNESDAY 	+ " tinyint(1), "
	                						+ KEY_CLASS_DOW_THURSDAY 	+ " tinyint(1), "	
	                						+ KEY_CLASS_DOW_FRIDAY 		+ " tinyint(1), "
	                						+ KEY_CLASS_DOW_SATURDAY 	+ " tinyint(1), "	
	                						+ KEY_CLASS_DOW_SUNDAY		+ " tinyint(1) "
	                						+ ")";
        db.execSQL(CREATE_TABLE_CLASS_QUERY);	
        db.close();
        
        Log.d(this.TAG, "Recreated TABLE_CLASS_ENROLL");
	}
	
	/**
	 * This method recreates the table TABLE_CHATE_MESSAGES.
	 */
	public void recreateChatMessages()
	{
		//System.out.println("Attempting to recreate table: " + TABLE_CHAT_MESSAGES);
		SQLiteDatabase db = this.getWritableDatabase();
		db = this.getDatabase();

		/**
		 * Drop the tables
		 */
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_MESSAGES);
		
		/**
		 * Recreate tables
		 */
		String CREATE_TABLE_CHAT_MESSAGES = "CREATE TABLE " 			+ TABLE_CHAT_MESSAGES 	+ "( "
											+ KEY_ID					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
											+ KEY_CHAT_ID 				+ " int(11) UNIQUE, "
											+ KEY_CHAT_USER_ID				+ " int(11),"
											+ KEY_CHAT_SEND_TIME		+ " int(11), "
											+ KEY_CHAT_MESSAGE_STRING 	+ " varchar(2048), "
											+ KEY_CHAT_EMAIL 			+ " varchar(255), "
											+ KEY_CHAT_DISPLAY_NAME 	+ " int(255) "
											+ ")";
		
		db.execSQL(CREATE_TABLE_CHAT_MESSAGES);
		db.close();
		
        Log.d(this.TAG, "Recreated TABLE_CHAT_MESSAGES");

	}
	
	/**
	 * This method recreates the table TABLE_PARTICIPANTS.
	 */
	public void recreateParticipants()
	{
		//System.out.println("Attempting to recreate table: " + TABLE_PARTICIPANTS);
		SQLiteDatabase db = this.getWritableDatabase();
		db = this.getDatabase();

		/**
		 * Drop the tables
		 */
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANTS);
		
		/**
		 * Recreate tables
		 */
		String CREATE_TABLE_PARTICIPANTS  = "CREATE TABLE " 			+ TABLE_PARTICIPANTS 	+ "( "
											+ KEY_ID					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
											+ KEY_PART_USER_ID			+ " int(11) UNIQUE, "
											+ KEY_CHAT_DISPLAY_NAME 	+ " int(255), "
											+ KEY_CHAT_EMAIL 			+ " varchar(255), "
											+ KEY_PART_TIME_LASTFOCUS	+ " int(11), "
											+ KEY_PART_TIME_LASTREQUEST + " int(11) "
											+ ")";
		
		db.execSQL(CREATE_TABLE_PARTICIPANTS);
		db.close();
		
        Log.d(this.TAG, "Recreated TABLE_PARTICIPANTS");
	}
	
	/**
	 * This method returns the SQLiteDatabase.
	 * 
	 * @return The SQLiteDatabase
	 */
	public SQLiteDatabase getDatabase()
	{
		return this.getReadableDatabase();
	}
}
