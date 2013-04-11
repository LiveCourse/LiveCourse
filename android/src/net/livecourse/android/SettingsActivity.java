package net.livecourse.android;

import android.os.Bundle;

import net.livecourse.R;


import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class SettingsActivity extends SherlockPreferenceActivity
{
	@SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.settings_layout);
        
	}
}
