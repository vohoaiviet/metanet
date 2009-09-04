/*
 * BlogMainScreen.java
 *
 * Created on 05. November 2007, 10:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.view;

import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import org.metadon.control.Controller;

import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class BlogMainScreen extends List implements CommandListener {
    
    private Controller controller;
    private final Command BACK_CMD = new Command(Locale.get("cmd.back"), Command.BACK, 1);
    
    private final int BLOG_PM = Integer.valueOf(Locale.get("blog.PM.id")).intValue();
    private final int BLOG_M = Integer.valueOf(Locale.get("blog.M.id")).intValue();
   
    /**
     * Creates a new instance of BlogMainScreen
     */
    public BlogMainScreen(Controller c) {
        super(Locale.get("title.newBlog"), List.IMPLICIT);
        this.controller = c;
        
        //#style photoBlogScreenItem
        this.append(Locale.get("menu.newBlog.blogPM"), null);
        //#style textBlogScreenItem
    	this.append(Locale.get("menu.newBlog.blogM"), null);
        
        this.addCommand(this.BACK_CMD);
        this.setCommandListener(this);
    }
    
    public void commandAction(Command cmd, Displayable disp)
    {
        Displayable screen = null;
        if (cmd == List.SELECT_COMMAND) {
            int selectedItem = this.getSelectedIndex();           
            switch (selectedItem)
            {
                // create photo and text blog
		case 0: this.controller.createBlog(this.BLOG_PM);
                    break;
                // create text blog
		case 1: this.controller.createBlog(this.BLOG_M);
                    break;
		default:
            }
        } else if (cmd == this.BACK_CMD) {
            screen = this.controller.getScreen(this.controller.MAIN_SCR);
            if(screen != null) {
                this.controller.show(screen);
            }
        }
    }
    
}
