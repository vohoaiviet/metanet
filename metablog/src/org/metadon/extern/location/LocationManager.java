/*
 * LocationManager.java
 *
 * Created on 09. Dezember 2007, 01:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.extern.location;


import de.enough.polish.util.Locale;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Alert;

import org.metadon.beans.Settings;
import org.metadon.control.Controller;
import org.metadon.extern.bluetooth.*;
import org.metadon.utils.ActivityAlert;

import java.io.IOException;

/**
 *
 * @author Hannes
 */
public class LocationManager implements CommandListener {
    
    private volatile static LocationManager uniqueInstance;
    private BluetoothManager bluetoothManager;
    private Settings settings;
    private int locationMethod;
    private Controller controller;
    private GPSServiceConnection gpsDevice;
    private GPSLocation gpsLocation;
    private GLPServiceConnection genericLocationProvider;
    private ActivityAlert activityScreen = new ActivityAlert(true);
    
    private final int GPS = 0;
    private final int GENERIC = 1;
    
    public static synchronized LocationManager getInstance(Controller c) {
        if(uniqueInstance == null) {
            //synchronize concurrent threads on creating a instance (performance decrease!)
            synchronized (LocationManager.class) {
                if(uniqueInstance == null) {
                    uniqueInstance = new LocationManager(c);
                }
            }
        }
        return uniqueInstance;
    }
    
    /** Creates a new instance of LocationManager */
    private LocationManager(Controller c) {
        super();
        this.controller = c;
        this.bluetoothManager = BluetoothManager.getInstance(this.controller);
    }
    
    public void retrieveLocation() {
        this.settings = this.controller.getSettings();
        // use external GPS receiver
        if(this.settings.getGPSDeviceType() == this.settings.GPS_TYPE_EXTERN) {
            // check if a stored GPS receiver is available
            if(!this.isValidDevice(this.settings.getGPSDevice())) {
                // search surrounding bluetooth devices
                this.bluetoothManager.searchEnvironment(this.bluetoothManager.GPS_DEVICE);
                // wait until a valid GPS device is available within cache
                Thread t = new Thread() {
                    public void run() {
                        while(!isValidDevice(controller.getSettings().getGPSDevice())) {
                            try {
                                Thread.sleep(100);
                            }catch(Exception e) {}
                        }
                        // update local settings
                        settings = controller.getSettings();
                        retrieveLocationFromBTD();
                    }
                };
                t.start();
            } else {
                this.retrieveLocationFromBTD();
            }
        }
        // try other generic location techniques
        else if(this.settings.getGPSDeviceType() == this.settings.GPS_TYPE_INTERN) {
            this.retrieveLocationFromGLP();
        }
    }
    
    // get current location from chosen external GPS bluetooth device (BTD) (no location API used)
    private void retrieveLocationFromBTD() {
        this.locationMethod = this.GPS;
        // get currently referenced gps device address
        String address = this.settings.getGPSDevice().getAddress();

        // check if gps device has changed in the meantime
        if(this.gpsDevice == null || !this.gpsDevice.getAddress().equals(address)) {
           // create GPS device for receiving the GPS data from satellites
           String alias = this.settings.getGPSDevice().getAlias();
           this.gpsDevice = new GPSServiceConnection(address, alias); 
           this.gpsDevice.setServiceConnectionURL(this.settings.getGPSDevice().getConnectionURL());
           this.gpsDevice.setController(this.controller);
           this.gpsDevice.setLocationManager(this);
        } else {
            //#debug
//#             System.out.println("using cached gps device: "+this.gpsDevice.getAlias());
        }
        if(this.gpsDevice.getStatus() == this.gpsDevice.STATUS_CONNECTED ||
           this.gpsDevice.getStatus() == this.gpsDevice.STATUS_DISCONNECTING) {
           return;
        }
        
        // show activity screen
        this.activityScreen.setString(Locale.get("locationManager.alert.connecting")+" "+this.gpsDevice.getAlias()+"...");
        this.controller.show(this.activityScreen);
        
        // connect to GPS device and get GPS location data
        Thread t = new Thread() {
            int maxConnectionAttemps = 1;
            int connectionAttempts = 0;
            public void run() {
                while(connectionAttempts < maxConnectionAttemps && gpsDevice.getStatus() == gpsDevice.STATUS_DISCONNECTED) {
                    try {
                        connectionAttempts++;
                        gpsDevice.connect();
                        activityScreen.setString(Locale.get("locationManager.alert.waiting"));
                    } catch(SecurityException se) {
                        cleanUp();
                        controller.show(controller.getScreen(controller.MAIN_SCR));
                    } catch(IOException ioe) {
                        if(connectionAttempts == this.maxConnectionAttemps) {
                           cleanUp();
                           Displayable nextScreen = controller.getScreen(controller.MAIN_SCR);
                           controller.show(controller.ERROR_ASCR, Locale.get("locationManager.alert.unreachableDevice"), null, nextScreen);
                           //#debug
//#                            System.out.println("Device not reachable!");
                           //System.out.println("Device not reachable!"+ioe); 
                        }
                    } catch(Exception e) {
                           cleanUp();
                           Displayable nextScreen = controller.getScreen(controller.MAIN_SCR);
                           controller.show(controller.ERROR_ASCR, Locale.get("locationManager.alert.unreachableDevice"), null, nextScreen);
                           //#debug error
                           System.out.println("Generic exception: ");
                           //System.out.println("Generic exception: "+e); 
                    }
                }
            }
        };
        t.start();
    }
    
    // get current location from generic location provider (GLP) determined from the system 
    private void retrieveLocationFromGLP() {
        //#if config.emulate
            // this device supports the Location API
            this.locationMethod = this.GENERIC;
            if(this.genericLocationProvider == null) {
                this.genericLocationProvider = new GLPServiceConnection();
                this.genericLocationProvider.setController(this.controller);
                this.genericLocationProvider.setLocationManager(this);
            }
            // show activity screen
            this.activityScreen.setString(Locale.get("locationManager.alert.waiting"));
            this.controller.show(this.activityScreen);
            this.genericLocationProvider.connect();
        //#else    
//#             Displayable nextScreen = controller.getScreen(controller.MAIN_SCR);
//#             this.controller.show(this.controller.CONF_ASCR, Locale.get("alert.notSupported"), null, nextScreen);
        //#endif
    }
    
    private boolean isValidDevice(BluetoothDevice device) {
        if(device == null || this.settings == null) return false;
        if(device.getAlias().equals(this.settings.GPS_DEVICE_EMPTY_ALIAS)) 
            return false;
        else return true;
    }
    
    private void cleanUp() {
        this.settings = null;
        this.gpsLocation = null;
    }
    
    // called from gps bluetooth device to notify manager about location status after timeout
    public void onLocationInquiryCompleteBTD(boolean available) {
        if(available) {
            // called when current location was found
            this.activityScreen.setString(Locale.get("locationManager.alert.disconnecting"));
            this.gpsDevice.disconnect();
            this.gpsLocation = this.gpsDevice.getGPSLocation(); 
            this.finished();
        } else {
            // called when current location was not found (usually no satellites in view)
            // ask user for further actions
            this.controller.show(this.controller.CONF_ASCR, Locale.get("locationManager.alert.unreachableGPSService"), this, null);
        }
    }
    
    // called from generic location provider to notify manager about location status
    public void onLocationInquiryCompleteGLP(boolean available) {
        if(available) {
            // called when current location was found
            this.gpsLocation = this.genericLocationProvider.getGPSLocation();
            //#debug
//#             System.out.println("location method: "+this.gpsLocation.getLocationMethod());
            this.finished();
        } else {
            // called when current location was not found (usually no service available)
            // ask user for further actions
            this.controller.show(this.controller.CONF_ASCR, Locale.get("locationManager.alert.unreachableGenericService"), this, null);
        }
    }
    
    public void commandAction(Command cmd, Displayable disp) {
         if (cmd == this.controller.YES_CMD && disp instanceof Alert) {
             // show activity screen
             this.activityScreen.setString(Locale.get("locationManager.alert.waiting"));
             this.controller.show(this.activityScreen);
             if(this.locationMethod == this.GPS)
                this.gpsDevice.scheduleTimeoutNotifier();
             else if(this.locationMethod == this.GENERIC)
                this.genericLocationProvider.connect();
             
         } else if(cmd == this.controller.NO_CMD && disp instanceof Alert) {
//             this.activityScreen.setString(Locale.get("text.disconnecting"));
//             this.controller.show(this.activityScreen);
             if(this.locationMethod == this.GPS) {
                 Thread t = new Thread() {
                     public void run() {
                        gpsDevice.disconnect();
                     }
                 };
                 t.start();
             } 
             this.finished();
         }
     }
    
    // location retrieval finished - notify controller
    private void finished() {
        this.controller.setCurrentGPSLocation(this.gpsLocation);
        this.controller.show(this.controller.getScreen(this.controller.BLOG_SCR));
        this.cleanUp();
    }
    
}
