package org.metadon.metaservice;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.axis2.context.MessageContext;
import org.metadon.metaservice.util.Status;

/**
 * 
 * @author Hannes Weingartner
 *
 */
public class MetaService
{

//	private DBClient	dbManager;

	public MetaService()
	{
//		this.dbManager = DBClient.getInstance();
	}

//	private Logger	log	= Logger.getLogger(MetaService.class);

	/********************************************************************/

	// sign-on mobile user
	public Status login(String deviceID,
	                    String user,
	                    String password)
	{
//		log.info("Login received.");

		// get SOAP message context
		MessageContext msgCxt = MessageContext.getCurrentMessageContext();
//		log.debug("SOAP envelope: " + msgCxt.getEnvelope().toString());
//		log.debug("DeviceId: " + deviceID);
//		log.debug("Username: " + user);
//		log.debug("Password: " + password);

		// TODO authenticate user
		//		boolean isValid = this.dbManager.isValidUser(user, deviceID);
		//		if(isValid) {
		//			this.dbManager.trackUser(username);
		//		}
		// TODO generate session id
		Status status = new Status("0134567", Status.ST_OK);

		return status;
	}

	/********************************************************************/

	// logout mobile user
	public boolean logout(String sessionId)
	{
//		log.info("Logout received.");

		// get SOAP message context
		MessageContext msgCxt = MessageContext.getCurrentMessageContext();
//		log.debug("SOAP envelope: " + msgCxt.getEnvelope().toString());
//		log.debug("SessionId: " + sessionId);
		
		// TODO check session id

		// TODO logout device
		//		// get username
		//		HttpServletRequest request = (HttpServletRequest) msgCxt.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
		//		String username = request.getRemoteUser();
		//		this.dbManager.releaseUser(username);

		return true;
	}

	/********************************************************************/

	// post blog
	public boolean publish(String sessionId,
	                       String indicatorId,
	                       Long tsCreation,
	                       String traceName,
	                       String description,
	                       String csvKeys,
	                       Double longitude,
	                       Double latitude,
	                       Double altitude,
	                       String encodedImage) throws RemoteException
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		
		// get SOAP message context
		MessageContext msgCxt = MessageContext.getCurrentMessageContext();
//		log.debug("SOAP envelope: " + msgCxt.getEnvelope().toString());
//		
//		log.debug("SessionId: " + sessionId);
//		log.debug("Creation date: " + sdf.format(new Date(tsCreation)));
//		log.debug("Trace name: " + traceName);
//		log.debug("Description: " + description);
//		log.debug("CSV keys: " + csvKeys);
//		log.debug("Longitude: " + longitude);
//		log.debug("Latitude: " + latitude);
//		log.debug("Altitude: " + altitude);
//		log.debug("Contains image: : " + (encodedImage != null?"yes":"no"));
		
		// TODO check sessionId
		

		// TODO
//		// create new blog
//		Blog blog = new Blog();
//		blog.setTimestamp(timestamp);
//		blog.setUserName(username);
//		blog.setJourneyName(journeyName);
//		blog.setMessage(message);
//		blog.setLongitude(longitude);
//		blog.setLatitude(latitude);
//		blog.setElevation(elevation);
//
//		// decode photo
//		byte[] photo = null;
//		if (encodedPhoto != null)
//		{
//			System.out.println("encoded photo is valid. start decoding...");
//			photo = Base64Decoder.decode(encodedPhoto);
//			blog.setPhotoLength(photo.length);
//			blog.setPhoto(photo);
//		}
//		else
//		{
//			blog.setPhotoLength(0);
//		}
//		try
//		{
//			if (blog.isValid())
//			{
//				System.out.println("storing blog...");
//				this.dbManager.storeBlog(blog);
//			}
//			else
//			{
//				throw new RemoteException();
//			}
//		}
//		catch (SQLException se)
//		{
//			System.out.println("sql problem: " + se.getMessage());
//			throw new RemoteException();
//		}
//		catch (IOException ioe)
//		{
//			System.out.println("problem storing photo: " + ioe.getMessage());
//			throw new RemoteException();
//		}
//		System.out.println("blog stored.");
		
		return true;
	}

	/********************************************************************/
}
