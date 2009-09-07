/*
 * Blog.java
 *
 * Created on 05. November 2007, 12:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.metadon.beans;

import org.metadon.location.GPSLocation;

/**
 *
 * @author Hannes
 */
public abstract class Blog
{

	private long	     timestamp	        = -1;
	private long	     timestampPosted	  = -1;
	private int	        number	           = 0;
	private int	        sizeBytes	        = -1;
	private String	     journeyName	     = "n/a";
	private Message	  message	        = null;
	private GPSLocation	location	        = null;
	public boolean	     containsSizeBytes	= false;
	public boolean	     containsMessage	  = false;
	public boolean	     containsLocation	= false;
	public boolean	     isJourneyBlog	  = false;

	/**
	 * Creates a new instance of Blog
	 */
	public Blog()
	{
		super();
	}

	// set date
	public void setTimestamp(long ts)
	{
		this.timestamp = ts;
	}

	// get timestamp
	public long getTimestamp()
	{
		return this.timestamp;
	}

	public void setTimestampPost(long ts)
	{
		this.timestampPosted = ts;
	}

	// get timestamp
	public long getTimestampPost()
	{
		return this.timestampPosted;
	}

	// set blog number
	public void setNumber(int number)
	{
		this.number = number;
		this.isJourneyBlog = true;
	}

	// get blog number
	public int getNumber()
	{
		return this.number;
	}

	// set blog size in bytes
	public void setSizeBytes(int size)
	{
		this.sizeBytes = size;
		this.containsSizeBytes = true;
	}

	// get blog size in bytes
	public int getSizeBytes()
	{
		return this.sizeBytes;
	}

	// set journey name
	public void setJourneyName(String journeyName)
	{
		this.journeyName = journeyName;
	}

	// get message
	public String getJourneyName()
	{
		return this.journeyName;
	}

	// set message
	public void setMessage(Message message)
	{
		if (message == null)
		{
			return;
		}
		this.message = message;
		this.containsMessage = true;
	}

	// get message
	public Message getMessage()
	{
		return this.message;
	}

	// set location
	public void setLocation(GPSLocation location)
	{
		if (location == null)
		{
			return;
		}
		this.location = location;
		this.containsLocation = true;
	}

	public GPSLocation getLocation()
	{
		return this.location;
	}

	public boolean containsSizeBytes()
	{
		return this.containsSizeBytes;
	}

	public boolean containsMessage()
	{
		return this.containsMessage;
	}

	public boolean containsLocation()
	{
		return this.containsLocation;
	}

	public boolean isJourneyBlog()
	{
		return this.isJourneyBlog;
	}

	public boolean isPosted()
	{
		if (this.timestampPosted != -1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
