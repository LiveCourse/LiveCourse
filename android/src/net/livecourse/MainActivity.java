package net.livecourse;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.*;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.Toast;
/**
 * 
 * The MainActivity class is the base activity for our Android project.  It handles the ActionBar and the swipey tabs.
 * The tabs are implemented through another fragment and that fragments adapter. 
 * 
 */
public class MainActivity extends SherlockFragmentActivity implements OnPageChangeListener{
	
	/**
	 * REST stuff
	 */
	//private String token;
	//private String password;
	
	/**
	 * Declares the required objects for the swipey tabs.
	 */
    private TabsFragmentAdapter mAdapter;
    private ViewPager mPager;
    private PageIndicator mIndicator;

	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //this.setToken(this.getIntent().getStringExtra("token"));
        //this.setPassword(this.getIntent().getStringExtra("password"));

        /**
         * The following code initializes the tabs.
         */
        mAdapter = new TabsFragmentAdapter(getSupportFragmentManager());
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mIndicator = (TitlePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        
        /**
         * Sends stuff through to the adapter so we can handle it at the fragment levels
         * Pretty sure there is a better way to do this
         */
        mAdapter.setIndicator(mIndicator);
        mAdapter.setPager(mPager);
        mAdapter.setActivity(this);
        
        mIndicator.setOnPageChangeListener(this);
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

	public TabsFragmentAdapter getTabsAdapter() {
		return mAdapter;
	}

	public void setTabsAdapter(TabsFragmentAdapter mAdapter) {
		this.mAdapter = mAdapter;
	}
}
