/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.metadon.beans;

/**
 *
 * @author Hannes
 */
public class Waypoint {
    
    int id;
    double longitude;
    double latitude;
    double elevation;
    
    public Waypoint(int id, double longitude, double latitude, double elevation) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.elevation = elevation;
    }

    public int getID() {
        return this.id;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }
    
    public double getElevation() {
        return this.elevation;
    }
}
