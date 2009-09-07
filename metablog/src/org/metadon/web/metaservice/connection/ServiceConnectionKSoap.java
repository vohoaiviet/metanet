/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.metadon.web.metaservice.connection;

import javax.xml.rpc.Stub;

import org.metadon.beans.Payload;
import org.metadon.control.Controller;
import org.metadon.utils.ActivityAlert;
import org.metadon.utils.Base64Encoder;

import de.enough.polish.rmi.RemoteException;
import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
/**
 * The WServiceConnector class does the call to the remote web service
 * It creates the stub and fills the parameters
 * The call is done in a separate thread and the results
 * are delivered asynchronously via WServiceListener
 */
public class ServiceConnectionKSoap implements Runnable {

    Controller controller;
    private boolean isThreadRunning = false;
    private IServiceInvocationHandler listener;
    private String endPoint;
    private String comment;
    private String deviceID;
    
    private Long timestamp;
    private String message;
    private byte[] photo;
    private Double longitude;
    private Double latitude;
    private Double elevation;
    private String journeyName;
    
    public static int OPERATION;
    public static final int OPERATION_LOGON = 0;
    public static final int OPERATION_LOGOUT = 1;
    public static final int OPERATION_POST = 2;
    private static ActivityAlert activityScreen = new ActivityAlert(true);

    public ServiceConnectionKSoap(IServiceInvocationHandler listener, String endPoint) {
        if (listener == null || endPoint == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        this.listener = listener;
        this.endPoint = endPoint;
    }

    public void setController(Controller c) {
        this.controller = c;
    }

    private void cleanUp() {
        this.deviceID = null;

        this.timestamp = null;
        this.message = null;
        this.photo = null;
        this.longitude = null;
        this.latitude = null;
        this.elevation = null;
        this.journeyName = null;
    }

    /**
     * Start the WebService call in a separate thread.
     * It checks that only one call is active at the same time
     */
    public synchronized void logon() {
        if (!isThreadRunning) {
            isThreadRunning = true;
            this.deviceID = this.controller.getCredentials().getBtAddress();
            OPERATION = OPERATION_LOGON;
            //#debug
//#             System.out.println("start login connector thread...");
            new Thread(this).start();
        }
    }

    /**
     * Start the WebService call in a separate thread.
     * It checks that only one call is active at the same time
     */
    public synchronized void logout() {
        if (!isThreadRunning) {
            isThreadRunning = true;
            this.deviceID = this.controller.getCredentials().getBtAddress();
            OPERATION = OPERATION_LOGOUT;
            //#debug
//#             System.out.println("start logout connector thread...");
            new Thread(this).start();
        }
    }

    /**
     * Start the WebService call in a separate thread.
     * It checks that only one call is active at the same time
     */
    public synchronized void post(Payload payload) {
        if (!isThreadRunning) {
            isThreadRunning = true;
            this.timestamp = new Long(payload.getTimestamp());
            this.journeyName = payload.getJourneyName();
            this.message = payload.getMessage();
            this.longitude = new Double(payload.getLocation()[0]);
            this.latitude = new Double(payload.getLocation()[1]);
            this.elevation = new Double(payload.getElevation());
            this.photo = payload.getPhoto();
            this.comment = payload.getComment();
            OPERATION = OPERATION_POST;
            //#debug
//#             System.out.println("start post connector thread...");
            new Thread(this).start();
        }
    }

    /**
     * This is an IO operation and thus is executed in a
     * separate thread
     */
    public void run() {
        Gateway_Stub gatewayServiceStub = new Gateway_Stub();
        gatewayServiceStub._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, this.endPoint);
        gatewayServiceStub._setProperty(Stub.SESSION_MAINTAIN_PROPERTY, new Boolean(false));

        // credentials for user authentication on system (basic http authentication).
        // username and password are base64 encoded by the JAX-RPC runtime before being send.
        // since this an unreliable security policy an SSL tunnel is used additionally.
        gatewayServiceStub._setProperty(javax.xml.rpc.Stub.USERNAME_PROPERTY, this.controller.getCredentials().getUser());
        gatewayServiceStub._setProperty(javax.xml.rpc.Stub.PASSWORD_PROPERTY, this.controller.getCredentials().getKey());

        // perform remote invocation
        IGateway gatewayService = (IGateway) gatewayServiceStub;
        if (OPERATION == OPERATION_LOGON) {
            activityScreen.setString(Locale.get("gatewayServiceConnection.authenticate"));
            this.controller.show(activityScreen);
            try {
                // pass an unique reference to his/her registered mobile devices
                //#debug
//#                 System.out.println("invoke remote signon method...");
                Boolean signed = gatewayService.signon(this.deviceID);
                if (signed.booleanValue()) {
                    this.listener.onAuthenticationRequestComplete(Locale.get("gatewayServiceConnection.accountAvailable"));
                } else {
                    // this happens if the user tries to login from a unregistered device
                    this.listener.onAuthenticationRequestError(Locale.get("gatewayServiceConnection.noAccountAvailable"));
                }
            } catch (RemoteException re) {
                // http authentication failed
                // 401 unauthorized: invalid credentials
                // 403 forbitten: no permissions
                // 500 server error: invalid SOAP request or system specific error
                //#debug
//#             System.out.println("remote exception.: "+re.getMessage());
                listener.onAuthenticationRequestError(Locale.get("gatewayServiceConnection.serviceError"));
            //#debug
//#             System.out.println(re.getMessage());
            } catch (SecurityException se) {
                this.controller.show(this.controller.getScreen(this.controller.LOGIN_SCR));
            } catch (Exception e) {
                //#debug
//#             System.out.println("IOex.: "+e.getMessage());
                // another IO problem e.g. Symbian Native Error-7372:
                // an SSL tunnel can not be established - server is maybe down
                listener.onAuthenticationRequestError(Locale.get("tmblogServer.serviceNotAvailable"));
            }
        } else if (OPERATION == OPERATION_LOGOUT) {
            activityScreen.setString(Locale.get("gatewayServiceConnection.logout"));
            this.controller.show(activityScreen);
            try {
                // logout silently
                //#debug
//#                 System.out.println("invoke remote logout method...");
                gatewayService.logout(this.deviceID);
                this.listener.onAuthenticationRequestComplete("logout ok.");
            } catch (Exception e) {
                //#debug
//#             System.out.println("ex.: "+e.getMessage());
                listener.onAuthenticationRequestError("logout failed.");
            }
        } else if (OPERATION == OPERATION_POST) {
            // get activity screen from monitor
            activityScreen = this.controller.getMonitor().getActivityScreen();
            activityScreen.setString(Locale.get("gatewayServiceConnection.posting") + " (" + this.comment + ") ...");

            // photo data must be encoded as a base64 string in order to send it within
            // an xml-based protocol - the photo payload is increasing by a factor of 1.33
            //#debug
//#         System.out.println("start encoder...");
            String encodedPhoto = null;
            if (this.photo != null) {
                encodedPhoto = Base64Encoder.encode(this.photo);
            }
            try {
                //#debug
//#                 System.out.println("invoke remote post method...");
                Boolean posted = gatewayService.post(this.timestamp, this.journeyName, this.message, this.longitude, this.latitude, this.elevation, encodedPhoto);
                if(posted.booleanValue()) {
                    activityScreen.setString("...");
                    activityScreen.setString(Locale.get("gatewayServiceConnection.updating"));
                    this.listener.onPostRequestComplete(Locale.get("gatewayServiceConnection.posted"));
                } else {
                    this.listener.onPostRequestError(Locale.get("gatewayServiceConnection.notAllowed"));
                }
            } catch (RemoteException re) {
                this.listener.onPostRequestError(Locale.get("gatewayServiceConnection.postFailed"));
                //#debug
//#             System.out.println(re.getMessage());
            } catch (Exception e) {
                // large photo test: error -36 = disconnected
                //#debug
//#                 System.out.println("IOex.: "+e.getMessage());
                // another IO problem e.g. Symbian Native Error-7372:
                // an SSL tunnel can not be established - server is maybe down
                listener.onPostRequestError(Locale.get("tmblogServer.serviceNotAvailable"));
            }
        }
        this.cleanUp();
    }
}
    
    