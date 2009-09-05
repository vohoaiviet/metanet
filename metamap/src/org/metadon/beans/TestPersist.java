package org.metadon.beans;

import java.util.Date;

import org.apache.commons.logging.impl.Log4JLogger;

public class TestPersist
{
	
	private long id;
	private String title;
	private Date timestamp;
	
	
	public long getId()
   {
   	return id;
   }
	public void setId(long id)
   {
   	this.id = id;
   }
	public String getTitle()
   {
   	return title;
   }
	public void setTitle(String title)
   {
   	this.title = title;
   }
	public Date getTimestamp()
   {
   	return timestamp;
   }
	public void setTimestamp(Date timestamp)
   {
   	this.timestamp = timestamp;
   }
	
	
	

	
	
}
