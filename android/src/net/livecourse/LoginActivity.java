package net.livecourse;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * This Class is responsible for allowing the user to login.
 * Upon login it will redirect the user to the MainActivity.
 * 
 * @author Darren
 *
 */
public class LoginActivity extends SherlockFragmentActivity{
	
	private EditText loginEmailEditTextView; 
	private EditText loginPasswordEditTextView;
	private TextView errorTextView;
    private Intent mainIntent;
    
    private ArrayList<String> errorList;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        
        mainIntent = new Intent(this, MainActivity.class);
        
        loginEmailEditTextView = (EditText) findViewById(R.id.login_email_edit_text_view);
        loginPasswordEditTextView = (EditText) findViewById(R.id.login_password_edit_text);
        errorTextView = (TextView) findViewById(R.id.error_text_view);
        
        errorList = new ArrayList<String>();
        
        loginEmailEditTextView.requestFocus();
        
        /**
         * These are used for easy login for testing
         */
        loginEmailEditTextView.setText("test@test.com");
        loginPasswordEditTextView.setText("123456789");
	}
	
	/**
	 * This is the method that runs when the login button is clicked.
	 * 
	 * @param v Don't really need to worry about it, it's the login button.
	 */
	public void onLoginClicked(View v)
	{
		boolean hasError =  false;
		
		/**
		 * Reset variables
		 */
		errorTextView.setText("");
		errorList.clear();
		
		/**
		 * Code to confirm correct entry of email and password fields
		 * 
		 * First use the isEmailValid method from the Utility ultity class to check if the email field is correct,
		 * Then check minimum and maximum password length
		 */
		if(!Utility.isEmailValid(loginEmailEditTextView.getText().toString()))
		{
			errorList.add("Invalid Email");
			hasError = true;
		}
		if(!Utility.isPasswordValid(loginPasswordEditTextView.getText().toString()))
		{	
			errorList.add("Invalid Password");
			hasError = true;
		}
		
		if(hasError)
		{
			String temp = "";
			for(int x = 0; x < errorList.size();x++)
			{
				temp += errorList.get(x) + "\n";
			}
			
			errorTextView.setText(temp);
			errorTextView.setTextColor(Color.RED);
			errorTextView.setVisibility(View.VISIBLE);
			
			return;
		}
		
		startActivity(mainIntent);	
	}
	
	/**
	 * This is the method that runs when the registration button is clicked.
	 * 
	 * @param v
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
}
