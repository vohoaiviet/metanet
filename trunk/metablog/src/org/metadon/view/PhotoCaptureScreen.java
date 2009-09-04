/*
 * PhotoCaptureScreen.java
 *
 * Created on 05. November 2007, 18:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.view;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import org.metadon.control.PhotoManager;

import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class PhotoCaptureScreen extends Canvas implements CommandListener {
    
    private PhotoManager photoManager;
    public final Command CAPTURE_CMD = new Command(Locale.get("cmd.capture"), Command.ITEM, 1);
    public final Command CANCEL_CMD = new Command(Locale.get("cmd.cancel"), Command.ITEM, 10);
    
    private int status;
    public final static int STATUS_READY = 0;
    public final static int STATUS_INITIALIZING = 1;

    /** Creates a new instance of PhotoCaptureScreen */
    public PhotoCaptureScreen(PhotoManager pm) {
        //super(Locale.get("text.title.capture"));
        this.photoManager = pm;
        this.setFullScreenMode(true);
        
        this.addCommand(this.CAPTURE_CMD);
        this.addCommand(this.CANCEL_CMD);
        this.setCommandListener(this);
    }
     
    public void paint(Graphics g) {
            g.setColor(0xEEF1E5);
            g.fillRect(0,0,getWidth(),getHeight());
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public int getStatus() {
        return this.status;
    }
    
    public void commandAction(Command cmd, Displayable d) {
        if(cmd == this.CAPTURE_CMD && this.status == STATUS_READY) {
            this.photoManager.takeSnapshot();
        } else if(cmd == this.CANCEL_CMD) {
            this.photoManager.cancel();
        }
    }
    
}
