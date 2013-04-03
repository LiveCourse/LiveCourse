package net.livecourse.android;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import net.livecourse.android.R;
import net.livecourse.rest.REST;

public class UserInfoActivity extends SherlockFragmentActivity
{
	private EditText editText; 
	
	int check = 0;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_layout);
        
        editText = (EditText) findViewById(R.id.edit_Text);
        
        editText.setText(REST.email);
	}
	
	public void testClick(View v)
	{
		if(check == 0)
		{
			editText.setClickable(false);
			editText.setFocusable(false);
			editText.setFocusableInTouchMode(false);
			
			check = 1;
		}
		else
		{
			editText.setClickable(true);
			editText.setFocusable(true);
			editText.setFocusableInTouchMode(true);
			editText.requestFocus();
			
			check = 0;
		}
	}
	

}
