package org.metadon.web.metaservice;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class Status implements KvmSerializable
{

	protected int statusId;
	protected String sessionId;
	
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

	/***************************************************************/

	// implementations
	/* (non-Javadoc)
	* @see org.ksoap2.serialization.KvmSerializable#setProperty(int, java.lang.Object)
	*/
	public void setProperty(int index, Object obj)
	{
		switch (index)
		{
			case 0:
				this.setStatusId((Integer) obj);
				break;
			case 1:
				this.setSessionId((String) obj);
				break;
			default:
				throw new IllegalArgumentException("invalid index.");
		}
	}

	/* (non-Javadoc)
	* @see org.ksoap2.serialization.KvmSerializable#getProperty(int)
	*/
	public Object getProperty(int index)
	{
		switch (index)
		{
			case 0:
				return this.getStatusId();
			case 1:
				return this.getSessionId();
			default:
				throw new IllegalArgumentException("invalid index.");
		}
	}

	/* (non-Javadoc)
	 * @see org.ksoap2.serialization.KvmSerializable#getPropertyCount()
	 */
	public int getPropertyCount()
	{
		return 2;
	}

	/* (non-Javadoc)
	 * @see org.ksoap2.serialization.KvmSerializable#getPropertyInfo(int, java.util.Hashtable, org.ksoap2.serialization.PropertyInfo)
	 */
	public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info)
	{
		switch (index)
		{
			case 0:
				info.name = "statusId";
				info.type = PropertyInfo.INTEGER_CLASS;
				break;
			case 1:
				info.name = "sessionId";
				info.type = PropertyInfo.STRING_CLASS;
				break;
			default:
				throw new IllegalArgumentException("invalid index.");
		}
	}

	public void register(SoapSerializationEnvelope envelope)
	{
		envelope.addMapping("", "status", this.getClass());
	}

}