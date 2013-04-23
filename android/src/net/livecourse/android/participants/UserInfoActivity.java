package net.livecourse.android.participants;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
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
import net.livecourse.android.classlist.Chatroom;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.Utility;

public class UserInfoActivity extends SherlockFragmentActivity implements OnRestCalled
{
	private final String TAG = " == User Info Activity == ";
	
	int check = 0;
	private ListView userInfoView;
	private ImageView profilePic;
	private UserInfoArrayAdapter adapter;
	
	/**
	 * These are the variables for the user for for this activity
	 */
	private String userId;
	@SuppressWarnings("unused")
	private String email;
	private String displayName;
	private ArrayList<Chatroom> emptyAList;
	/**
	 * Temporary list of classes used, will be changed later
	 */
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
        this.userId = this.getIntent().getStringExtra("userId");
        Log.d(this.TAG, "The current user id: " + this.getIntent().getStringExtra("userId"));        

        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_layout);
        
        Utility.changeActivityColorBasedOnPref(this, this.getSupportActionBar());
        
        Log.d(this.TAG, "ID1:" + this.userId);
        new Restful(Restful.GET_USER_PATH, Restful.GET, new String[]{"id"}, new String[]{this.userId}, 1, this);
		
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
		emptyAList = new ArrayList<Chatroom>(10);
        adapter = new UserInfoArrayAdapter(this,R.layout.classlist_item_layout, emptyAList);
        
		
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
	public void onRestHandleResponseSuccess(String restCall, String response) 
	{
		JSONArray parse = null;
		JSONObject ob = null;
		
		if(restCall.equals(Restful.GET_USER_PATH))
		{
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
		else if(restCall.equals(Restful.CHANGE_DISPLAY_NAME_PATH))
		{

			
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
		else if(restCall.equals(Restful.GET_SUBSCRIBED_SECTIONS_PATH))
		{
			
			try 
			{
				parse = new JSONArray(response);
				this.emptyAList = new ArrayList<Chatroom>(parse.length());
				
				for(int x = 0; x < parse.length(); x++)
				{
					ob = parse.getJSONObject(x);
					Chatroom room = new Chatroom(ob);
		        	this.emptyAList.add(room);
					Log.d(this.TAG, "Added Chatroom " + room.getName() + " to query array list");
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
			Log.d(this.TAG, "ID2:" + this.userId);
			new Restful(Restful.GET_SUBSCRIBED_SECTIONS_PATH,Restful.GET, new String[]{"user_id"}, new String[]{this.userId}, 1, this);
		}
		else if(restCall.equals(Restful.CHANGE_DISPLAY_NAME_PATH));
		{
			this.setTitle(this.displayName);
		}
		if(restCall.equals(Restful.GET_SUBSCRIBED_SECTIONS_PATH))
		{
			this.adapter.clear();
        	this.adapter.addAll(this.emptyAList);
        	this.adapter.notifyDataSetChanged();
        	
		}
	}

	@Override
	public void onRestPostExecutionFailed(String restCall, int code, String result) 
	{
		Log.d(this.TAG, "Rest call: " + restCall + "failed with status code: " + code);
		Log.d(this.TAG,"Result from server is:\n" + result);		
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
