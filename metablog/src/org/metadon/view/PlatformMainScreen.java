/*
 * PlatformMainScreen.java
 *
 * Created on 30. Oktober 2007, 19:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.view;


import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import org.metadon.control.Controller;
import org.metadon.info.BluetoothInfoCollector;
import org.metadon.info.DisplayInfoCollector;
import org.metadon.info.FileConnectionInfoCollector;
import org.metadon.info.LibrariesInfoCollector;
import org.metadon.info.PlatformInfoCollector;

import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class PlatformMainScreen extends List implements CommandListener {
    
    private Controller controller;
    private final Command BACK_CMD = new Command(Locale.get("cmd.back"), Command.BACK, 1);
    private boolean btAvailable = false;
    private boolean fcAvailable = false;    
    
    /** Creates a new instance of CapabScreen */
    public PlatformMainScreen(Controller c) {
        super(Locale.get("title.platform"), List.IMPLICIT);
        this.controller = c;
        
        //#style systemScreenItem
        this.append(Locale.get("menu.platform.generic"), null);
        //#style systemScreenItem
        this.append(Locale.get("menu.platform.display"), null);
        //#style systemScreenItem
    	this.append(Locale.get("menu.platform.libraries"), null);
        
        if(new LibrariesInfoCollector().bluetoothAPIAvailable()) {
            this.btAvailable = true;
            //#style systemScreenItem
            this.append(Locale.get("menu.platform.bluetooth"), null);
        }
        if(new LibrariesInfoCollector().fileconnectionAPIAvailable()) {
            this.fcAvailable = true;
            //#style systemScreenItem
            this.append(Locale.get("menu.platform.fileConnection"), null);
        }
        this.addCommand(this.BACK_CMD);
        this.setCommandListener(this);
    }
    
     public void commandAction(Command cmd, Displayable disp)
    {
        Displayable screen = null;
        if (cmd == List.SELECT_COMMAND) {
            int selectedItem = this.getSelectedIndex();
            
            switch (selectedItem)
            {
                // platform
		case 0:
                    this.controller.show(Locale.get("menu.platform.generic"), new PlatformInfoCollector());
                    break;
                // display
		case 1:
                    this.controller.show(Locale.get("menu.platform.display"), new DisplayInfoCollector());
                    break;
                // libraries
		case 2:
                    this.controller.show(Locale.get("menu.platform.libraries"), new LibrariesInfoCollector());
                    break;
                // bluetooth
                case 3:
                    if(btAvailable) {
                        this.controller.show(Locale.get("menu.platform.bluetooth"), new BluetoothInfoCollector());
                        break;
                    }
                // fileconnection
                case 4:
                    if(fcAvailable) {
                        this.controller.show(Locale.get("menu.platform.fileConnection"), new FileConnectionInfoCollector());
                        break;
                    }
		default:
            }
        } else if (cmd == this.BACK_CMD) {
            screen = this.controller.getScreen(this.controller.MAIN_SCR);
            if(screen != null) {
                this.controller.show(screen);
            }
        }
    }
    
}
