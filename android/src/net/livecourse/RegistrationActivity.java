package net.livecourse;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;

/**
 * This class allows the user to register.
 * 
 * @author Darren
 *
 */
public class RegistrationActivity extends SherlockActivity
{
	int check = 0;
	EditText testText;

	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);
        
        testText = (EditText) findViewById(R.id.editText1);
	}
	
	public void onTestClick(View v)
	{
		if(check==0)
		{
			testText.setClickable(false);
			testText.setFocusable(false);
			testText.setFocusableInTouchMode(false);
			
			check =1;
		}
		else
		{
			testText.setClickable(true);
			testText.setFocusable(true);
			testText.setFocusableInTouchMode(true);
			testText.requestFocus();
			
			check =0;
		}
	}
}
