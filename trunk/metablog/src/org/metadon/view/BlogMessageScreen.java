/*
 * BlogMessageScreen.java
 *
 * Created on 09. November 2007, 23:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.view;

import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.TextBox;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import org.metadon.control.BlogManager;
import org.metadon.control.Controller;

import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */

// framed Form?
public class BlogMessageScreen extends TextBox implements CommandListener {
    
    private Controller controller;
    private BlogManager blogManager;
    private TextBox messageBox;
    
    private final Command STORE_CMD = new Command(Locale.get("cmd.store"), Command.ITEM, 1);
    private final Command CANCEL_CMD = new Command(Locale.get("cmd.cancel"), Command.ITEM, 10);
    
    private final int BLOG_PM = Integer.valueOf(Locale.get("blog.PM.id")).intValue();
    private final int BLOG_M = Integer.valueOf(Locale.get("blog.M.id")).intValue();
    
    
    /**
     * Creates a new instance of BlogMessageScreen
     */
    public BlogMessageScreen(Controller c, BlogManager bm)  {
        //#style messageScreen
        super(Locale.get("title.message"), null, 200, TextField.ANY);
        this.controller = c;
        this.blogManager = bm;
        
        this.addCommand(this.STORE_CMD);
        this.addCommand(this.CANCEL_CMD);
        this.setCommandListener(this);
    }
    
    // check if user input is valid
    public boolean validMessage() {
        if(this.getInput() == null)
            return false;
        else return true;
    }
    
    // returns prepared user input
    public String getInput() {
        String input = this.getString();
        if(input == null || input.length() == 0) return null;
        // remove whitespaces at begin and end
        return input.trim();
    }
    
    
    public void commandAction(Command cmd, Displayable disp)
    {
        if(cmd == this.STORE_CMD) {
             if(this.blogManager.blogType == this.BLOG_M && !this.validMessage()) {
                this.controller.show(this.controller.INFO_ASCR, "Nothing to Store.", null, null);
                //#debug error
                System.out.println("no message to store");
            } else {
                this.blogManager.storeBlog();
            }
        } else if(cmd == this.CANCEL_CMD) {
            this.blogManager.cancel();
        }
    }
    
    
}
