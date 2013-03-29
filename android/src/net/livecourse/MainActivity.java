package net.livecourse;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.*;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.Toast;
/**
 * 
 * The MainActivity class is the base activity for our Android project.  It handles the ActionBar and the swipey tabs.
 * The tabs are implemented through another fragment and that fragments adapter. 
 * 
 */
public class MainActivity extends SherlockFragmentActivity {
	
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
        
        
        
        /**
         * The following code is used to change UI and logic based on which page the user is on,
         * it is currently left empty
         */
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //Toast.makeText(MainActivity.this, "Changed to page " + position, Toast.LENGTH_SHORT).show();
            	
            	if(position == 0)
            	{
            		mAdapter.getItem(1).setMenuVisibility(false);

            	}
            	if(position == 1)
            	{
            		mAdapter.getItem(1).setMenuVisibility(true);
            	}
            	if(position == 2)
            	{
            		mAdapter.getItem(1).setMenuVisibility(false);


            	}
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        
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
	
	/**
	 * This method handles when the menu button "View History" is clicked and
	 * sends the user to the history activity
	 */
	public void onViewHistoryClick(MenuItem item)
	{
		Intent historyIntent = new Intent(this, HistoryViewActivity.class);
		this.startActivity(historyIntent);
	}
}
