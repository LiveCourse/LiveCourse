package net.livecourse.utility;

import com.actionbarsherlock.view.ActionMode;

import net.livecourse.android.ChatFragment;
import net.livecourse.android.ClassListFragment;
import net.livecourse.android.MainActivity;
import net.livecourse.android.ParticipantsFragment;
import net.livecourse.database.Chatroom;
import net.livecourse.database.DatabaseHandler;

public class Globals 
{
	/**
	 * The ID from Google's GCM Console
	 */
	public static final String SENDER_ID = "584781219532";
	
	/**
	 * Database
	 */
	public static DatabaseHandler appDb;
	
    /**
     * Fragments 
     */
	public static MainActivity mainActivity;
    public static ClassListFragment classListFragment;
    public static ChatFragment chatFragment;
    public static ParticipantsFragment participantsFragment;
    
    /**
     * The loader numbers
     */
    public static final int CLASS_LIST_LOADER = 1;
    
	
	/**
	 * Variables saved that is used globally
	 */
	public static Chatroom[] 			roomList;
	public static String				userId;
	public static String 				email;
	public static String 				name;
	public static String 				passwordToken;
	public static String 				query;
	public static String 				token;
	public static String 				chatId;
	public static String				regId;
	public static String				colorPref;
	public static String				startEpoch;
	public static String				message;
	public static String				chatName;
	
	public static ActionMode			mode;
}

