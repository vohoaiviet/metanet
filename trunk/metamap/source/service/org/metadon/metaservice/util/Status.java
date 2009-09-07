package org.metadon.metaservice.util;

public class Status
{
	
	public static final int ST_OK = 200;
	public static final int ST_ERR_INTERNAL = 500;
	public static final int ST_ERR_UNKNOWN_USER = 501;
	public static final int ST_ERR_UNKNOWN_PWD = 502;
	public static final int ST_ERR_UNKNOWN_DEVICE = 503;
	
	private int statusId;
	private String sessionId;
	
	
	public Status(String sessionId, int statusId)
   {
	   super();
	   this.sessionId = sessionId;
	   this.statusId = statusId;
   }
	public int getStatusId()
   {
   	return statusId;
   }
	public void setStatusId(int statusId)
   {
   	this.statusId = statusId;
   }
	public String getSessionId()
   {
   	return sessionId;
   }
	public void setSessionId(String sessionId)
   {
   	this.sessionId = sessionId;
   }
	
}
