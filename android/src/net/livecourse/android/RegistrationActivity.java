package net.livecourse.android;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import net.livecourse.R;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.Utility;

/**
 * This class allows the user to register.
 * 
 * @author Darren
 *
 */
public class RegistrationActivity extends SherlockActivity implements OnClickListener, OnRestCalled
{
	private final String TAG = " == Registration Activity == ";
	
	int check = 0;
	private EditText textEmail, textPass1, textPass2, textDisplayName;
	private TextView errorTextView;
	private String user_email, user_display_name, user_pass; 
	
	/**
	 * This ArrayList holds errors that occur when the user attempts to login
	 */
    private ArrayList<String> errorList;

	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout_old);
        
        textEmail = (EditText) findViewById(R.id.editText1);
        textPass1 = (EditText) findViewById(R.id.editText2);
        textPass2 = (EditText) findViewById(R.id.editText3);
        textDisplayName = (EditText) findViewById(R.id.editText4);
        errorTextView = (TextView) findViewById(R.id.error_text_view);
        
         
        /**
         * Inits the error list
         */
        errorList = new ArrayList<String>();
        
        /**
         * Sets the activity to focus on the email EditText view
         */
        textEmail.requestFocus();
        
	}
	
	public void onRegClick(View v)
	{
		/**
		 * This variable tells the program if there is an error or not
		 */
		boolean hasError =  false;
		
		/**
		 * Reset the error list
		 */
		errorTextView.setText("");
		errorList.clear();
		
		/**
		 * Code to confirm correct entry of email and password fields
		 * 
		 * First use the isEmailValid method from the Utility ultity class to check if the email field is correct,
		 * Then check minimum and maximum password length
		 */
		if(!Utility.isEmailValid(textEmail.getText().toString()))
		{
			errorList.add("Invalid Email Address");
			hasError = true;
		}
		if(!Utility.isPasswordValid(textPass1.getText().toString()))
		{	
			errorList.add("Password Must Be Between 6 And 20 Characters");
			hasError = true;
		}
		if( !textPass1.getText().toString().equals( (textPass2.getText().toString()) ) )
		{
			errorList.add("Passwords do not match");
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
		
		
		 user_email = textEmail.getText().toString();
		 user_pass = Utility.convertStringToSha1(textPass1.getText().toString());
	     user_display_name = textDisplayName.getText().toString();
		
		new Restful(Restful.REGISTER_USER_PATH, Restful.POST, new String[]{"email", "password", "display_name"},new String[]{user_email, user_pass, user_display_name}, 3, this);

	
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
		errorTextView.setVisibility(View.VISIBLE);
	}



	@Override
	public void preRestExecute(String restCall) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestHandleResponseSuccess(String restCall, String response)
	{
		
	}

	@Override
	public void onRestPostExecutionSuccess(String restCall, String result)
	{
		if(restCall.equals(Restful.REGISTER_USER_PATH))
		{
			Intent loginIntent = new Intent(this,LoginActivity.class);
			startActivity(loginIntent);
		}
	}

	@Override
	public void onRestPostExecutionFailed(String restCall, int code,
			String result)
	{
		Log.d(this.TAG, "Rest call: " + restCall + "failed with status code: " + code);
		Log.d(this.TAG,"Result from server is:\n" + result);	
		
		if(restCall.equals(Restful.REGISTER_USER_PATH))
		{
			switch(code)
			{
				case 403:
					errorList.add("Invalid Input");
					break;
				case 409:
					errorList.add("Email Already Exists");
					break;
			}
		}
		this.showErrors();
	}

	@Override
	public void onRestCancelled(String restCall, String result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}
