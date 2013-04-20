package net.livecourse.android;

import java.text.ParseException;
import java.util.Calendar;

import net.livecourse.utility.Globals;
import net.livecourse.utility.Utility;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class HistoryDatePickerFragment extends SherlockDialogFragment implements DatePickerDialog.OnDateSetListener
{
	private boolean invoked = false;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
        int month  = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

		return new DatePickerDialog(this.getSherlockActivity(), this, year, month, day);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int date) 
	{
		if(invoked)
			return;
		invoked = true;
		
		String curDate = String.format("%02d/%02d/%04d", month + 1, date, year) + " 00:00:00";
		Log.d("== DatePicker ==", "The date: " + curDate);
		
		long epoch;
		
		try 
		{
			epoch = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(curDate).getTime() / 1000;
			Globals.historyTime = epoch;
			Intent historyIntent = new Intent(this.getSherlockActivity(), HistoryViewActivity.class);
			historyIntent.putExtra("time", epoch);
			historyIntent.putExtra("date", Utility.convertToStringDate(month, date, year));
			historyIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			
			Log.d("== DatePicker ==", "History activity started");
			this.getSherlockActivity().startActivity(historyIntent);
			
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
		this.dismiss();
	}
}
