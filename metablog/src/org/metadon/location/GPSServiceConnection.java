/*
 * GPSDevice.java
 *
 * Created on 07. Dezember 2007, 13:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.location;


import java.io.IOException;
import java.io.InputStreamReader;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import de.enough.polish.util.Locale;
import javax.microedition.lcdui.Displayable;

import org.metadon.bluetooth.BluetoothDevice;
import org.metadon.control.Controller;

import java.util.TimerTask;
import java.util.Timer;

/**
 *
 * @author Hannes
 */
public class GPSServiceConnection extends BluetoothDevice implements Runnable {
    
    private Controller controller;
    private LocationManager locationManager;
    private NMEASentenceParser nmeaSentenceParser;
    private StreamConnection connection;
    private String serviceConnectionURL;
    private InputStreamReader reader;
    private GPSLocation gpsLocation;
    private Thread locationListenerThread;
    private int status;
    private boolean reconnectOnTermination;
    private final int maxReconnectionAttemps = 1;
    private Timer timeoutNotifier;
    
    private static final int CONNECTION_TIMEOUT = 3000;
    private static final int RECONNECTION_TIMEOUT = 1000;
    private static final int LINE_DELIMITER = 13;
    
    public final static int STATUS_CONNECTED = 0;
    public final static int STATUS_DISCONNECTING = 1;
    public final static int STATUS_DISCONNECTED = 2;
    
    /** Creates a new instance of GPSDevice */
    public GPSServiceConnection(String address, String alias) {
        super(address, alias);
        this.nmeaSentenceParser = NMEASentenceParser.getInstance();
        this.status = STATUS_DISCONNECTED;
    }
    
    public void setServiceConnectionURL(String url) {
        this.serviceConnectionURL = url;
    }
    
    public String getServiceConnectionURL() {
        return this.serviceConnectionURL;
    }
    
    public void setController(Controller c) {
        this.controller = c;
    }
    
    public void setLocationManager(LocationManager lm) {
        this.locationManager = lm;
    }
    
    // connect to bluetooth device and start location listener thread
    public synchronized void connect() throws IOException {
        this.reconnectOnTermination = true;
        if(this.status == this.STATUS_DISCONNECTED) {
            ////#debug
            System.out.println("gps service url: "+this.getServiceConnectionURL());

            // open connection to gps service using it's service url
            //this.connection = (StreamConnection) Connector.open(this.getServiceConnectionURL(), Connector.READ);
            if(this.connection == null) {
                // open connection to gps device using channel 1 (default for gps data transfer)
                this.connection = (StreamConnection) Connector.open("btspp://" + this.getAddress() + ":1", Connector.READ);
                //#debug
    //#             System.out.println("default gps connection used.");
            }
        }
        if(this.connection != null) {
            this.status = STATUS_CONNECTED;
            //#debug
//#             System.out.println("gps connection established");
        } else throw new IOException();
        
        // open input stream to read nmea sentences from device
        this.reader = new InputStreamReader(this.connection.openInputStream());
        
        this.locationListenerThread = new Thread(this);
        this.locationListenerThread.start();
        ////#debug
        System.out.println("gps thread started.");
    }
    
    // initiate bluetooth device disconnection
    public synchronized void disconnect() {
        if(this.status == STATUS_CONNECTED) {
            this.reconnectOnTermination = false;
            this.status = STATUS_DISCONNECTING;

            // wait until listener thread is terminated
            try {
                //#debug
//#                 System.out.println("before join");
                this.locationListenerThread.join();
                //#debug
//#                 System.out.println("after join");
                this.closeConnection();
                //#debug
//#                 System.out.println("closed");
            } catch(InterruptedException ie) {}
        }
    }
    
    // disconnect from blueooth device
    private void closeConnection() {
        try {
            if (this.reader != null) {
                this.reader.close();
            }
        } catch (IOException ioe) {
            //#debug error
            System.out.println("closing input stream reader: "+ioe);
        }
        try {
            if (this.connection != null) {
                this.connection.close();
            }
        } catch (IOException ioe) {
            //#debug error
            System.out.println("closing bt connection: "+ioe);
        }
        this.status = STATUS_DISCONNECTED;
        this.cleanUp();
        
        ////#debug
        System.out.println("gps connection closed.");
    }
    
    private void cleanUp() {
        this.reader = null;
        this.connection = null;
        this.locationListenerThread = null;
        this.timeoutNotifier.cancel();
        this.nmeaSentenceParser.cleanUp();
    }
    
    public int getStatus() {
        return this.status;
    }
    
    // get current location
    public GPSLocation getGPSLocation() {
        return this.gpsLocation;
    }
    
    // listen for nmea sentences and parse received data
    public void run() {
        try {
            // automatically disconnect gps device after a specific amount of time
            this.scheduleTimeoutNotifier();
            while(this.status == STATUS_CONNECTED) {
                try {
                    StringBuffer output = new StringBuffer();
                    
                    // read one sentence and try to parse it
                    int input;
                    while ((input = this.reader.read()) != LINE_DELIMITER) {
                        output.append((char) input);
                    }
                    
                    ////#debug
                    System.out.println("read data length: "+output.length());
                    ////#debug
                    System.out.println("received raw NMEA string: "+output.toString());
                    
                    try{
                        // trim start and end of any NON-Displayable characters.
                        while (output.charAt(0) < '!' || output.charAt(0) > '~') {
                            output.deleteCharAt(0);
                        }
                        while (output.charAt(output.length() - 1) < '!'
                            || output.charAt(output.length() - 1) > '~') {
                            output.deleteCharAt(output.length() - 1);
                        }
                    }catch(IndexOutOfBoundsException e){
                        // ignore but do not parse it
                        continue;
                    }
                    // only parse data beginning with valid sentence character: '$'
                    if(output.length() > 1 && output.charAt(0) == '$'){
                        ////#debug
                        System.out.println("NMEA sentence for parsing: "+output.toString());
                        ////#debug
                        System.out.println("start parsing...");
                        // parse sentence only if no disconnection was initialized yet
                        if(this.status == STATUS_CONNECTED)
                            this.nmeaSentenceParser.parse(output.toString());
                    }
                    
                // thrown on sentence read error
                // (e.g. GPS device out of reach, turned off, low battery)
                } catch (IOException ie) {
                    if(this.reconnectOnTermination) {
                        this.status = STATUS_DISCONNECTING;
                        // automatically reconnect to GPS receiver
                        // wait some time, then close connection and reconnect
                        try {
                            Thread.sleep(RECONNECTION_TIMEOUT);
                        } catch (InterruptedException e) {/*ignore*/}
                        this.closeConnection();
                        
                        // try to reconnect
                        int reconnectionAttempts = 0;
                        while(this.status == STATUS_DISCONNECTED &&
                              reconnectionAttempts < this.maxReconnectionAttemps) {
                            try {
                                reconnectionAttempts++;
                                this.connect();
                                //#debug
//#                                 System.out.println("reconnected.");
                            } catch (IOException e) {
                                 if(reconnectionAttempts == this.maxReconnectionAttemps) {
                                   if(this.controller != null) {
                                       Displayable nextScreen = this.controller.getScreen(this.controller.MAIN_SCR);
                                       this.controller.show(this.controller.ERROR_ASCR, Locale.get("gpsDevice.alert.connectionLost")+": "+this.getAlias(), null, nextScreen);
                                   }
                                   //#debug error
                                   System.out.println("Connection to GPS Receiver lost!"); 
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Displayable nextScreen = this.controller.getScreen(this.controller.MAIN_SCR);
                    this.controller.show(this.controller.ERROR_ASCR, Locale.get("gpsDevice.alert.noData")+": "+this.getAlias(), null, nextScreen);
                    //#debug error
                    System.out.println("generic gps device exception: "+e);
                }
            }
        } catch (Throwable e) {
            if(e instanceof Error){
                //#debug error
                System.out.println("GPS device: unexpected error");
            }else if(e instanceof Exception){
                //#debug error
                System.out.println("GPS device: unexpected exception");
            }else{
                //#debug error
                System.out.println("GPS device: fatal exception");
            }
        }
    }
    
    public void scheduleTimeoutNotifier() {
        this.timeoutNotifier = new Timer();
        this.timeoutNotifier.schedule(new LocationNotifier(), CONNECTION_TIMEOUT);
    }
    
    // read location, disconnect gps device, and notify location manager
    private class LocationNotifier extends TimerTask 
    {
        public void run(){
            if(status == STATUS_CONNECTED) {
                // set current location
                gpsLocation = nmeaSentenceParser.getCurrentLocation();
                // notify location manager
                if(gpsLocation == null)
                    locationManager.onLocationInquiryCompleteBTD(false);
                else
                    locationManager.onLocationInquiryCompleteBTD(true);
            }
        }
    }
    
}
