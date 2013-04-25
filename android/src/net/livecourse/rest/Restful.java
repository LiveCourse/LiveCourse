package net.livecourse.rest;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import net.livecourse.utility.Globals;
import net.livecourse.utility.ProgressMultipartEntity;
import net.livecourse.utility.Utility;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

/**
 * This class is responsible for sending and receiving the HttpGet or HttpPost requests.  This class
 * will create either a HttpGet or HttpPost object and sends it to our server and then receive it, all
 * done on a background thread.  It will then call the OnRestCalled callback class to do or finish up tasks.
 * All classes that call this class should implement OnRestCalled in order to use its methods.  There are
 * three methods that OnRestCalled implements, which are documented in the OnRestCalled interface.  In short
 * there is one method that runs only if the return from the server is successful and performs actions in 
 * the same background thread, one method that handles the results of the call if they were successful, and
 * one method that handles the results of the call if it failed.  There may be a fourth method added later on
 * that handles setup before the going into the background thread.
 * 
 * This class is mostly completed and should not be touched.  If something breaks, it would generally be your
 * code, not this class.
 * 
 * @author Darren Cheng
 */
public class Restful extends AsyncTask <Void, String, String> 
{
	private final String			TAG								= " == Restful == ";
	/**
	 * Max number of messages that the REST API call FETCH_RECENT would get
	 */
	public static final String 		MAX_MESSAGE_SIZE 				= "100";

	/**
	 * Flags for type of command
	 */
	public static final int			GET								= 0;
	public static final int			POST							= 1;
	
	/**
	 * Flags for file type
	 */
	public static final int			POST_NO_FILE					= -1;
	public static final int			POST_FILE						= 0;
	
	/**
	 * Path locations for specific commands
	 */
	public static final String		API_PATH						= "http://livecourse.net/index.php/api/"	;
	public static final String		AUTH_PATH						= "auth"									;
	public static final String		VERIFY_PATH						= "auth/verify"								;
	public static final String		GET_SUBSCRIBED_CHATS_PATH		= "chats"									;
	public static final String		GET_ALL_FILES_PATH				= "chats/fetch_all_files_by_class"			;
	public static final String		GET_CHAT_HISTORY_PATH			= "chats/fetch_day"							;
	public static final String		GET_RECENT_MESSAGES_PATH		= "chats/fetch_recent"						;
	public static final String		GET_PARTICIPANTS_PATH			= "chats/get_participants"					;
	public static final String		GET_CHAT_INFORMATION_PATH		= "chats/info"								;
	public static final String		UNSUBSCRIBE_CHAT_PATH			= "chats/leave"								;
	public static final String		SEND_MESSAGE_PATH				= "chats/send"								;
	public static final String		GET_NOTES_PATH					= "notes"									;
	public static final String		ADD_NOTE_PATH					= "notes/add"								;
	public static final String		GET_SUBSCRIBED_SECTIONS_PATH 	= "sections"								;
	public static final String		JOIN_SECTION_PATH				= "sections/join"							;
	public static final String		SEARCH_FOR_CHAT_PATH			= "sections/search_advanced"				;
	public static final String		REGISTER_USER_PATH				= "users/add"								;
	public static final String		GET_USER_PATH					= "users/index"								;
	public static final String		UPDATE_COLOR_PREF_PATH			= "users/update_color"						;
	public static final String		UPDATE_USER_STATUS_PATH			= "users/focus"								;
	public static final String		REGISTER_ANDROID_USER_PATH		= "users/android_add"						;
	public static final String		IGNORE_USER_PATH				= "users/ignore_user"						;
	public static final String		UNIGNORE_USER_PATH				= "users/unignore_user"						;
	public static final String		CHANGE_DISPLAY_NAME_PATH		= "users/change_display_name"				;
	
	/**
	 * Private variables used by Restful
	 */
	private String					path;
	private int 					commandType;
	private String[]				serverArgs;
	private String[]				args;
	private int						numArgs;
	private OnRestCalled			callback;
	private int						fileType;
	private File					file;
	private long					fileSize;

	private boolean 				success;
	private int						returnCode;
	
	/**
	 * Calling this constructor will make a Rest call to the server depending on the arguments provided
	 * 
	 * @param path			The path of the execution, ex. auth/verify, all paths are stored as static final
	 * 						variables in Restful and can be called upon
	 * @param command		The command to be called, 0 for GET and 1 for POST
	 * @param serverArgs	The String arguments variable names for the server, ex. for the pair "email",
	 * 						"test1@test.com", "email" would be the server argument
	 * @param args			The client side value to be passed, following the above example, would be 
	 * 						"test1@test.com"
	 * @param numArgs		The number of arguments passed
	 * @param call			The OnRestCalled callback that Restful will return to
	 */
	public Restful(String path, int command, String[] serverArgs, String[] args, int numArgs, OnRestCalled call)
	{
		super();
		
		this.path 			= path;
		this.commandType 	= command;
		this.serverArgs		= serverArgs;
		this.args			= args;
		this.numArgs		= numArgs;
		this.callback 		= call;
		this.success		= false;
		this.returnCode		= -1;
		this.fileType		= -1;
		
		this.execute();
	}
	
	/**
	 * Calling this constructor will make a Rest call to the server depending on the arguments provided
	 * This constructor differs in that it is used to upload a byte array jpeg file, just provide the byte
	 * array of the jpeg file
	 * 
	 * @param path			The path of the execution, ex. auth/verify, all paths are stored as static final
	 * 						variables in Restful and can be called upon
	 * @param command		The command to be called, 0 for GET and 1 for POST
	 * @param serverArgs	The String arguments variable names for the server, ex. for the pair "email",
	 * 						"test1@test.com", "email" would be the server argument
	 * @param args			The client side value to be passed, following the above example, would be 
	 * 						"test1@test.com"
	 * @param numArgs		The number of arguments passed
	 * @param data			The byte array to be uploaded
	 * @param call			The OnRestCalled callback that Restful will return to
	 */
	public Restful(String path, int command, String[] serverArgs, String[] args, int numArgs, File file, OnRestCalled call)
	{
		super();
		
		this.path 			= path;
		this.commandType 	= command;
		this.serverArgs		= serverArgs;
		this.args			= args;
		this.numArgs		= numArgs;
		this.callback 		= call;
		this.success		= false;
		this.returnCode		= -1;
		this.file			= file;
		this.fileSize		= file.length();
		this.fileType		= Restful.POST_FILE;
		
		this.execute();
	}
	
	@Override
	protected void onPreExecute()
	{
		this.callback.preRestExecute(this.path);
	}

	@Override
	protected String doInBackground(Void... params) 
	{
		HttpResponse response = null;
		HttpEntity entity = null;
		String responseStr = "";
		
		/**
		 * Performs either GET or POST based on command
		 */
		switch(commandType)
		{
			case Restful.GET:
				response = getHttpGetResponse(this.path, this.numArgs, this.serverArgs, this.args);
				break;
			case Restful.POST:
				response = getHttpPostResponse(this.path, this.numArgs, this.serverArgs, this.args);
				break;
		}
		
		/**
		 * If the response is null, someone passed the wrong command, scream at them
		 */
		if(response == null)
		{
			Log.e(this.TAG, "Error executing Rest command " + path + ", incorrect command.  Command can only be 0 or 1 for either GET or POST");
			return null;
		}
		
		/**
		 * Grabs the string from the HttpEntity and calls back to execute code
		 * If the string cannot be grabbed then we have a serious problem
		 */
		entity = response.getEntity();
		try 
		{
			responseStr = Utility.getASCIIContentFromEntity(entity);
			this.returnCode = Restful.getResponseCodeFromHttpResponse(response);
			Log.d(this.TAG, "Status Code: " + this.returnCode);
			if(returnCode/100 == 2)
			{
				this.success = true;
				callback.onRestHandleResponseSuccess(path, responseStr);
			}
			
			return responseStr;
		} 
		catch (IllegalStateException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		Log.e(this.TAG,"Error: Could not grab string from HttpEntity");
		return responseStr;
	}
	
	@Override
	protected void onPostExecute(String results) 
	{
		/**
		 * Either calls the success or failed callback method if the Rest call
		 * is successful or not
		 */
		if(this.success)
		{
			this.callback.onRestPostExecutionSuccess(this.path, results);
			if(this.fileType == Restful.POST_FILE && this.path == Restful.SEND_MESSAGE_PATH)
				Utility.finishProgressNotification(Globals.mainActivity, "Uploading", "Completed");
		}
		else
		{
			this.callback.onRestPostExecutionFailed(this.path, this.returnCode, results);
			if(this.fileType == Restful.POST_FILE && this.path == Restful.SEND_MESSAGE_PATH)
				Utility.finishProgressNotification(Globals.mainActivity, "Uploading", "Failed");
		}
	}
	
	@Override
	protected void onProgressUpdate(String... progress)
	{
		
	}
	
	@Override
	protected void onCancelled(String results)
	{
		this.callback.onRestCancelled(this.path, results);
	}
	
	/**
	 * Performs a GET call to the server with the given arguments,
	 * The arguments are similar to the constructor, look at that if
	 * for reference.
	 * 
	 * @param path			The path
	 * @param numArgs		The number of arguments
	 * @param serverArgs	The server arguments
	 * @param args			The client arguments
	 * @return				The HttpResponse object returned by the server
	 */
	private HttpResponse getHttpGetResponse(String path, int numArgs, String[] serverArgs, String[] args)
	{
		String shaHead = "";
		if(!path.equals(Restful.AUTH_PATH))
			shaHead = Utility.convertStringToSha1(Globals.token + Globals.passwordToken + path);
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		
		Uri b = Uri.parse(Restful.API_PATH + path);
		Uri.Builder builder = b.buildUpon();
		for(int x = 0; x < numArgs; x++)
		{
			builder.appendQueryParameter(serverArgs[x], args[x]);
		}
		b = builder.build();
		
		HttpGet httpGet = new HttpGet(b.toString());
		if(!path.equals(Restful.AUTH_PATH))
			httpGet.addHeader("Auth", "LiveCourseAuth token="+Globals.token+" auth="+shaHead);
		
		Log.d(this.TAG, "Created HttpGet with URI: " + httpGet.getURI().toString());
		
		HttpResponse response = null;
		try 
		{
			long startTime = System.currentTimeMillis();
			response = httpClient.execute(httpGet, localContext);
			Log.d(this.TAG, "Executed HttpGet request for " + path + " in " + (System.currentTimeMillis() - startTime) + "ms");
		} 
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return response;
	}
	
	/**
	 * Performs a POST call to the server with the given arguments,
	 * The arguments are similar to the constructor, look at that if
	 * for reference.
	 * 
	 * @param path			The path
	 * @param numArgs		The number of arguments
	 * @param serverArgs	The server arguments
	 * @param args			The client arguments
	 * @return				The HttpResponse object returned by the server
	 */
	private HttpResponse getHttpPostResponse(String path, int numArgs, String[] serverArgs, String[] args)
	{
		String shaHead = "";
		if(!path.equals(Restful.REGISTER_USER_PATH))
			shaHead = Utility.convertStringToSha1(Globals.token + Globals.passwordToken + path);
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		
		Uri b = Uri.parse(Restful.API_PATH + path);
		
		HttpPost httpPost = new HttpPost(b.toString());
		if(!path.equals(Restful.REGISTER_USER_PATH))
			httpPost.setHeader("Auth", "LiveCourseAuth token="+Globals.token+" auth="+shaHead);

		try 
		{
			if(this.fileType == Restful.POST_NO_FILE)
			{
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(numArgs);
				for(int x = 0; x < numArgs; x++)
				{
					nameValuePairs.add(new BasicNameValuePair(serverArgs[x], args[x]));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			}
			if(this.fileType == Restful.POST_FILE)
			{
				String contentType = Utility.getMimeType(this.file.getName());
				Log.d(this.TAG, "File path: " + this.file.getAbsolutePath());
				Log.d(this.TAG, "The content type: " + 	contentType);
				
				ProgressMultipartEntity ent = new ProgressMultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, "Uploading " + file.getName(), this.fileSize);
				
				if(contentType != null)
					ent.addPart("file", new FileBody(this.file, Utility.getMimeType(this.file.getName())));
				else
					ent.addPart("file", new FileBody(this.file));
								
				for(int x = 0; x < numArgs; x++)
				{
					ent.addPart(serverArgs[x], new StringBody(args[x]));
				}
				httpPost.setEntity(ent);
			}
			if(this.fileType == Restful.POST_FILE)
			{
				
			}

		}
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		
		Log.d(this.TAG, "Created HttpPost with URI: " + httpPost.getURI().toString());

		HttpResponse response = null;
		try 
		{
			response = httpClient.execute(httpPost,localContext);
			
		} 
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return response;
	}
	
	/**
	 * Simple method to grab the status code given a httpResponse.
	 * 
	 * @param response	HttpResponse to grab the status code from
	 * @return			The status code
	 */
	public static int getResponseCodeFromHttpResponse(HttpResponse response)
	{
		return response.getStatusLine().getStatusCode();
	}
	

}
