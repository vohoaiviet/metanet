/*
 * FileConnectionInfoCollector.java
 *
 * Created on 31. Oktober 2007, 17:18
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.info;

import javax.microedition.lcdui.Display;

import org.metadon.control.Controller;

/**
 *
 * @author Hannes
 */
public class FileConnectionInfoCollector extends InfoCollector {
    
    /** Creates a new instance of FileConnectionInfoCollector */
    public FileConnectionInfoCollector() {
        super();
    }
    
    public void collectInfos(Controller controller, Display display) { 
    
        if(this.memoryCardAvailable()) {
            this.addInfo("Memory Card: ", "yes" );
            this.addInfo("Memory Card (Name): ", System.getProperty("fileconn.dir.memorycard.name"));
            this.addInfo("Memory Card (Root): ", System.getProperty("fileconn.dir.memorycard"));
        } else {
            this.addInfo("Memory Card: ", "no");
        }
        this.addInfo("Photo Directory: ", System.getProperty("fileconn.dir.photos"));  
        this.addInfo("Video Directory: ", System.getProperty("fileconn.dir.videos"));  
        this.addInfo("Audio Directory: ", System.getProperty("fileconn.dir.tones")); 
         
    }   
    
    public boolean memoryCardAvailable() {
        if(System.getProperty("fileconn.dir.memorycard") != null) {
            return true;
        }
        else {
            return false;
        }
    }

    
}
