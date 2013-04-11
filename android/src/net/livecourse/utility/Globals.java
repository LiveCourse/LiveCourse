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
	 * The loaders
	 */
	public static final int				CLASSLIST_LOADER		= 1;
	public static final int				CHAT_LOADER				= 2;
	public static final int				PARTICIPANT_LOADER 		= 3;
	
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
    public static final int CLASS_LIST_LOADER 					= 1;
    public static final int CHAT_MESSAGES_LOADER 				= 2;
    public static final int PARTICIPANTS_LOADER 				= 3;
    
	
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
	
	/**
	 * UI Colors
	 */
	public static final String			HEX_BLUE				= "#2e587e";
	public static final String			HEX_RED					= "#7e2e2e";
	public static final String			HEX_BROWN				= "#7e4f2e";
	public static final String			HEX_GREEN				= "#2e7e3b";
	public static final String			HEX_CYAN				= "#2e7e7e";
	public static final String			HEX_PURPLE				= "#7e2e7e";
	
	/**
	 * UI Color cases
	 */
	public static final int				INDEX_BLUE				= 0;
	public static final int				INDEX_RED				= 1;
	public static final int				INDEX_BROWN				= 2;
	public static final int				INDEX_GREEN				= 3;
	public static final int				INDEX_CYAN				= 4;
	public static final int				INDEX_PURPLE			= 5;	
}

