package net.livecourse;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

public class REST extends AsyncTask <Void, Void, String> 
{
	/**
	 * Variables saved for use
	 */
	private int commandType;
	private String email;
	private String password;
	private String query;
	private String token;
	private boolean success;
	
	/**
	 * Flags for type of command
	 */
	public static final int AUTH_AND_VERIFY = 0;
	public static final int CLASS_LIST = 1;
	
	/**
	 * Activity is passed through so that REST can make changes to the UI and such
	 */
	private SherlockFragmentActivity mActivity;
	
	/**
	 * The constructor used for auth and verify
	 * @param a
	 * @param email
	 */
	public REST(SherlockFragmentActivity a, String email, String password)
	{
		super();
		this.mActivity = a;
		this.email = email;
		this.password = password;
		this.success = false;
		
		this.commandType = AUTH_AND_VERIFY;
	}

	/**
	 * The constructor used for class list
	 * @param array
	 */
	public REST(SherlockFragmentActivity a, String query, String password, String token)
	{
		super();
		this.mActivity = a;
		this.token = token;
		this.success = false;
		this.query = query;
		this.password = password;
		
		this.commandType = CLASS_LIST;
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
		switch(commandType)
		{
			case AUTH_AND_VERIFY:
				result = "Invalid email";
				this.token = this.auth(this.email);
				if(success)
				{
					this.success = false;
					result = this.verify(this.token, this.password);
				}
				break;
			case CLASS_LIST:
				result = "Search Failed";
				getClassList(this.query,this.token,this.password);
				break;
		}
		return result;
	}
	
	@Override
	protected void onPostExecute(String results) 
	{
		switch(commandType)
		{
			case AUTH_AND_VERIFY:
				if(success)
				{
					Intent mainIntent = new Intent(mActivity, MainActivity.class);
			        mainIntent.putExtra("token", this.token);
			        mainIntent.putExtra("password", this.password);
			        
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
			case CLASS_LIST:
				//TODO: handle results
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
	private String verify(String token, String password)
	{
		//Auth:LiveCourseAuth token=OCZPcM55aSKdywZy auth=83851042dcf898927a79b0c040addd8e69023e65
		String shaHead = this.toSha1(token+this.toSha1(password)+"auth/verify");
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		
		Uri b = Uri.parse("http://www.livecourse.net/index.php/api/auth/verify").buildUpon()
		    .build();		
		
		HttpGet httpGet = new HttpGet(b.toString());
		System.out.println("shaHead:"+shaHead);
		httpGet.addHeader("Auth", "LiveCourseAuth token="+token+" auth="+shaHead);
		
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
	
	private String getClassList(String query, String token, String password){
		//Auth:LiveCourseAuth token=OCZPcM55aSKdywZy auth=83851042dcf898927a79b0c040addd8e69023e65
		
		System.out.println("Get Class List - token: "+token+" password: " + password);
		String shaHead = this.toSha1(token+
				this.toSha1(password)+"chats/search");
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		
		Uri b = Uri.parse("http://www.livecourse.net/index.php/api/chats/search").buildUpon()
			.appendQueryParameter("query", query)
			.build();		
		
		HttpGet httpGet = new HttpGet(b.toString());
		System.out.println("shaHead:"+shaHead);
		httpGet.addHeader("Auth", "LiveCourseAuth token="+token+" auth="+shaHead);
		
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
					JSONObject parse = new JSONObject(result.trim());
			        Iterator<?> keys = parse.keys();
			        Chatroom[] roomlist = new Chatroom[1024];

			        for(int j=0; keys.hasNext();){
			            String key = (String)keys.next();
			            if(key.equals("id")){
			            	roomlist[j] = new Chatroom();
			            }
			            if(key.equals("subject_id")){
			            	(roomlist[j]).setSubject_id(parse.getString(key));
			            }
			            if(key.equals("course_number")){
			            	(roomlist[j]).setCourse_number(parse.getString(key));
			            }
			            if(key.equals("name")){
			            	(roomlist[j]).setName(parse.getString(key));
			            }
			            if(key.equals("start_time")){
			            	(roomlist[j]).setStart_time(parse.getString(key));
			            }
			            if(key.equals("dow_sunday")){
			            	j++;
			            }
			        }
					result = parse.getJSONObject("authentication").getString("token");
					//parse.
					
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
		
		System.out.println("Search chat result: " + result+"\n");
		
		return result;
	}
}
