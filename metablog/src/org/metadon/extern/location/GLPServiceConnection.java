/*
 * GenericLocationProvider.java
 *
 * Created on 05. Dezember 2007, 10:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.extern.location;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;
import javax.microedition.lcdui.Displayable;

import org.metadon.control.Controller;


import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class GLPServiceConnection {

    private Controller controller;
    private LocationManager locationManager;
    private GPSLocation gpsLocation;
    
    private Criteria criteria = new Criteria();
    private final int TIMEOUT = 10;     // in seconds
    private final int ACCURACY_H = 200; // in meters
    private final int ACCURACY_V = 200; // in meters
    
    
    /**
     * Creates a new instance of GenericLocationProvider
     */
    public GLPServiceConnection() {
        super();
        criteria.setHorizontalAccuracy(this.ACCURACY_H);
        criteria.setVerticalAccuracy(this.ACCURACY_V);
        criteria.setAltitudeRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPreferredResponseTime(criteria.NO_REQUIREMENT);
        criteria.setPreferredPowerConsumption(criteria.NO_REQUIREMENT);
    }
    
    public void setController(Controller c) {
        this.controller = c;
    }
    
    public void setLocationManager(LocationManager lm) {
        this.locationManager = lm;
    }
    
    public void connect() {
        new Thread() {
            public void run() {
                try {
                    LocationProvider provider = LocationProvider.getInstance(criteria);
                    if(provider != null) {
                        try {
                            Location location = provider.getLocation(TIMEOUT);
                            //#debug
//#                             System.out.println("GLP - nmea string: "+location.getExtraInfo("application/X-jsr179-location-nmea"));
                            if(location.isValid()) {
                                QualifiedCoordinates coord = location.getQualifiedCoordinates();
                                double lng = coord.getLongitude();
                                double lat = coord.getLatitude();
                                // avoid processing errorous data from gps service.
                                // the exact value of 0.0 is very unlikely in real scenarios
                                if(lng == 0.0 || lat == 0.0) {
                                    //#debug
//#                                 System.out.println("gps 0.0 value received.");
                                    throw new LocationException();
                                }
                                gpsLocation = new GPSLocation(lng, lat);
                                //#debug
//#                                 System.out.println("GLP: long.: "+gpsLocation.getLongitude());
                                //#debug
//#                                 System.out.println("GLP: lat.: "+gpsLocation.getLatitude());
                                
                                String method = getLocationMethodName(location.getLocationMethod());
                                gpsLocation.setLocationMethod(method);
                                locationManager.onLocationInquiryCompleteGLP(true);
                            }
                            if(gpsLocation == null)
                                locationManager.onLocationInquiryCompleteGLP(false);
                            
                        } catch(LocationException le) {
                            locationManager.onLocationInquiryCompleteGLP(false);
                        } catch(InterruptedException ie) {
                            locationManager.onLocationInquiryCompleteGLP(false);
                        } catch(SecurityException se) {
                            controller.show(controller.getScreen(controller.MAIN_SCR));
                        }
                   } else
                        throw new LocationException();
                   
                } catch(LocationException le) {
                    Displayable nextScreen = controller.getScreen(controller.MAIN_SCR);
                    controller.show(controller.ERROR_ASCR, Locale.get("genericLocationProvider.alert.unreachableProvider"), null, nextScreen);
                    //#debug
//#                     System.out.println("GLP: no Provider reachable.");

                } catch(SecurityException se) {}
            }
        }.start();
    }
    
    private String getLocationMethodName(int i){
            StringBuffer buffer = new StringBuffer();
            if (i == 0)                                 buffer.append(Locale.get("locationInfo.notAvailable"));
            if ((i & Location.MTA_ASSISTED) > 0)        buffer.append(Locale.get("locationInfo.hybridInfo.hybrid.yes"));
            if ((i & Location.MTA_UNASSISTED) > 0)      buffer.append(Locale.get("locationInfo.hybridInfo.hybrid.no"));
            if ((i & Location.MTE_ANGLEOFARRIVAL) > 0)  buffer.append(Locale.get("locationInfo.methodInfo.aoa"));
            if ((i & Location.MTE_SATELLITE) > 0)       buffer.append(Locale.get("locationInfo.methodInfo.satellite"));
            if ((i & Location.MTE_CELLID) > 0)          buffer.append(Locale.get("locationInfo.methodInfo.cellID"));
            if ((i & Location.MTE_SHORTRANGE) > 0)      buffer.append(Locale.get("locationInfo.methodInfo.shortRange"));
            if ((i & Location.MTE_TIMEDIFFERENCE) > 0)  buffer.append(Locale.get("locationInfo.methodInfo.timeDiff"));
            if ((i & Location.MTE_TIMEOFARRIVAL) > 0)   buffer.append(Locale.get("locationInfo.methodInfo.toa"));
            if ((i & Location.MTY_NETWORKBASED) > 0)    buffer.append(Locale.get("locationInfo.typeInfo.networkBased"));
            if ((i & Location.MTY_TERMINALBASED) > 0)   buffer.append(Locale.get("locationInfo.typeInfo.terminalBased"));
            return buffer.toString();
    }
    
    // get current location
    public GPSLocation getGPSLocation() {
        return this.gpsLocation;
    }
}
