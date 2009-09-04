/*
 * BluetoothManager.java
 *
 * Created on 05. Dezember 2007, 13:22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.extern.bluetooth;

import javax.bluetooth.*;

import org.metadon.control.Controller;
import org.metadon.utils.ActivityAlert;

import java.util.Vector;
import java.io.IOException;

import de.enough.polish.util.Locale;

import java.util.TimerTask;
import java.util.Timer;

/**
 *
 * @author Hannes
 */
public class BluetoothManager implements DiscoveryListener {
   
    private volatile static BluetoothManager uniqueInstance;
    private Controller controller;
    private LocalDevice localDevice;
    private DiscoveryAgent discoveryAgent;
    private BluetoothDeviceList bluetoothDeviceList;
    
    // collects the remote bluetooth devices found during a search
    private Vector remoteDevices;
    // collects the services found on discovered devices
    private Vector remoteServices;
    // collects the discovered and formatted remote bluetooth devices
    private Vector remoteBTDevices;
   
    private BluetoothDevice selectedDevice;
    private Timer timeoutNotifier;
    private ActivityAlert activityScreen;
    private boolean deviceSearchComplete;
    private boolean serviceSearchComplete;
    private int context;
    private int searchTransID;
    
    public final int GPS_DEVICE = 0;
    public final int GATEWAY_DEVICE = 1;
    private final int DEVICE_SEARCH_TIMEOUT = 20000;
    private final int SERVICE_SEARCH_TIMEOUT = 20000;
    
    
    public static synchronized BluetoothManager getInstance(Controller c) {
        if(uniqueInstance == null) {
            //synchronize concurrent threads on creating a instance (performance decrease!)
            synchronized (BluetoothManager.class) {
                if(uniqueInstance == null) {
                    uniqueInstance = new BluetoothManager(c);
                }
            }
        }
        return uniqueInstance;
    }
    
    private BluetoothManager(Controller c) {
        this.controller = c;
        this.deviceSearchComplete = false;
        this.serviceSearchComplete = false;
        
        this.activityScreen = new ActivityAlert(true);
        this.bluetoothDeviceList = new BluetoothDeviceList(this.controller, this);
    }
    
    public void searchEnvironment(int context) {
        this.context = context;
        this.init();
        this.bluetoothDeviceList.update();
    }
        
    public void init() {
        localDevice = null;
        discoveryAgent = null;
        try {
            // retrieve the local device to get to the bluetooth manager
            localDevice = LocalDevice.getLocalDevice();
            // servers set the discoverable mode to GIAC
            localDevice.setDiscoverable(DiscoveryAgent.GIAC);
        } catch(BluetoothStateException bse) {
            //#debug error
            System.out.println("get local bt device: "+bse);
        }
        // clients retrieve the discovery agent
        discoveryAgent = localDevice.getDiscoveryAgent();
    }
    
    public void searchDevices() {
        this.remoteDevices = new Vector();
        this.remoteServices = new Vector();
        this.remoteBTDevices = new Vector();
        
        try {
            this.deviceSearchComplete = false;
            // schedule timoutNotifier to cancel inquiry after a specified period if not finished
            this.scheduleTimeoutNotifier();
            // user feedback
            this.activityScreen.setString(Locale.get("bluetoothManager.alert.searching"));
            this.controller.show(this.activityScreen);
            // start bluetooth devices search
            this.discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this);
            
        } catch (BluetoothStateException bse) {
            //#debug error
            System.out.println("search bt devices: "+bse);
        }
    }

    public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {

        // same device may found several times during single search
        if (this.remoteDevices.indexOf(deviceClass) == -1) {
            String address = remoteDevice.getBluetoothAddress();
            String name = null;
            try {
                // query remote device for it's name (if not already known)
                name = remoteDevice.getFriendlyName(false);
            } catch (IOException ioe) {}
            // when buggy bluetooth stack (known problem on some nokia devices),
            // show at least bluetooth address in the device list
            if (name == null || name.trim().length() == 0) {
                try {
                    // try again
                    name = remoteDevice.getFriendlyName(true);
                } catch (IOException ioe) {}
                if (name == null || name.trim().length() == 0) {
                    // give up
                    name = address;
                }
            }
            ////#debug
            System.out.println("bt device found: "+name+" ("+address+")");
            this.remoteDevices.addElement(remoteDevice);
            
            // format device as bluetooth device
            BluetoothDevice remoteBTDevice = new BluetoothDevice(address, name);
            remoteBTDevice.setRemoteDevice(remoteDevice);
            this.remoteBTDevices.addElement(remoteBTDevice);
        }
    }
    
    // search for specified services on the discovered devices
    private void searchServices(RemoteDevice device) {
        if(device == null) return;
        this.serviceSearchComplete = false;
        this.scheduleTimeoutNotifier();
        
        UUID[] uuidSet = new UUID[1];
        // service record of interest
        uuidSet[0] = new UUID(0x0100); // L2CAP service (used by gps devices)
//      uuidSet[1] = new UUID(0x0001); // SDP
//      uuidSet[2] = new UUID(0x0003); // RFCOMM
//      uuidSet[3] = new UUID(0x1101); // serial port
//      ...
        
       /*
        * service record attributes of interest
        *
        * Service search will always give the default attributes:
        * ServiceRecordHandle (0x0000), ServiceClassIDList (0x0001),
        * ServiceRecordState (0x0002), ServiceID (0x0003) and
        * ProtocolDescriptorList (0x004).
        *
        * We want additional attributes, ServiceName (0x100),
        * ServiceDescription (0x101) and ProviderName (0x102).
        *
        * These hex-values must be supplied through an int array
        */
        int[] attr = {0x100,0x101,0x102};
        try {
            // search on the remote device (SDP server) service discovery database SDDB
            // for the specified services
            this.searchTransID = this.discoveryAgent.searchServices(null, uuidSet, device, this);
        } catch (BluetoothStateException bse) {
        /* Thrown when: 
         * - the number of concurrent service search transactions exceeds the limit
         *   specified by the bluetooth.sd.trans.max property obtained from the
         *   class LocalDevice.
         * - the system is unable to start one due to current conditions
         * + IllegalArgumentException - if attrSet has an illegal service attribute
         *   ID or exceeds the property bluetooth.sd.attr.retrievable.max defined
         *   in the class LocalDevice; if attrSet or uuidSet is of length 0;
         *   if attrSet or uuidSet contains duplicates
         * + NullPointerException - if uuidSet, btDev, or discListener is null;
         *   if an element in uuidSet array is null
         */
            //#debug error
            System.out.println("search transactions exceeds 'bluetooth.sd.trans.max property'");
        }
    }

    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        ////#debug
        System.out.println("service record length: "+servRecord.length);
        for(int j = 0; j < servRecord.length; j++){
            this.remoteServices.addElement(servRecord[j]);
        }
    }

    public void serviceSearchCompleted(int transID, int respCode) {
        this.timeoutNotifier.cancel();
        String status = "n/a";
        
        if (respCode == DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE) {
            status = "device not reachable.";
        }
        else if(respCode == DiscoveryListener.SERVICE_SEARCH_NO_RECORDS) {
            status = "service not available.";
        }
        else if (respCode == DiscoveryListener.SERVICE_SEARCH_COMPLETED) {
            status = "service search complete.";
            Vector services = this.getServices();
            ////#debug
            System.out.println("services found: "+services.size());
            
            for(int j = 0; j < services.size(); j++){
                ServiceRecord service = (ServiceRecord)services.elementAt(j);
                String connURL = service.getConnectionURL(0, false);
                // TODO: manage more services and store in BluetoothDevice
                // GPS receiver provides only one service
                this.selectedDevice.setConnectionURL(connURL);
                // on a server the connection url can by retrieved by: LocalDevice.getRecord()
                 
                
                /* populateRecord(id) is blocking forever on tested gps receiver (GNS).
                 * however, further service information is not mandatory,
                 * since the connection url is sufficient to connect and use the gps service
                 */
                 
                /*  retrieve the attribute values for the given attribute id
                 *  
                 *  the system might impose a limit on the number of service attribute ID values
                 *  one can request at a time. one can determine this behavior by quering
                 *  LocalDevice.getProperty("bluetooth.sd.attr.retrievable.max"). to be sure, only
                 *  perform one value request at a time.
                 */
//                int[] attrIDSet = service.getAttributeIDs();
//                try {
//                    boolean success = service.populateRecord(attrIDSet);
//                    if(success) {
//                        for(int i = 0; i < attrIDSet.length; i++) {
//                        DataElement value = service.getAttributeValue(attrIDSet[i]);
//                        //#debug
//                        System.out.println("attrID "+attrIDSet[i]+": "+value.getValue().toString());
//                        }
//                    }
//                } catch(IOException ioe) {
//                  //#debug error
//                  System.out.println("populateRecord: "+ioe);
//                }
             }
        }
        else if(respCode == DiscoveryListener.SERVICE_SEARCH_TERMINATED) {
            status = "service search canceled.";
        }
        else if (respCode == DiscoveryListener.SERVICE_SEARCH_ERROR) {
            //#debug error
            System.out.println("service search error.");
        }
        ////#debug
        System.out.println(status);
        this.serviceSearchComplete = true;
    }
    
     // check search status
    public boolean isDevice() {
        return deviceSearchComplete;
    }
    
    // get discovered devices
    public Vector getDevices() {
        return this.remoteBTDevices;
    }
    
    // get discovered services
    public Vector getServices() {
        return this.remoteServices;
    }
    
    public void setSelectedDevice(BluetoothDevice device) {
        this.selectedDevice = device;
        if(this.context == this.GPS_DEVICE) {
            // search services on selected bluetooth device and determine how to connect the device
            this.searchServices(this.selectedDevice.getRemoteDevice());
            while(!this.serviceSearchComplete) {
                try {
                    Thread.sleep(100);
                } catch(Exception e) {}
            }
            // check if selected device has a valid service connection url
            if(this.selectedDevice.getConnectionURL() != null) {
                // remember device (store in RMS database)
                this.controller.storeAsDefaultBTD(this.context, this.selectedDevice);
            } else {
                // user feedback
                this.controller.show(this.controller.ERROR_ASCR, Locale.get("bluetoothManager.alert.serviceNotFound"), null, null);
            }
        } else if(this.context == this.GATEWAY_DEVICE) {
            // TODO.
        }
    }
    
    public void inquiryCompleted(int discType) {
        switch (discType) {
            // Inquiry completed normally
            case DiscoveryListener.INQUIRY_COMPLETED:
                this.timeoutNotifier.cancel();
                this.deviceSearchComplete = true;
                ////#debug
                System.out.println("device search complete.");
                ////#debug
                System.out.println("devices found: "+this.remoteBTDevices.size());
                break;
            // Inquiry terminated through agent.cancelInquiry()
            case DiscoveryListener.INQUIRY_TERMINATED:
                this.deviceSearchComplete = true;
                //#debug
//#                 System.out.println("device search canceled.");
                break;
            // Error during inquiry
            case DiscoveryListener.INQUIRY_ERROR:
                //#debug error
                System.out.println("device search error.");
        }
    }
    
    private void cancelDeviceSearch() {
        if(this.discoveryAgent == null) return;
        this.discoveryAgent.cancelInquiry(this);
    }
    
    private void cancelServiceSearch() {
        if(this.discoveryAgent == null) return;
        this.discoveryAgent.cancelServiceSearch(this.searchTransID);
    }
    
    private void scheduleTimeoutNotifier() {
        this.timeoutNotifier = new Timer();
        if (!this.deviceSearchComplete) {
            this.timeoutNotifier.schedule(new InquiryTimeoutNotifier(), this.DEVICE_SEARCH_TIMEOUT);
        } else if(!this.serviceSearchComplete) {
            this.timeoutNotifier.schedule(new InquiryTimeoutNotifier(), this.SERVICE_SEARCH_TIMEOUT);
        }
    }
    
    // cancel active inquiry
    private class InquiryTimeoutNotifier extends TimerTask 
    {
        public void run(){
            if (!deviceSearchComplete) {
              // device search not completed yet, so cancel it
              cancelDeviceSearch();
            }
            if (!serviceSearchComplete) {
              // service search not completed yet, so cancel it
              cancelServiceSearch();
            }
        }
    }
    
}
