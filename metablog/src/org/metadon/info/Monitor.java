/*
 * Statistics.java
 *
 * Created on 16. November 2007, 02:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.metadon.info;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Form;

import org.metadon.beans.Settings;
import org.metadon.control.Controller;
import org.metadon.utils.ActivityAlert;
import org.metadon.utils.DateFormatter;


import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;


import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class Monitor {

    private Controller controller;
    private Settings settings;
    private Form trafficForm;
    
    private int totalBytes;
    private int postedBlogsPM;
    private int postedBlogsM;
    private long lastReset;
    
    private final Command CLOSE_CMD = new Command(Locale.get("cmd.close"), Command.BACK, 1);
    private final Command RESET_CMD = new Command(Locale.get("cmd.reset"), Command.SCREEN, 3);

    private final ActivityAlert activityScreen = new ActivityAlert(true);
     
    /** Creates a new instance of Statistics */
    public Monitor(Controller c) {
        this.controller = c;

        this.trafficForm = new Form(Locale.get("title.monitor"));
        this.trafficForm.addCommand(this.CLOSE_CMD);
        this.trafficForm.addCommand(this.RESET_CMD);
        
        //this.controller.show(activityScreen);

        this.trafficForm.setCommandListener(new CommandListener() {

            public void commandAction(Command cmd, Displayable d) {
                if (cmd == CLOSE_CMD) {
                    Displayable screen = controller.getScreen(controller.MAIN_SCR);
                    controller.show(screen);
                } else if (cmd == RESET_CMD) {
                    controller.show(controller.CONF_ASCR,
                    Locale.get("monitor.alert.reset"),
                    this,
                    null);
                } else if (cmd == controller.YES_CMD) {
                    reset();
                } else if (cmd == controller.NO_CMD) {
                    controller.show(trafficForm);
                }
            }
        });
    }

    public void show() {
        this.settings = this.controller.getSettings();
        this.totalBytes = this.settings.getTransferedPayload();
        this.postedBlogsPM = this.settings.getPostedMultimediaBlogs();
        this.postedBlogsM = this.settings.getPostedMessageBlogs();
        this.lastReset = this.settings.getLastMonitorReset();
        
        this.trafficForm.deleteAll();

        Hashtable traffic = this.getTrafficData();

        if (traffic != null) {
            this.appendItems(traffic);
            this.controller.show(this.trafficForm);
        }
    }

    private void appendItems(Hashtable traffic) {
        Enumeration keys = traffic.keys();

        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            Vector values = (Vector) traffic.get(key);
            Enumeration trafficValues = values.elements();

            //#style statisticsHeader
            this.trafficForm.append(key);

            while (trafficValues.hasMoreElements()) {
                //#style statisticsBody
                this.trafficForm.append(trafficValues.nextElement().toString());
            }
        }
    }

    // returns information about the current state of the RMS system
    private Hashtable getTrafficData() {
        Hashtable table = new Hashtable();

        try {
            Vector values = new Vector();

            // store name
            String name = Locale.get("monitor.label.postedBlogs");

            if (totalBytes < 1024) {
                values.addElement(new String(Locale.get("monitor.label.transferedPayload") + ": " + this.totalBytes + " bytes"));
            } else {
                values.addElement(new String(Locale.get("monitor.label.transferedPayload") + ": " + this.totalBytes / 1024 + " kb"));
            }
            values.addElement(new String(Locale.get("monitor.label.multimediaBlogs") + ": " + this.postedBlogsPM));
            values.addElement(new String(Locale.get("monitor.label.messageBlogs") + ": " + this.postedBlogsM));
            values.addElement(new String(Locale.get("monitor.label.lastReset") + ": " + DateFormatter.getUserFriendlyDate(this.lastReset)[0]+" "+DateFormatter.getUserFriendlyDate(this.lastReset)[1]));
            table.put(name, values);

            return table;
        } catch (Exception e) {
            //#debug error
            System.out.println("monitor: " + e);
            return null;
        }
    }
    
    private void reset() {
        this.settings.resetMonitor();
        controller.storeSettings(this.settings);
        // reload monitor
        controller.loadMonitor();
    }
    
    public ActivityAlert getActivityScreen() {
        return this.activityScreen;
    }
}
