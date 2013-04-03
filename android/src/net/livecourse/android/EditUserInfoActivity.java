package net.livecourse.android;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class EditUserInfoActivity extends SherlockPreferenceActivity
{
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.edituserinfo_layout);
        
	}
}
