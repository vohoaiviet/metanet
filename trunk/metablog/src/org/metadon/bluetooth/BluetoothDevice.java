/*
 * BluetoothDevice.java
 *
 * Created on 05. Dezember 2007, 14:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.bluetooth;

import javax.bluetooth.RemoteDevice;

/**
 *
 * @author Hannes
 */
public class BluetoothDevice {
    
    private String address = "n/a";
    private String alias = "n/a";
    private RemoteDevice remoteDevice;
    private String connectionURL = "n/a";
    
    /** Creates a new instance of BluetoothDevice */
    public BluetoothDevice(String address, String alias) {
        this.address = address;
        this.alias = alias;
    }
    
    public String getAddress() {
        return this.address;
    }
    
    public String getAlias() {
        return this.alias;
    }
    
    // remote device as discovered by the system (not serialized)
    public void setRemoteDevice(RemoteDevice device) {
        this.remoteDevice = device;
    }
    
    public RemoteDevice getRemoteDevice() {
        return this.remoteDevice;
    }
    
    // the connection url stores the bt address, the channel number and authentication and security
    // information specific for this device
    // e.g. "btspp://0050CD00321B:3;authenticate=true;encrypt=false;master=true"
    public void setConnectionURL(String url) {
        this.connectionURL = url;
    }
    
    public String getConnectionURL() {
        return this.connectionURL;
    }
    
}