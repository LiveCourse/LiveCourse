package net.livecourse.android.login;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
	private EditText 	regEmailEditTextView;
	private EditText 	regPasswordEditTextView;
	private EditText 	regRePasswordEditTextView;
	private EditText 	regDisplayNameEditTextView;
	private TextView 	errorTextView;
	private TextView 	regTitleTextView;
	private Button		regButton;
	private String 		user_email;
	private String 		user_display_name;
	private String 		user_pass; 
	
	/**
	 * This ArrayList holds errors that occur when the user attempts to login
	 */
    private ArrayList<String> errorList;

	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);
        
        this.regEmailEditTextView 		= (EditText) 	this.findViewById(R.id.reg_email_edit_text_view);
        this.regPasswordEditTextView 	= (EditText) 	this.findViewById(R.id.reg_password_edit_text_view);
        this.regRePasswordEditTextView 	= (EditText) 	this.findViewById(R.id.reg_re_password_edit_text_view);
        this.regDisplayNameEditTextView = (EditText) 	this.findViewById(R.id.reg_display_name_edit_text_view);
        this.errorTextView 				= (TextView) 	this.findViewById(R.id.error_text_view);
        this.regTitleTextView 			= (TextView) 	this.findViewById(R.id.reg_title_text_view);
        this.regButton					= (Button)		this.findViewById(R.id.reg_finish_button);
        
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Cicle_Gordita.ttf"); 
        this.regTitleTextView.setTypeface(type);
        
        this.regTitleTextView.setTextColor(Color.WHITE);
        this.regEmailEditTextView.setTextColor(Color.WHITE);
        this.regEmailEditTextView.setHintTextColor(Color.argb(205, 204, 204, 204));
        this.regPasswordEditTextView.setTextColor(Color.WHITE);
        this.regPasswordEditTextView.setHintTextColor(Color.argb(205, 204, 204, 204));
        this.regRePasswordEditTextView.setTextColor(Color.WHITE);
        this.regRePasswordEditTextView.setHintTextColor(Color.argb(205, 204, 204, 204));
        this.regDisplayNameEditTextView.setTextColor(Color.WHITE);
        this.regDisplayNameEditTextView.setHintTextColor(Color.argb(205, 204, 204, 204));
        this.regButton.setTextColor(Color.WHITE);
        
        /**
         * Inits the error list
         */
        this.errorList = new ArrayList<String>();
        
        this.regButton.setOnClickListener(this);
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
		this.errorTextView.setText("");
		this.errorList.clear();
		
		/**
		 * Code to confirm correct entry of email and password fields
		 * 
		 * First use the isEmailValid method from the Utility ultity class to check if the email field is correct,
		 * Then check minimum and maximum password length
		 */
		if(!Utility.isEmailValid(this.regEmailEditTextView.getText().toString()))
		{
			this.errorList.add("Invalid Email Address");
			hasError = true;
		}
		if(!Utility.isPasswordValid(regPasswordEditTextView.getText().toString()))
		{	
			this.errorList.add("Password Must Be Between 6 And 20 Characters");
			hasError = true;
		}
		if( !regPasswordEditTextView.getText().toString().equals( (regRePasswordEditTextView.getText().toString()) ) )
		{
			this.errorList.add("Passwords do not match");
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
		
		
		this.user_email = this.regEmailEditTextView.getText().toString();
		this.user_pass = Utility.convertStringToSha1(this.regPasswordEditTextView.getText().toString());
		this.user_display_name = this.regDisplayNameEditTextView.getText().toString();
		
		new Restful(Restful.REGISTER_USER_PATH, Restful.POST, new String[]{"email", "password", "display_name"},new String[]{this.user_email, this.user_pass, this.user_display_name}, 3, this);

	
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
		
		this.errorTextView.setText(temp);
		this.errorTextView.setTextColor(Color.RED);
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
					this.errorList.add("Invalid Input");
					break;
				case 409:
					this.errorList.add("Email Already Exists");
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
	public void onClick(View view) 
	{
		switch(view.getId())
		{
			case R.id.reg_finish_button:
				this.onRegClick(view);
				break;
		}
	}
}
