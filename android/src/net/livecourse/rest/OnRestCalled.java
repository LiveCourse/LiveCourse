package net.livecourse.rest;

/**
 * This is the interface used to interface the Restful class and other classes that call the
 * Restful class.
 * 
 * @author Darren
 */
public interface OnRestCalled 
{
	/**
	 * This method runs in the background thread and will only be called if the Rest call returned 
	 * a success.  This method is invoked after the rest of doInBackground is completed.  It 
	 * handles the HttpResponse that gets returned back.  If the HttpResponse is null (failed 
	 * execution), this method will not be called and instead onRestPostExecutionFailed will 
	 * be called instead.
	 * 
	 * @param response 	The response from the server is string format
	 */
	public void onRestHandleResponseSuccess(String restCall, String response);
	
	/**
	 * This method runs in the foreground thread after the rest execution is finished and the execution of the
	 * rest call is successful.
	 * 
	 * @param restCall 	The path of the call that the rest used, ex. "auth/verify"
	 * @param result 	The result of the call
	 */
	public void onRestPostExecutionSuccess(String restCall, String result);
	
	/**
	 * This method runs in the foreground thread after the Rest execution is finished and the execution of the Rest
	 * call failed.
	 * 
	 * @param restCall 	The path of the call that the rest used, ex. "auth/verify"
	 * @param code		The status code returned by the HttpResponse
	 * @param result	The result of the call
	 */
	public void onRestPostExecutionFailed(String restCall, int code, String result);
}
