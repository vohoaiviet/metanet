/*
 * PlatformInfoCollector.java
 *
 * Created on 31. Oktober 2007, 13:00
 *
 */

package org.metadon.info;

import javax.microedition.lcdui.Display;

import org.metadon.control.Controller;


/**
 *
 * @author Hannes
 */
public class PlatformInfoCollector extends InfoCollector {
    
    /**
     * 
     * Creates a new instance of PlatformInfoCollector
     */
    public PlatformInfoCollector() {
    	super();
    }

    public void collectInfos(Controller controller, Display display) {
        
        String prop0 = System.getProperty("microedition.configuration");
        if (prop0 != null) {
            this.addInfo("Microedition Configuration: ", prop0);
        }
        String prop1 = System.getProperty("microedition.profiles");
        if (prop1 != null) {
            this.addInfo("Microedition Profiles: ", prop1);
        }
        String prop2 = System.getProperty("microedition.jtwi.version");
        if (prop2 != null) {
            this.addInfo("Microedition JTWI Version:", prop2);
        }
        String prop3 = System.getProperty("microedition.platform");
        if (prop3 != null) {
            this.addInfo("Microedition Platform:", prop3);
        }
        String prop4 = System.getProperty("microedition.hostname");
        if (prop4 != null) {
            this.addInfo("Microedition Hostname:", prop4);
        }
        String prop5 = System.getProperty("microedition.locale");
        if (prop5 != null) {
            this.addInfo("Microedition Locale:", prop5);
        }
        String prop6 = System.getProperty("microedition.encoding");
        if (prop6 != null) {
            this.addInfo("Default Encoding:", prop6);
        }
        String prop7 = System.getProperty("supports.video.capture");
        if(prop7 != null) {
            this.addInfo( "Video Capture Support:", prop7);
            String prop7_1 = System.getProperty("video.encodings");
            if(prop7_1 != null) {
                this.addInfo( "Video Encoding:", prop7_1);
            }
            String prop7_2 = System.getProperty("video.snapshot.encodings");
            if(prop7_2 != null) {
                this.addInfo( "Snapshot Encoding:", prop7_2);
            }
        } else {
            this.addInfo( "Video Capture Support:", "no");
        }
        String prop8 = System.getProperty("com.nokia.mid.imei");
        if(prop8 != null) {
            this.addInfo( "IMEI:", prop8);
        }
        String prop9 = Long.toString(Runtime.getRuntime().totalMemory()) + " bytes";
        if(prop9 != null) {
            this.addInfo( "Total Memory:", prop9);
        }
        String prop10 = Long.toString(Runtime.getRuntime().freeMemory()) + " bytes";
        if(prop10 != null) {
            this.addInfo( "Free Memory:", prop10);
        }
        String prop11 = java.util.TimeZone.getDefault().getID();
        if(prop11 != null) {
            this.addInfo( "Default Time Zone:", prop11);
        }
    }

}
