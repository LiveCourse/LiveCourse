package net.livecourse.android;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.util.Log;

import net.livecourse.R;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.Globals;
import net.livecourse.utility.Utility;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class SettingsActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener, OnRestCalled
{
	private ProgressDialog progressDialog;
	
	@SuppressWarnings("deprecation")
	@Override
	/**
	 * Using the deprecated method for now as the other method is long and annoying
	 * and not supported by ABS
	 */
    protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.settings_layout);  
        
        Utility.changeActivityColorBasedOnPref(this, this.getSupportActionBar());
        
        progressDialog = new ProgressDialog(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) 
	{
		Log.d("SettingsActivity", "The key: " + key + " value: " + sharedPreferences.getString(key, null));
		if(key.equals("prefColor"))
		{
			String curColor = sharedPreferences.getString(key, null);
			if(curColor != null)
			{
				Globals.colorPref = curColor;
				
				new Restful(Restful.UPDATE_COLOR_PREF_PATH, Restful.POST,new String[]{"color"}, new String[]{Globals.colorPref}, 1, this);
				
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() 
	{
	    super.onResume();
	    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() 
	{
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void preRestExecute(String restCall) 
	{
		if(restCall.equals(Restful.UPDATE_COLOR_PREF_PATH))
		{
			Utility.startDialog(progressDialog, "Updating Preferences", "Updating Color...");
		}
	}

	@Override
	public void onRestHandleResponseSuccess(String restCall, String response) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestPostExecutionSuccess(String restCall, String result) 
	{
		if(restCall.equals(Restful.UPDATE_COLOR_PREF_PATH))
		{
			Utility.stopDialog(progressDialog);
			Utility.changeActivityColorBasedOnPref(Globals.mainActivity, Globals.mainActivity.getSupportActionBar());
			Utility.changeActivityColorBasedOnPref(this, this.getSupportActionBar());
		}		
	}

	@Override
	public void onRestPostExecutionFailed(String restCall, int code, String result) 
	{
		if(restCall.equals(Restful.UPDATE_COLOR_PREF_PATH))
		{
			Utility.stopDialog(progressDialog);
		}		
	}

	@Override
	public void onRestCancelled(String restCall, String result) 
	{
		if(restCall.equals(Restful.UPDATE_COLOR_PREF_PATH))
		{
			Utility.stopDialog(progressDialog);
		}		
	}
}
