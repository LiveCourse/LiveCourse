package net.livecourse;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.*;

import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
