package net.livecourse.gcm;
 
import net.livecourse.R;
import net.livecourse.android.MainActivity;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.Globals;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
 
import com.google.android.gcm.GCMBaseIntentService;
 
public class GCMIntentService extends GCMBaseIntentService implements OnRestCalled
{
	private final String TAG = " == GCMIntentService == ";

	public GCMIntentService() 
	{
		super(Globals.SENDER_ID);
	}	 
	 
	@Override
	protected void onRegistered(Context arg0, String registrationId) 
	{
		Log.i(TAG, "Device registered: regId = " + registrationId);
		
		Globals.regId 			= registrationId;
		if(Globals.newReg)
		{
			new Restful(Restful.REGISTER_ANDROID_USER_PATH, Restful.POST, new String[]{"email","name","reg_id","device_id"}, new String[]{Globals.email,Globals.displayName,Globals.regId, Secure.getString(this.getContentResolver(),Secure.ANDROID_ID)}, 4, this);
		}
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
		
		if(!intent.getStringExtra("user_id").equals(Globals.userId))
		{
			Bitmap notPic = BitmapFactory.decodeResource(context.getResources(),R.drawable.paperairplanewhite);
			notPic = Bitmap.createScaledBitmap(notPic, 48, 48, false); 
			
			Intent launchIntent = new Intent(this, MainActivity.class);
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			stackBuilder.addNextIntent(launchIntent);
			// Gets a PendingIntent containing the entire back stack
			PendingIntent resultPendingIntent =
			        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			
			NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(Globals.mainActivity)
				.setSmallIcon(R.drawable.paperairplanewhite)
				.setLargeIcon(notPic)
				.setContentTitle(intent.getStringExtra("display_name"))
				.setContentText(intent.getStringExtra("message_string"))
				.setContentIntent(resultPendingIntent);
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			Log.d(this.TAG, "Exists: " + prefs.contains("pref_vibration") + " The vibration: " + prefs.getBoolean("pref_vibration", true));
			if(prefs.getBoolean("pref_vibration", true))
				notBuilder.setVibrate(new long[]{100,100});
			
			Notification not = notBuilder.build();
			not.flags = Notification.FLAG_AUTO_CANCEL;
			
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(71237, not);
		}
			
		Globals.appDb.addChatMessageFromIntent(false, intent);
		
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

	@Override
	public void preRestExecute(String restCall) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestHandleResponseSuccess(String restCall, String response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestPostExecutionSuccess(String restCall, String result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestPostExecutionFailed(String restCall, int code,
			String result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestCancelled(String restCall, String result) {
		// TODO Auto-generated method stub
		
	}
}
