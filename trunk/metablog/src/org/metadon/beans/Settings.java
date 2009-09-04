/*
 * Settings.java
 *
 * Created on 04. November 2007, 00:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.beans;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.metadon.extern.bluetooth.BluetoothDevice;
import org.metadon.utils.ISerializable;

import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class Settings implements ISerializable {
    
    private boolean forceLogin;
    private boolean forceSecureChannel;
    private boolean forceLocation;
    private boolean forceBluetooth;
    private int distanceUnit;
    private int blogRelation;
    private int blogOrder;
    private int photoSizeID;
    private int gpsDeviceTyp;
    private BluetoothDevice gpsDevice;
    private String journeyName;
    
    // system variables
    private int deletedJourneyBlogs;
    private int transferedPayload;
    private int postedMultimediaBlogs;
    private int postedMessageBlogs;
    private long lastReset;
    
    public final int PHOTO_SMALL = 0;
    public final int PHOTO_LARGE = 1;
    
    public final int GPS_TYPE_EXTERN = 3;
    public final int GPS_TYPE_INTERN = 4;
    
    public final int DISTANCE_KM = 5;
    public final int DISTANCE_ML = 6;

    public final int BLOG_RELATION_JOURNEY = 7;
    public final int BLOG_RELATION_STANDALONE = 8;
    
    public final int BLOG_ORDER_TIME = 9;
    public final int BLOG_ORDER_TYPE = 10;
    
    public final String GPS_DEVICE_EMPTY_ALIAS = Locale.get("settings.gpsDevice.search");
    public final BluetoothDevice GPS_DEVICE_EMPTY = new BluetoothDevice("n/a", this.GPS_DEVICE_EMPTY_ALIAS);
    
    /** Creates a new instance of Settings */
    public Settings() {
        super();
    }
    
    // set default settings values
    public void setDefaults() {
        this.setForceLogin(true);
        this.setForceSecureChannel(true);
        this.setForceLocation(false);
        this.setGPSDeviceType(this.GPS_TYPE_INTERN);
        this.setGPSDevice(this.GPS_DEVICE_EMPTY);
        this.setForceBluetooth(false);
        this.setPhotoSizeID(this.PHOTO_SMALL);
        this.setBlogRelation(this.BLOG_RELATION_JOURNEY);
        this.setBlogOrder(this.BLOG_ORDER_TIME);
        this.setDistanceUnit(this.DISTANCE_KM);
        this.setDeletedJourneyBlogs(0);
        this.setJourneyName(Locale.get("statistics.defaultValue.journeyName"));
        this.resetMonitor();
    }
    
    /***************************************************************************************/
    
    // set whether user has to login every application startup, or auto login should be performed
    public void setForceLogin(boolean flag) {
        this.forceLogin = flag;
    }
    
    // return login option
    public boolean getForceLogin() {
        return this.forceLogin;
    }
    
    /***************************************************************************************/
    
    // set whether user wants to use as secure channel to the server
    public void setForceSecureChannel(boolean flag) {
        this.forceSecureChannel = flag;
    }
    
    // return location option
    public boolean getForceSecureChannel() {
        return this.forceSecureChannel;
    }
    
    /***************************************************************************************/
    
    // set whether user wants to use on map blogs by determining current location
    public void setForceLocation(boolean flag) {
        this.forceLocation = flag;
    }
    
    // return location option
    public boolean getForceLocation() {
        return this.forceLocation;
    }
    
    /***************************************************************************************/
    
    // set type of GPS receiver
    public void setGPSDeviceType(int type) {
        this.gpsDeviceTyp = type;
    }
    
    // return gps device type
    public int getGPSDeviceType() {
        return this.gpsDeviceTyp;
    }
    
    /***************************************************************************************/
    
    // set GPS receiver
    public void setGPSDevice(BluetoothDevice gpsDevice) {
        this.gpsDevice = gpsDevice;
    }
    
    // return gps device
    public BluetoothDevice getGPSDevice() {
        return this.gpsDevice;
    }
    
    /***************************************************************************************/
    
    // set whether bluetooth data tranfer is prefered
    public void setForceBluetooth(boolean flag) {
        this.forceBluetooth = flag;
    }
    
    // return bluetooth option
    public boolean getForceBluetooth() {
        return this.forceBluetooth;
    }
    
    /***************************************************************************************/
    
    // set prefered photo quality
    public void setPhotoSizeID(int quality) {
        this.photoSizeID = quality;
    }
    
    // return photo quality option
    public int getPhotoSizeID() {
        return this.photoSizeID;
    }
    
    /***************************************************************************************/
    
    // set prefered blog reationship
    public void setBlogRelation(int relation) {
        this.blogRelation = relation;
    }
    
    // return prefered blog reationship
    public int getBlogRelation() {
        return this.blogRelation;
    }
    
    /***************************************************************************************/
    
    // set prefered blog order in browser
    public void setBlogOrder(int order) {
        this.blogOrder = order;
    }
    
    // return prefered blog order in browser
    public int getBlogOrder() {
        return this.blogOrder;
    }
    
    /***************************************************************************************/
    
    // set whether distance should be measured in kilometers or miles and sub units respectively
    public void setDistanceUnit(int unit) {
        this.distanceUnit = unit;
    }
    
    // return login option
    public int getDistanceUnit() {
        return this.distanceUnit;
    }
    
    /***************************************************************************************/
    
    // name of the current journey
    public void setJourneyName(String name) {
        this.journeyName = name;
    }
    
    public String getJourneyName() {
        return this.journeyName;
    }
    
    /***************************************************************************************/
    
    // track number of deleted blogs to be able to use unique blog numbers
    public void setDeletedJourneyBlogs(int numberDeletedJourneyBlogs) {
        this.deletedJourneyBlogs = numberDeletedJourneyBlogs;
    }
    
    public int getDeletedJourneyBlogs() {
        return this.deletedJourneyBlogs;
    }
    
    /***************************************************************************************/
    
    // reset transfered payload
    private void resetTransferedPayload() {
        this.transferedPayload = 0;
    }
    
    // add bytes to the transfered payload
    public void addTransferedPayload(int bytes) {
        this.transferedPayload += bytes;
    }
    
    public int getTransferedPayload() {
        return this.transferedPayload;
    }
    
    /***************************************************************************************/
    
    // posted multimedia blogs
    private void resetPostedMultimediaBlogs() {
        this.postedMultimediaBlogs = 0;
    }
    
    public void updatePostedMultimediaBlogs() {
        this.postedMultimediaBlogs++;
    }
    
    public int getPostedMultimediaBlogs() {
        return this.postedMultimediaBlogs;
    }
    
    /***************************************************************************************/
    
    // posted message blogs
    private void resetPostedMessageBlogs() {
        this.postedMessageBlogs = 0;
    }
    
    public void updatePostedMessageBlogs() {
        this.postedMessageBlogs++;
    }
    
    public int getPostedMessageBlogs() {
        return this.postedMessageBlogs;
    }
    
    /***************************************************************************************/
    
    // (re-)init traffic monitor
    public void resetMonitor() {
        this.resetTransferedPayload();
        this.resetPostedMultimediaBlogs();
        this.resetPostedMessageBlogs();
        this.lastReset = System.currentTimeMillis();
    }
    
    public long getLastMonitorReset() {
        return this.lastReset;
    }
    
    /***************************************************************************************/
    
    // serialize settings data for storage
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        
        dos.writeBoolean(this.forceLogin);
        dos.writeBoolean(this.forceSecureChannel);
        dos.writeBoolean(this.forceLocation);
        dos.writeInt(this.gpsDeviceTyp);
        
        dos.writeUTF(this.gpsDevice.getAddress());
        dos.writeUTF(this.gpsDevice.getAlias());
        dos.writeUTF(this.gpsDevice.getConnectionURL());
        
        dos.writeBoolean(this.forceBluetooth);
        dos.writeInt(this.photoSizeID);
        dos.writeInt(this.blogRelation);
        dos.writeInt(this.blogOrder);
        dos.writeInt(this.distanceUnit);
        dos.writeInt(this.deletedJourneyBlogs);
        dos.writeUTF(this.journeyName);
        
        dos.writeInt(this.transferedPayload);
        dos.writeInt(this.postedMultimediaBlogs);
        dos.writeInt(this.postedMessageBlogs);
        dos.writeLong(this.lastReset);
        
        dos.close(); // closes baos as well
        return baos.toByteArray();
    }
    
    // reconstruct settings data from byte stream
    public void unserialize(byte[] record) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(record);
        DataInputStream dis = new DataInputStream(bais);
        
        // read instance variables in same order as written out
        this.forceLogin = dis.readBoolean();
        this.forceSecureChannel = dis.readBoolean();
        this.forceLocation = dis.readBoolean();
        this.gpsDeviceTyp = dis.readInt();
        // reconstuct gps device
        this.gpsDevice = new BluetoothDevice(dis.readUTF(), dis.readUTF());
        if(this.gpsDevice != null)
            this.gpsDevice.setConnectionURL(dis.readUTF());
        this.forceBluetooth = dis.readBoolean();
        this.photoSizeID = dis.readInt();
        this.blogRelation = dis.readInt();
        this.blogOrder = dis.readInt();
        this.distanceUnit = dis.readInt();
        this.deletedJourneyBlogs = dis.readInt();
        this.journeyName = dis.readUTF();
        
        this.transferedPayload = dis.readInt();
        this.postedMultimediaBlogs = dis.readInt();
        this.postedMessageBlogs = dis.readInt();
        this.lastReset = dis.readLong();
        
        dis.close(); // closes bais as well
    }
}
