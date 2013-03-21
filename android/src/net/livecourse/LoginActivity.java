package net.livecourse;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class LoginActivity extends SherlockFragmentActivity{
	
	private EditText loginEmailEditTextView; 
	private EditText loginPasswordEditTextView;
	private Button loginButton;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        
        final Intent mainIntent = new Intent(this, MainActivity.class);
        
        loginEmailEditTextView = (EditText) findViewById(R.id.login_email_edit_text_view);
        loginPasswordEditTextView = (EditText) findViewById(R.id.login_password_edit_text);
        loginButton = (Button) findViewById(R.id.login_button);
        
        loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				try {
					startActivity(mainIntent);
				    
				} catch ( ActivityNotFoundException e) {
				    e.printStackTrace();
				}
				
			}
		});
        
        
	}
	

}
