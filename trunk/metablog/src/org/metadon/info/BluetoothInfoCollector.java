/*
 * BluetoothInfoCollector.java
 *
 * Created on 31. Oktober 2007, 16:29
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.info;

import javax.microedition.lcdui.Display;
import javax.bluetooth.*;

import org.metadon.control.Controller;

/**
 * 
 * @author Hannes
 */
public class BluetoothInfoCollector extends InfoCollector {

	private static String btAddress;
	private LocalDevice localDevice = null;

	/** Creates a new instance of BluetoothInfoCollector */
	public BluetoothInfoCollector() {
		super();
		try {
			this.localDevice = LocalDevice.getLocalDevice();
		} catch (BluetoothStateException e) {
		}
	}

	public void collectInfos(Controller controller, Display display) {

		this.btAddress = this.getLocalBluetoothAddress();
		if (this.btAddress != null) {
			this.addInfo("Address: ", this.btAddress);
		}
		if (this.localDevice != null) {
			String prop0 = this.localDevice
					.getProperty("bluetooth.connected.devices.max");
			if (prop0 != null) {
				this.addInfo("Conc. connected Devices: ", prop0);
			}
			String prop1 = this.localDevice
					.getProperty("bluetooth.master.switch");
			if (prop1 != null) {
				this.addInfo("Master/Slave switch: ", prop1);
			}
			String prop2 = this.localDevice
					.getProperty("bluetooth.connected.inquiry");
			if (prop2 != null) {
				this.addInfo("Inquiry while connected: ", prop2);
			}
			String prop3 = this.localDevice
					.getProperty("bluetooth.sd.trans.max");
			if (prop3 != null) {
				this.addInfo("Conc. Service Disc. Transactions: ", prop3);
			}
			String prop4 = this.localDevice
					.getProperty("bluetooth.sd.attr.retrievable.max");
			if (prop4 != null) {
				this.addInfo("Conc. retrievable Attributes: ", prop4);
			}
		}
	}

	public static String getLocalBluetoothAddress() {

		//#if ${config.emulate}
			btAddress = "1234567890_fake";
		//#else
			try {
				btAddress = LocalDevice.getLocalDevice().getBluetoothAddress()
						.toUpperCase();
			} catch (BluetoothStateException e) {
				// #debug error
				System.out.println("Error getting Local Bluetooth Device" + e);
			}
		//#endif
		return btAddress;
	}

}
