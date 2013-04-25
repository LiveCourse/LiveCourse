package net.livecourse.android;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.util.Log;

import net.livecourse.R;
import net.livecourse.rest.OnRestCalled;
import net.livecourse.rest.Restful;
import net.livecourse.utility.Globals;
import net.livecourse.utility.Utility;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

/**
 * This class controls the settings for the main activity and the application
 * in general.
 */
public class SettingsActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener, OnRestCalled
{
	private final String TAG = " == Settings Activity ==";
	private String	tempChangeStorage;
	
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
        
        ListPreference listPreference = (ListPreference) findPreference("pref_color");
        listPreference.setValueIndex(Integer.parseInt(Globals.colorPref));       
	}

	@Override
	/**
	 * Updates setting if they are changed
	 */
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) 
	{
		Log.d(this.TAG, "The key: " + key);
		
		if(key.equals("pref_color"))
		{
			this.tempChangeStorage = sharedPreferences.getString(key, "0");
			Log.d(this.TAG, "Temp Change Storage for color: " + this.tempChangeStorage);
			Log.d(this.TAG, "Global Email: " + Globals.email);
			
			if(this.tempChangeStorage != null)
			{				
				new Restful(Restful.UPDATE_COLOR_PREF_PATH, Restful.POST,new String[]{"color"}, new String[]{this.tempChangeStorage}, 1, this);
			}
		}
		if(key.equals("pref_display_name"))
		{
			this.tempChangeStorage = sharedPreferences.getString(key, null);
			if(this.tempChangeStorage != null)
			{				
		        new Restful(Restful.CHANGE_DISPLAY_NAME_PATH, Restful.POST,new String[]{"name"}, new String[]{this.tempChangeStorage}, 1, this);
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
			Utility.startDialog(this, "Updating Preferences", "Updating Color...");
			Log.d(this.TAG, "The current color pref: " + Globals.colorPref);
		}
		else if(restCall.equals(Restful.CHANGE_DISPLAY_NAME_PATH))
		{
			Utility.startDialog(this, "Updating Preferences", "Updating Name...");
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
			if(this.tempChangeStorage != null)
				Globals.colorPref = this.tempChangeStorage;
			else
				Log.e(this.TAG, "Variable TempChangeStorage is null at updating color");

			Utility.changeActivityColorBasedOnPref(Globals.mainActivity, Globals.mainActivity.getSupportActionBar());
			Utility.changeActivityColorBasedOnPref(this, this.getSupportActionBar());
		}		
		else if(restCall.equals(Restful.CHANGE_DISPLAY_NAME_PATH))
		{
			if(this.tempChangeStorage != null)
				Globals.displayName = this.tempChangeStorage;
			else
				Log.e(this.TAG, "Variable TempChangeStorage is null at updating color");
		}
		
		Utility.stopDialog();
	}

	@Override
	public void onRestPostExecutionFailed(String restCall, int code, String result) 
	{	
		Log.d(this.TAG, "Rest call: " + restCall + "failed with status code: " + code);
		Log.d(this.TAG,"Result from server is:\n" + result);
		
		Utility.stopDialog();
	}

	@Override
	public void onRestCancelled(String restCall, String result) 
	{
		
		Utility.stopDialog();
	}
}
