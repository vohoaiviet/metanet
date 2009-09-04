/*
 * PlatformInfoScreen.java
 *
 * Created on 31. Oktober 2007, 12:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.view;

import javax.microedition.lcdui.Form;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import org.metadon.beans.PlatformInfo;
import org.metadon.control.Controller;
import org.metadon.info.InfoCollector;

import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class PlatformInfoScreen extends Form implements CommandListener {
    
    private Controller controller;
    private final Command CLOSE_CMD = new Command(Locale.get("cmd.close"), Command.BACK, 1);
    
    /**
     * Creates a new instance of PlatformInfoScreen
     */
    public PlatformInfoScreen(Controller c, String title, InfoCollector infoCollector ) {
        super(title);
        this.controller = c;
        PlatformInfo[] infos = infoCollector.getInfos();
        for (int i = 0; i < infos.length; i++) {
            PlatformInfo info = infos[i];
            String label = new String(info.name);
            String value = new String(info.value);
            //#style infoLabel
            this.append(label);
            //#style infoValue
            this.append(value);
        }
        this.addCommand(this.CLOSE_CMD);
        this.setCommandListener(this);
    }
    
     public void commandAction(Command cmd, Displayable disp)
    {
         Displayable screen = null;
         if (cmd == this.CLOSE_CMD) {
            screen = this.controller.getScreen(this.controller.SYST_SCR);
            if(screen != null) {
                this.controller.show(screen);
            }
        }
    }
}
