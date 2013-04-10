package net.livecourse.database;

import net.livecourse.utility.Globals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * This class is responsible for the managing the SQLite Database that the LiveCourse Android App uses.
 * 
 * @author Darren Cheng
 */
public class DatabaseHandler extends SQLiteOpenHelper
{
	private final String TAG = " == DatabaseHandler == ";
	
<<<<<<< HEAD
	private static final int DATABASE_VERSION = 30;
=======
	private static final int DATABASE_VERSION = 33;
>>>>>>> branch 'master' of https://hayden.visualstudio.com/DefaultCollection/_git/LiveCourse
	
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
	public static final String TABLE_HISTORY				= "history";
	
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
	public static final String KEY_USER_ID					= "user_id";
	public static final String KEY_CHAT_SEND_TIME			= "send_time";
	public static final String KEY_CHAT_MESSAGE_STRING		= "message_string";
	public static final String KEY_USER_EMAIL				= "email";
	public static final String KEY_USER_DISPLAY_NAME		= "display_name";
	
	/**
	 * Fields used for the participant object
	 */
	public static final String KEY_USER_TIME_LASTFOCUS		= "time_lastfocus";
	public static final String KEY_USER_TIME_LASTREQUEST	= "time_lastrequest";
	
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
<<<<<<< HEAD
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

		String CREATE_TABLE_HISTORY		  = "CREATE TABLE " 			+ TABLE_HISTORY 	+ "( "
											+ KEY_ID					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
											+ KEY_CHAT_USER_ID			+ " int(11),"
											+ KEY_CHAT_ID 				+ " int(11) UNIQUE, "
											+ KEY_CHAT_SEND_TIME		+ " int(11), "
											+ KEY_CHAT_MESSAGE_STRING 	+ " varchar(2048), "
											+ KEY_CHAT_EMAIL 			+ " varchar(255), "
											+ KEY_CHAT_DISPLAY_NAME 	+ " varchar(255) "
											+ ")";
											
        db.execSQL(CREATE_TABLE_CLASS_QUERY);
        db.execSQL(CREATE_TABLE_CHAT_MESSAGES);
        db.execSQL(CREATE_TABLE_PARTICIPANTS);
        db.execSQL(CREATE_TABLE_HISTORY);
=======
	public void onCreate(SQLiteDatabase db) 
	{
		this.createChatMessages(db);
		this.createClassEnroll(db);
		this.createParticipants(db);
		this.createHistory(db);
>>>>>>> branch 'master' of https://hayden.visualstudio.com/DefaultCollection/_git/LiveCourse
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
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_ENROLL);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_MESSAGES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
		
		onCreate(db);
		
		Log.d(this.TAG, "Recreated all database tables");
	}
	
	/**
	 * Creates the table TABLE_CLASS_ENROLL
	 * @param db
	 */
	private void createClassEnroll(SQLiteDatabase db)
	{
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
	}
	
	/**
	 * Creates the table TABLE_CHAT_MESSAGES
	 * @param db
	 */
	private void createChatMessages(SQLiteDatabase db)
	{
		String CREATE_TABLE_CHAT_MESSAGES = "CREATE TABLE " 			+ TABLE_CHAT_MESSAGES 	+ "( "
											+ KEY_ID					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
											+ KEY_CHAT_ID 				+ " int(11) UNIQUE, "
											+ KEY_USER_ID			+ " int(11),"
											+ KEY_CHAT_SEND_TIME		+ " int(11), "
											+ KEY_CHAT_MESSAGE_STRING 	+ " varchar(2048), "
											+ KEY_USER_EMAIL 			+ " varchar(255), "
											+ KEY_USER_DISPLAY_NAME 	+ " int(255) "
											+ ")";

		db.execSQL(CREATE_TABLE_CHAT_MESSAGES);
	}
	
	/**
	 * Creates the table TABLE_PARTICIPANTS
	 * @param db
	 */
	private void createParticipants(SQLiteDatabase db)
	{
		String CREATE_TABLE_PARTICIPANTS  = "CREATE TABLE " 			+ TABLE_PARTICIPANTS 	+ "( "
											+ KEY_ID					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
											+ KEY_USER_ID				+ " int(11) UNIQUE, "
											+ KEY_USER_DISPLAY_NAME 	+ " int(255), "
											+ KEY_USER_EMAIL 			+ " varchar(255), "
											+ KEY_USER_TIME_LASTFOCUS	+ " int(11), "
											+ KEY_USER_TIME_LASTREQUEST + " int(11) "
											+ ")";
		
		db.execSQL(CREATE_TABLE_PARTICIPANTS);
	}
	
	/**
	 * Creates the table TABLE_HISTORY
	 * @param db
	 */
	private void createHistory(SQLiteDatabase db)
	{
		String CREATE_TABLE_HISTORY		  = "CREATE TABLE " 			+ TABLE_HISTORY 	+ "( "
											+ KEY_ID					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
											+ KEY_USER_ID				+ " int(11),"
											+ KEY_CHAT_ID 				+ " int(11), "
											+ KEY_CHAT_SEND_TIME		+ " int(11), "
											+ KEY_CHAT_MESSAGE_STRING 	+ " varchar(2048), "
											+ KEY_USER_EMAIL 			+ " varchar(255), "
											+ KEY_USER_DISPLAY_NAME 	+ " varchar(255) "
											+ ")";
		db.execSQL(CREATE_TABLE_HISTORY);
	}
	/**
	 * This method recreates the table TABLE_CLASS_ENROLL.
	 */
	public void recreateClassEnroll()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_ENROLL);
		this.createClassEnroll(db);
		db.close();
		
        Log.d(this.TAG, "Recreated TABLE_CLASS_ENROLL");
	}
	
	/**
	 * This method recreates the table TABLE_CHATE_MESSAGES.
	 */
	public void recreateChatMessages()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_MESSAGES);
		this.createChatMessages(db);
		db.close();
		
        Log.d(this.TAG, "Recreated TABLE_CHAT_MESSAGES");
	}
	
	/**
	 * This method recreates the table TABLE_PARTICIPANTS.
	 */
	public void recreateParticipants()
	{
		SQLiteDatabase db = this.getWritableDatabase();

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANTS);
		this.createParticipants(db);
		db.close();
		
        Log.d(this.TAG, "Recreated TABLE_PARTICIPANTS");
	}
	
	/**
	 * This method recreates the table TABLE_HISTORY
	 */
	public void recreateHistory()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
		this.createHistory(db);
		db.close();
		
        Log.d(this.TAG, "Recreated TABLE_HISTORY");
	}
	
	/**
	 * Adds a list of classes to the database from a JSON string
	 * 
	 * @param cancel 	The boolean given and used to cancel the execution of the database
	 * @param classes 	The list of classes in a string in JSON
	 */
	public void addClassesFromJSON(boolean cancel, String classes)
	{
		JSONArray parse = null;
		JSONObject ob = null;
		SQLiteDatabase db = null;
		SQLiteStatement statement = null;

		try 
		{
			parse = new JSONArray(classes);
			db = this.getWritableDatabase();
			statement = db.compileStatement(
					"INSERT INTO " 	+ TABLE_CLASS_ENROLL +
						" ( " 		+ KEY_CLASS_ID_STRING 		+
						", "		+ KEY_CLASS_SUBJECT_ID 		+
						", "		+ KEY_CLASS_COURSE_NUMBER 	+
						", "		+ KEY_CLASS_NAME 			+
						", "		+ KEY_CLASS_INSTITUTION_ID 	+
						", "		+ KEY_CLASS_ROOM_ID 		+ 	
						", "		+ KEY_CLASS_START_TIME 		+
						", "		+ KEY_CLASS_END_TIME 		+	
						", "		+ KEY_CLASS_START_DATE 		+
						", "		+ KEY_CLASS_END_DATE 		+
						", "		+ KEY_CLASS_DOW_MONDAY 		+
						", "		+ KEY_CLASS_DOW_TUESDAY 	+	
						", "		+ KEY_CLASS_DOW_WEDNESDAY 	+
						", "		+ KEY_CLASS_DOW_THURSDAY 	+
						", "		+ KEY_CLASS_DOW_FRIDAY 		+
						", "		+ KEY_CLASS_DOW_SATURDAY 	+	
						", "		+ KEY_CLASS_DOW_SUNDAY		+
						") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			
			db.beginTransaction();
			for(int x = 0; x < parse.length(); x++)
			{
				if(cancel)
				{
					this.closeConnections(db, statement);
					Log.d(this.TAG, "Connection canceled while adding classes from JSON");
					return;
				}
				
				ob = parse.getJSONObject(x);
				
				statement.bindString(1,  ob.getString(	"id_string"		));
				statement.bindString(2,  ob.getString(	"subject_id"	));
				statement.bindString(3,  ob.getString(	"course_number"	));
				statement.bindString(4,  ob.getString(	"name"			));
				statement.bindString(5,  ob.getString(	"institution_id"));
				statement.bindString(6,  ob.getString(	"room_id"		));
				statement.bindString(7,  ob.getString(	"start_time"	));
				statement.bindString(8,  ob.getString(	"end_time"		));
				statement.bindString(9,  ob.getString(	"start_date"	));
				statement.bindString(10, ob.getString(	"end_date"		));
				statement.bindString(11, ob.getString(	"dow_monday"	));
				statement.bindString(12, ob.getString(	"dow_tuesday"	));
				statement.bindString(13, ob.getString(	"dow_wednesday"	));
				statement.bindString(14, ob.getString(	"dow_thursday"	));
				statement.bindString(15, ob.getString(	"dow_friday"	));
				statement.bindString(16, ob.getString(	"dow_saturday"	));
				statement.bindString(17, ob.getString(	"dow_sunday"	));
				
				statement.execute();
			}
			db.setTransactionSuccessful();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			db.endTransaction();
		}
		statement.close();
		db.close();		
	}
	
	/**
	 * Adds a list of messages to the database from a JSON string
	 * 
	 * @param cancel 	The boolean given and used to cancel the execution of the database
	 * @param messages	The list of messages in a string in JSON
	 */
	public void addChatMessagesFromJSON(boolean cancel, String messages)
	{
		JSONArray parse = null;
		JSONObject ob = null;
		SQLiteDatabase db = null;
		SQLiteStatement statement = null;
		
		try 
		{
			parse = new JSONArray(messages);
			db = this.getWritableDatabase();
			statement = db.compileStatement(
					"INSERT INTO " 	+ DatabaseHandler.TABLE_CHAT_MESSAGES + 
						" ( " 		+ DatabaseHandler.KEY_CHAT_ID + 
						", "		+ DatabaseHandler.KEY_USER_ID +
						", " 		+ DatabaseHandler.KEY_CHAT_SEND_TIME + 
						", " 		+ DatabaseHandler.KEY_CHAT_MESSAGE_STRING + 
						", " 		+ DatabaseHandler.KEY_USER_EMAIL + 
						", " 		+ DatabaseHandler.KEY_USER_DISPLAY_NAME + 
						") VALUES (?, ?, ?, ?, ?, ?)");

			db.beginTransaction();
			for(int x = 0;x<parse.length();x++)
	        {
				if(cancel)
				{
					this.closeConnections(db, statement);
					Log.d(this.TAG, "Connection canceled while adding messages from JSON");
					return;
				}
				
	        	ob = parse.getJSONObject(x);
	   		    	        	
	        	statement.bindString(1, ob.getString("id"));
	        	statement.bindString(2, ob.getString("user_id"));
	        	statement.bindString(3, ob.getString("send_time"));
	        	statement.bindString(4, ob.getString("message_string"));
	        	statement.bindString(5, ob.getString("email"));
	        	statement.bindString(6, ob.getString("display_name"));
	        	
	        	statement.execute();
	        }
			db.setTransactionSuccessful();	
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}	
		finally
		{
			db.endTransaction();

		}
		statement.close();
		db.close();
	}
	
	/**
	 * Adds a list of participants to the database from a JSON string
	 * 
	 * @param cancel 	The boolean given and used to cancel the execution of the database
	 * @param messages	The list of participants in a string in JSON
	 */
	public void addParticipantsFromJSON(boolean cancel, String participants)
	{
		JSONArray parse = null;
		JSONObject ob = null;
		SQLiteDatabase db = null;
		SQLiteStatement statement = null;
		
		try 
		{
			parse = new JSONArray(participants);
			db = Globals.appDb.getWritableDatabase();
			statement = db.compileStatement(
					"INSERT INTO " 	+ DatabaseHandler.TABLE_PARTICIPANTS + 
						" ( " 		+ DatabaseHandler.KEY_USER_ID + 
						", " 		+ DatabaseHandler.KEY_USER_DISPLAY_NAME + 
						", " 		+ DatabaseHandler.KEY_USER_EMAIL + 
						", " 		+ DatabaseHandler.KEY_USER_TIME_LASTFOCUS + 
						", " 		+ DatabaseHandler.KEY_USER_TIME_LASTREQUEST + 
						") VALUES (?, ?, ?, ?, ?)");
			
			db.beginTransaction();
			for(int x = 0; x < parse.length(); x++)
			{
				if(cancel)
				{
					this.closeConnections(db, statement);
					Log.d(this.TAG, "Connection canceled while adding participants from JSON");
					return;
				}
				
				ob = parse.getJSONObject(x);
				
				statement.bindString(1, ob.getString("id"));
				statement.bindString(2, ob.getString("display_name"));
				statement.bindString(3, ob.getString("email"));
				statement.bindString(4, ob.getString("time_lastfocus"));
				statement.bindString(5, ob.getString("time_lastrequest"));
				
				statement.execute();
			}
			db.setTransactionSuccessful();
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			db.endTransaction();
		}
		statement.close();
		db.close();		
	}
	
	public Cursor queryAndFormatParticipants(SQLiteDatabase db)
	{
		Cursor cursor = db.query(DatabaseHandler.TABLE_PARTICIPANTS, null, null, null, null, null, DatabaseHandler.KEY_USER_DISPLAY_NAME);
		String[][] tempStorage = new String[cursor.getCount()][cursor.getColumnCount()];
        
		cursor.moveToFirst();
		for(int x = 1; x < cursor.getCount(); x++)
		{
			if(cursor.getString(1).equals(Globals.userId))
			{
				tempStorage[0][0] = cursor.getString(0);
				tempStorage[0][1] = cursor.getString(1);
				tempStorage[0][2] = cursor.getString(2);
				tempStorage[0][3] = cursor.getString(3);
				tempStorage[0][4] = cursor.getString(4);
				tempStorage[0][5] = cursor.getString(5);
				cursor.moveToNext();
			}
			
			tempStorage[x][0] = cursor.getString(0);
			tempStorage[x][1] = cursor.getString(1);
			tempStorage[x][2] = cursor.getString(2);
			tempStorage[x][3] = cursor.getString(3);
			tempStorage[x][4] = cursor.getString(4);
			tempStorage[0][5] = cursor.getString(5);
			
			cursor.moveToNext();
        }
        
		MatrixCursor returnCursor = new MatrixCursor(cursor.getColumnNames(), cursor.getCount());
		
		for(int x = 0; x < cursor.getCount(); x++)
		{
			returnCursor.addRow(tempStorage[x]);
		}
		
		return returnCursor;
	}
	
	/**
	 * This method is used to close connections that were used in a SQL transaction
	 * when the transaction gets canceled for whatever reason
	 * 
	 * @param db 		The database whose connection is to be closed
	 * @param statement The statement which needs to be closed
	 */
	private void closeConnections(SQLiteDatabase db, SQLiteStatement statement)
	{
		db.endTransaction();
		statement.close();
		db.close();
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
