package net.livecourse.android;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the ultities class that focuses on ultity based methods as well as
 * methods that focus around the REST API calls.  All REST API calls should go here.
 * 
 * @author Darren
 *
 */
public class Utility {

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
		if(password.length() >= 8 && password.length() <= 20)
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
	
	
	
}
