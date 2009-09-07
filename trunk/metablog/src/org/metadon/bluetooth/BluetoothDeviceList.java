/*
 * BluetoothDeviceList.java
 *
 * Created on 06. Dezember 2007, 01:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.bluetooth;

import java.util.Vector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import org.metadon.control.Controller;

import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class BluetoothDeviceList extends List implements Runnable, CommandListener {
    
    private Controller controller;
    private BluetoothManager bluetoothManager;
   
    private final static int STATUS_READY = 0;
    private final static int STATUS_SEARCHING = 1;
    private final static int STATUS_FINISHED = 2;
    
    private Thread searchThread;
    private int status;
    private boolean active;
    
    private Command updateCommand = new Command(Locale.get("cmd.update"), Command.ITEM, 5);
    private Command discardCommand = new Command(Locale.get("cmd.discard"), Command.SCREEN, 8);
    
    
    public BluetoothDeviceList(Controller c, BluetoothManager bm) {
        super(Locale.get("title.btDeviceList"), List.IMPLICIT);
        this.controller = c;
        this.bluetoothManager = bm;

        this.addCommand(updateCommand);
        this.addCommand(discardCommand);
        this.setCommandListener(this);
        this.status = STATUS_READY;
    }
    
    public void update() {
        if(this.searchThread == null) {
            this.searchThread = new Thread(this);
            this.active = true;
        }
        this.status = STATUS_READY;
        this.searchThread.start();
    } 
    
    public void run() {
        while(this.active) {
            if(this.status == STATUS_READY) {
                //#debug
//#                 System.out.println("Searching BT devices...");

                this.status = STATUS_SEARCHING;
                this.bluetoothManager.searchDevices();
                while(!this.bluetoothManager.isDevice()) {
                    try {
                        Thread.sleep(100);
                    } catch(Exception e) {}
                }
                //#debug
//#                 System.out.println("device search complete.");

                // update device list
                Vector devices = this.bluetoothManager.getDevices();
                this.deleteAll();
                if(devices != null && devices.size() > 0) {
                    //#debug
//#                     System.out.println("devices found: "+devices.size());
                    for(int i = 0; i < devices.size(); i++) {
                        BluetoothDevice device = (BluetoothDevice)devices.elementAt(i);
                        //#style btDeviceListItem
                        this.append(device.getAlias(), null);
                    }
                }
                this.controller.show(this);
                this.status = STATUS_FINISHED;
            }
            try {
                Thread.sleep(100);
            } catch(Exception e) {/*ignore*/}
        }
        //#debug
//#         System.out.println("Searching thread died.");
    }
       
    /** Get selected bluetooth device */
    public BluetoothDevice getSelectedDevice() {
        BluetoothDevice selectedDevice = null;
        int selectedIndex = this.getSelectedIndex();
        String selectedDeviceAlias = this.getString(selectedIndex);

        Vector devices = this.bluetoothManager.getDevices();
        for(int i = 0; i < devices.size(); i++) {
            BluetoothDevice dev = (BluetoothDevice)devices.elementAt(i);
            String devAlias = dev.getAlias();
            if(selectedDeviceAlias.equals(devAlias)) {
                selectedDevice = dev;
            }
        }      
        return selectedDevice;
    }
    
    private void cleanUp() {
        this.active = false;
        this.searchThread = null;
    }

    public void commandAction(Command cmd, Displayable disp) {
        if(cmd == List.SELECT_COMMAND && disp == this) {
            BluetoothDevice selectedDevice = this.getSelectedDevice();
            if(selectedDevice != null) {
                this.bluetoothManager.setSelectedDevice(selectedDevice);
            }
            this.cleanUp();
        } else if(cmd == this.updateCommand) {
            this.cleanUp();
            this.update();
        } else if(cmd == this.discardCommand) {
            this.cleanUp();
            Displayable screen = this.controller.getScreen(this.controller.MAIN_SCR);
            this.controller.show(screen);
        } 
    }
    
    
}