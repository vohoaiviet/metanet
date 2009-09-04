/*
 * AppMainScreen.java
 *
 * Created on 30. Oktober 2007, 15:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.view;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Displayable;

import org.metadon.control.Controller;

import de.enough.polish.util.Locale;
import de.enough.polish.util.Debug;
    
/**
 *
 * @author Hannes
 */
public class AppMainScreen extends List implements CommandListener {
    
    private Controller controller;
    private final Command EXIT_CMD = new Command(Locale.get("cmd.exit"), Command.EXIT, 10);
    //#ifdef polish.debugEnabled
//#          private final Command LOG_CMD = new Command("log", Command.OK, 1);
    //#endif
    
    /**
     * Creates a new instance of AppMainScreen
     */
    public AppMainScreen(Controller c) {
        super(Locale.get("title.main"), List.IMPLICIT);
        this.controller = c;
        
        //#style mainScreenItem
        this.append(Locale.get("menu.main.platform"), null);
        //#style mainScreenItem
        this.append(Locale.get("menu.main.settings"), null);
        //#style mainScreenItem
        this.append(Locale.get("menu.main.newBlog"), null);
        //#style mainScreenItem
        this.append(Locale.get("menu.main.storedBlogs"), null);
        //#style mainScreenItem
        this.append(Locale.get("menu.main.postedBlogs"), null);
        //#style mainScreenItem
        this.append(Locale.get("menu.main.statistics"), null);
        //#style mainScreenItem
        this.append(Locale.get("menu.main.monitor"), null);
        //#style mainScreenItem
        this.append(Locale.get("menu.main.quit"), null);
        
        this.addCommand(this.EXIT_CMD);
        //#ifdef polish.debugEnabled
//#         this.addCommand(this.LOG_CMD);
        //#endif
        this.setCommandListener(this);
    }
    
    public void commandAction(Command cmd, Displayable disp)
    {
        Displayable screen = null;
	if (cmd == List.SELECT_COMMAND && disp == this) {
            int selectedItem = this.getSelectedIndex();
            
            switch (selectedItem)
            {
                // system
		case 0:
                    screen = this.controller.getScreen(this.controller.SYST_SCR);
                    break;
                // phone settings
		case 1:
                    screen = this.controller.getScreen(this.controller.SETT_SCR);
                    ((AppSettingsScreen)screen).updateItems();
                    break;
                // create blog
		case 2:
                    if(this.controller.getSettings().getForceLocation()) {
                        this.controller.retrieveLocation();
                    } else {
                        screen = this.controller.getScreen(this.controller.BLOG_SCR);
                    }
                    break;
                // show stored blogs (browser)
		case 3:
                     this.controller.loadStoredBlogs();
                    break;
                // show posted blogs (successfully sent)
                case 4:
                    this.controller.loadPostedBlogs();
                    break;
                // show statistics
                case 5:
                    this.controller.loadStatistics();
                    break;
                // show monitor
                case 6:
                    this.controller.loadMonitor();
                    break;
                // exit
                case 7:
                    this.controller.confirmExit(this);
		default: 
            }
            if(screen != null) {
                this.controller.show(screen);
            }
            
        } else if (cmd == this.EXIT_CMD) {
            this.controller.confirmExit(this);
        } 
        //#ifdef polish.debugEnabled
//#         else if (cmd == this.LOG_CMD) {
//#             Debug.showLog(this.controller.getDisplay());
//#             return;
//#         }
        //#endif
    }
}
