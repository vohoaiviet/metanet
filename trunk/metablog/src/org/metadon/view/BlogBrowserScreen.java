/*
 * BlogBrowserScreen.java
 *
 * Created on 12. November 2007, 22:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.metadon.view;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Displayable;

import org.metadon.control.BlogBrowser;
import org.metadon.control.Controller;

import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class BlogBrowserScreen extends List implements CommandListener {

    private Controller controller;
    private BlogBrowser blogBrowser;
    private final Command CLOSE_CMD = new Command(Locale.get("cmd.close"), Command.BACK, 1);
    public final Command OPEN_CMD = new Command(Locale.get("cmd.open"), Command.ITEM, 1);
    public final Command POST_CMD = new Command(Locale.get("cmd.post"), Command.ITEM, 3);
    public final Command DELETE_CMD = new Command(Locale.get("cmd.delete"), Command.ITEM, 5);
    public final Command RESOLVE_CMD = new Command(Locale.get("cmd.resolveJourney"), Command.ITEM, 6);
    public final Command POST_JOURNEY_CMD = new Command(Locale.get("cmd.post.journey"), Command.ITEM, 8);
    public final Command DELETE_JOURNEY_CMD = new Command(Locale.get("cmd.delete.journey"), Command.ITEM, 10);
    public final Command CLEAR_CMD = new Command(Locale.get("cmd.clear"), Command.ITEM, 1);
    private final int CONF_DELETE_J = 0;
    private final int CONF_POST_J = 1;
    private int operation = -1;
    
    /**
     * Creates a new instance of BlogBrowserScreen
     */
    public BlogBrowserScreen(Controller c, BlogBrowser bb, String title) {
        super(title, List.IMPLICIT);
        this.controller = c;
        this.blogBrowser = bb;

        this.addCommand(this.CLOSE_CMD);
        this.setCommandListener(this);
        this.setSelectCommand(null);
    }

    public void commandAction(Command cmd, Displayable disp) {
        if (cmd == this.OPEN_CMD || cmd == List.SELECT_COMMAND && disp == this) {
            this.blogBrowser.openSelectedBlog();
        } else if (cmd == this.RESOLVE_CMD) {
            this.blogBrowser.resolveJourney();
        } else if (cmd == this.POST_CMD) {
            this.blogBrowser.postSelectedBlog();
        } else if (cmd == this.POST_JOURNEY_CMD) {
            this.operation = this.CONF_POST_J;
            this.controller.show(this.controller.CONF_ASCR,
                    Locale.get("blogBrowserScreen.alert.postJourney"),
                    this,
                    null);
        } else if (cmd == this.DELETE_CMD) {
            this.blogBrowser.deleteSelectedBlog();
        } else if (cmd == this.DELETE_JOURNEY_CMD) {
            this.operation = this.CONF_DELETE_J;
            // confirm user
            this.controller.show(this.controller.CONF_ASCR,
                    Locale.get("blogBrowserScreen.alert.deleteJourney"),
                    this,
                    null);
        } else if (cmd == this.CLEAR_CMD) {
            this.blogBrowser.clear();
        } else if (cmd == this.CLOSE_CMD) {
            this.controller.show(this.controller.getScreen(this.controller.MAIN_SCR));

        } else if (cmd == this.controller.YES_CMD && disp instanceof Alert) {
            if(this.operation == this.CONF_DELETE_J) {
                // delete journey blogs
                this.blogBrowser.deleteJourney();
                this.controller.show(this);
            } else if(this.operation == this.CONF_POST_J) {
                // post journey blogs
                this.blogBrowser.postJourney();
            }
        } else if (cmd == this.controller.NO_CMD && disp instanceof Alert) {
            // cancel
            this.controller.show(this);
        }
    }
}
