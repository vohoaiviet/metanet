/*
 * GPSLocation.java
 *
 * Created on 05. Dezember 2007, 10:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.location;

/**
 *
 * @author Hannes
 */
public class GPSLocation {

    /** The longitude at this GPSLocation */
    private final double longitude;
    /** The latitude at this GPSLocation */
    private final double latitude;
    
    private String locationMethod;
    
//    private short satellitesCount;
//    public boolean containsSatellitesCount = false;
    
    /** The quality of the GPS service */
    //private short serviceQuality;
    /** The speed at this GPSLocation in knots */
    //private double speed;
    /** The course/direction at this GPSLocation */
    //private short course;
    /** The number of satellites, used by the service */
    
    //public boolean containsSpeed = false;
    //public boolean containsServiceQuality = false;
    

    /** Creates a new instance of GPSLocation */
    public GPSLocation(double longitude, double latitude) {
        super();
        this.longitude = longitude;
        this.latitude = latitude;
    }
    
    public double getLongitude() {
         return this.longitude;
    }
    
    public double getLatitude() {
         return this.latitude;
    }
    
    public void setLocationMethod(String method) {
        this.locationMethod = method;
    }
    
    public String getLocationMethod() {
        return this.locationMethod;
    }
    
//    public void setSatellitesCount(short satellitesCount) {
//        if(satellitesCount == 0) return;
//        this.satellitesCount = satellitesCount;
//        this.containsSatellitesCount = true;
//    }
//    
//    public short getSatellitesCount() {
//        return this.satellitesCount;
//    }
    
//    public boolean containsSatellitesCount() {
//        return this.containsSatellitesCount;
//    }
    
//    public void setSpeed(double speed) {
//        this.speed = speed;
//        this.containsSpeed = true;
//    }
//    
//    public double getSpeed() {
//        return this.speed;
//    }
    
//    public void setServiceQuality(short quality) {
//        if(quality == -1) return;
//        this.serviceQuality = quality;
//        this.containsServiceQuality = true;
//    }
//    
//    public short getServiceQuality() {
//        return this.serviceQuality;
//    }
    
//    public void setCourse(int course) {
//        this.speed = speed;
//    }
//    
//    public int getCourse() {
//        return this.course;
//    }
    
//    public boolean containsSpeed() {
//        return this.containsSpeed;
//    }
    
//    public boolean containsServiceQuality() {
//        return this.containsServiceQuality;
//    }
    
}
