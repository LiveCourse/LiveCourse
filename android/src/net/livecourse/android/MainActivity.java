package net.livecourse.android;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.*;
import com.google.zxing.integration.android.IntentIntegrator;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;
import net.livecourse.R;
import net.livecourse.database.DatabaseHandler;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.Globals;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
	public static final int VIEW_PAGE_LOAD_COUNT = 2;
	
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
        
        Globals.mainActivity = this;
        
        /**
         * Init database
         */
        Globals.appDb = new DatabaseHandler(this.getApplicationContext());
        Globals.appDb.recreateClassEnroll();

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
				Intent settings = new Intent(this, EditUserInfoActivity.class);
	            startActivityForResult(settings, RESULT_SETTINGS);
				break;
			case R.id.item1:
				Toast.makeText(this, "Menu item 1 tapped", Toast.LENGTH_SHORT).show();
				break;
			case R.id.subItem1:
				Toast.makeText(this, "Sub Menu item 1 tapped", Toast.LENGTH_SHORT).show();
				break;
			case R.id.subItem2:
				Toast.makeText(this, "Sub Menu item 2 tapped", Toast.LENGTH_SHORT).show();
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	

	/*
	@Override
	protected void onPause() 
	{
		super.onPause();
		GCMRegistrar.unregister(this);
	}*/

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
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
	    		break;
			case 1:
	    		mAdapter.getItem(0).setMenuVisibility(false);
	    		mAdapter.getItem(1).setMenuVisibility(true);
	    		break;
			case 2:
	    		mAdapter.getItem(0).setMenuVisibility(false);
	    		mAdapter.getItem(1).setMenuVisibility(false);
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
	
	private void showUserSettings() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
 
        StringBuilder builder = new StringBuilder();
 
        builder.append("" + sharedPrefs.getString("prefUsername", "NULL"));

        System.out.println(builder.toString());
        
        Log.d(this.TAG, "The username: " + builder.toString());
        new Restful(Restful.CHANGE_DISPLAY_NAME_PATH, Restful.POST,new String[]{"name"}, new String[]{builder.toString()}, 1, this);
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
			case RESULT_SETTINGS:
				showUserSettings();
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
	
	
}
