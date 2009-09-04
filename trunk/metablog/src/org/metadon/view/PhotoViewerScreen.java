/*
 * PhotoViewerScreen.java
 *
 * Created on 07. November 2007, 14:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.view;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import org.metadon.control.PhotoManager;

import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class PhotoViewerScreen extends Form implements CommandListener {
    
    private PhotoManager photoManager;
    
    private final Command RESUME_CMD = new Command(Locale.get("cmd.resume"), Command.ITEM, 1);
    private final Command DISCARD_CMD = new Command(Locale.get("cmd.discard"), Command.ITEM, 5);
    private final Command CANCEL_CMD = new Command(Locale.get("cmd.cancel"), Command.ITEM, 10);
    
    /** Creates a new instance of PhotoViewerScreen */
    public PhotoViewerScreen(PhotoManager pm) {
        //#style photoViewerScreen
        super(Locale.get("title.preview"));
        this.photoManager = pm;
        
        this.addCommand(this.RESUME_CMD);
        this.addCommand(this.DISCARD_CMD);
        this.addCommand(this.CANCEL_CMD);
        this.setCommandListener(this);
    }
    
    public void commandAction(Command cmd, Displayable d) {
        if(cmd == this.RESUME_CMD) {
            //#debug
//#             System.out.println("resume");
            this.photoManager.finished();
        } else if(cmd == this.DISCARD_CMD) {
            //#debug
//#             System.out.println("show video again");
            this.photoManager.restart();
        } else if(cmd == this.CANCEL_CMD) {
             //#debug
//#              System.out.println("canceled");
             this.photoManager.cancel();
        }
    }
}
