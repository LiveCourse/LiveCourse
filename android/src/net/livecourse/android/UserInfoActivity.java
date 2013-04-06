package net.livecourse.android;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import net.livecourse.R;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.REST;
import net.livecourse.rest.Restful;

public class UserInfoActivity extends SherlockFragmentActivity implements OnRestCalled
{
	private final String TAG = " == User Info Activity == ";
	
	int check = 0;
	private static final int RESULT_SETTINGS = 1;
	private ListView userinfoView;
	private UserInfoAdapter adapter;
	
	/**
	 * Temporary list of classes used, will be changed later
	 */
	String[] array = {
	        "Systems Programmin A",
	        "Foods",
	        "Intro To Pants",
	        "Female Anatomy",
	        "Elvish, the language of \"Lord of the Rings\"",
	        "European Witchcraft",
	        "Age of Piracy",
	        "The Amazing World of Bubbles",
	        "The Strategy of Starcraft",
	        "Star Trek and Religion",
	        "The Art of Warcraft: A Closer Look at the Virtual World Phenomenon"
		};
	ArrayList<String> participants;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_layout);
        
        /**
		 * Initialize the temporary list
		 */
		participants = new ArrayList<String>();
		participants.addAll(Arrays.asList(array));
		
		/**
		 * Conencts the list to the XML
		 */
		//userinfoLayout = inflater.inflate(R.layout.classlist_layout, container, false);
		userinfoView = (ListView) this.findViewById(R.id.message_list_view);
    	
    	
    	/** 
    	 * Create the adapter and set it to the list and populate it
    	 * **/
        adapter = new UserInfoAdapter(this, android.R.layout.simple_list_item_1, participants);
        userinfoView.setAdapter(adapter);        
	}
	
	 
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater=getSupportMenuInflater();
		inflater.inflate(R.menu.userinfo_menu, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.userinfo_menu:
				Toast.makeText(this, "Menu item 1 tapped", Toast.LENGTH_SHORT).show();
				//Intent edituserInfo = new Intent(this.getSherlockActivity(),UserInfoActivity.class);
				//startActivity(userInfoIntent);
				Intent i = new Intent(this, EditUserInfoActivity.class);
	            startActivityForResult(i, RESULT_SETTINGS);
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 
        switch (requestCode) {
        case RESULT_SETTINGS:
            showUserSettings();
            break;
 
        }
 
    }
	
	private void showUserSettings() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
 
        StringBuilder builder = new StringBuilder();
 
        builder.append("" + sharedPrefs.getString("prefUsername", "NULL"));

        //TextView settingsTextView = (TextView) findViewById(R.id.edit_Text);
 
        //settingsTextView.setText(builder.toString());
        //this.setTitle(builder.toString());
        System.out.println(builder.toString());
        
        Log.d(this.TAG, "The username: " + builder.toString());
        new Restful(Restful.CHANGE_DISPLAY_NAME_PATH, Restful.POST,new String[]{"name"}, new String[]{builder.toString()}, 1, this);
    }



	@Override
	public void onRestHandleResponseSuccess(String restCall, String response) 
	{
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onRestPostExecutionSuccess(String restCall, String result) 
	{
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onRestPostExecutionFailed(String restCall, int code, String result) 
	{
		Log.d(this.TAG, "Rest call: " + restCall + "failed with status code: " + code);
		Log.d(this.TAG,"Result from server is:\n" + result);		
	}
}
