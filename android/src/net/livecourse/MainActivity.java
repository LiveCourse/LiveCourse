package net.livecourse;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.*;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

public class MainActivity extends SherlockFragmentActivity {
	
    TestFragmentAdapter mAdapter;
    ViewPager mPager;
    PageIndicator mIndicator;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new TestFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (TitlePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
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
		case R.id.subItem1:
			Toast.makeText(this, "Sub Menu item 1 tapped", Toast.LENGTH_SHORT).show();
		case R.id.subItem2:
			Toast.makeText(this, "Sub Menu item 2 tapped", Toast.LENGTH_SHORT).show();
		}
		
		return super.onOptionsItemSelected(item);
	}
}
