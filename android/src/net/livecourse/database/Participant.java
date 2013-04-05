package net.livecourse.database;

/**
 * This is the object that holds the information for Participants.
 * 
 * @author Jeremy Meyer
 */
public class Participant 
{	
	private String chatId;
	private String displayName;
	private String email;
	private String time_lastfocus;
	private String time_lastrequest;
	
	public String toString()
	{
		return "Chat ID: "+ chatId + " Display Name: " + displayName + " Email: " + email + "Last Focus" + time_lastfocus + "Last Request" + time_lastrequest;
	}
	public String getChatId() 
	{
		return chatId;
	}
	public void setChatId(String chatId) 
	{
		this.chatId = chatId;
	}
	public String getDisplayName() 
	{
		return displayName;
	}
	public void setDisplayName(String displayName) 
	{
		this.displayName = displayName;
	}
	public String getEmail() 
	{
		return email;
	}
	public void setEmail(String email) 
	{
		this.email = email;
	}
	public String getTime_lastfocus() {
		return time_lastfocus;
	}
	public void setTime_lastfocus(String time_lastfocus) {
		this.time_lastfocus = time_lastfocus;
	}
	public String getTime_lastrequest() {
		return time_lastrequest;
	}
	public void setTime_lastrequest(String time_lastrequest) {
		this.time_lastrequest = time_lastrequest;
	}
}
