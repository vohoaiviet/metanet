/*
 * BlogInfoScreen.java
 *
 * Created on 13. Dezember 2007, 15:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.view;

import de.enough.polish.ui.TabbedForm;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.ScreenStateListener;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.ImageItem;
import javax.microedition.lcdui.StringItem;
import java.util.Vector;


/**
 *
 * @author Hannes
 */
public class BlogInfoScreen extends TabbedForm implements ScreenStateListener {
    
    private String[] tabNames;
    
    /** Creates a new instance of BlogInfoScreen */
    public BlogInfoScreen(String[] tabNames, Image[] tabIcons) {
        super(null, null, tabIcons);
        this.tabNames = tabNames;
        this.setScreenStateListener(this);
        
        this.setTitle(this.tabNames[0]);
    }
    
    public void setBlogInfoItems(int tab, Vector categoryInfo, boolean binary) {
        for(int i = 0; i < categoryInfo.size(); i++) {
            if(!binary || (binary && i != 0)) {
                String[] itemContent = (String[])categoryInfo.elementAt(i);
                //#style infoLabel
                StringItem label = new StringItem(null, itemContent[0]);
                this.append(tab, label);
                //#style infoValue
                StringItem value = new StringItem(null, itemContent[1]);
                this.append(tab, value);
            } else if(i == 0) {
                // first item is thumbnail
                Object[] itemContent = (Object[])categoryInfo.elementAt(i);
                //#style infoLabel
                StringItem label = new StringItem(null, (String)itemContent[0]);
                this.append(tab, label);
                //#style photoPreview
                ImageItem value = new ImageItem(null, (Image)itemContent[1], ImageItem.PLAIN, "n/a");
                this.append(tab, value);
            }
        }
    }
    
    public void screenStateChanged(Screen tabbedScreen) {
        this.setTitle(this.tabNames[this.getSelectedTab()]);        
    }
    
}
