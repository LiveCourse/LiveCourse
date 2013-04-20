package net.livecourse.utility;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.livecourse.R;

import org.apache.http.HttpEntity;

import com.actionbarsherlock.app.ActionBar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.widget.TextView;

/**
 * This is the ultities class that focuses on ultity based methods as well as
 * methods that focus around the REST API calls.  All REST API calls should go here.
 * 
 * @author Darren
 *
 */
public class Utility 
{
	private static final String TAG = " == Utility == ";

	/**
	 * This method checks the email string to see if it is a valid email string.
	 * Uses regex to check.
	 * 
	 * @param email The email to be checked
	 * @return 	True is email fits the regex
	 * 			False otherwise
	 */
	public static boolean isEmailValid(String email)
	{
		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    CharSequence inputStr = email;
		
	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches())
	        return true;
	    return false;
	}
	
	/**
	 * Checks if the password has 8 or more characters and has 20 or less
	 * 
	 * @param password
	 * @return	true if password is valid
	 * 			false otherwise
	 */
	public static boolean isPasswordValid(String password)
	{
		if(password.length() >= 6 && password.length() <= 20)
			return true;
		return false;
	}
	
	/**
	 * Checks if password is the same, and then check if password one keeps to
	 * the character limits.
	 * 
	 * @param pass1 First password
	 * @param pass2 Second password
	 * @return	0 if ok
	 * 			1 if wrong lengthes
	 * 			2 if doesn't match
	 */
	public static int isPasswordSame(String pass1, String pass2)
	{
		if(!isPasswordValid(pass1))
			return 1;
		if(!pass1.equals(pass2))
			return 2;
		return 0;
	}
	
	/**
	 * Converts minutes (in string) to HH:mm
	 * @param minutes
	 * @return HH:mm
	 */
	public static String convertMinutesTo24Hour(String minutes)
	{
		int time = Integer.parseInt(minutes);
		if(time%60 == 0)
			return time/60 + ":" + time%60+"0";
		return time/60 + ":" + time%60;
	}
	
	/**
	 * Converts string into SHA-1 hash
	 * @param input
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	public static String convertStringToSha1(String input)
	{
		String cpyInput = input;
		MessageDigest md = null;
		try 
		{
			md = MessageDigest.getInstance("SHA-1");
		} 
		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
		}
        md.update(cpyInput.getBytes());
        
        byte byteData[] = md.digest();
        
        /**
         * Convert from byte to hex
         */
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) 
        {
        	buffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        
		return buffer.toString();
	}
	
	/**
	 * Grabs the String content from an HttpEntity
	 * 
	 * @param entity
	 * @return The String in the entity
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException 
	{
		InputStream in = entity.getContent();
		StringBuffer out = new StringBuffer();
		
		int n = 1;
		while (n>0) 
		{
			byte[] b = new byte[4096];
			n =  in.read(b);
			if (n>0) 
			{
				out.append(new String(b, 0, n));
			}
		}
		
		return out.toString();
	}
	
	/**
	 * Used to start progress dialogs, the dialog must have been already created
	 * 
	 * @param progressDialog	The dialog to start
	 * @param context			The context to start in
	 * @param title				The title
	 * @param message			The message
	 */
	public static void startDialog(ProgressDialog progressDialog, String title, String message)
	{
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		progressDialog.show();
	}
	
	/**
	 * Used to change dialog text
	 * 
	 * @param progressDialog	The dialog to change
	 * @param title				The title to change, setting it to null will not change it
	 * @param message			The message to change, setting it to null will not change it
	 */
	public static void changeDialog(ProgressDialog progressDialog, String title, String message)
	{
		Log.d(Utility.TAG, "Dialog: " + progressDialog + " title: " + title + " message: " + message);
		if(message != null)
			progressDialog.setMessage(message);
		if(title != null)
			progressDialog.setTitle(title);
	}
	
	/**
	 * Used to close the dialog
	 * 
	 * @param progressDialog	The dialog to close
	 */
	public static void stopDialog(ProgressDialog progressDialog)
	{
		if(progressDialog != null && progressDialog.isShowing())
		{
			progressDialog.dismiss();
		}
	}
	
	/**
	 * This method is used to hange the color of a specified activity based on the color in preferences
	 * 
	 * @param activity	The activity whose color is to be changed
	 */
	public static void changeActivityColorBasedOnPref(Activity activity, ActionBar actionBar)
	{
		switch(Integer.parseInt(Globals.colorPref))
		{
			case Globals.INDEX_BLUE:
				Utility.changeActivityColor(activity, actionBar, Globals.HEX_BLUE);
				break;
			case Globals.INDEX_RED:
				Utility.changeActivityColor(activity, actionBar,Globals.HEX_RED);
				break;
			case Globals.INDEX_BROWN:
				Utility.changeActivityColor(activity, actionBar,Globals.HEX_BROWN);
				break;
			case Globals.INDEX_GREEN:
				Utility.changeActivityColor(activity, actionBar,Globals.HEX_GREEN);
				break;
			case Globals.INDEX_CYAN:
				Utility.changeActivityColor(activity, actionBar,Globals.HEX_CYAN);
				break;
			case Globals.INDEX_PURPLE:
				Utility.changeActivityColor(activity, actionBar,Globals.HEX_PURPLE);
				break;
		}
	}
	
	/**
	 * This method converts the month given as an int into the month given as a string,
	 * with 0 = January
	 * 
	 * @param month	The month of the year with 0 being January
	 * @return		The month as a string, null if month not between 0 (inclusive) and
	 * 				12 (exclusive)
	 */
	public static String convertMonthToString(int month)
	{
		switch(month)
		{
			case  0:
				return "January";
			case  1:
				return "Febuary";
			case  2:
				return "March";
			case  3:
				return "April";
			case  4:
				return "May";
			case  5:
				return "June";
			case  6:
				return "July";
			case  7:
				return "August";
			case  8:
				return "September";
			case  9:
				return "October";
			case 10:
				return "November";
			case 11:
				return "December";	
		}
		return null;
	}
	
	/**
	 * This method takes the month, date, and year in ints and outputs the String of the date
	 * 
	 * @param month	The month
	 * @param date	The date
	 * @param year	The year
	 * @return		The date in the format: Month Date, Year
	 */
	public static String convertToStringDate(int month, int date, int year)
	{
		return Utility.convertMonthToString(month) + " " + date + ", " + year;
	}
	
	/**
	 * This method is used to change a specified activity to a color in hex
	 * 
	 * @param activity	The activity whose color is to be changed
	 * @param hexColor	The color in hex that the activity's color is going to change to
	 */
	public static void changeActivityColor(Activity activity, ActionBar actionBar, String hexColor)
	{
		Log.d(Utility.TAG, "The hex color: " + hexColor);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(hexColor)));
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
        int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");    
        
        if ( titleId == 0 ) 
        {
        	titleId = com.actionbarsherlock.R.id.abs__action_bar_title;
        }
        actionBar.setIcon(R.drawable.paperairplanewhite);
        
        TextView yourTextView = (TextView) activity.findViewById(titleId);
        yourTextView.setTextColor(Color.WHITE);
	}
}
