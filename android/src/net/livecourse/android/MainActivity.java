package net.livecourse.android;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.*;
import com.google.zxing.integration.android.IntentIntegrator;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;
import net.livecourse.R;
import net.livecourse.android.login.LoginActivity;
import net.livecourse.database.DatabaseHandler;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.utility.Globals;
import net.livecourse.utility.Utility;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
/**
 * 
 * The MainActivity class is the base activity for our Android project.  It handles the ActionBar and the swipey tabs.
 * The tabs are implemented through another fragment and that fragments adapter. 
 * 
 */
public class MainActivity extends SherlockFragmentActivity implements OnPageChangeListener, OnRestCalled
{
	private final String TAG = " == MainActivity == ";

	private static final int RESULT_SETTINGS = 1;
	
	/**
	 * Declares the required objects for the swipey tabs.
	 */
    private TabsFragmentAdapter mAdapter;
    private ViewPager 			mPager;
    private PageIndicator 		mIndicator;

	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if(Globals.email == null || Globals.colorPref == null)
        {
        	Log.d(this.TAG, "Loading data from prefs");
        	this.resumeSaveState();
        }
        
        Utility.changeActivityColorBasedOnPref(this, this.getSupportActionBar());
        
        Globals.mainActivity = this;
        
        /**
         * Init database
         */
        Globals.appDb = new DatabaseHandler(this.getApplicationContext());

        /**
         * The following code initializes the tabs and sets up the tabs adapter and
         * the view pager indicator
         */
        mPager = (ViewPager)findViewById(R.id.pager);
        mIndicator = (TitlePageIndicator)findViewById(R.id.indicator);
        mAdapter = new TabsFragmentAdapter(this.getSupportFragmentManager());
        
        mPager.setOffscreenPageLimit(Globals.VIEW_PAGE_LOAD_COUNT);
        mPager.setAdapter(mAdapter);
        
        mIndicator.setViewPager(mPager);
        mIndicator.setOnPageChangeListener(this);

        mAdapter.setIndicator(mIndicator);
        mAdapter.setPager(mPager);
        mAdapter.setActivity(this);

        Globals.downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        this.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        this.registerReceiver(onNotificationClick, new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));
        
        Globals.viewPager = this.mPager;
    }
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();		
		this.unregisterReceiver(onComplete);
		this.unregisterReceiver(onNotificationClick);
		Log.d(this.TAG, "onDestroy");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater=getSupportMenuInflater();
		inflater.inflate(R.menu.activity_main,menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.main_options_menu_settings:
				Intent settings = new Intent(this, SettingsActivity.class);
	            startActivityForResult(settings, RESULT_SETTINGS);
				break;
			case R.id.main_options_logout:				
				Intent intent = new Intent(this, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				this.startActivity(intent);
		        
				this.clearPrefs();
		        this.finish();		
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	
	@Override
	protected void onPause() 
	{
		super.onPause();
		SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
		
        prefs.edit().putString("pref_user_id"		, Globals.userId		).commit();
        prefs.edit().putString("pref_email"			, Globals.email			).commit();
        prefs.edit().putString("pref_display_name"	, Globals.displayName	).commit();
        prefs.edit().putString("pref_password_token", Globals.passwordToken	).commit();
        prefs.edit().putString("pref_token"			, Globals.token			).commit();
        prefs.edit().putString("pref_chat_id"		, Globals.sectionId		).commit();
        prefs.edit().putString("pref_reg_id"		, Globals.regId			).commit();
        prefs.edit().putString("pref_color"			, Globals.colorPref		).commit();
        prefs.edit().putString("pref_chat_name"		, Globals.chatName		).commit();
        
        Log.d(this.TAG, "saving color: " + Globals.colorPref + " the pref: " + prefs.getString("pref_color", null));
        
        Globals.isOnForeground = false;
        
        //Intent unregIntent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
        //unregIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
        //startService(unregIntent);
	}
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		Globals.isOnForeground = true;
	}
	
	/**
	 * This method will grab all the preferences and store them back into
	 * Global variables
	 */
	private void resumeSaveState()
	{
		SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
		
        Globals.userId 			= prefs.getString("pref_user_id"		, null	);
        Globals.email 			= prefs.getString("pref_email"			, null	);
        Globals.displayName 	= prefs.getString("pref_display_name"	, null	);
        Globals.passwordToken 	= prefs.getString("pref_password_token"	, null	);
        Globals.token 			= prefs.getString("pref_token"			, null	);
        Globals.sectionId 		= prefs.getString("pref_chat_id"		, null	);
        Globals.regId 			= prefs.getString("pref_reg_id"			, null	);
        Globals.colorPref 		= prefs.getString("pref_color"			, null	);
        Globals.chatName 		= prefs.getString("pref_chat_name"		, null	);
        
        Log.d(this.TAG, "saved color: " + Globals.colorPref + " the pref: " + prefs.getString("pref_color", null));
        Log.d(this.TAG, "saved email: " + Globals.colorPref + " the pref: " + prefs.getString("pref_email", null));
        
        Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
		registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
		registrationIntent.putExtra("sender", Globals.SENDER_ID);
		startService(registrationIntent);
		Globals.newReg = true;
	}
	
	/**
	 * This method will clear the preferences of the user, should only be used
	 * when the user decides to logout.
	 */
	private void clearPrefs()
	{
		SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
		
        prefs.edit().putString("pref_user_id"		, null	).commit();
        prefs.edit().putString("pref_email"			, null	).commit();
        prefs.edit().putString("pref_display_name"	, null	).commit();
        prefs.edit().putString("pref_password_token", null	).commit();
        prefs.edit().putString("pref_token"			, null	).commit();
        prefs.edit().putString("pref_chat_id"		, null	).commit();
        prefs.edit().putString("pref_reg_id"		, null	).commit();
        prefs.edit().putString("pref_color"			, null	).commit();
        prefs.edit().putString("pref_chat_name"		, null	).commit();
        
        Intent unregIntent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
        unregIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
        startService(unregIntent);
        
        Globals.newReg = false;
        Globals.progressDialog = null;    
        Globals.roomList = null;
		Globals.userId = null;
		Globals.email = null;
		Globals.displayName = null;
		Globals.passwordToken = null;
		Globals.query = null;
		Globals.token = null;
		Globals.sectionId = null;
		Globals.chatId = null;
		Globals.regId = null;
		Globals.colorPref = null;
		Globals.startEpoch = null;
		Globals.message = null;
		Globals.chatName = null;
	}

	@Override
	public void onPageScrollStateChanged(int state) 
	{
	    if (state == ViewPager.SCROLL_STATE_IDLE)
	    {
	    	Utility.hideKeyboard(this);
	    }
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) 
	{
		// TODO Auto-generated method stub
		
	}

    /**
     * The following code is used to change UI and logic based on which page the user is on,
     * it is currently left empty
     */
	@Override
	public void onPageSelected(int position) 
	{
		/**
		 * Currently just sets the options menu on off
		 */
		Log.d(this.TAG, "OnPageSelected entered with position: " + position);
		
		if(Globals.mode != null)
			Globals.mode.finish();
				
		switch(position)
		{
			case 0:
	    		mAdapter.getItem(0).setMenuVisibility(true);
	    		mAdapter.getItem(1).setMenuVisibility(false);
	    		mAdapter.getItem(2).setMenuVisibility(false);
	    		break;
			case 1:
	    		mAdapter.getItem(0).setMenuVisibility(false);
	    		mAdapter.getItem(1).setMenuVisibility(true);
	    		mAdapter.getItem(2).setMenuVisibility(false);
	    		break;
			case 2:
	    		mAdapter.getItem(0).setMenuVisibility(false);
	    		mAdapter.getItem(1).setMenuVisibility(false);
	    		mAdapter.getItem(2).setMenuVisibility(true);
			case 3:
				mAdapter.getItem(0).setMenuVisibility(false);
	    		mAdapter.getItem(1).setMenuVisibility(false);
	    		mAdapter.getItem(2).setMenuVisibility(false);
	    		break;
		}		
	}

	public TabsFragmentAdapter getTabsAdapter() 
	{
		return mAdapter;
	}

	public void setTabsAdapter(TabsFragmentAdapter mAdapter) 
	{
		this.mAdapter = mAdapter;
	}
	
	@Override
	/**
	 * The results from other activites will end up here, it is then
	 * forwarded to their respective fragments
	 */
	public void onActivityResult(int request, int result, Intent data) 
	{
		Log.d(this.TAG, "onActivityResult request code: " + request + " result code: " + result);
		/**
		 * Forwards the QR Code result
		 */
		switch(request)
		{
			case IntentIntegrator.REQUEST_CODE:
				Globals.classListFragment.onActivityResult(request, result, data);
				break;
			case Globals.CAMERA_RESULT:
				Globals.chatFragment.onActivityResult(request, result, data);
				break;
			case Globals.GALLERY_RESULT:
				Globals.chatFragment.onActivityResult(request, result, data);
				break;
			case Globals.EXPLORER_RESULT:
				Globals.chatFragment.onActivityResult(request, result, data);
				break;
		}		
		
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
	public void preRestExecute(String restCall) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestCancelled(String restCall, String result) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Upon completion of a download, the application will put up is own
	 * notification that is clickable and will open the file upon clicking it.
	 */
	BroadcastReceiver onComplete = new BroadcastReceiver() 
	{
		public void onReceive(Context ctxt, Intent intent) 
		{
			Long dwnId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			Cursor c = Globals.downloadManager.query(new DownloadManager.Query().setFilterById(dwnId)); 
			c.moveToFirst();
			Log.d("Main Activity", "Download Complete: " + 	c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)));
			c.close();
			
			Intent launchIntent = new Intent(Intent.ACTION_VIEW);
			Log.d("Main Activity", Utility.getMimeType(Globals.currentDownloadName));
			launchIntent.setDataAndType(Uri.fromFile(Environment.getExternalStoragePublicDirectory(Globals.currentDownloadLocation + "/" + Globals.currentDownloadName)), Utility.getMimeType(Globals.currentDownloadName));
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(Globals.mainActivity);
			stackBuilder.addNextIntent(launchIntent);
			
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

			
			Globals.notiManager = (NotificationManager) Globals.mainActivity.getSystemService(Context.NOTIFICATION_SERVICE);
			NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(Globals.mainActivity);
			notiBuilder.setContentTitle(Globals.currentDownloadName);
			notiBuilder.setContentText(Globals.currentDownloadLocation);
			notiBuilder.setSmallIcon(R.drawable.av_download);
			notiBuilder.setContentIntent(resultPendingIntent);
			
			Notification noti =notiBuilder.build();
			noti.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
			
			
			Globals.notiManager.notify(Globals.DOWNLOAD_NOTIFICATION, noti);
			
			Globals.currentDownloadLocation = null;
			Globals.currentDownloadName = null;
		}
	};

	BroadcastReceiver onNotificationClick = new BroadcastReceiver() 
	{
		public void onReceive(Context ctxt, Intent intent) 
		{
			Log.d("Main Activity", "Download CLicked");
		}
	};

}
