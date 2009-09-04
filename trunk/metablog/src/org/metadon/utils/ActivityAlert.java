/*
 * ActivityAlert.java
 *
 * Created on 19. November 2007, 16:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.utils;

import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class ActivityAlert extends Alert implements CommandListener {
    
    private Gauge gauge;
    // add empty dummy command to disable the implicit OK command with alerts
    private Command dummyCommand = new Command("", Command.ITEM, 0);
    
    /** Creates a new instance of ActivityAlert */
    public ActivityAlert(boolean showGauge) {
        //#style alertActivityScreen
        super(Locale.get("title.alert.activity"), null, null, AlertType.INFO);
        this.addCommand(dummyCommand);
        this.setCommandListener(this);
        
        if(showGauge) {
            this.gauge = new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
            this.setIndicator(this.gauge);
        }
        //this.setTimeout(20000);
    }
    
    // dummy listener
    public void commandAction(Command cmd, Displayable disp) {}
    
}
