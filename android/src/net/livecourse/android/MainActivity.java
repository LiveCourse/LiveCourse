package net.livecourse.android;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.*;
import com.google.zxing.integration.android.IntentIntegrator;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;
import net.livecourse.R;
import net.livecourse.database.DatabaseHandler;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.utility.Globals;
import net.livecourse.utility.Utility;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.widget.Toast;
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
	public static final int VIEW_PAGE_LOAD_COUNT = 3;
	
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
        
        mPager.setOffscreenPageLimit(MainActivity.VIEW_PAGE_LOAD_COUNT);
        mPager.setAdapter(mAdapter);
        
        mIndicator.setViewPager(mPager);
        mIndicator.setOnPageChangeListener(this);

        mAdapter.setIndicator(mIndicator);
        mAdapter.setPager(mPager);
        mAdapter.setActivity(this);
        
        
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
				
				Toast.makeText(this, "Logging Out", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(this, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				
				Globals.roomList = null;
				Globals.userId = null;
				Globals.email = null;
				Globals.displayName = null;
				Globals.passwordToken = null;
				Globals.query = null;
				Globals.token = null;
				Globals.chatId = null;
				Globals.regId = null;
				Globals.colorPref = null;
				Globals.startEpoch = null;
				Globals.message = null;
				Globals.chatName = null;
				
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
		        
				this.startActivity(intent);
				
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
        prefs.edit().putString("pref_chat_id"		, Globals.chatId		).commit();
        prefs.edit().putString("pref_reg_id"		, Globals.regId			).commit();
        prefs.edit().putString("pref_color"			, Globals.colorPref		).commit();
        prefs.edit().putString("pref_chat_name"		, Globals.chatName		).commit();
        
        //Intent unregIntent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
        //unregIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
        //startService(unregIntent);
	}
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
	}
	
	private void resumeSaveState()
	{
		SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
		
        prefs.getString("pref_user_id"			, Globals.userId		);
        prefs.getString("pref_email"			, Globals.email			);
        prefs.getString("pref_display_name"		, Globals.displayName	);
        prefs.getString("pref_password_token"	, Globals.passwordToken	);
        prefs.getString("pref_token"			, Globals.token			);
        prefs.getString("pref_chat_id"			, Globals.chatId		);
        prefs.getString("pref_reg_id"			, Globals.regId			);
        prefs.getString("pref_color"			, Globals.colorPref		);
        prefs.getString("pref_chat_name"		, Globals.chatName		);
        
        Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
		registrationIntent.putExtra("app", PendingIntent.getBroadcast(this, 0, new Intent(), 0));
		registrationIntent.putExtra("sender", Globals.SENDER_ID);
		startService(registrationIntent);
		Globals.newReg = true;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) 
	{
		// TODO Auto-generated method stub
		
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
	
	public void onActivityResult(int request, int result, Intent data) 
	{
		/**
		 * Forwards the QR Code result
		 */
		switch(request)
		{
			
			case IntentIntegrator.REQUEST_CODE:
				Globals.classListFragment.onActivityResult(request, result, data);
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
	
	
}
