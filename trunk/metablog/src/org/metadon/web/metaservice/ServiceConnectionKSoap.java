package org.metadon.web.metaservice;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Timer;
import java.util.TimerTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransport;
import org.metadon.beans.Credentials;
import org.metadon.beans.Indicator;
import org.metadon.control.Controller;
import org.metadon.utils.ActivityAlert;
import org.metadon.utils.Base64Encoder;
import org.metadon.utils.ServiceConnectionNotAvailableException;

import de.enough.polish.rmi.RemoteException;
import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes Weingartner
 */
/**
 * The WServiceConnector class does the call to the remote web service
 * It creates the stub and fills the parameters
 * The call is done in a separate thread and the results
 * are delivered asynchronously via WServiceListener
 */
public class ServiceConnectionKSoap implements Runnable
{

	private boolean	                isThreadRunning;
	private Credentials	             credentials;
	private IServiceInvocationHandler	handler;
	private String	                   endPoint;

	private SoapSerializationEnvelope	envelope;
	private HttpTransport	          httpTransport;
	private String	                   serviceNamespace;

	private Indicator	                indicator;

	private String	                   comment;

	public static int	                OPERATION;
	public static final int	          OPERATION_LOGIN	     = 0;
	public static final int	          OPERATION_LOGOUT	     = 1;
	public static final int	          OPERATION_PUBLISH	  = 2;
	private static ActivityAlert	    activityScreen	     = new ActivityAlert(true);

	// TODO different timeouts for different connection types
	private final int	                HTTP_TIMEOUT	        = 10;	                    // seconds
	private Timer	                   timeoutTimer;

	public static final int	          ST_OK	              = 200;
	public static final int	          ST_ERR_INTERNAL	     = 500;
	public static final int	          ST_ERR_UNKNOWN_USER	  = 501;
	public static final int	          ST_ERR_UNKNOWN_PWD	  = 502;
	public static final int	          ST_ERR_UNKNOWN_DEVICE	= 503;
	
	public static String 				 sessionId;

	/**
	 * 
	 * @param handler
	 * @param endPoint
	 */
	public ServiceConnectionKSoap(IServiceInvocationHandler _handler, String _endPoint,
	                              Credentials _credentials)
	{
		if (_handler == null || _endPoint == null)
			throw new IllegalArgumentException("ServiceInvocationHandler is not defined.");
		if (endPoint == null)
			throw new IllegalArgumentException("End Point is not defined.");

		handler = _handler;
		endPoint = _endPoint;
		credentials = _credentials;

		httpTransport = new HttpTransport(endPoint + "?wsdl");
		httpTransport.debug = true;
		httpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	}

	/**
	 * Creates and returns the KSoap request object.
	 * 
	 * @param remoteMethod the remote method to invoke.
	 * @return the SoapObject
	 */
	private SoapObject getRequestObject(String _remoteMethod)
	{
		// create soap envelope with version 1.1
		envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.implicitTypes = true;
		envelope.dotNet = false;

		// Create request object:
		// A simple dynamic object that can be used to build soap calls without implementing 
		// KvmSerializable. Essentially, this is what goes inside the body of a soap envelope -
		// it is the direct subelement of the body and all further subelements. Instead of this
		// this class, custom classes can be used if they implement the KvmSerializable interface. 
		return new SoapObject(serviceNamespace, _remoteMethod);
	}

	/**
	 * Start the WebService call in a separate thread.
	 * It checks that only one call is active at the same time
	 * @throws ServiceConnectionNotAvailableException 
	 */
	public void login() throws ServiceConnectionNotAvailableException
	{
		if (!isThreadRunning)
		{
			OPERATION = OPERATION_LOGIN;
			new Thread(this).start();
			isThreadRunning = true;
		}
		else
			throw new ServiceConnectionNotAvailableException("Connection thread is busy.");
	}

	/**
	 * Start the WebService call in a separate thread.
	 * It checks that only one call is active at the same time
	 * @throws ServiceConnectionNotAvailableException 
	 */
	public void logout() throws ServiceConnectionNotAvailableException
	{
		if (!isThreadRunning)
		{
			OPERATION = OPERATION_LOGOUT;
			new Thread(this).start();
			isThreadRunning = true;
		}
		else
			throw new ServiceConnectionNotAvailableException("Connection thread is busy.");
	}

	/**
	 * Start the WebService call in a separate thread.
	 * It checks that only one call is active at the same time
	 */
	public void publish(Indicator _indicator)
	{
		if (!isThreadRunning)
		{
			OPERATION = OPERATION_PUBLISH;
			indicator = _indicator;
			new Thread(this).start();
			isThreadRunning = true;
		}
	}

	/**
	 * This is an IO operation and thus is executed in a
	 * separate thread
	 */
	public void run()
	{

		// TODO messages
		if (OPERATION == OPERATION_LOGIN)
		{
			// show activity alert
			activityScreen.setString(Locale.get("gatewayServiceConnection.authenticate"));
			Controller.show(activityScreen);

			System.out.println("ServiceConnectionKSoap: remote authentication...");

			// create request
			SoapObject request = this.getRequestObject("login");
			request.addProperty("deviceId", credentials.getBtAddress());
			request.addProperty("username", credentials.getUser());
			request.addProperty("password", credentials.getKey());

			// set mappings for complex types - used for XML to Java mapping and vice versa.
			new Status().register(this.envelope);

			envelope.bodyOut = request;
			timeoutTimer = new Timer();
			timeoutTimer.schedule(getInstanceOfTimoutTask(), HTTP_TIMEOUT * 1000);

			try
			{
				System.out.println("Authenticate at server...");

				// executes a SOAP method and returns the response
				this.httpTransport.call(SoapEnvelope.ENV, this.envelope);

				// this SOAP object represents the entire response body - the response envelope is parsed by kSOAP libs.
				Status loginStatus = (Status) this.envelope.bodyIn;

				System.out.println("Received status: " + loginStatus.getStatusId());
				if (loginStatus.getStatusId() == ServiceConnectionKSoap.ST_OK)
				{
					sessionId = loginStatus.getSessionId();
					handler.onAuthenticationSuccess(Locale.get("gatewayServiceConnection.accountAvailable"));
				}
				else
				{
					// TODO fine grain errors
					handler.onAuthenticationError(Locale.get("gatewayServiceConnection.noAccountAvailable"));
				}
			}
			catch (SecurityException se)
			{
				// user prohibits network connection
				handler.onAuthenticationCancel();
			}
			catch (InterruptedIOException e)
			{
				System.out.println("ServiceConnectionKSoap: Connection refused: timeout.");
				this.handler.onAuthenticationError(Locale.get("server.validationService.notAvailable"));

			}
			catch (IOException e)
			{
				System.out.println("ServiceConnectionKSoap: I/O Error. Cause: ");
				e.printStackTrace();
				this.handler.onAuthenticationError(Locale.get("server.validationService.notAvailable"));

			}
			catch (Exception e)
			{
				System.out.println("ServiceConnectionKSoap: Unexpected Error. Cause: ");
				e.printStackTrace();
				this.handler.onAuthenticationError(Locale.get("server.validationService.notAvailable"));

			}
			finally
			{
				this.cleanUp();
			}
		}
		else if (OPERATION == OPERATION_LOGOUT)
		{
			// show activity alert
			activityScreen.setString(Locale.get("gatewayServiceConnection.logout"));
			Controller.show(activityScreen);
			
			System.out.println("ServiceConnectionKSoap: logout...");

			// create request
			SoapObject request = this.getRequestObject("logout");
			request.addProperty("sessionId", sessionId);
			
			envelope.bodyOut = request;
			timeoutTimer = new Timer();
			timeoutTimer.schedule(getInstanceOfTimoutTask(), HTTP_TIMEOUT * 1000);
			
			try
			{
				
				System.out.println("Logout at server...");

				// executes a SOAP method and returns the response
				this.httpTransport.call(SoapEnvelope.ENV, this.envelope);

				// this SOAP object represents the entire response body - the response envelope is parsed by kSOAP libs.
				Boolean logoutSuccess = (Boolean) this.envelope.bodyIn;

				System.out.println("Logged out: " + logoutSuccess);
				handler.onLogoutSuccess("logout ok.");
			}
			catch (InterruptedIOException e)
			{
				System.out.println("ServiceConnectionKSoap: Connection refused: timeout.");
				this.handler.onLogoutError(Locale.get("server.validationService.notAvailable"));

			}
			catch (IOException e)
			{
				System.out.println("ServiceConnectionKSoap: I/O Error. Cause: ");
				e.printStackTrace();
				this.handler.onLogoutError(Locale.get("server.validationService.notAvailable"));

			}
			catch (Exception e)
			{
				System.out.println("ServiceConnectionKSoap: Unexpected Error. Cause: ");
				e.printStackTrace();
				this.handler.onLogoutError(Locale.get("server.validationService.notAvailable"));

			}
			finally
			{
				this.cleanUp();
			}
		}
		
		
		
		
		
		
		// TODO only transmit non binary indicator parameters with webservice - binary content als bytestream over simple http connection
		// step 1: photo
		// step 2: params
		else if (OPERATION == OPERATION_PUBLISH)
		{
			// get activity screen from monitor
			activityScreen = this.controller.getMonitor().getActivityScreen();
			activityScreen.setString(Locale.get("gatewayServiceConnection.posting") + " (" + this.comment
			                         + ") ...");

			// send photo binary over http with indicator id
			
			
			// photo data must be encoded as a base64 string in order to send it within
			// an xml-based protocol - the photo payload is increasing by a factor of 1.33
			
			
			String encodedPhoto = null;
			if (indicator.getPhoto() != null)
			{
				encodedPhoto = Base64Encoder.encode(indicator.getPhoto());
			}
			try
			{
				//#debug
				//#                 System.out.println("invoke remote post method...");
				Boolean posted = gatewayService.post(this.timestamp, this.journeyName, this.message, this.longitude, this.latitude, this.elevation, encodedPhoto);
				if (posted.booleanValue())
				{
					activityScreen.setString("...");
					activityScreen.setString(Locale.get("gatewayServiceConnection.updating"));
					this.listener.onPublishRequestComplete(Locale.get("gatewayServiceConnection.posted"));
				}
				else
				{
					this.listener.onPublishRequestError(Locale.get("gatewayServiceConnection.notAllowed"));
				}
			}
			catch (RemoteException re)
			{
				this.listener.onPublishRequestError(Locale.get("gatewayServiceConnection.postFailed"));
				//#debug
				//#             System.out.println(re.getMessage());
			}
			catch (Exception e)
			{
				// large photo test: error -36 = disconnected
				//#debug
				//#                 System.out.println("IOex.: "+e.getMessage());
				// another IO problem e.g. Symbian Native Error-7372:
				// an SSL tunnel can not be established - server is maybe down
				listener.onPublishRequestError(Locale.get("tmblogServer.serviceNotAvailable"));
			}
		}
		this.cleanUp();
	}

	/**
	 * Returns a timer task used for resetting the HTTP connection.
	 * 
	 * @return the task.
	 */
	private TimerTask getInstanceOfTimoutTask()
	{
		return new TimerTask() {
			public void run()
			{
				httpTransport.reset();
			}
		};
	}

	private void cleanUp()
	{
		indicator = null;
	}

}
