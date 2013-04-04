package net.livecourse.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import net.livecourse.android.MainActivity;
import net.livecourse.android.QueryActivity;
import net.livecourse.android.R;
import net.livecourse.database.Chatroom;
import net.livecourse.database.ChatMessage;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class REST extends AsyncTask <Void, Void, String> 
{
	/**
	 * Variables saved for use
	 */
	private int commandType;
	
	public static String email;
	public static String name;
	public static String password;
	public static String query;
	public static String token;
	public static String chatId;
	private boolean success;
	
	public static Chatroom[] roomList;
	
	/**
	 * Flags for type of command
	 */
	public static final int AUTH_AND_VERIFY 	= 0;
	public static final int CLASS_QUERY 		= 1;
	public static final int CHANGE_NAME 		= 2;
	public static final int GRAB_CHATS			= 3;
	public static final int JOIN_CHAT			= 4;
	public static final int FETCH_RECENT		= 5;
	
	
	/**
	 * Activity is passed through so that REST can make changes to the UI and such
	 */
	private SherlockFragmentActivity mActivity;
	private SherlockFragment mFragment;
	
	/**
	 * The constructor used REST, all unused args will be set to null
	 * TODO: para and shit
	 */
	public REST(SherlockFragmentActivity a, SherlockFragment f, String email, String password, String name, String query, String token, String chatId, int command)
	{
		super();
		
		this.mActivity = a;
		this.success = false;
		this.commandType = command;
		System.out.println("Constructor Reached with command: "+ this.commandType);
		
		switch(commandType)
		{
			case AUTH_AND_VERIFY:
				REST.email = email;
				REST.password = password;
				break;
			case CLASS_QUERY:
				REST.query = query;
				//REST.password = password;
				break;
			case CHANGE_NAME:
				System.out.println("Constructor switch case");
				REST.name = name;
				break;	
			case GRAB_CHATS:
				this.mFragment = f;
				break;
			case JOIN_CHAT:
				REST.chatId = chatId;
				break;
			case FETCH_RECENT:
				REST.chatId = chatId;
				this.mFragment = f;
				break;
		}
	}
	
	protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException 
	{
		InputStream in = entity.getContent();
		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n>0) 
		{
			byte[] b = new byte[4096];
			n =  in.read(b);
			if (n>0) 
				out.append(new String(b, 0, n));
		}
		return out.toString();
	}

	@Override
	protected String doInBackground(Void... params) 
	{
		String result = "No Command Executed\n";
		System.out.println("do in background reached");
		switch(commandType)
		{
			case AUTH_AND_VERIFY:
				result = "Authentication Timeout";
				REST.token = this.auth(REST.email);
				if(success)
				{
					this.success = false;
					result = this.verify(REST.password);
				}
				break;
			case CLASS_QUERY:
				result = "Class Query Timeout";
				this.queryClassList(REST.query);
				break;
			case CHANGE_NAME:
				result = "Change Name Timeout";
				System.out.println("Change Name REST background reached");
				this.changeName(REST.name);
				break;
			case GRAB_CHATS:
				result = "Class Enroll Timeout";
				this.grabChats();
				break;
			case JOIN_CHAT:
				this.joinChat(REST.chatId);
				break;
			case FETCH_RECENT:
				this.fetchRecent(REST.chatId);
				break;
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onPostExecute(String results) 
	{
		switch(commandType)
		{
			case AUTH_AND_VERIFY:
				if(success)
				{
					Intent mainIntent = new Intent(mActivity, MainActivity.class);
			        //mainIntent.putExtra("token", REST.token);
			        //mainIntent.putExtra("password", REST.password);
			        
					mActivity.startActivity(mainIntent);	
					
					System.out.println(results);
				}
				else
				{
					TextView errorTextView = (TextView) mActivity.findViewById(R.id.error_text_view);
					errorTextView.setText(results);
					errorTextView.setTextColor(Color.RED);
					errorTextView.setVisibility(View.VISIBLE);
				}
				break;
			case CLASS_QUERY:
				((QueryActivity) mActivity).getAdapter().clear();
				((QueryActivity) mActivity).getAdapter().addAll(REST.roomList);
				((QueryActivity) mActivity).getAdapter().notifyDataSetChanged();
				break;
			case CHANGE_NAME:
				if(success)
					mActivity.setTitle(REST.name);
				break;
			case GRAB_CHATS:
				mActivity.getSupportLoaderManager().initLoader(1, null, (LoaderCallbacks<Cursor>) mFragment);
				break;
			case JOIN_CHAT:
				if(success)
				{	
					MainActivity.classListFragment.updateList();
					mActivity.finish();
				}
				else
				{
					Toast.makeText(mActivity, "Failed", Toast.LENGTH_SHORT).show();

				}
				break;
			case FETCH_RECENT:
				mActivity.getSupportLoaderManager().initLoader(2, null, (LoaderCallbacks<Cursor>) mFragment);
				break;
		}
	}
	
	/**
	 * This is the auth api call.  You send the server a email address and the server
	 * will send back a token if the email address is valid.
	 * @param email
	 * @return result, which is the token
	 */
	private String auth(String email)
	{		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		
		Uri b = Uri.parse("http://www.livecourse.net/index.php/api/auth").buildUpon()
		    .appendQueryParameter("email", email)
		    .appendQueryParameter("device", "1")
		    .build();
		
		HttpGet httpGet = new HttpGet(b.toString());
		System.out.println(httpGet.getURI().toString());
		String result = "";
		
		try 
		{
			HttpResponse response = httpClient.execute(httpGet, localContext);
			HttpEntity entity = response.getEntity();
			result = getASCIIContentFromEntity(entity);
			
			switch(response.getStatusLine().getStatusCode())
			{
				case 200:
					JSONObject parse = new JSONObject(result);
					result = parse.getJSONObject("authentication").getString("token");
					this.success = true;
					break;
					
				case 404:
					this.success = false;
					result = "Invalid email";
					break;
			}
			
			
		} 
		catch (Exception e) 
		{
			return e.getLocalizedMessage();
		}
		
		System.out.println("Auth result: " + result+"\n");
		return result;
	}
	
	/**
	 * This is the verify api call, it will verify the user given the token and the password
	 * @param token
	 * @param password
	 * @return result
	 */
	private String verify(String password)
	{
		//Auth:LiveCourseAuth token=OCZPcM55aSKdywZy auth=83851042dcf898927a79b0c040addd8e69023e65
		String shaHead = this.toSha1(REST.token+this.toSha1(password)+"auth/verify");
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		
		Uri b = Uri.parse("http://www.livecourse.net/index.php/api/auth/verify").buildUpon()
		    .build();		
		
		HttpGet httpGet = new HttpGet(b.toString());
		System.out.println("shaHead:"+shaHead);
		httpGet.addHeader("Auth", "LiveCourseAuth token="+REST.token+" auth="+shaHead);
		
		System.out.println(httpGet.getURI().toString());
		String result = null;
		
		try 
		{
			HttpResponse response = httpClient.execute(httpGet, localContext);
			HttpEntity entity = response.getEntity();
			result = this.getASCIIContentFromEntity(entity);
			
			switch(response.getStatusLine().getStatusCode())
			{
				case 200:
					this.success = true;
					break;
					
				case 401:
					this.success = false;
					result = "Invalid password";
					break;
			}
		} 
		catch (Exception e) 
		{
			return e.getLocalizedMessage();
		}
		
		System.out.println("Verify result: " + result+"\n");
		return result;
	}
	
	/**
	 * This is the chat room query api call.  By passing it a query, a token, and the password,
	 * this call will call the query api and get a list of clatrooms.  The list is passed to the global list
	 * @param query
	 * @param token
	 * @param password
	 * @return the message
	 */
	private String queryClassList(String query)
	{
		//Auth:LiveCourseAuth token=OCZPcM55aSKdywZy auth=83851042dcf898927a79b0c040addd8e69023e65
		
		System.out.println("Get Class List - token: "+REST.token+" password: " + REST.password);
		String shaHead = this.toSha1(token+this.toSha1(REST.password)+"chats/search");
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		
		Uri b = Uri.parse("http://www.livecourse.net/index.php/api/chats/search").buildUpon()
			.appendQueryParameter("query", query)
			.build();		
		
		HttpGet httpGet = new HttpGet(b.toString());
		System.out.println("shaHead:"+shaHead);
		httpGet.addHeader("Auth", "LiveCourseAuth token="+REST.token+" auth="+shaHead);
		
		System.out.println(httpGet.getURI().toString());
		String result = null;
		
		try 
		{
			HttpResponse response = httpClient.execute(httpGet, localContext);
			HttpEntity entity = response.getEntity();
			result = getASCIIContentFromEntity(entity);
			
			switch(response.getStatusLine().getStatusCode())
			{
				case 200:
			        JSONArray parse = new JSONArray(result.trim());
			        System.out.println("Length of JSONArray: " +parse.length());
			        
			        roomList = new Chatroom[parse.length()];
			        
			        for(int j = 0;j<parse.length();j++)
			        {
			        	JSONObject ob = parse.getJSONObject(j);
			        	
			        	System.out.println("JSONObject @: "+j+" = "+ob.toString());
			        	
			        	roomList[j] = new Chatroom();
			        	
			        	roomList[j].setIdString(ob.getString(		"id_string"));
		            	roomList[j].setSubjectId(ob.getString(		"subject_id"));
		            	roomList[j].setCourseNumber(ob.getString(	"course_number"));
		            	roomList[j].setName(ob.getString(			"name"));
		            	roomList[j].setStartTime(ob.getString(		"start_time"));	            	
			        	roomList[j].setInstitutionId(ob.getString(	"institution_id"));
			        	roomList[j].setRoomId(ob.getString(			"room_id"));
			        	roomList[j].setStartTime(ob.getString(		"start_time"));
			        	roomList[j].setEndTime(ob.getString(		"end_time"));
			        	roomList[j].setStartDate(ob.getString(		"start_date"));
			        	roomList[j].setEndDate(ob.getString(		"end_date"));
			        	roomList[j].setDowMonday(ob.getString(		"dow_monday"));
			        	roomList[j].setDowTuesday(ob.getString(		"dow_tuesday"));
			        	roomList[j].setDowWednesday(ob.getString(	"dow_wednesday"));
			        	roomList[j].setDowThursday(ob.getString(	"dow_thursday"));
			        	roomList[j].setDowFriday(ob.getString(		"dow_friday"));
			        	roomList[j].setDowSaturday(ob.getString(	"dow_saturday"));
			        	roomList[j].setDowSunday(ob.getString(		"dow_sunday"));			   
			        }
					
					this.success = true;
					break;
					
				case 401:
					this.success = false;
					break;
			}
		} 
		catch (Exception e) 
		{
			System.out.println("Query Failed:\n"+e.getLocalizedMessage());
			return e.getLocalizedMessage();
		}
		
		System.out.println("Query Classes Result: " + result+"\n");
		
		return result;
	}
	
	private String grabChats()
	{
		//Auth:LiveCourseAuth token=OCZPcM55aSKdywZy auth=83851042dcf898927a79b0c040addd8e69023e65
		
		System.out.println("Get Class List - token: "+REST.token+" password: " + REST.password);
		String shaHead = this.toSha1(REST.token+this.toSha1(REST.password)+"chats");
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		
		Uri b = Uri.parse("http://www.livecourse.net/index.php/api/chats").buildUpon()
			.build();		
		
		HttpGet httpGet = new HttpGet(b.toString());
		System.out.println("shaHead:"+shaHead);
		httpGet.addHeader("Auth", "LiveCourseAuth token="+REST.token+" auth="+shaHead);
		
		System.out.println(httpGet.getURI().toString());
		String result = null;
		
		try 
		{
			HttpResponse response = httpClient.execute(httpGet, localContext);
			HttpEntity entity = response.getEntity();
			result = getASCIIContentFromEntity(entity);
			
			switch(response.getStatusLine().getStatusCode())
			{
				case 200:
			        JSONArray parse = new JSONArray(result.trim());
			        System.out.println("Length of JSONArray: " +parse.length());
			        //MainActivity.getAppDb().recreateClassEnroll();
			        
			        roomList = new Chatroom[parse.length()];
			        
			        for(int j = 0;j<parse.length();j++)
			        {
			        	JSONObject ob = parse.getJSONObject(j);
			        	
			        	System.out.println("JSONObject @: "+j+" = "+ob.toString());
			        	
			        	roomList[j] = new Chatroom();
			        	
			        	roomList[j].setIdString(ob.getString(		"id_string"));
		            	roomList[j].setSubjectId(ob.getString(		"subject_id"));
		            	roomList[j].setCourseNumber(ob.getString(	"course_number"));
		            	roomList[j].setName(ob.getString(			"name"));
		            	roomList[j].setStartTime(ob.getString(		"start_time"));	            	
			        	roomList[j].setInstitutionId(ob.getString(	"institution_id"));
			        	roomList[j].setRoomId(ob.getString(			"room_id"));
			        	roomList[j].setStartTime(ob.getString(		"start_time"));
			        	roomList[j].setEndTime(ob.getString(		"end_time"));
			        	roomList[j].setStartDate(ob.getString(		"start_date"));
			        	roomList[j].setEndDate(ob.getString(		"end_date"));
			        	roomList[j].setDowMonday(ob.getString(		"dow_monday"));
			        	roomList[j].setDowTuesday(ob.getString(		"dow_tuesday"));
			        	roomList[j].setDowWednesday(ob.getString(	"dow_wednesday"));
			        	roomList[j].setDowThursday(ob.getString(	"dow_thursday"));
			        	roomList[j].setDowFriday(ob.getString(		"dow_friday"));
			        	roomList[j].setDowSaturday(ob.getString(	"dow_saturday"));
			        	roomList[j].setDowSunday(ob.getString(		"dow_sunday"));
			        	
			        	MainActivity.getAppDb().addClassEnroll(roomList[j]);
			        	System.out.println(roomList[j].toString());
			        }
					
					this.success = true;
					break;
					
				case 401:
					this.success = false;
					result = "Grabbing Class List Failed";
					break;
			}
		}
		catch (Exception e) 
		{
			System.out.println("Query Failed:\n"+e.getLocalizedMessage());
			return e.getLocalizedMessage();
		}
		
		return result;
	}
	
	private String joinChat(String roomId)
	{
		//Auth:LiveCourseAuth token=OCZPcM55aSKdywZy auth=83851042dcf898927a79b0c040addd8e69023e65
		String shaHead = this.toSha1(REST.token+this.toSha1(REST.password)+"chats/join");
		
		HttpClient httpClient = new DefaultHttpClient();
		
		Uri b = Uri.parse("http://www.livecourse.net/index.php/api/chats/join").buildUpon()
		    .build();
		
		HttpPost httpPost = new HttpPost(b.toString());
	    try {

			httpPost.addHeader("Auth", "LiveCourseAuth token="+REST.token+" auth="+shaHead);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("id", chatId));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
		} 
	    catch (UnsupportedEncodingException e1) 
	    {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println(httpPost.getURI().toString());
		String result = "";
		
		try 
		{
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			result = getASCIIContentFromEntity(entity);
			
			switch(response.getStatusLine().getStatusCode())
			{
				case 200:
					this.success = true;
					break;
				case 401:
					this.success = false;
					break;
				case 403:
					this.success = false;
					break;
				case 404:
					this.success = false;
					//result = "Change Name Failed";
					break;
			}
			
			
		} 
		catch (Exception e) 
		{
			return e.getLocalizedMessage();
		}
		
		System.out.println("Join Chat Result: " + result+"\n");
		return result;
	}
	
	private String fetchRecent(String chatId)
	{
		//Auth:LiveCourseAuth token=OCZPcM55aSKdywZy auth=83851042dcf898927a79b0c040addd8e69023e65
		
		//System.out.println("Get Class List - token: "+REST.token+" password: " + REST.password);
		String shaHead = this.toSha1(REST.token+this.toSha1(REST.password)+"chats/fetch_recent");
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		
		Uri b = Uri.parse("http://www.livecourse.net/index.php/api/chats/fetch_recent").buildUpon()
			.appendQueryParameter("char_id", REST.chatId)
			.build();		
		
		HttpGet httpGet = new HttpGet(b.toString());
		System.out.println("shaHead:"+shaHead);
		httpGet.addHeader("Auth", "LiveCourseAuth token="+REST.token+" auth="+shaHead);
		
		System.out.println(httpGet.getURI().toString());
		String result = null;
		
		try 
		{
			HttpResponse response = httpClient.execute(httpGet, localContext);
			HttpEntity entity = response.getEntity();
			result = getASCIIContentFromEntity(entity);
			
			switch(response.getStatusLine().getStatusCode())
			{
				case 200:
			        JSONArray parse = new JSONArray(result.trim());
			        System.out.println("Length of JSONArray: " +parse.length());
			        //MainActivity.getAppDb().recreateClassEnroll();
			        
			        ChatMessage message = new ChatMessage();
			        
			        for(int j = 0;j<parse.length();j++)
			        {
			        	JSONObject ob = parse.getJSONObject(j);
			        	
			        	System.out.println("JSONObject @: "+j+" = "+ob.toString());
			        	
			        	message.setChatId(ob.getString("chat_id"));
			        	message.setSendTime(ob.getString("send_time"));
			        	message.setMessageString(ob.getString("message_string"));
			        	message.setEmail(ob.getString("email"));
			        	message.setDisplayName(ob.getString("display_name"));
			        	
			        	MainActivity.getAppDb().addChatMessage(message);
			        	//System.out.println(message.toString());
			        }
			        
					this.success = true;
					break;
					
				case 401:
					this.success = false;
					result = "Fetching Chats Messages Failed";
					break;
			}
		}
		catch (Exception e) 
		{
			return e.getLocalizedMessage();
		}
		
		System.out.println("Join Chat Result: " + result+"\n");
		return result;
	}
	
	
	/**
	 * This is the REST API call that allows users to change their names
	 * @param password
	 * @param name
	 * @return
	 */
	private String changeName(String name)
	{
		String shaHead = this.toSha1(REST.token+this.toSha1(REST.password)+"users/change_display_name");
		
		HttpClient httpClient = new DefaultHttpClient();
		
		Uri b = Uri.parse("http://www.livecourse.net/index.php/api/users/change_display_name").buildUpon()
		    .build();
		
		HttpPost httpPost = new HttpPost(b.toString());
	    try {

			httpPost.addHeader("Auth", "LiveCourseAuth token="+REST.token+" auth="+shaHead);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("name", name));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println(httpPost.getURI().toString());
		String result = "";
		
		try 
		{
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			result = getASCIIContentFromEntity(entity);
			
			switch(response.getStatusLine().getStatusCode())
			{
				case 200:
					this.success = true;
					break;
					
				case 404:
					this.success = false;
					//result = "Change Name Failed";
					break;
			}
			
			
		} 
		catch (Exception e) 
		{
			return e.getLocalizedMessage();
		}
		
		System.out.println("Change name result: " + result+"\n");
		return result;
	}
	/**
	 * Converts string into SHA-1 hash
	 * @param input
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	private String toSha1(String input)
	{
		String cpyInput = input;
		MessageDigest md = null;
		try 
		{
			md = MessageDigest.getInstance("SHA-1");
		} 
		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
		}
        md.update(cpyInput.getBytes());
        
        byte byteData[] = md.digest();
        
        /**
         * Convert from byte to hex
         */
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) 
        {
        	buffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        
		return buffer.toString();
	}
}
