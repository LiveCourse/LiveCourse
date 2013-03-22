package net.livecourse;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

/**
 * This class allows the user to register.
 * 
 * @author Darren
 *
 */
public class RegistrationActivity extends SherlockActivity
{

	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);
	}
}
