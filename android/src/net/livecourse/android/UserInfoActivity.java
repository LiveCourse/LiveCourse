package net.livecourse.android;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import net.livecourse.R;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;

public class UserInfoActivity extends SherlockFragmentActivity implements OnRestCalled
{
	private final String TAG = " == User Info Activity == ";
	
	int check = 0;
	private static final int RESULT_SETTINGS = 1;
	private ListView userInfoView;
	private ImageView profilePic;
	private UserInfoAdapter adapter;
	
	/**
	 * These are the variables for the user for for this activity
	 */
	private String userId;
	private String email;
	private String displayName;
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
        this.userId = this.getIntent().getStringExtra("userId");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_layout);
        
        new Restful(Restful.GET_USER_PATH, Restful.GET, new String[]{"id"}, new String[]{this.userId}, 1, this);
        
        /**
		 * Initialize the temporary list
		 */
		participants = new ArrayList<String>();
		participants.addAll(Arrays.asList(array));
		
		/**
		 * Conencts the list to the XML
		 */
		userInfoView = (ListView) this.findViewById(R.id.userinfo_list_view);
		profilePic = (ImageView) getLayoutInflater().inflate(R.layout.userinfo_header_layout,null);
		
		/**
		 * The following code enlarges the picture and sets it
		 * The picture will take up the entire width of the screen and 1/4 of the screen height
		 * It will be the header in the list view and therefore scrollable
		 */
		Bitmap bmp=BitmapFactory.decodeResource(getResources(), R.drawable.ic_contact_picture);
		int height = bmp.getHeight();
		int width = bmp.getWidth();
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int dwidth = size.x;
		int dheight = size.y;
		
		double scale;
		
		if(width > dwidth)
		{
			scale = ((double) width)/((double)dwidth);
			scale = scale*dheight;
		}
		else
		{
			scale = ((double) dwidth)/((double)width);
			scale = scale*height;
		}
		
		
		bmp = Bitmap.createScaledBitmap(bmp, dwidth, (int) scale , true);
		
		Bitmap resizedbitmap=Bitmap.createBitmap(bmp,0,0, dwidth, dheight/4);
		profilePic.setImageBitmap(resizedbitmap);
		
		userInfoView.addHeaderView(profilePic);
    	/** 
    	 * Create the adapter and set it to the list and populate it
    	 * **/
        adapter = new UserInfoAdapter(this, android.R.layout.simple_list_item_1, participants);
        userInfoView.setAdapter(adapter);        
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
		if(restCall.equals(Restful.GET_USER_PATH))
		{
			
		}
		else if(restCall.equals(Restful.CHANGE_DISPLAY_NAME_PATH));
		{
			JSONArray parse = null;
			JSONObject ob = null;
			
			try 
			{
				parse = new JSONArray(response);
				
				for(int x = 0; x < parse.length(); x++)
				{
					ob = parse.getJSONObject(x);
					
					this.email = ob.getString("email");
					this.displayName = ob.getString("display_name");
				}
				
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			
		}
	}
	
	@Override
	public void onRestPostExecutionSuccess(String restCall, String result) 
	{
		if(restCall.equals(Restful.GET_USER_PATH))
		{
			this.setTitle(this.displayName);
		}
		else if(restCall.equals(Restful.CHANGE_DISPLAY_NAME_PATH));
		{
			
		}		
	}

	@Override
	public void onRestPostExecutionFailed(String restCall, int code, String result) 
	{
		Log.d(this.TAG, "Rest call: " + restCall + "failed with status code: " + code);
		Log.d(this.TAG,"Result from server is:\n" + result);		
	}
}
