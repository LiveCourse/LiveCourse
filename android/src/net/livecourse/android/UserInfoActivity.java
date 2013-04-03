package net.livecourse.android;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import net.livecourse.android.R;
import net.livecourse.rest.REST;

public class UserInfoActivity extends SherlockFragmentActivity
{
	private EditText editText; 
	
	int check = 0;
	private static final int RESULT_SETTINGS = 1;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_layout);
        
        editText = (EditText) findViewById(R.id.edit_Text);
        
        editText.setText(REST.email);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater=getSupportMenuInflater();
		inflater.inflate(R.menu.userinfo_menu, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.userinfo_menu:
				Toast.makeText(this, "Menu item 1 tapped", Toast.LENGTH_SHORT).show();
				//Intent edituserInfo = new Intent(this.getSherlockActivity(),UserInfoActivity.class);
				//startActivity(userInfoIntent);
				Intent i = new Intent(this, EditUserInfoActivity.class);
	            startActivityForResult(i, RESULT_SETTINGS);
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 
        switch (requestCode) {
        case RESULT_SETTINGS:
            showUserSettings();
            break;
 
        }
 
    }
	
	private void showUserSettings() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
 
        StringBuilder builder = new StringBuilder();
 
        builder.append(""
                + sharedPrefs.getString("prefUsername", "NULL"));

        //TextView settingsTextView = (TextView) findViewById(R.id.edit_Text);
 
        //settingsTextView.setText(builder.toString());
        //this.setTitle(builder.toString());
        System.out.println(builder.toString());
        new REST(this, null, null, builder.toString(), null, null, REST.CHANGE_NAME).execute();
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
