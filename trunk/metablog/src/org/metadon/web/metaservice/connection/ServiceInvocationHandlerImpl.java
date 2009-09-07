/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.metadon.web.metaservice.connection;


import javax.microedition.lcdui.Displayable;

import org.metadon.beans.Payload;
import org.metadon.beans.Settings;
import org.metadon.control.BlogBrowserLocal;
import org.metadon.control.Controller;

import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class ServiceInvocationHandlerImpl implements IServiceInvocationHandler {

    private Controller controller;
    private static final String TRANSFER_BASIC = "http://";
    private static final String TRANSFER_SECURE = "https://";
    private static final int PORT_BASIC = 80;
    private static final int PORT_SECURE = 443;

    public ServiceInvocationHandlerImpl(Controller c) {
        this.controller = c;
    }

    public void logon() {
        //#debug
//#         System.out.println("invoke service connector logon...");
        try {
            this.getServiceConnection().logon();
        } catch (IllegalArgumentException iae) {
            //#debug error
            System.out.println("ill. arg.: " + iae);
        }
    }

    public void logout() {
        //#debug
//#         System.out.println("invoke service connector logout...");
        try {
            this.getServiceConnection().logout();
        } catch (IllegalArgumentException iae) {
            //#debug error
            System.out.println("ill. arg.: " + iae);
        }
    }

    public void post(Payload payload) {
        //#debug
//#         System.out.println("invoke service connector post...");
        try {
            this.getServiceConnection().post(payload);
        } catch (IllegalArgumentException iae) {
            //#debug error
            System.out.println("ill. arg.: " + iae);
        }
    }

    /**
     * Called when a valid result is retrieved from the
     * WebService
     */
    public void onAuthenticationRequestComplete(String info) {
        //#debug
//#         System.out.println("request ok: " + info);
        if (ServiceConnectionKSoap.OPERATION == ServiceConnectionKSoap.OPERATION_LOGON) {
            this.controller.setLoggedIn(true);
        } else if (ServiceConnectionKSoap.OPERATION == ServiceConnectionKSoap.OPERATION_LOGOUT) {
            this.controller.exit();
        }
    }

    /**
     * Called when an error happens during the WebService call
     */
    public void onAuthenticationRequestError(String error) {
        //#debug
//#         System.out.println("request failed: " + error);
        if (ServiceConnectionKSoap.OPERATION == ServiceConnectionKSoap.OPERATION_LOGON) {
            Displayable nextScreen = this.controller.getScreen(this.controller.LOGIN_SCR);
            this.controller.show(this.controller.ERROR_ASCR, error, null, nextScreen);
            this.controller.setLoggedIn(false);
        } else if (ServiceConnectionKSoap.OPERATION == ServiceConnectionKSoap.OPERATION_LOGOUT) {
            this.controller.exit();
        }
    }

    /**
     * Called when a valid result is retrieved from the
     * WebService
     */
    public void onPostRequestComplete(String info) {
        //#debug
//#         System.out.println(info);
        try {
            this.controller.setPosted(true);
        } catch (Exception e) {
            Displayable nextScreen = BlogBrowserLocal.getInstance().getBrowserScreen();
            this.controller.show(this.controller.ERROR_ASCR, Locale.get("gatewayService.updateFailed"), null, nextScreen);
            //#debug
//#             System.out.println("error while updating.: " + e.getMessage());
        }
    }

    /**
     * Called when an error happens during the WebService call
     */
    public void onPostRequestError(String error) {
        Displayable nextScreen = BlogBrowserLocal.getInstance().getBrowserScreen();
        this.controller.show(this.controller.ERROR_ASCR, error, null, nextScreen);
        this.controller.setPosted(false);
    }

    private ServiceConnectionKSoap getServiceConnection() throws IllegalArgumentException {
        ServiceConnectionKSoap serviceConnection = null;
        String endpoint = null;
        Settings settings = this.controller.getSettings();

        if (settings.getForceSecureChannel()) {
            endpoint = TRANSFER_SECURE +
                    Locale.get("tmblogServer.location") + ":" +
                    PORT_SECURE +
                    Locale.get("gatewayService.address");
        } else {
            endpoint = TRANSFER_BASIC +
                    Locale.get("tmblogServer.location") + ":" +
                    PORT_BASIC +
                    Locale.get("gatewayService.address");
        }
        if (endpoint != null) {
            serviceConnection = new ServiceConnectionKSoap(this, endpoint);
            serviceConnection.setController(this.controller);
            //#debug
//#             System.out.println("endpoint: " + endpoint);
        } else {
            throw new IllegalArgumentException("no service endpoint defined.");
        }
        return serviceConnection;
    }
}
