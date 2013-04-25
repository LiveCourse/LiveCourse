package net.livecourse.utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.livecourse.R;

import org.apache.http.HttpEntity;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

/**
 * This is the utilities class, it's purpose is to contain general purpose 
 * static methods that are used or can be used throughout the program by
 * any class.  All methods in this class must be static.  This class should
 * not contain any global variables.  For application wide global variables,
 * please refer to the Globals class.
 *
 */
public class Utility 
{
	private static final String TAG = " == Utility == ";

	/**
	 * This method checks the email string to see if it is a valid email string.
	 * Uses regex to check.
	 * 
	 * @param email 	The email to be checked
	 * @return 			True is email fits the regex
	 * 					False otherwise
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
	 * This method checks if password is the same, and then check if password one keeps to
	 * the character limits.
	 * 
	 * @param pass1 	First password
	 * @param pass2 	Second password
	 * @return			0 if ok
	 * 					1 if wrong lengthes
	 * 					2 if doesn't match
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
	 * This method SHA-1 hashs the given string
	 * 
	 * @param input 	The string to be hashed
	 * @return			The hashed string
	 * @throws 			NoSuchAlgorithmException 
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
	 * @param entity 	The entity for which to grab the content
	 * 					NOTE: The content will get comsumed
	 * @return 			The String in the entity
	 * @throws 			IllegalStateException
	 * @throws 			IOException
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
	public static void startDialog(Context context, String title, String message)
	{
		Globals.progressDialog = new ProgressDialog(context);
		
		if(title != null)
			Globals.progressDialog.setTitle(title);
		if(message != null)
			Globals.progressDialog.setMessage(message);
		Globals.progressDialog.show();
	}
	
	/**
	 * Used to change dialog text
	 * 
	 * @param progressDialog	The dialog to change
	 * @param title				The title to change, setting it to null will not change it
	 * @param message			The message to change, setting it to null will not change it
	 */
	public static void changeDialog(String title, String message)
	{
		
		if(Globals.progressDialog == null)
		{
			Log.e(Utility.TAG, "progressDialog is null on changeDialog");
			return;
		}
		
		Log.d(Utility.TAG, "Dialog: " + Globals.progressDialog + " title: " + title + " message: " + message);
		if(message != null)
			Globals.progressDialog.setMessage(message);
		if(title != null)
			Globals.progressDialog.setTitle(title);
	}
	
	/**
	 * Used to close the dialog
	 * 
	 * @param progressDialog	The dialog to close
	 */
	public static void stopDialog()
	{
		if(Globals.progressDialog == null)
		{
			Log.e(Utility.TAG, "progressDialog is null on stopDialog");
			return;
		}
		
		if(Globals.progressDialog.isShowing())
			Globals.progressDialog.dismiss();

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
	 * @param month		The month of the year with 0 being January
	 * @return			The month as a string, null if month not between 0 (inclusive) and
	 * 					12 (exclusive)
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
	 * @param month		The month
	 * @param date		The date
	 * @param year		The year
	 * @return			The date in the format: Month Date, Year
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
	
	/**
	 * This method saves a picture taken from the camera
	 * 
	 * @return 			The file with the path to the picture
	 */
	public static File savePictureFromCamera()
	{
		File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "LiveCourse");  
		storageDir.mkdir();
			
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = Globals.displayName + "_" + timeStamp;
		File image = new File(storageDir, imageFileName + ".jpg");
		try 
		{
			image.createNewFile();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		Globals.filePath = image.getAbsolutePath();
		return image;
	}
	
	/**
	 * This method will convert an URI to a full file path and return the File object
	 * with that path
	 * 
	 * @param uri 		The URI to be converted
	 * @return 			The file with the file path of the URI
	 */
	public static File fileFromURI(Uri uri)
	{
		String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = Globals.mainActivity.getContentResolver().query( uri, proj, null, null, null); 
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        String filePath = cursor.getString(columnIndex);
		return new File(filePath);
	}
	
	/**
	 * Force hides the keyboard for the given activity
	 * 
	 * @param activity 		The activity for which to hide the keyboard
	 */
	public static void hideKeyboard(SherlockFragmentActivity activity)
	{
		if(activity == null)
		{
			Log.e(Utility.TAG, "activity is null on hideKeyboard");
			return;
		}
		else if(activity.getCurrentFocus() == null)
		{
			Log.e(Utility.TAG, "activity.getCurrentFocus() is null on hideKeyboard");
			return;
		}
		else if(activity.getCurrentFocus().getWindowToken() == null)
		{
			Log.e(Utility.TAG, "activity.getCurrentFocus().getWindowToken() is null on hideKeyboard");
			return;
		}
		
		InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	/**
	 * Returns the MINE content type o the file given as a string
	 * 
	 * @param fileUrl		The url or filename of the file
	 * @return				The MINE content type as a string
	 */
	public static String getMimeType(String fileUrl) 
	{
		int start = fileUrl.lastIndexOf(".");
		
		if(start == -1 || start == fileUrl.length()-1)
			return null;
		
	    String extension = fileUrl.substring(start, fileUrl.length());
	    Log.d(Utility.TAG, "File URI: " + fileUrl + " Extension: " + extension + " Start char: " + fileUrl.lastIndexOf(".") + " Length: " + fileUrl.length());
	    
	    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1,extension.length()));
	}
	
	/**
	 * This method starts the upload progress notification by showing it
	 * 
	 * @param context		The context
	 * @param title			The title of the progress bar
	 * @param content		The content of the progress bar
	 * @param maxProgress	The maximum value the progress bar holds
	 */
	public static void startUploadProgressNotification(Context context, String title, String content, int maxProgress)
	{
		Globals.notiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Globals.notiProgress = new NotificationCompat.Builder(context);
		Globals.notiProgress.setContentTitle(title);
		Globals.notiProgress.setContentText(content);
		Globals.notiProgress.setSmallIcon(R.drawable.ic_menu_refresh);
		Globals.notiProgress.setProgress(maxProgress, 0, false);
		Globals.notiProgress.build();
		
		Globals.notiManager.notify(Globals.UPLOAD_PROGRESS_NOTIFICATION, Globals.notiProgress.build());
	}
	
	/**
	 * This method updates the upload progress notification by changing its percentage value
	 * 
	 * @param context		The context
	 * @param title			The title of the progress bar
	 * @param content		Tje content of the progress bar
	 * @param progress		The progress of the progress bar
	 * @param maxProgress	The maximum value the progress bar holds
	 */
	public static void updateUploadProgressNotification(Context context, String title, String content, int progress, int maxProgress)
	{
		if(Globals.notiProgress == null)
		{
			Log.e(Utility.TAG, "Notification Progress is null at updateProgressNotification");
			return;
		}
		//Log.e(Utility.TAG, "Size: " + progress + " Max: " + maxProgress);

		Globals.notiProgress.setContentTitle(title);
		Globals.notiProgress.setContentText(content);
		Globals.notiProgress.setProgress(maxProgress, progress, false);
		
		Globals.notiManager.notify(Globals.UPLOAD_PROGRESS_NOTIFICATION, Globals.notiProgress.build());
	}
	
	/**
	 * This method finishs the upload progress notification by changing it's message to
	 * the content
	 * 
	 * @param context		The context
	 * @param title			The title of the progress bar
	 * @param content		The content of the progress bar
	 */
	public static void finishUploadProgressNotification(Context context, String title, String content)
	{
		if(Globals.notiProgress == null)
		{
			Log.e(Utility.TAG, "Notification Progress is null at finishProgressNotification");
			return;
		}
		Log.d(Utility.TAG, "Finishing notification");
		
		Globals.notiProgress.setContentTitle(title);
		Globals.notiProgress.setContentText(content);
		Globals.notiProgress.setProgress(0, 0, false);
		
		Globals.notiManager.notify(Globals.UPLOAD_PROGRESS_NOTIFICATION, Globals.notiProgress.build());
	}
}
