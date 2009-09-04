/*
 * TraceManager.java
 *
 * Created on 16. Dezember 2007, 12:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.metadon.extern.location;

import java.util.Vector;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import java.io.IOException;




import javax.microedition.lcdui.Displayable;

import org.metadon.beans.Settings;
import org.metadon.control.BlogBrowserLocal;
import org.metadon.control.Controller;
import org.metadon.control.RecordManager;
import org.metadon.extern.web.geoservice.GeonamesWSC;
import org.metadon.extern.web.geoservice.Toponym;
import org.metadon.utils.DateFormatter;
import org.metadon.utils.RMSWaypointSelector;

import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class TraceManager {

    private volatile static TraceManager uniqueInstance;
    private Controller controller;
    private RecordManager recordManager;
    private BlogBrowserLocal blogBrowser;
    public Vector trace;
    // distance cut-off in kilometer
    private double CUT_OFF = 0.0;
    // private static final ActivityAlert activityScreen = new ActivityAlert(true);
    /**
     * Creates a new instance of TraceManager
     */
    private TraceManager(Controller c) {
        super();
        this.controller = c;
        this.recordManager = RecordManager.getInstance(this.controller);
        this.blogBrowser = BlogBrowserLocal.getInstance();
        // load stored waypoints from RMS
        RMSWaypointSelector selector = new RMSWaypointSelector(RMSWaypointSelector.LESSER_THEN, System.currentTimeMillis());
        this.trace = this.recordManager.getWaypoint(selector);
        ////#debug
        System.out.println("rms init waypoints: " + this.trace.size());
    }

    public static synchronized TraceManager getInstance(Controller c) {
        if (uniqueInstance == null) {
            //synchronize concurrent threads on creating a instance (performance decrease!)
            synchronized (TraceManager.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new TraceManager(c);
                }
            }
        }
        return uniqueInstance;
    }

    // track waypoint
    public void trackWaypoint(long timestamp, GPSLocation location) {
        double distance = 0.0;
        long interval = 0;

        Waypoint previousWaypoint = this.getPredecessor();
        if (previousWaypoint != null) {
            GPSLocation currentLocation = location;
            GPSLocation previousLocation = previousWaypoint.getLocation();

            // test distance calculation
            // (vienna - salzburg)
//            GPSLocation salzburg = new GPSLocation(13.0563497543,47.8075904846,0);
//            distance = WaypointToolkit.getDistance(salzburg, previousLocation);

            // (vienna - tokio)
//            GPSLocation tokio = new GPSLocation(139.882354736,35.62890625,0);
//            distance = WaypointToolkit.getDistance(tokio, previousLocation);

            // get distance to predecessor
            distance = WaypointToolkit.getDistance(currentLocation, previousLocation);
            if (distance <= this.CUT_OFF) {
                distance = 0.0;
            }
            // get interval to predecessor
            interval = timestamp - previousWaypoint.getTimestamp();
        }
        // create new waypoint
        Waypoint currentWaypoint = new Waypoint(timestamp, location);
        // set waypoint relation
        currentWaypoint.setDistance(distance);
        currentWaypoint.setInterval(interval);

        // store waypoint
        if (this.recordManager.setWaypoint(currentWaypoint, null)) {
            this.trace.addElement(currentWaypoint);
        //#debug
//#             System.out.println("rms 1 stored - trace size: "+this.trace.size());
        }
    }

    // return the last waypoint
    private Waypoint getPredecessor() {
        try {
            return (Waypoint) this.trace.lastElement();
        } catch (NoSuchElementException nsee) {
            return null;
        }
    }

    public String getJourneyDistance() {
        double distance = 0.0;

        Enumeration enumeration = this.trace.elements();
        while (enumeration.hasMoreElements()) {
            Waypoint wp = (Waypoint) enumeration.nextElement();
            distance += wp.getDistance();
        }
        // get user preferences
        Settings settings = this.controller.getSettings();
        int unit = WaypointToolkit.KILOMETER;
        if (settings.getDistanceUnit() == settings.DISTANCE_ML) {
            unit = WaypointToolkit.MILE;
        }
        return WaypointToolkit.getUserFriendlyDistance(distance, unit, false);
    }

    public String getJourneyDuration() {
        long time = 0;

        Enumeration enumeration = this.trace.elements();
        while (enumeration.hasMoreElements()) {
            Waypoint wp = (Waypoint) enumeration.nextElement();
            time += wp.getInterval();
        }
        return DateFormatter.getUserFriendlyInterval(time);
    }

    public int getJourneyWaypointCount() {
        return this.trace.size();
    }

    // return distance and interval to the predecessor in an user friendly format
    public String[] getWaypointInformation(long timestamp) {
        int wpIndex = getWaypointIndex(timestamp);
        if (wpIndex == -1) {
            return null;
        }

        Waypoint wp = (Waypoint) this.trace.elementAt(wpIndex);
        if (wp != null) {
            // set prefered distance unit
            int unit = WaypointToolkit.KILOMETER;
            Settings settings = this.controller.getSettings();
            if (settings.getDistanceUnit() == settings.DISTANCE_ML) {
                unit = WaypointToolkit.MILE;
            }

            // format distance and interval values
            String distance = WaypointToolkit.getUserFriendlyDistance(
                    wp.getDistance(),
                    unit,
                    false);
            String interval = DateFormatter.getUserFriendlyInterval(wp.getInterval());
            return new String[]{distance, interval};
        }
        return null;
    }

    private int getWaypointIndex(long timestamp) {
        // TODO: search for waypoint in trace vector (binary search algorithm)
        for (int i = 0; i < this.trace.size(); i++) {
            Waypoint wp = (Waypoint) this.trace.elementAt(i);
            if (wp.getTimestamp() == timestamp) {
                return i;
            }
        }
        ////#debug error
        System.out.println("waypoint index not found in trace.");
        return -1;
    }

    public Waypoint getWaypoint(long timestamp) {
        for (int i = 0; i < this.trace.size(); i++) {
            Waypoint wp = (Waypoint) this.trace.elementAt(i);
            if (wp.getTimestamp() == timestamp) {
                return wp;
            }
        }
        ////#debug error
        System.out.println("waypoint not found in trace.");
        return null;
    }

    // remove a specific waypoint from trace
    // return false if no waypoint was deleted
    public boolean removeWaypoint(long timestamp) {
        int wpIndex = this.getWaypointIndex(timestamp);
        if (wpIndex == -1) {
            return false;
        }
        double newDistance = 0.0;
        long newInterval = 0;

        // update waypoint relation if successor exists
        // skip calculation if the waypoint is the most recent one
        if (wpIndex + 1 < this.trace.size()) {
            Waypoint wp = (Waypoint) this.trace.elementAt(wpIndex);
            Waypoint wpSuccessors = (Waypoint) this.trace.elementAt(wpIndex + 1);

            // check whether waypoint is journey starting waypoint
            if (wpIndex > 0 && this.trace.size() > 2) {
                // calculate distance and time interval between successor and predecessor
                newDistance = wpSuccessors.getDistance() + wp.getDistance();
                newInterval = wpSuccessors.getInterval() + wp.getInterval();
            }
            // update distance and interval of successor
            wpSuccessors.setDistance(newDistance);
            wpSuccessors.setInterval(newInterval);
        }

        // delete waypoint
        RMSWaypointSelector selector = new RMSWaypointSelector(RMSWaypointSelector.EQUAL_TO, timestamp);
        if (this.recordManager.removeWaypoint(selector) == 1) {
            this.trace.removeElementAt(wpIndex);
            ////#debug
            System.out.println("rms 1 deleted - trace size: " + this.trace.size());
            return true;
        } else {
            return false;
        }
    }

    // remove trace
    public void removeTrace() {
        RMSWaypointSelector selector = new RMSWaypointSelector(RMSWaypointSelector.LESSER_THEN, System.currentTimeMillis());
        int deletedWaypoints = this.recordManager.removeWaypoint(selector);
        if (deletedWaypoints >= 0) {
            this.trace.removeAllElements();
            ////#debug
            System.out.println("rms all waypoints deleted: " + deletedWaypoints);
        }
    }

    /********************* trace resolution *********************/
    // resolves all 'longitude/latitude' pairs to user friendly names and updates
    // each waypoint with this information
    // long/lat -> nearbyPlace/countryCode/elevation
    // the data is from a public webservice: 'http://www.geonames.org'
    // the elevation is high accurate since the value is based on a static database
    // with a resolution of 90 meters.
    public void resolveTrace() {
        new Thread() {

            Displayable nextScreen = blogBrowser.getBrowserScreen();
            // starts new webservice thread
            GeonamesWSC geoNamesWS = new GeonamesWSC();

            public void run() {
                geoNamesWS.setController(controller);
                int resolvedWaypoints = 0;
                for (int i = 0; i < trace.size(); i++) {
                    Waypoint wp = (Waypoint) trace.elementAt(i);
                    if (wp.isResolved()) {
                        continue;
                    }
                    Toponym toponym = null;

                    if (wp != null) {
                        try {
                            toponym = geoNamesWS.getToponym(wp);
                        } catch (SecurityException se) {
                        } catch (IOException ioe) {
                            //#debug error
                            System.out.println("IO Exception: " + ioe);
                        } catch (Exception e) {
                            //#debug error
                            System.out.println("WS Exception: " + e);
                        } finally {
                        }
                    }
                    // update waypoint information
                    if (toponym != null && toponym.containsStateName &&
                            toponym.containsCountryCode &&
                            toponym.containsCountryName &&
                            toponym.containsNearbyPlaceName &&
                            toponym.containsElevation)  {

                        // get mandatory attributes
                        wp.setElevation(toponym.getElevation().doubleValue());
                        wp.setNearbyPlace(toponym.getNearbyPlaceName());
                        wp.setState(toponym.getStateName());
                        wp.setCountry(toponym.getCountryName());
                        wp.setCountryCode(toponym.getCountryCode());
                        wp.setResolved();

                        // store updated waypoint
                        RMSWaypointSelector selector = new RMSWaypointSelector(RMSWaypointSelector.EQUAL_TO, wp.getTimestamp());
                        if (recordManager.setWaypoint(wp, selector)) {
                            resolvedWaypoints++;
                        }
                    }
                    geoNamesWS.cleanUp();
                }
                geoNamesWS.stop();
                if (resolvedWaypoints > 0) {
                    controller.show(controller.OK_ASCR, resolvedWaypoints + " " + Locale.get("traceManager.waypointsResolved"), null, nextScreen);
                    blogBrowser.updateBrowserScreen();
                } else {
                    controller.show(controller.ERROR_ASCR, Locale.get("traceManager.notResolved"), null, nextScreen);
                }
            }
        }.start();
    }

    public boolean isResolvedTrace() {
        RMSWaypointSelector selector = new RMSWaypointSelector(RMSWaypointSelector.IS_RESOLVED, System.currentTimeMillis());
        Vector resolvedWaypoints = this.recordManager.getWaypoint(selector);
        if (resolvedWaypoints.size() == this.trace.size()) {
            return true;
        } else {
            return false;
        }
    }
}
