package org.metadon.metaservice;

import java.rmi.RemoteException;
import java.sql.SQLException;
import org.apache.axis2.transport.http.HTTPConstants;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import org.apache.axis2.context.MessageContext;
import org.metadon.metaservice.util.Base64Decoder;


public class Gateway {

	private DBClient dbManager;

	public Gateway() {
		this.dbManager = DBClient.getInstance();
	}
	
	/********************************************************************/

	// sign-on mobile user
	public boolean signon(String deviceID) {
		// get SOAP message context
		MessageContext msgCxt = MessageContext.getCurrentMessageContext();
		System.out.println("SOAP envelope: "+msgCxt.getEnvelope().toString());
		
		// get username
		HttpServletRequest request = (HttpServletRequest)msgCxt.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
		String username = request.getRemoteUser();
		System.out.println("username from request: "+username);
		
		boolean isValid = this.dbManager.isValidUser(username, deviceID);
		if(isValid) {
			this.dbManager.trackUser(username);
		}
		return isValid;
	}
	
	/********************************************************************/
	
	// logout mobile user
	public boolean logout(String deviceID) {
		// get SOAP message context
		MessageContext msgCxt = MessageContext.getCurrentMessageContext();
		System.out.println("SOAP envelope: "+msgCxt.getEnvelope().toString());
		
		// get username
		HttpServletRequest request = (HttpServletRequest)msgCxt.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
		String username = request.getRemoteUser();
		
		this.dbManager.releaseUser(username);
		return true;
	}
	
	/********************************************************************/

	// post blog
	public boolean post (
			long timestamp,
			String journeyName,
			String message,
			double longitude,
			double latitude,
			double elevation,
			String encodedPhoto) throws RemoteException {
		
		// get SOAP message context
		MessageContext msgCxt = MessageContext.getCurrentMessageContext();
		System.out.println("SOAP envelope post: "+msgCxt.getEnvelope().toString());
		
		// get username
		HttpServletRequest request = (HttpServletRequest)msgCxt.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
		String username = request.getRemoteUser();
		
		// check if user is valid
		if(!this.dbManager.isTrackedUser(username)) {
			System.out.println("user: "+username+" is invalid.");
			return false;
		}
		
		// create new blog
		Blog blog = new Blog();
		blog.setTimestamp(timestamp);
		blog.setUserName(username);
		blog.setJourneyName(journeyName);
		blog.setMessage(message);
		blog.setLongitude(longitude);
		blog.setLatitude(latitude);
		blog.setElevation(elevation);
		
		// decode photo
		byte[] photo = null;
		if(encodedPhoto != null) {
			System.out.println("encoded photo is valid. start decoding...");
			photo = Base64Decoder.decode(encodedPhoto);
			blog.setPhotoLength(photo.length);
			blog.setPhoto(photo);
		} else {
			blog.setPhotoLength(0);
		}
		try {
			if(blog.isValid()) {
				System.out.println("storing blog...");
				this.dbManager.storeBlog(blog);
			} else {
				throw new RemoteException();
			}
		} catch (SQLException se) {
			System.out.println("sql problem: "+se.getMessage());
			throw new RemoteException();
		} catch (IOException ioe) {
			System.out.println("problem storing photo: "+ioe.getMessage());
			throw new RemoteException();
		}
		System.out.println("blog stored.");
		return true;
	}
	
	/********************************************************************/
}
