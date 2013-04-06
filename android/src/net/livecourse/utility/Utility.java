package net.livecourse.utility;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;

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
}
