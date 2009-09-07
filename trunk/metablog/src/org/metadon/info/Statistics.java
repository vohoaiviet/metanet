/*
 * Statistics.java
 *
 * Created on 16. November 2007, 02:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.info;

import javax.microedition.rms.*;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import org.metadon.beans.Settings;
import org.metadon.control.BlogManager;
import org.metadon.control.Controller;
import org.metadon.control.RecordManager;
import org.metadon.location.TraceManager;
import org.metadon.utils.DateFormatter;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;


import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class Statistics {
    
    private Controller controller;
    private RecordManager recordManager;
    private BlogManager blogManager;
    private TraceManager traceManager;
    private Settings settings;
     
    private RecordStore blogPMRS;
    private RecordStore blogMRS;
    
    private Form statisticsForm;
    
    private String journeyName;
    private int numJourneyBlogs;
    
    private final int RMS_JOURNEY_STATISTICS = 0;
    private final int RMS_SUM_STATISTICS = 1;
    
    private final String BLOG_PM_STORE = Locale.get("recordManager.store.PM");
    private final String BLOG_M_STORE = Locale.get("recordManager.store.M");
    
    private final Command CLOSE_CMD = new Command(Locale.get("cmd.close"), Command.BACK, 1);
    private final Command EDIT_CMD = new Command(Locale.get("cmd.editName"), Command.SCREEN, 3);
    private final Command OK_CMD = new Command(Locale.get("cmd.ok"), Command.OK, 3);
     
    private TextField journeyNameField = new TextField(
                null,
                null,
                20,
                TextField.ANY | TextField.UNEDITABLE);
    
    
    /** Creates a new instance of Statistics */
    public Statistics(Controller c) {
        this.controller = c;
        this.recordManager = RecordManager.getInstance(this.controller);
        this.traceManager = TraceManager.getInstance(this.controller);
        this.blogManager = BlogManager.getInstance(this.controller);
        this.settings = this.controller.getSettings();
        this.journeyName = this.settings.getJourneyName();
        
        this.statisticsForm = new Form(Locale.get("title.statistics"));
        this.statisticsForm.addCommand(this.CLOSE_CMD);
        
        this.statisticsForm.setCommandListener(new CommandListener() {
            public void commandAction(Command cmd, Displayable d) {
                if(cmd == CLOSE_CMD) {
                    statisticsForm.removeCommand(OK_CMD);
                    statisticsForm.addCommand(EDIT_CMD);
                    Displayable screen = controller.getScreen(controller.MAIN_SCR);
                    controller.show(screen);
                } else if(cmd == EDIT_CMD) {
                    journeyNameField.setConstraints(TextField.ANY);
                    statisticsForm.removeCommand(EDIT_CMD);
                    statisticsForm.addCommand(OK_CMD);
                } else if(cmd == OK_CMD) {
                    journeyNameField.setConstraints(TextField.UNEDITABLE);
                    // check if store update is necessary
                    if(!journeyNameField.getString().equals(journeyName)) {
                        journeyName = journeyNameField.getString();
                        settings.setJourneyName(journeyName);
                        controller.storeSettings(settings);
                    }
                    statisticsForm.removeCommand(OK_CMD);
                    statisticsForm.addCommand(EDIT_CMD);
                }
            }
        });
    }
    
    public void show() {
        this.settings = this.controller.getSettings();
        this.statisticsForm.deleteAll();
        this.numJourneyBlogs = 0;
        
        // create statistics
        Hashtable journeyStatistics = this.getRMSStatistics(new Integer(this.RMS_JOURNEY_STATISTICS));
        Hashtable summaryStatistics = this.getRMSStatistics(new Integer(this.RMS_SUM_STATISTICS));
        Hashtable blogTypeStatistics = this.getRMSStatistics(null);
        
        // set journey name
        journeyNameField.setString(this.journeyName);
        journeyNameField.setConstraints(TextField.UNEDITABLE);
        
        // check if journey name field should be editable
        if(this.settings.getBlogRelation() == this.settings.BLOG_RELATION_JOURNEY || this.numJourneyBlogs > 0) {
            this.statisticsForm.addCommand(this.EDIT_CMD);
        }
        if(this.numJourneyBlogs == 0) {
            this.journeyNameField.setString(Locale.get("statistics.defaultValue.journeyName"));
            this.statisticsForm.removeCommand(this.EDIT_CMD);
        }
                
        //#style journeyNameTextField
        this.statisticsForm.append(this.journeyNameField);
        
        if(journeyStatistics != null &&
           summaryStatistics != null &&
           blogTypeStatistics != null) {
            this.appendItems(journeyStatistics);
            this.appendItems(summaryStatistics);
            this.appendItems(blogTypeStatistics);
            this.controller.show(this.statisticsForm);
        }
    }
    
    private void appendItems(Hashtable statistics) {
        Enumeration keys = statistics.keys();
        
        while(keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            Vector values = (Vector)statistics.get(key);
            Enumeration statValues = values.elements();
            
            //#style statisticsHeader
            this.statisticsForm.append(key);
            
            while(statValues.hasMoreElements()) {
                //#style statisticsBody
                this.statisticsForm.append(statValues.nextElement().toString());
            }
        }
    }
    
     // returns information about the current state of the RMS system
    private Hashtable getRMSStatistics(Integer type) {
        Hashtable table = new Hashtable();
        
        // open multimedia blog store
        this.blogPMRS = this.recordManager.openRecordStore(this.BLOG_PM_STORE);
        if(this.blogPMRS == null) return null;
        
        // open message blog store
        this.blogMRS = this.recordManager.openRecordStore(this.BLOG_M_STORE);
        if(this.blogMRS == null) return null;
        
        RecordStore[] stores = new RecordStore[2];
        stores[0] = this.blogPMRS;
        stores[1] = this.blogMRS;
                
        try {
            if(type == null) {
                for(int i = 0; i < stores.length; i++) {
                    Vector values = new Vector();
                    // store name
                    String storeName = stores[i].getName();
                    // number of stored records
                    values.addElement(new String(Locale.get("statistics.label.blogs")+": "+stores[i].getNumRecords()));
                    
                    // current store size
                    int storeSize = stores[i].getSize();
                    if(storeSize < 1024) {
                        values.addElement(new String(Locale.get("statistics.label.currentSize")+": "+storeSize+" bytes"));
                    } else {
                        values.addElement(new String(Locale.get("statistics.label.currentSize")+": "+storeSize/1024+" kb"));
                    }
                    // last modification
                    String[] date = DateFormatter.getUserFriendlyDate(stores[i].getLastModified());
                    values.addElement(new String(Locale.get("statistics.label.lastModification")+":\n"+date[0]+" "+date[1]));
                    table.put(storeName, values);
                }
                
            } else if(type.intValue() == this.RMS_SUM_STATISTICS){
                Vector values = new Vector();
                int blogs = 0;
                int storeSize = 0;
                
                // store name
                String name = Locale.get("statistics.label.storedBlogsSummary");
               
                // summary of stored blogs
                for(int i = 0; i < stores.length; i++) {
                    // number of stored records
                    blogs += stores[i].getNumRecords();
                    storeSize += stores[i].getSize();
                }
                values.addElement(new String(Locale.get("statistics.label.blogs")+": "+blogs));
                
                // summary of stored waypoints (blogs with location information)
                int numWaypoints = this.blogManager.getWaypointCount();
                values.addElement(new String(Locale.get("statistics.label.waypoints")+": "+numWaypoints));
                
                // storage size information
                if(storeSize < 1024) {
                    values.addElement(new String(Locale.get("statistics.label.currentSize")+": "+storeSize+" bytes"));
                } else {
                    values.addElement(new String(Locale.get("statistics.label.currentSize")+": "+storeSize/1024+" kb"));
                }
                int availableSize = stores[0].getSizeAvailable()/1024;
                values.addElement(new String(Locale.get("statistics.label.availableSize")+": "+availableSize+" kb"));
                table.put(name, values);
                
            } else if(type.intValue() == this.RMS_JOURNEY_STATISTICS){
                Vector values = new Vector();
                
                // store name
                String name = Locale.get("statistics.label.storedBlogsJourney");

                // total journey distance
                values.addElement(new String(Locale.get("statistics.label.journeyDistance")+": "+this.traceManager.getJourneyDistance()));
                // total journey time
                values.addElement(new String(Locale.get("statistics.label.journeyTime")+": "+this.traceManager.getJourneyDuration()));
                
                // summary of stored journey blogs
                this.numJourneyBlogs = this.blogManager.getJourneyBlogCount();
                values.addElement(new String(Locale.get("statistics.label.blogs")+": "+this.numJourneyBlogs));
                
                // summary of stored journey waypoints (journey blogs with location information)
                int numWaypoints = this.traceManager.getJourneyWaypointCount();
                values.addElement(new String(Locale.get("statistics.label.waypoints")+": "+numWaypoints));
                
                table.put(name, values);
            }
            return table;
        } catch(Exception e) {
            //#debug error
            System.out.println("get rms statistics: "+e);
            return null;
        }
    }
    
    public String getJourneyName() {
        return this.journeyName;
    }
    
}
