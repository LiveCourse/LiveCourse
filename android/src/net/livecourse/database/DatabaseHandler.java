package net.livecourse.database;

import net.livecourse.utility.Globals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
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
	private final String TAG 									= " == DatabaseHandler == ";

	private static final int DATABASE_VERSION 					= 45;
	
	/**
	 * Database name
	 */
	private static final String DATABASE_NAME 					= "livecourseDB";
	
	/**
	 * The tables we are going to use
	 */
	public static final String TABLE_CLASS_ENROLL 				= "classEnroll";
	public static final String TABLE_CHAT_MESSAGES				= "chatMessages";
	public static final String TABLE_PARTICIPANTS				= "participants";
	public static final String TABLE_HISTORY					= "history";
	public static final String TABLE_DOCUMENTS					= "documents";
	
	/**
	 * Fields used for the classroom object
	 */
	public static final String KEY_ID							= "_id";
	public static final String KEY_CHAT_ID_STRING 				= "id_string";
	public static final String KEY_SECTION_ID_STRING			= "section_id_string";
	public static final String KEY_CLASS_SUBJECT_CODE 			= "subject_code";
	public static final String KEY_CLASS_COURSE_NUMBER 			= "course_number";
	public static final String KEY_CLASS_NAME 					= "name";
	public static final String KEY_CLASS_ROOM_NUMBER 			= "room_number";
	public static final String KEY_CLASS_BUILDING_NAME			= "building_name";
	public static final String KEY_CLASS_TYPE					= "class_type";
	public static final String KEY_CLASS_CRN					= "crn";
	public static final String KEY_CLASS_SECTION				= "section";
	public static final String KEY_CLASS_START_TIME 			= "start_time";
	public static final String KEY_CLASS_END_TIME 				= "end_time";
	public static final String KEY_CLASS_START_DATE 			= "start_date";
	public static final String KEY_CLASS_END_DATE 				= "end_date";
	public static final String KEY_CLASS_DOW_MONDAY 			= "dow_monday";
	public static final String KEY_CLASS_DOW_TUESDAY 			= "dow_tuesday";
	public static final String KEY_CLASS_DOW_WEDNESDAY 			= "dow_wednesday";
	public static final String KEY_CLASS_DOW_THURSDAY 			= "dow_thursday";
	public static final String KEY_CLASS_DOW_FRIDAY 			= "dow_friday";
	public static final String KEY_CLASS_DOW_SATURDAY 			= "dow_saturday";
	public static final String KEY_CLASS_DOW_SUNDAY 			= "dow_sunday";
	public static final String KEY_CLASS_INSTRUCTOR				= "instructor";
	public static final String KEY_CLASS_NOTES					= "notes";
	public static final String KEY_CLASS_CAPACITY				= "capacity";
	
	/**
	 * Fields used for the message object
	 */
	public static final String KEY_MESSAGE_ID					= "chat_id";
	public static final String KEY_USER_ID						= "user_id";
	public static final String KEY_CHAT_SEND_TIME				= "send_time";
	public static final String KEY_CHAT_MESSAGE_STRING			= "message_string";
	public static final String KEY_USER_EMAIL					= "email";
	public static final String KEY_USER_DISPLAY_NAME			= "display_name";
	
	/**
	 * Fields used for the participant object
	 */
	public static final String KEY_USER_TIME_LASTFOCUS			= "time_lastfocus";
	public static final String KEY_USER_TIME_LASTREQUEST		= "time_lastrequest";
	public static final String KEY_USER_IGNORED					= "ignored";
	
	/**
	 * Fields used for the documents object
	 */
	public static final String KEY_DOCS_FILENAME				= "filename";
	public static final String KEY_DOCS_ORI_FILENAME			= "original_filename";
	public static final String KEY_DOCS_SIZE					= "size";
	public static final String KEY_DOCS_UPLOAD_TIME				= "upload_time";
	
	/**
	 * used to lock down database access
	 */
	private final Object classListLock							= new Object();
	private final Object chatLock								= new Object();	
	private final Object particiantsLock						= new Object();
	private final Object historyLock							= new Object();
	private final Object documentsLock							= new Object();
	
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
	public void onCreate(SQLiteDatabase db) 
	{
		this.createChatMessages	(db);
		this.createClassEnroll	(db);
		this.createParticipants	(db);
		this.createHistory		(db);
		this.createDocuments	(db);
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
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_ENROLL	);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_MESSAGES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANTS	);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY		);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCUMENTS	);
		
		onCreate(db);
		
		Log.d(this.TAG, "Recreated all database tables");
	}
	
	/**
	 * Creates the table TABLE_CLASS_ENROLL
	 * @param db
	 */
	private void createClassEnroll(SQLiteDatabase db)
	{
		synchronized(this.classListLock)
		{
			String CREATE_TABLE_CLASS_ENROLL = 	"CREATE TABLE " 				+ TABLE_CLASS_ENROLL 	+ "( "
												+ KEY_ID						+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
												+ KEY_CHAT_ID_STRING 			+ " varchar(12), "
												+ KEY_SECTION_ID_STRING			+ " varchar(12), "
												+ KEY_CLASS_SUBJECT_CODE		+ " varchar(12), "
												+ KEY_CLASS_COURSE_NUMBER 		+ " int(11), "
												+ KEY_CLASS_NAME 				+ " varchar(100), "
												+ KEY_CLASS_ROOM_NUMBER			+ " varchar(12), "	
												+ KEY_CLASS_BUILDING_NAME		+ " int(11), "
												+ KEY_CLASS_TYPE				+ " varchar(20), "
												+ KEY_CLASS_CRN					+ "	int(11), "
												+ KEY_CLASS_SECTION				+ " varchar(12), "
												+ KEY_CLASS_START_TIME 			+ " int(5), "
												+ KEY_CLASS_END_TIME 			+ " int(5), "	
												+ KEY_CLASS_START_DATE 			+ " date, "
												+ KEY_CLASS_END_DATE 			+ " date, "	
												+ KEY_CLASS_DOW_MONDAY 			+ " tinyint(1), "
												+ KEY_CLASS_DOW_TUESDAY 		+ " tinyint(1), "	
												+ KEY_CLASS_DOW_WEDNESDAY 		+ " tinyint(1), "
												+ KEY_CLASS_DOW_THURSDAY 		+ " tinyint(1), "	
												+ KEY_CLASS_DOW_FRIDAY 			+ " tinyint(1), "
												+ KEY_CLASS_DOW_SATURDAY 		+ " tinyint(1), "	
												+ KEY_CLASS_DOW_SUNDAY			+ " tinyint(1), "
												+ KEY_CLASS_INSTRUCTOR			+ " varchar(100), "
												+ KEY_CLASS_NOTES				+ " varchar(100), "
												+ KEY_CLASS_CAPACITY			+ " int(11)"
												+ ")";
			
			db.execSQL(CREATE_TABLE_CLASS_ENROLL);	
		}
	}
	
	/**
	 * Creates the table TABLE_CHAT_MESSAGES
	 * @param db
	 */
	private void createChatMessages(SQLiteDatabase db)
	{
		synchronized(this.chatLock)
		{
			String CREATE_TABLE_CHAT_MESSAGES = "CREATE TABLE " 			+ TABLE_CHAT_MESSAGES 	+ "( "
												+ KEY_ID					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
												+ KEY_MESSAGE_ID 			+ " int(11) UNIQUE, "
												+ KEY_USER_ID				+ " int(11),"
												+ KEY_CHAT_SEND_TIME		+ " int(11), "
												+ KEY_CHAT_MESSAGE_STRING 	+ " varchar(2048), "
												+ KEY_USER_EMAIL 			+ " varchar(255), "
												+ KEY_USER_DISPLAY_NAME 	+ " int(255) "
												+ ")";
	
			db.execSQL(CREATE_TABLE_CHAT_MESSAGES);
		}
	}
	
	/**
	 * Creates the table TABLE_PARTICIPANTS
	 * @param db
	 */
	private void createParticipants(SQLiteDatabase db)
	{
		synchronized(this.particiantsLock)
		{
			String CREATE_TABLE_PARTICIPANTS  = "CREATE TABLE " 			+ TABLE_PARTICIPANTS 	+ "( "
												+ KEY_ID					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
												+ KEY_USER_ID				+ " int(11) UNIQUE, "
												+ KEY_USER_DISPLAY_NAME 	+ " int(255), "
												+ KEY_USER_EMAIL 			+ " varchar(255), "
												+ KEY_USER_TIME_LASTFOCUS	+ " int(11), "
												+ KEY_USER_TIME_LASTREQUEST + " int(11), "
												+ KEY_USER_IGNORED			+ " tinyint(1)"
												+ ")";
			
			db.execSQL(CREATE_TABLE_PARTICIPANTS);
		}
	}
	
	/**
	 * Creates the table TABLE_HISTORY
	 * @param db
	 */
	private void createHistory(SQLiteDatabase db)
	{
		synchronized(this.historyLock)
		{
			String CREATE_TABLE_HISTORY		  = "CREATE TABLE " 			+ TABLE_HISTORY 	+ "( "
												+ KEY_ID					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
												+ KEY_USER_ID				+ " int(11),"
												+ KEY_MESSAGE_ID 			+ " int(11), "
												+ KEY_CHAT_SEND_TIME		+ " int(11), "
												+ KEY_CHAT_MESSAGE_STRING 	+ " varchar(2048), "
												+ KEY_USER_EMAIL 			+ " varchar(255), "
												+ KEY_USER_DISPLAY_NAME 	+ " varchar(255) "
												+ ")";
			db.execSQL(CREATE_TABLE_HISTORY);
		}
	}
	
	/**
	 * Creates the table TABLE_DOCUMENTS
	 * @param db
	 */
	private void createDocuments(SQLiteDatabase db)
	{
		synchronized(this.documentsLock)
		{
			String CREATE_TABLE_DOCUMENTS 		= "CREATE TABLE " 			+ TABLE_DOCUMENTS 	+ "( "
												+ KEY_ID					+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
												+ KEY_USER_ID				+ " int(11),"
												+ KEY_CHAT_ID_STRING		+ " varchar(12), "
												+ KEY_DOCS_FILENAME			+ " varchar(255), "
												+ KEY_DOCS_ORI_FILENAME		+ " varchar(255), "
												+ KEY_DOCS_SIZE				+ " int(11), "
												+ KEY_MESSAGE_ID 			+ " int(11), "
												+ KEY_DOCS_UPLOAD_TIME		+ " int(11)"
												+ ")";
			db.execSQL(CREATE_TABLE_DOCUMENTS);
		}
	}
	/**
	 * This method recreates the table TABLE_CLASS_ENROLL.
	 */
	public void recreateClassEnroll()
	{
		synchronized(this.classListLock)
		{
			SQLiteDatabase db = this.getWritableDatabase();
			
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASS_ENROLL);
			this.createClassEnroll(db);
			
	        Log.d(this.TAG, "Recreated TABLE_CLASS_ENROLL");
		}
	}
	
	/**
	 * This method recreates the table TABLE_CHATE_MESSAGES.
	 */
	public void recreateChatMessages()
	{
		synchronized(this.chatLock)
		{
			SQLiteDatabase db = this.getWritableDatabase();
			
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_MESSAGES);
			this.createChatMessages(db);
			
	        Log.d(this.TAG, "Recreated TABLE_CHAT_MESSAGES");
		}
	}
	
	/**
	 * This method recreates the table TABLE_PARTICIPANTS.
	 */
	public void recreateParticipants()
	{
		synchronized(this.particiantsLock)
		{
			SQLiteDatabase db = this.getWritableDatabase();
	
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTICIPANTS);
			this.createParticipants(db);
			
	        Log.d(this.TAG, "Recreated TABLE_PARTICIPANTS");
		}
	}
	
	/**
	 * This method recreates the table TABLE_HISTORY
	 */
	public void recreateHistory()
	{
		synchronized(this.historyLock)
		{
			SQLiteDatabase db = this.getWritableDatabase();
			
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
			this.createHistory(db);
			
	        Log.d(this.TAG, "Recreated TABLE_HISTORY");
		}
	}
	
	/**
	 * This method recreates the table TABLE_DOCUMENTS
	 */
	public void recreateDocuments()
	{
		synchronized(this.documentsLock)
		{
			SQLiteDatabase db = this.getWritableDatabase();
			
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCUMENTS);
			this.createDocuments(db);
			
	        Log.d(this.TAG, "Recreated TABLE_DOCUMENTS");
		}
	}
	
	/**
	 * Adds a list of classes to the database from a JSON string
	 * 
	 * @param cancel 	The boolean given and used to cancel the execution of the database
	 * @param classes 	The list of classes in a string in JSON
	 */
	public void addClassesFromJSON(boolean cancel, String classes)
	{
		synchronized(this.classListLock)
		{
			JSONArray parse = null;
			JSONObject ob = null;
			SQLiteDatabase db = null;
			SQLiteStatement statement = null;
	
			if(classes.equals(""))
				return;
			
			try 
			{
				parse = new JSONArray(classes);
				
				db = this.getWritableDatabase();
				statement = db.compileStatement(
						"INSERT INTO " 	+ TABLE_CLASS_ENROLL 			+
							" ( " 		+ KEY_CHAT_ID_STRING 			+
							", "		+ KEY_SECTION_ID_STRING			+
							", "		+ KEY_CLASS_SUBJECT_CODE 		+
							", "		+ KEY_CLASS_COURSE_NUMBER 		+
							", "		+ KEY_CLASS_NAME 				+
							", "		+ KEY_CLASS_ROOM_NUMBER 		+ 
							", "		+ KEY_CLASS_BUILDING_NAME		+ 
							", "		+ KEY_CLASS_TYPE	 			+ 
							", "		+ KEY_CLASS_CRN		 			+
							", "		+ KEY_CLASS_SECTION 			+ 
							", "		+ KEY_CLASS_START_TIME 			+
							", "		+ KEY_CLASS_END_TIME 			+	
							", "		+ KEY_CLASS_START_DATE 			+
							", "		+ KEY_CLASS_END_DATE 			+
							", "		+ KEY_CLASS_DOW_MONDAY 			+
							", "		+ KEY_CLASS_DOW_TUESDAY 		+	
							", "		+ KEY_CLASS_DOW_WEDNESDAY 		+
							", "		+ KEY_CLASS_DOW_THURSDAY 		+
							", "		+ KEY_CLASS_DOW_FRIDAY 			+
							", "		+ KEY_CLASS_DOW_SATURDAY 		+	
							", "		+ KEY_CLASS_DOW_SUNDAY			+
							", "		+ KEY_CLASS_INSTRUCTOR 			+ 
							", "		+ KEY_CLASS_NOTES	 			+ 
							", "		+ KEY_CLASS_CAPACITY 			+ 							
							") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				
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
					
					statement.bindString( 1, ob.getString(	"class_id_string"		));
					statement.bindString( 2, ob.getString(	"section_id_string"		));
					statement.bindString( 3, ob.getString(	"subject_code"			));
					statement.bindString( 4, ob.getString(	"course_number"			));
					statement.bindString( 5, ob.getString(	"name"					));
					statement.bindString( 6, ob.getString(	"room_number"			));
					statement.bindString( 7, ob.getString(	"building_short_name"	));
					statement.bindString( 8, ob.getString(	"type"					));
					statement.bindString( 9, ob.getString(	"crn"					));
					statement.bindString(10, ob.getString(	"section"				));
					statement.bindString(11, ob.getString(	"start_time"			));
					statement.bindString(12, ob.getString(	"end_time"				));
					statement.bindString(13, ob.getString(	"start_date"			));
					statement.bindString(14, ob.getString(	"end_date"				));
					statement.bindString(15, ob.getString(	"dow_monday"			));
					statement.bindString(16, ob.getString(	"dow_tuesday"			));
					statement.bindString(17, ob.getString(	"dow_wednesday"			));
					statement.bindString(18, ob.getString(	"dow_thursday"			));
					statement.bindString(19, ob.getString(	"dow_friday"			));
					statement.bindString(20, ob.getString(	"dow_saturday"			));
					statement.bindString(21, ob.getString(	"dow_sunday"			));
					statement.bindString(22, ob.getString(	"instructor"			));
					statement.bindString(23, ob.getString(	"notes"					));
					statement.bindString(24, ob.getString(	"capacity"				));
					
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
		}
	}
	
	/**
	 * Adds a list of messages to the database from a JSON string
	 * 
	 * @param cancel 	The boolean given and used to cancel the execution of the database
	 * @param messages	The list of messages in a string in JSON
	 */
	public void addChatMessagesFromJSON(boolean cancel, String messages)
	{
		synchronized(this.chatLock)
		{
			JSONArray parse = null;
			JSONObject ob = null;
			SQLiteDatabase db = null;
			SQLiteStatement statement = null;
			
			//Log.d(this.TAG, "The message: " + messages);
			
			if(messages.equals(""))
				return;
			
			try 
			{
				parse = new JSONArray(messages);	
				db = this.getWritableDatabase();
				statement = db.compileStatement(
						"INSERT INTO " 	+ DatabaseHandler.TABLE_CHAT_MESSAGES + 
							" ( " 		+ DatabaseHandler.KEY_MESSAGE_ID + 
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
		        	//Log.d(this.TAG, "Message id: " + ob.getString("id") + " with message: "  + ob.getString("message_string"));
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
		}
	}
	
	/**
	 * Adds a list of history messages to the database from a JSON string
	 * 
	 * @param cancel 	The boolean given and used to cancel the execution of the database
	 * @param messages	The list of messages in a string in JSON
	 */
	public void addHistoryMessagesFromJSON(boolean cancel, String messages)
	{
		synchronized(this.historyLock)
		{
			JSONArray parse = null;
			JSONObject ob = null;
			SQLiteDatabase db = null;
			SQLiteStatement statement = null;
			
			Log.d(this.TAG, "The history message: " + messages);
			
			if(messages.equals(""))
				return;
			
			try 
			{
				parse = new JSONArray(messages);	
				db = this.getWritableDatabase();
				statement = db.compileStatement(
						"INSERT INTO " 	+ DatabaseHandler.TABLE_HISTORY + 
							" ( " 		+ DatabaseHandler.KEY_MESSAGE_ID + 
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
		        	Log.d(this.TAG, "History Message id: " + ob.getString("id") + " with message: "  + ob.getString("message_string"));
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
		}
	}
	
	/**
	 * Adds a single message to the database from a Intent
	 * 
	 * @param cancel 	The boolean given and used to cancel the execution of the database
	 * @param messages	The message intent
	 */
	public void addChatMessageFromIntent(boolean cancel, Intent messages)
	{
		synchronized(this.chatLock)
		{
			SQLiteDatabase db = Globals.appDb.getWritableDatabase();
			SQLiteStatement statement = db.compileStatement(
					"INSERT INTO " 	+ DatabaseHandler.TABLE_CHAT_MESSAGES + 
						" ( " 		+ DatabaseHandler.KEY_MESSAGE_ID + 
						", "		+ DatabaseHandler.KEY_USER_ID +
						", " 		+ DatabaseHandler.KEY_CHAT_SEND_TIME + 
						", " 		+ DatabaseHandler.KEY_CHAT_MESSAGE_STRING + 
						", " 		+ DatabaseHandler.KEY_USER_DISPLAY_NAME + 
						") VALUES (?, ?, ?, ?, ?)");
	
			db.beginTransaction();
	   		        	    	
	    	statement.bindString(1, messages.getStringExtra("message_id"));
	    	statement.bindString(2, messages.getStringExtra("user_id"));
	    	statement.bindString(3, messages.getStringExtra("send_time"));
	    	statement.bindString(4, messages.getStringExtra("message_string"));
	    	statement.bindString(5, messages.getStringExtra("display_name"));
	    	
	    	statement.execute();
	
			db.setTransactionSuccessful();	
			db.endTransaction();
				
			statement.close();
		}
	}
	
	/**
	 * Adds a list of participants to the database from a JSON string
	 * 
	 * @param cancel 	The boolean given and used to cancel the execution of the database
	 * @param messages	The list of participants in a string in JSON
	 */
	public void addParticipantsFromJSON(boolean cancel, String participants)
	{
		synchronized(this.particiantsLock)
		{
			JSONArray parse = null;
			JSONObject ob = null;
			SQLiteDatabase db = null;
			SQLiteStatement statement = null;
			
			if(participants.equals(""))
				return;
			
			try 
			{
				parse = new JSONArray(participants);
				db = Globals.appDb.getWritableDatabase();
				statement = db.compileStatement(
						"INSERT INTO " 	+ DatabaseHandler.TABLE_PARTICIPANTS 		+ 
							" ( " 		+ DatabaseHandler.KEY_USER_ID 				+ 
							", " 		+ DatabaseHandler.KEY_USER_DISPLAY_NAME 	+ 
							", " 		+ DatabaseHandler.KEY_USER_EMAIL 			+ 
							", " 		+ DatabaseHandler.KEY_USER_TIME_LASTFOCUS 	+ 
							", " 		+ DatabaseHandler.KEY_USER_TIME_LASTREQUEST + 
							", "		+ DatabaseHandler.KEY_USER_IGNORED 			+
							") VALUES (?, ?, ?, ?, ?, ?)");
				
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
					
					statement.bindString(1, ob.getString(	"id"				));
					statement.bindString(2, ob.getString(	"display_name"		));
					statement.bindString(3, ob.getString(	"email"				));
					statement.bindString(4, ob.getString(	"time_lastfocus"	));
					statement.bindString(5, ob.getString(	"time_lastrequest"	));
					statement.bindString(6, ob.getString(	"ignored"			));
					
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
		}
	}
	
	/**
	 * Adds a list of documents to the database from a JSON string
	 * 
	 * @param cancel 	The boolean given and used to cancel the execution of the database
	 * @param messages	The list of documents in a string in JSON
	 */
	public void addDocumentsFromJSON(boolean cancel, String documents)
	{
		synchronized(this.documentsLock)
		{
			JSONArray parse 			= null;
			JSONObject ob 				= null;
			SQLiteDatabase db 			= null;
			SQLiteStatement statement 	= null;
			
			if(documents.equals(""))
				return;
			
			try 
			{
				parse = new JSONArray(documents);
				db = Globals.appDb.getWritableDatabase();
				statement = db.compileStatement(
						"INSERT INTO " 	+ DatabaseHandler.TABLE_DOCUMENTS	 		+ 
							" ( " 		+ DatabaseHandler.KEY_USER_ID 				+ 
							", " 		+ DatabaseHandler.KEY_CHAT_ID_STRING	 	+ 
							", " 		+ DatabaseHandler.KEY_DOCS_FILENAME			+ 
							", " 		+ DatabaseHandler.KEY_DOCS_ORI_FILENAME	 	+ 
							", " 		+ DatabaseHandler.KEY_DOCS_SIZE				+ 
							", " 		+ DatabaseHandler.KEY_MESSAGE_ID			+ 
							", "		+ DatabaseHandler.KEY_DOCS_UPLOAD_TIME 		+
							") VALUES (?, ?, ?, ?, ?, ?, ?)");
				
				db.beginTransaction();
				for(int x = 0; x < parse.length(); x++)
				{
					if(cancel)
					{
						this.closeConnections(db, statement);
						Log.d(this.TAG, "Connection canceled while adding documents from JSON");
						return;
					}
					
					ob = parse.getJSONObject(x);
					
					statement.bindString(1, ob.getString(	"user_id"			));
					statement.bindString(2, ob.getString(	"chat_id"			));
					statement.bindString(3, ob.getString(	"filename"			));
					statement.bindString(4, ob.getString(	"original_name"		));
					statement.bindString(5, ob.getString(	"size"				));
					statement.bindString(6, ob.getString(	"message_id"		));
					statement.bindString(7, ob.getString(	"uploaded_at"		));
					
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
		}
	}
	
	/**
	 * Formats the query from participants and returns a cursor with the formatted result,
	 * this method will sort the participants by display name and than move the user's own
	 * name to the top of the list
	 * 
	 * @param db	The database
	 * @return		The cursor with the formatted result
	 */
	public Cursor queryAndFormatParticipants(SQLiteDatabase db)
	{
		Cursor cursor = db.query(DatabaseHandler.TABLE_PARTICIPANTS, null, null, null, null, null, DatabaseHandler.KEY_USER_DISPLAY_NAME);
		String[][] tempStorage = new String[cursor.getCount()][cursor.getColumnCount()];
        
		cursor.moveToFirst();
		
		if(cursor.getCount() == 1)
		{
			tempStorage[0][0] = cursor.getString(0);
			tempStorage[0][1] = cursor.getString(1);
			tempStorage[0][2] = cursor.getString(2);
			tempStorage[0][3] = cursor.getString(3);
			tempStorage[0][4] = cursor.getString(4);
			tempStorage[0][5] = cursor.getString(5);
			tempStorage[0][6] = cursor.getString(6);
		}
		else
		{
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
					tempStorage[0][6] = cursor.getString(6);
					cursor.moveToNext();
				}
				
				tempStorage[x][0] = cursor.getString(0);
				tempStorage[x][1] = cursor.getString(1);
				tempStorage[x][2] = cursor.getString(2);
				tempStorage[x][3] = cursor.getString(3);
				tempStorage[x][4] = cursor.getString(4);
				tempStorage[x][5] = cursor.getString(5);
				tempStorage[x][6] = cursor.getString(6);
				
				cursor.moveToNext();
	        }
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
