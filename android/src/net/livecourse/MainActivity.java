package net.livecourse;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.*;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

public class MainActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		final ActionBar actionBar = getSupportActionBar();
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		ActionBar.TabListener tabListener = new ActionBar.TabListener()
		{

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}
		};
		
		
		//Adds three tabs
		/*
		for(int i = 0; i<3;i++)
		{
			actionBar.addTab(actionBar.newTab().setText("Tab " + (i+1)).setTabListener(tabListener));
		}
		*/
		actionBar.addTab(actionBar.newTab().setText("Class List").setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("Chat").setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("Participants").setTabListener(tabListener));


	
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
