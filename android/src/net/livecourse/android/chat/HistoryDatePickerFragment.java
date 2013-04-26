package net.livecourse.android.chat;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Calendar;

import net.livecourse.utility.Globals;
import net.livecourse.utility.Utility;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class HistoryDatePickerFragment extends SherlockDialogFragment
{
	private boolean	invoked	= false;
	private DatePicker mDatePickerInstance;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		final Calendar c = Calendar.getInstance();
		final int year = c.get(Calendar.YEAR);
		final int month = c.get(Calendar.MONTH);
		final int day = c.get(Calendar.DAY_OF_MONTH);

		final DatePickerDialog picker = new DatePickerDialog(this.getSherlockActivity(), null, year, month, day);
		
		Field mDatePickerField = null;
		try
		{
			mDatePickerField = picker.getClass().getDeclaredField("mDatePicker");
			mDatePickerField.setAccessible(true);
			this.mDatePickerInstance = (DatePicker) mDatePickerField.get(picker);
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		
		
		picker.setCancelable(true);
		picker.setCanceledOnTouchOutside(true);
		picker.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					Log.d("Picker", "Cancel!");
				}
			});
		picker.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					Log.d("Picker", "Correct behavior!");
					changeDateOnSet(mDatePickerInstance.getYear(), mDatePickerInstance.getMonth(), mDatePickerInstance.getDayOfMonth());
				}
			});

		return picker;
	}

	public void changeDateOnSet(int year, int month, int date)
	{
		if (invoked)
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
