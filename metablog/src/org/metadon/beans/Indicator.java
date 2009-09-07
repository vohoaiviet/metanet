/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.metadon.beans;

/**
 *
 * @author Hannes
 */
public class Indicator {
    
    private long timestamp = -1;
    
    private String message = null;
    private byte[] photo = null;
    
    private double longitude = -1;
    private double latitude = -1;
    private double elevation = -1;
    
    private String journeyName = null;
    private String comment = null;
    
    public boolean containsMessage = false;
    public boolean containsPhoto = false;
    public boolean containsLocation = false;
    public boolean containsElevation = false;
    public boolean containsJourneyName = false;
    
    public Indicator(long timestamp) {
        this.timestamp = timestamp;
    }
    
    // get timestamp
    public long getTimestamp() {
        return this.timestamp;
    }
    
    // set message
    public void setMessage(String message) {
        this.message = message;
        this.containsMessage = true;
    }
    
    // get message
    public String getMessage() {
        return this.message;
    }
    
    // set photo
    public void setPhoto(byte[] photo) {
        this.photo = photo;
        this.containsPhoto = true;
    }
    
    // get photo
    public byte[] getPhoto() {
        return this.photo;
    }
    
    // set location
    public void setLocation(double[] location) {
        this.longitude = location[0];
        this.latitude = location[1];
        this.containsLocation = true;
    }
    
    // get location
    public double[] getLocation() {
        return new double[] {this.longitude, this.latitude};
    }
    
    // set elevation
    public void setElevation(double elevation) {
        this.elevation = elevation;
        this.containsElevation = true;
    }
    
    // get elevation
    public double getElevation() {
        return this.elevation;
    }
    
    // set comment
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    // get comment
    public String getComment() {
        return this.comment;
    }
    
    // set journey name
    public void setJourneyName(String journeyName) {
        this.journeyName = journeyName;
        this.containsJourneyName = true;
    }
    
    // get journey name
    public String getJourneyName() {
        return this.journeyName;
    }

}
