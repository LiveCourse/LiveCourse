package net.livecourse.android;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import net.livecourse.R;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;

/**
 * This class allows the user to register.
 * 
 * @author Darren
 *
 */
public class RegistrationActivity extends SherlockActivity implements OnClickListener, OnRestCalled
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

	@Override
	public void onClick(View v) 
	{
		new Restful(Restful.REGISTER_USER_PATH,Restful.GET, new String[]{"user_id"}, new String[]{this.userId}, 1, this);
		
	}

	@Override
	public void preRestExecute(String restCall) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestHandleResponseSuccess(String restCall, String response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestPostExecutionSuccess(String restCall, String result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestPostExecutionFailed(String restCall, int code,
			String result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestCancelled(String restCall, String result) {
		// TODO Auto-generated method stub
		
	}
}
