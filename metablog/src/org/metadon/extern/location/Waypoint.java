/*
 * Waypoint.java
 *
 * Created on 16. Dezember 2007, 13:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.extern.location;


import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.metadon.beans.Blog;
import org.metadon.utils.ISerializable;
/**
 *
 * @author Hannes
 */
public class Waypoint extends Blog implements ISerializable {
    
    private double distance;
    private long interval;
    private String country;
    private String state;
    private String nearbyPlace;
    private String countryCode;
    private double elevation;
    private boolean isResolved = false;
    
    private boolean containsCountry = false;
    private boolean containsState = false;
    private boolean containsNearbyPlace = false;
    private boolean containsCountryCode = false;
    private boolean containsElevation = false;
    
    
    /** Creates a new instance of Waypoint */
    public Waypoint(long timestamp, GPSLocation location) {
        super();
        this.setTimestamp(timestamp);
        this.setLocation(location);
    }
    
    public void setDistance(double distanceInKilometer) {
        this.distance = distanceInKilometer;
    }
    
    public double getDistance() {
        return this.distance;
    }
    
    public void setInterval(long interval) {
        this.interval = interval;
    }
    
    public long getInterval() {
        return this.interval;
    }
    
    public void setCountry(String country) {
        this.country = country;
        this.containsCountry = true;
    }
    
    public String getCountry() {
        return this.country;
    }
    
    public void setState(String state) {
        this.state = state;
        this.containsState = true;
    }
    
    public String getState() {
        return this.state;
    }
    
    public void setNearbyPlace(String nearbyPlace) {
        this.nearbyPlace = nearbyPlace;
        this.containsNearbyPlace = true;
    }
    
    public String getNearbyPlace() {
        return this.nearbyPlace;
    }
    
     public void setCountryCode(String code) {
        this.countryCode = code;
        this.containsCountryCode = true;
    }
    
    public String getCountryCode() {
        return this.countryCode;
    }
    
    public void setElevation(double elevation) {
        this.elevation = elevation;
        this.containsElevation = true;
    }
    
    public double getElevation() {
        return this.elevation;
    }
    
    public void setResolved() {
        this.isResolved = true;
    }
    
    public boolean isResolved() {
        return this.isResolved;
    }
    
    public boolean containsCountry() {
        return this.containsCountry;
    }
    
    public boolean containsState() {
        return this.containsState;
    }
    
    public boolean containsNearbyPlace() {
        return this.containsNearbyPlace;
    }
    
    public boolean containsCountryCode() {
        return this.containsCountryCode;
    }
    
    public boolean containsElevation() {
        return this.containsElevation;
    }
    
      // serialize settings data for storage
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
       
        // used by RMS - must be serialized first
        dos.writeLong(this.getTimestamp());
        dos.writeBoolean(this.isResolved());
        
        // fundamental attributes
        dos.writeDouble(this.getLocation().getLongitude());
        dos.writeDouble(this.getLocation().getLatitude());
        
        // related attributes
        dos.writeLong(this.getInterval());
        dos.writeDouble(this.getDistance());
        
        // country
        dos.writeBoolean(this.containsCountry());
        if(this.containsCountry())
            dos.writeUTF(this.getCountry());
        
        // state
        dos.writeBoolean(this.containsState());
        if(this.containsState())
            dos.writeUTF(this.getState());
        
        // nearby place
        dos.writeBoolean(this.containsNearbyPlace());
        if(this.containsNearbyPlace())
            dos.writeUTF(this.getNearbyPlace());
        
        // country code
        dos.writeBoolean(this.containsCountryCode());
        if(this.containsCountryCode())
            dos.writeUTF(this.getCountryCode());
        
        // elevation
        dos.writeBoolean(this.containsElevation());
        if(this.containsElevation())
            dos.writeDouble(this.getElevation());
        
        return baos.toByteArray();
    }
    
    // reconstruct settings data from byte stream
    public void unserialize(byte[] record) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(record);
        DataInputStream dis = new DataInputStream(bais);
        
        // read instance variables in same order as written out
        // used by RMS - must be unserialized first
        this.setTimestamp(dis.readLong());
        this.isResolved = dis.readBoolean();
        
        // fundamental attributes
        GPSLocation location = new GPSLocation(dis.readDouble(), dis.readDouble());
        this.setLocation(location);
        
        // related attributes
        this.setInterval(dis.readLong());
        this.setDistance(dis.readDouble());
        
        // country
        this.containsCountry = dis.readBoolean();
        if(this.containsCountry())
            this.setCountry(dis.readUTF());
        
        // state
        this.containsState = dis.readBoolean();
        if(this.containsState())
            this.setState(dis.readUTF());
        
        // nearby place
        this.containsNearbyPlace = dis.readBoolean();
        if(this.containsNearbyPlace())
            this.setNearbyPlace(dis.readUTF());
        
        // country code
        this.containsCountryCode = dis.readBoolean();
        if(this.containsCountryCode())
            this.setCountryCode(dis.readUTF());
        
        // elevation
        this.containsElevation = dis.readBoolean();
        if(this.containsElevation())
            this.setElevation(dis.readDouble());
        
        dis.close(); // closes bais as well
    }
    
}
