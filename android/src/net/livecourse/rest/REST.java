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
import net.livecourse.database.Participant;

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
	 * Max number of messages that the REST API call FETCH_RECENT would get
	 */
	public static final String 			MAX_MESSAGE_SIZE = "20";

	/**
	 * Flags for type of command
	 */
	public static final int 			AUTH_AND_VERIFY 	= 0;
	public static final int 			CLASS_QUERY 		= 1;
	public static final int 			CHANGE_NAME 		= 2;
	public static final int 			GRAB_CHATS			= 3;
	public static final int 			JOIN_CHAT			= 4;
	public static final int 			FETCH_RECENT		= 5;
	public static final int 			SEND				= 6;
	public static final int				ANDROID_ADD			= 7;
	public static final int				PARTICIPANTS		= 8;
	public static final int				HISTORY				= 9;
	
	/**
	 * Variables saved for use
	 */
	public static Chatroom[] 			roomList;
	public static String				userId;
	public static String 				email;
	public static String 				name;
	public static String 				passwordToken;
	public static String 				query;
	public static String 				token;
	public static String 				chatId;
	public static String				regId;
	public static String				colorPref;
	public static String				startEpoch;
	
	private int 						commandType;
	private boolean 					success;
	
	/**
	 * Activity is passed through so that REST can make changes to the UI and such
	 */
	private SherlockFragmentActivity 	mActivity;
	private SherlockFragment			mFragment;
	private String 						message;
	
	/**
	 * The constructor used REST, all unused args will be set to null
	 * TODO: para and shit
	 */
	public REST(SherlockFragmentActivity a, SherlockFragment f, String email, String password, String name, String query, String token, String chatId, String message,int command)
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
				REST.passwordToken = this.toSha1(password);
				break;
			case CLASS_QUERY:
				REST.query = query;
				//REST.password = password;
				break;
			case CHANGE_NAME:
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
			case SEND:
				this.message = message;
				REST.chatId = chatId;
				break;
			case PARTICIPANTS:
				REST.chatId = chatId;
				break;
			case HISTORY:
				REST.chatId = chatId;
				break;
		}
	}
	
	/**
	 * This is the new constructor used for REST.
	 * 
	 * Should always pass the current activity and/or the current fragment as well as the command type.
	 * The command types and their respective arguments are listed as follows:
	 * 
	 * For AUTH_AND_VERIFY:
	 * 		args0 = email
	 * 		args1 = password
	 * 
	 * For CLASS_QUERY
	 * 		args0 = query
	 * 
	 * For CHANGE_NAME
	 * 		args0 = name
	 * 
	 * For GRAB_CHATS
	 * 		no arguments needed
	 * 
	 * For JOIN_CHAT
	 * 		args0 = chatId
	 * 
	 * For FETCH_RECENT
	 * 		args0 = chatId
	 * 
	 * For SEND
	 * 		args0 = chatId
	 * 		args1 = message
	 * 
	 * For ANDROID_ADD
	 * 		args0 = regId
	 * 
	 * For PARTICIPANTS
	 * 		args0 = chatId
	 * 
	 * For HISTORY
	 * 		args0 = chatId
	 * 
	 * @param a The SherlockFragmentActivity
	 * @param f The SherlockFragment
	 * @param args0 The first argument
	 * @param args1 The second argument
	 * @param command The type of REST API command call
	 */
	public REST(SherlockFragmentActivity a, SherlockFragment f, String args0, String args1, int command)
	{
		super();
		
		this.mActivity 		= a;
		this.mFragment 		= f;
		this.commandType 	= command;
		this.success		= false;
		
		switch(commandType)
		{
			case AUTH_AND_VERIFY:
				REST.email = args0;
				REST.passwordToken = this.toSha1(args1);
				break;
			case CLASS_QUERY:
				REST.query = args0;
				break;
			case CHANGE_NAME:
				REST.name = args0;
				break;	
			case GRAB_CHATS:
				//No arguments needed
				break;
			case JOIN_CHAT:
				REST.chatId = args0;
				break;
			case FETCH_RECENT:
				REST.chatId = args0;
				break;
			case SEND:
				REST.chatId = args0;
				this.message = args1;
				break;
			case ANDROID_ADD:
				REST.regId = args0;
				break;
			case PARTICIPANTS:
				REST.chatId = args0;
				break;
			case HISTORY:
				REST.chatId = args0;
				break;
		}
	}

	/**
	 * This method is processed in the background tasks, and is used to call the REST API.
	 * 
	 * @param params
	 * @return The result of the REST API call as a string.
	 */
	@Override
	protected String doInBackground(Void... params) 
	{
		String result = "";

		switch(commandType)
		{
			case AUTH_AND_VERIFY:
				REST.token = this.auth(REST.email);
				if(success)
				{
					this.success = false;
					result = this.verify(REST.passwordToken);
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
			case SEND:
				this.sendMessage(REST.chatId,this.message);
				break;
			case ANDROID_ADD:
				this.androidAdd(REST.regId);
				break;
			case PARTICIPANTS:
				this.fetchParticipants(REST.chatId);
				break;
			case HISTORY:
				this.fetchHistory(REST.chatId);
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
			        //mainIntent.putExtra("token", REST.token);
			        //mainIntent.putExtra("password", REST.password);
					new REST(this.mActivity,this.mFragment,MainActivity.SENDER_ID,null,REST.ANDROID_ADD).execute();
					
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
				mActivity.getSupportLoaderManager().restartLoader(2, null, (LoaderCallbacks<Cursor>) mFragment);
				break;
			case SEND:
				break;
			case ANDROID_ADD:
				Intent mainIntent = new Intent(mActivity, MainActivity.class);
				mActivity.startActivity(mainIntent);	
				break;
			case PARTICIPANTS:
				mActivity.getSupportLoaderManager().restartLoader(3, null, (LoaderCallbacks<Cursor>) mFragment);
				break;
			case HISTORY:
				mActivity.getSupportLoaderManager().restartLoader(4, null, (LoaderCallbacks<Cursor>) mFragment);
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
		
		Uri b = Uri.parse("http://livecourse.net/index.php/api/auth").buildUpon()
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
		String shaHead = this.toSha1(REST.token + REST.passwordToken + "auth/verify");
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		
		Uri b = Uri.parse("http://livecourse.net/index.php/api/auth/verify").buildUpon()
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
					
					JSONObject obje = new JSONObject(result);
					JSONObject auth = obje.getJSONObject("authentication");
					JSONObject user = obje.getJSONObject("user"); 
					
					REST.userId 	= auth.getString("user_id");
					REST.name 		= user.getString("display_name");
					REST.colorPref 	= user.getString("color_preference");
					
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
	
	private String androidAdd(String regId)
	{
		//Auth:LiveCourseAuth token=OCZPcM55aSKdywZy auth=83851042dcf898927a79b0c040addd8e69023e65
		String shaHead = this.toSha1(REST.token + REST.passwordToken + "users/android_add");
		
		HttpClient httpClient = new DefaultHttpClient();
		
		Uri b = Uri.parse("http://livecourse.net/index.php/api/users/android_add").buildUpon()
		    .build();
		
		HttpPost httpPost = new HttpPost(b.toString());
	    try {

			httpPost.addHeader("Auth", "LiveCourseAuth token="+REST.token+" auth="+shaHead);
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
			nameValuePairs.add(new BasicNameValuePair("email", REST.email));
			nameValuePairs.add(new BasicNameValuePair("display_name", REST.name));
			nameValuePairs.add(new BasicNameValuePair("reg_id", regId));
			
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
					System.out.println("Sent Add Android");
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
		
		System.out.println("Get Class List - token: "+REST.token+" password: " + REST.passwordToken);
		String shaHead = this.toSha1(token + REST.passwordToken + "chats/search");
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		
		Uri b = Uri.parse("http://livecourse.net/index.php/api/chats/search").buildUpon()
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
		
		System.out.println("Get Class List - token: "+REST.token+" password: " + REST.passwordToken);
		String shaHead = this.toSha1(REST.token + REST.passwordToken + "chats");
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		
		Uri b = Uri.parse("http://livecourse.net/index.php/api/chats").buildUpon()
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
		String shaHead = this.toSha1(REST.token + REST.passwordToken + "chats/join");
		
		HttpClient httpClient = new DefaultHttpClient();
		
		Uri b = Uri.parse("http://livecourse.net/index.php/api/chats/join").buildUpon()
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
		String shaHead = this.toSha1(REST.token + REST.passwordToken + "chats/fetch_recent");
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		
		Uri b = Uri.parse("http://livecourse.net/index.php/api/chats/fetch_recent").buildUpon()
			.appendQueryParameter("chat_id", REST.chatId)
			.appendQueryParameter("num_messages", REST.MAX_MESSAGE_SIZE)
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
			        	
			        	//System.out.println("JSONObject @: "+j+" = "+ob.toString());
			        	
			        	message.setChatId(ob.getString("id"));
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
	
	private String fetchParticipants(String chatId)
	{
		//Auth:LiveCourseAuth token=OCZPcM55aSKdywZy auth=83851042dcf898927a79b0c040addd8e69023e65
		
		//System.out.println("Get Class List - token: "+REST.token+" password: " + REST.password);
		String shaHead = this.toSha1(REST.token + REST.passwordToken + "chats/get_participants");
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		
		Uri b = Uri.parse("http://livecourse.net/index.php/api/chats/get_participants").buildUpon()
			.appendQueryParameter("id", REST.chatId)
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
			        //System.out.println("Length of JSONArray: " +parse.length());
			        
			        Participant participant = new Participant();
			        
			        for(int j = 0;j<parse.length();j++)
			        {
			        	JSONObject ob = parse.getJSONObject(j);
			        	
			        	//System.out.println("JSONObject @: "+j+" = "+ob.toString());
			        	
			        	participant.setChatId(ob.getString("id"));
			        	participant.setEmail(ob.getString("email"));
			        	participant.setDisplayName(ob.getString("display_name"));
			        	participant.setTime_lastfocus(ob.getString("time_lastfocus"));
			        	participant.setTime_lastrequest(ob.getString("time_lastrequest"));
			        	
			        	MainActivity.getAppDb().addParticipant(participant);
			        	//System.out.println(participant.toString());
			        }
			        
					this.success = true;
					break;
					
				case 401:
					this.success = false;
					result = "Unauthorized Access";
					break;
					
				case 403:
					this.success = false;
					result = "Unspecified Chat ID";
					break;
					
				case 404:
					this.success = false;
					result = "No Users/Chat Room Does Not Exist";
					break;
					
				case 500:
					this.success = false;
					result = "Server Error";
					break;
			}
		}
		catch (Exception e) 
		{
			return e.getLocalizedMessage();
		}
		
		System.out.println("Get Participants Result: " + result+"\n");
		return result;
	}
	
	private String fetchHistory(String chatId)
	{
		//Auth:LiveCourseAuth token=OCZPcM55aSKdywZy auth=83851042dcf898927a79b0c040addd8e69023e65
		
		//System.out.println("Get Class List - token: "+REST.token+" password: " + REST.password);
		String shaHead = this.toSha1(REST.token + REST.passwordToken + "chats/get_day");
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		
		Uri b = Uri.parse("http://livecourse.net/index.php/api/chats/get_day").buildUpon()
			.appendQueryParameter("id", REST.chatId)
			.appendQueryParameter("start_epoch", REST.startEpoch)
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
			        
			        ChatMessage message = new ChatMessage();
			        
			        for(int j = 0;j<parse.length();j++)
			        {
			        	JSONObject ob = parse.getJSONObject(j);
			        	
			        	message.setChatId(ob.getString("id"));
			        	message.setSendTime(ob.getString("send_time"));
			        	message.setMessageString(ob.getString("message_string"));
			        	message.setEmail(ob.getString("email"));
			        	message.setDisplayName(ob.getString("display_name"));
			        	
			        	MainActivity.getAppDb().addChatMessage(message);
			        }
			        
					this.success = true;
					break;
					
				case 401:
					this.success = false;
					result = "Unauthorized Access";
					break;
					
				case 403:
					this.success = false;
					result = "Unspecified Chat ID";
					break;
					
				case 404:
					this.success = false;
					result = "Chat Room Does Not Exist";
					break;
					
				case 500:
					this.success = false;
					result = "Server Error";
					break;
			}
		}
		catch (Exception e) 
		{
			return e.getLocalizedMessage();
		}
		
		System.out.println("Get History Result: " + result+"\n");
		return result;
	}
	
	private String sendMessage(String chatId, String message)
	{
		String shaHead = this.toSha1(REST.token + REST.passwordToken + "chats/send");
		
		HttpClient httpClient = new DefaultHttpClient();
		
		Uri b = Uri.parse("http://livecourse.net/index.php/api/chats/send").buildUpon()
		    .build();
		
		HttpPost httpPost = new HttpPost(b.toString());
	    try {

			httpPost.addHeader("Auth", "LiveCourseAuth token="+REST.token+" auth="+shaHead);
			
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("chat_id", chatId));
			nameValuePairs.add(new BasicNameValuePair("message", message));
			
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
		
		System.out.println("Send Chat Result: " + result+"\n");
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
		String shaHead = this.toSha1(REST.token + REST.passwordToken + "users/change_display_name");
		
		HttpClient httpClient = new DefaultHttpClient();
		
		Uri b = Uri.parse("http://livecourse.net/index.php/api/users/change_display_name").buildUpon()
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
				case 500:
					result = "threw 500";
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
	
	/**
	 * Grabs the String content from an HttpEntity
	 * 
	 * @param entity
	 * @return The String in the entity
	 * @throws IllegalStateException
	 * @throws IOException
	 */
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
			{
				out.append(new String(b, 0, n));
			}
		}
		
		return out.toString();
	}
}
