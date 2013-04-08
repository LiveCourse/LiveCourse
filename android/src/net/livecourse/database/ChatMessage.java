package net.livecourse.database;

/**
 * This is the object that holds the information for ChatMessages.
 * 
 * @author Darren Cheng
 */
public class ChatMessage 
{	
	private String chatId;
	private String sendTime;
	private String messageString;
	private String email;
	private String displayName;
	
	public String toString()
	{
		return "Chat ID: "+ chatId +" Send Time: " + sendTime +	" MessageString: " + messageString + " email: " + email + " Display Name: " + displayName;
	}
	public String getChatId() 
	{
		return chatId;
	}
	public void setChatId(String chatId) 
	{
		this.chatId = chatId;
	}
	public String getSendTime() 
	{
		return sendTime;
	}
	public void setSendTime(String sendTime) 
	{
		this.sendTime = sendTime;
	}
	public String getMessageString() 
	{
		return messageString;
	}
	public void setMessageString(String messageString) 
	{
		this.messageString = messageString;
	}
	public String getEmail() 
	{
		return email;
	}
	public void setEmail(String email) 
	{
		this.email = email;
	}
	public String getDisplayName() 
	{
		return displayName;
	}
	public void setDisplayName(String displayName) 
	{
		this.displayName = displayName;
	}
}
