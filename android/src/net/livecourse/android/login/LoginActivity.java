package net.livecourse.android.login;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import net.livecourse.R;
import net.livecourse.android.MainActivity;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.Globals;
import net.livecourse.utility.Utility;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gcm.GCMRegistrar;

/**
 * This Class is responsible for allowing the user to login 
 * and see the registration page.
 * Upon login it will redirect the user to the MainActivity.
 * Upon hitting registration it will redirect the user to the
 * RegistrationActivity.
 * 
 * @author Darren Cheng
 */
public class LoginActivity extends SherlockFragmentActivity implements OnRestCalled, OnClickListener, TextWatcher
{
	private final String TAG = " == LoginActivity == ";
	
	/**
	 * The XML Views
	 */
	private EditText 	loginEmailEditTextView; 
	private EditText 	loginPasswordEditTextView;
	private TextView 	loginLogoTextView;
	private TextView 	errorTextView;
	private CheckBox 	loginSaveCredsCheckBoxView;
	private Button		loginButton;
	private Button		regButton;
    
	/**
	 * This ArrayList holds errors that occur when the user attempts to login
	 */
    private ArrayList<String> errorList;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        
        GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		
		final String regId = GCMRegistrar.getRegistrationId(this);
		
		if (regId.equals("")) 
		{
			GCMRegistrar.register(this, Globals.SENDER_ID);
		} 
		else 
		{
			Globals.regId 			= regId;
			Log.d(this.TAG, "GCMRegister failed, already registered");
		}
		

		                
        /**
         * Links to the XML
         */
        this.loginEmailEditTextView 	= (EditText) 	this.findViewById(R.id.login_email_edit_text_view);
        this.loginPasswordEditTextView 	= (EditText) 	this.findViewById(R.id.login_password_edit_text);
        this.loginSaveCredsCheckBoxView = (CheckBox) 	this.findViewById(R.id.login_save_creds_check_box);
        this.loginLogoTextView 			= (TextView) 	this.findViewById(R.id.login_title_text_view);
        this.errorTextView 				= (TextView) 	this.findViewById(R.id.error_text_view);
        this.loginButton				= (Button) 		this.findViewById(R.id.login_button);
        this.regButton					= (Button) 		this.findViewById(R.id.reg_button);
        
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Cicle_Gordita.ttf"); 
        this.loginLogoTextView.setTypeface(type);
        
        this.loginLogoTextView.setTextColor(Color.WHITE);
        this.loginEmailEditTextView.setTextColor(Color.WHITE);
        this.loginEmailEditTextView.setHintTextColor(Color.argb(205, 204, 204, 204));
        this.loginPasswordEditTextView.setTextColor(Color.WHITE);
        this.loginPasswordEditTextView.setHintTextColor(Color.argb(205, 204, 204, 204));
        this.loginSaveCredsCheckBoxView.setTextColor(Color.WHITE);
        this.loginButton.setTextColor(Color.WHITE);
        this.regButton.setTextColor(Color.WHITE);
        //TODO: make a custom style
        
        /**
         * Inits the error list
         */
        this.errorList = new ArrayList<String>();
        this.errorTextView.setText(" ");
                
        /**
         * These are used for easy login for testing
         */
        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE); 
        
        if(prefs.getBoolean("pref_save_cred", false))
        {
        	this.loginSaveCredsCheckBoxView.setChecked(true);
        	this.loginEmailEditTextView.setText(prefs.getString("pref_save_email", ""));
        	this.loginPasswordEditTextView.setText(prefs.getString("pref_save_password", ""));
        }
        
        this.loginButton.setOnClickListener(this);
        this.regButton.setOnClickListener(this);
        this.loginSaveCredsCheckBoxView.setOnClickListener(this);
        this.loginEmailEditTextView.addTextChangedListener(this);
        this.loginPasswordEditTextView.addTextChangedListener(this);  
	}
	
	/**
	 * This is the method that runs when the login button is clicked.
	 * 
	 * @param v The login button
	 */
	public void onLoginClicked(View v)
	{
		/**
		 * This variable tells the program if there is an error or not
		 */
		boolean hasError =  false;
		
		/**
		 * Reset the error list
		 */
		errorTextView.setText(" ");
		errorList.clear();
		
		/**
		 * Code to confirm correct entry of email and password fields
		 * 
		 * First use the isEmailValid method from the Utility ultity class to check if the email field is correct,
		 * Then check minimum and maximum password length
		 */
		if(!Utility.isEmailValid(loginEmailEditTextView.getText().toString()))
		{
			errorList.add("Invalid Email Address");
			hasError = true;
		}
		if(!Utility.isPasswordValid(loginPasswordEditTextView.getText().toString()))
		{	
			errorList.add("Password Must Be Between 6 And 20 Characters");
			hasError = true;
		}
		
		/**
		 * If there are errors, show them
		 */
		if(hasError)
		{
			this.showErrors();
			
			return;
		}
		
		/**
         * Start GCMRegister and tells it to register this device,
         * If this device is already registered it will use the previous
         * ID instead.
         */
		
		
		/**
		 * Sends variables over to be stored in Global and starts the Rest
		 * call to authenticate
		 */
		Globals.passwordToken 	= Utility.convertStringToSha1(loginPasswordEditTextView.getText().toString());
		Globals.email 			= loginEmailEditTextView.getText().toString();
		
		/**
		 * Rest call for auth is called, this will start a chain of calls going from Authentication to Verify to
		 * Android Add in order to get all information about the user and than enters the MainActivity.  If there
		 * are errors along the way it will display for the user in red
		 */
		new Restful(Restful.AUTH_PATH, Restful.GET, new String[]{"email","device"},new String[]{Globals.email, "1"}, 2, this);
	}
	
	/**
	 * This is the method that runs when the registration button is clicked.
	 * 
	 * @param v The button
	 */
	public void onRegClick(View v)
	{
		String tempEmail = "";
		String tempPassword = "";
		Intent regIntent = new Intent(this,RegistrationActivity.class);
		//Intent regIntent = new Intent(this,UserInfoActivity.class);

		regIntent.putExtra("KEYemail", tempEmail);
		regIntent.putExtra("KEYpassword", tempPassword);
		
		if(!loginEmailEditTextView.getText().equals(""))
		{
			tempEmail = loginEmailEditTextView.getText().toString();
			regIntent.putExtra("KEYemail", tempEmail);
		}
		
		if(!loginPasswordEditTextView.getText().equals(""))
		{
			tempPassword = loginPasswordEditTextView.getText().toString();
			regIntent.putExtra("KEYpassword", tempPassword);
		}
		
		startActivity(regIntent);
	}
	
	public void onCheckClick(View v)
	{
		if(((CheckBox)v).isChecked())
		{
			SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
			
	        prefs.edit().putBoolean	("pref_save_cred"		, true).commit();
	        prefs.edit().putString	("pref_save_email"		, loginEmailEditTextView.getText().toString()	).commit();
	        prefs.edit().putString	("pref_save_password"	, loginPasswordEditTextView.getText().toString()).commit();
		}
		else
		{
			SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
			
	        prefs.edit().putBoolean	("pref_save_cred"		, false	).commit();
	        prefs.edit().putString	("pref_save_email"		, null	).commit();
	        prefs.edit().putString	("pref_save_password"	, null	).commit();
		}
	}
	@Override
	public void preRestExecute(String restCall) 
	{
		if(restCall.equals(Restful.AUTH_PATH))
		{
			Utility.startDialog(this, "Authenticating...", "Logging In");
		}
	}

	@Override
	public void onRestHandleResponseSuccess(String restCall, String response) 
	{
		/**
		 * Used to parse the response into String objects
		 */
		JSONObject parse = null;
		
		/**
		 * This section of code is executed in the Rest background thread after grabbing
		 * data from the Rest call to the server.
		 * 
		 * Does different actions based on the type of Rest call
		 * 
		 * If the call is to Authenticate, than it will grab the authentication token
		 * and store it in Globals
		 * 
		 * If the call is to Verify, than it will grab user data and store it in Globals
		 * 
		 * If the call is to Android Add, then do nothing
		 */
		if(restCall.equals(Restful.AUTH_PATH))
		{
			try 
			{
				parse = new JSONObject(response);
				Globals.token = parse.getJSONObject("authentication").getString("token");
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
		else if(restCall.equals(Restful.VERIFY_PATH))
		{
			try 
			{
				parse = new JSONObject(response);
				JSONObject auth = parse.getJSONObject("authentication");
				JSONObject user = parse.getJSONObject("user"); 
				
				Globals.userId 		= auth.getString("user_id");
				Globals.displayName = user.getString("display_name");
				Globals.colorPref 	= user.getString("color_preference");
				
		        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
		        prefs.edit().putString("pref_color", Globals.colorPref).commit();
		        prefs.edit().putString("pref_display_name", Globals.displayName).commit();
		        
		        
		        Log.d(this.TAG, "The Color Pref: " + prefs.getString("pref_color", "0"));
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}	
		}
		else if(restCall.equals(Restful.REGISTER_ANDROID_USER_PATH))
		{

		}
	}

	@Override
	public void onRestPostExecutionSuccess(String restCall, String result) 
	{
		Log.d(this.TAG, "Rest call: " + restCall + " success!");
		
		/**
		 * This section of code is executed in the UI thread after the Rest call 
		 * is finished and returned a success (status code 200 - 299).
		 * 
		 * Does different actions based on the type of Rest call
		 * 
		 * If the call is to Authenticate, than it will launch the Verify Rest call
		 * 
		 * If the call is to Verify, than it will launch the Android Add Rest call
		 * 
		 * If the call is to Android Add, then it will launch the MainActivity
		 */
		if(restCall.equals(Restful.AUTH_PATH))
		{
			Utility.changeDialog(null, "Verifying...");
			new Restful(Restful.VERIFY_PATH, Restful.GET, null, null, 0, this);
		}
		else if(restCall.equals(Restful.VERIFY_PATH))
		{
			Utility.changeDialog(null, "Registering Android Device...");
			
			Log.d(this.TAG, "Current Reg ID: " + Globals.regId);
			Log.d(this.TAG, "Current Device ID: " + Secure.getString(this.getContentResolver(), Secure.ANDROID_ID));
			
			new Restful(Restful.REGISTER_ANDROID_USER_PATH, Restful.POST, new String[]{"email","name","reg_id","device_id"}, new String[]{Globals.email,Globals.displayName,Globals.regId, Secure.getString(this.getContentResolver(), Secure.ANDROID_ID)}, 4, this);
		}
		else if(restCall.equals(Restful.REGISTER_ANDROID_USER_PATH))
		{
			Utility.stopDialog();
			Intent mainIntent = new Intent(this, MainActivity.class);
			this.startActivity(mainIntent);	
		}
	}

	@Override
	public void onRestPostExecutionFailed(String restCall, int code, String result) 
	{
		Log.d(this.TAG, "Rest call: " + restCall + "failed with status code: " + code);
		Log.d(this.TAG,"Result from server is:\n" + result);
		
		/**
		 * This section of code is executed in the UI thread after the Rest call 
		 * is finished and returned a failure.
		 * 
		 * Does different actions based on the type of Rest call
		 * 
		 * If the call is to Authenticate, than it will tell the user the email
		 * input is incorrect
		 * 
		 * If the call is to Verify, than it will tell the user the password input
		 * is incorrect
		 * 
		 * If the call is to Android Add, it will launch MainActivity if the status
		 * code is 409 as that means that the registration id is already in the server,
		 * otherwise it is a bug and needs to be checked.
		 */
		if(restCall.equals(Restful.AUTH_PATH))
		{
			switch(code)
			{
				case 404:
					errorList.add("Invalid Email Address");
					break;
			}
		}
		else if(restCall.equals(Restful.VERIFY_PATH))
		{
			switch(code)
			{
				case 401:
					errorList.add("Invalid Password");
					break;
			}
		}
		else if(restCall.equals(Restful.REGISTER_ANDROID_USER_PATH))
		{
			switch(code)
			{
				case 409: 
					Intent mainIntent = new Intent(this, MainActivity.class);
					this.startActivity(mainIntent);	
					break;
			}
			errorList.add("Login Failed");
		}
		
		Utility.stopDialog();
		this.showErrors();
	}
	
	@Override
	public void onRestCancelled(String restCall, String result) 
	{
		Utility.stopDialog();
	}
	
	/**
	 * This method will show the errors stored in errorList to the user
	 */
	private void showErrors()
	{
		String temp = "";
		for(int x = 0; x < errorList.size();x++)
		{
			temp += errorList.get(x) + "\n";
		}
		
		errorTextView.setText(temp);
		errorTextView.setTextColor(Color.RED);
	}

	@Override
	public void onClick(View view) 
	{
		switch(view.getId())
		{
			case R.id.login_button:
				this.onLoginClicked(view);
				break;
			case R.id.reg_button:
				this.onRegClick(view);
				break;
			case R.id.login_save_creds_check_box:
				this.onCheckClick(view);
				break;
		}
	}

	@Override
	public void afterTextChanged(Editable arg0) 
	{
		SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
		
        prefs.edit().putBoolean	("pref_save_cred"		, false	).commit();
        prefs.edit().putString	("pref_save_email"		, null	).commit();
        prefs.edit().putString	("pref_save_password"	, null	).commit();
        
        this.loginSaveCredsCheckBoxView.setChecked(false);
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) 
	{
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) 
	{
		
	}


}
