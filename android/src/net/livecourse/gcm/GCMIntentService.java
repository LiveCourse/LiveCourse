package net.livecourse.gcm;
 
import net.livecourse.R;
import net.livecourse.database.DatabaseHandler;
import net.livecourse.utility.Globals;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
 
import com.google.android.gcm.GCMBaseIntentService;
 
public class GCMIntentService extends GCMBaseIntentService
{
	 
	public GCMIntentService() 
	{
		super(Globals.SENDER_ID);
	}
	 
	private static final String TAG = " == GCMIntentService == ";
	 
	 
	@Override
	protected void onRegistered(Context arg0, String registrationId) 
	{
		Log.i(TAG, "Device registered: regId = " + registrationId);
	}
	 
	@Override
	protected void onUnregistered(Context arg0, String arg1) 
	{
		Log.i(TAG, "unregistered = "+arg1);
	}
	 
	@Override
	protected void onMessage(Context context, Intent intent) 
	{
		Log.d(TAG, "MESSAGE RECIEVED: " + intent.getStringExtra("message_id") + " " + intent.getStringExtra("message_string"));
		
		if(Globals.chatFragment == null)
			return;
			
		Log.d(TAG, "Server chat id " + intent.getStringExtra("chat_id") + " Client chat id: " + Globals.chatId);

		if(!intent.getStringExtra("chat_id").equals(Globals.chatId))
			return;
		
		NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(Globals.mainActivity)
			.setSmallIcon(R.drawable.airplaneblue)
			.setContentTitle(intent.getStringExtra("display_name"))
			.setContentText(intent.getStringExtra("message_string"));
		
		NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			mNotificationManager.notify(71237, notBuilder.build());
			
		SQLiteDatabase db = Globals.appDb.getWritableDatabase();
		SQLiteStatement statement = db.compileStatement(
				"INSERT INTO " 	+ DatabaseHandler.TABLE_CHAT_MESSAGES + 
					" ( " 		+ DatabaseHandler.KEY_CHAT_ID + 
					", "		+ DatabaseHandler.KEY_USER_ID +
					", " 		+ DatabaseHandler.KEY_CHAT_SEND_TIME + 
					", " 		+ DatabaseHandler.KEY_CHAT_MESSAGE_STRING + 
					", " 		+ DatabaseHandler.KEY_USER_DISPLAY_NAME + 
					") VALUES (?, ?, ?, ?, ?)");

		db.beginTransaction();
   		        	
    	//statement.clearBindings();
    	
    	statement.bindString(1, intent.getStringExtra("message_id"));
    	statement.bindString(2, intent.getStringExtra("user_id"));
    	statement.bindString(3, intent.getStringExtra("send_time"));
    	statement.bindString(4, intent.getStringExtra("message_string"));
    	statement.bindString(5, intent.getStringExtra("display_name"));
    	
    	statement.execute();

		db.setTransactionSuccessful();	
		db.endTransaction();
			
		statement.close();
		db.close();
		
		//Globals.chatFragment.updateList();
		Globals.chatFragment.updateListNoRRecreate();
	}
	 
	@Override
	protected void onError(Context arg0, String errorId) 
	{
		Log.i(TAG, "Received error: " + errorId);
	}
	 
	@Override
	protected boolean onRecoverableError(Context context, String errorId) 
	{
		return super.onRecoverableError(context, errorId);
	}
}
