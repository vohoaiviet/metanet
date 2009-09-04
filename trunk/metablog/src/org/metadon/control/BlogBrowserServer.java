/*
 * BlogBrowser.java
 *
 * Created on 12. November 2007, 22:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.metadon.control;

import java.util.Stack;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Image;

import org.metadon.beans.Blog;
import org.metadon.beans.Settings;
import org.metadon.utils.BlogInfoFormatter;
import org.metadon.utils.BlogUnserializer;
import org.metadon.utils.RMSBlogSelector;
import org.metadon.view.BlogBrowserScreen;
import org.metadon.view.BlogInfoScreen;




import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class BlogBrowserServer extends BlogBrowser {

    private volatile static BlogBrowserServer uniqueInstance;
    private Controller controller;
    private BlogUnserializer blogUnserializer;
    private BlogManager blogManager;
    private BlogBrowserScreen browserScreen;
    private RecordManager recordManager;
    private BlogInfoFormatter blogInfoFormatter;
    private Settings settings;
    private long lastLoading;
    private int startIndex;
    private int blogOrder = -1;
    private Stack browseableBlogSet = null;
    private Stack blogSetPM = null;
    private Stack blogSetM = null;
    
    private final int POSTED_BLOGS = Integer.valueOf(Locale.get("blog.posted")).intValue();
    
    private final int BLOG_ALL = Integer.valueOf(Locale.get("blog.ALL.id")).intValue();
    private final int BLOG_PM = Integer.valueOf(Locale.get("blog.PM.id")).intValue();
    private final int BLOG_M = Integer.valueOf(Locale.get("blog.M.id")).intValue();
    
    private final int QUALITY_BROWSE = Integer.valueOf(Locale.get("blog.PM.id.quality.browse")).intValue();

    /** Creates a new instance of BlogBrowser */
    public BlogBrowserServer() {
        super();
    }

    public static synchronized BlogBrowserServer getInstance() {
        if (uniqueInstance == null) {
            //synchronize concurrent threads on creating a instance (performance decrease!)
            synchronized (BlogBrowserServer.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new BlogBrowserServer();
                }
            }
        }
        return uniqueInstance;
    }

    public void setController(Controller c) {
        this.controller = c;
    }

    public void setBlogManager(BlogManager bm) {
        this.blogManager = bm;
    }

    public void init() {
        this.browserScreen = new BlogBrowserScreen(this.controller, this, Locale.get("title.browserServer"));
        this.blogInfoFormatter = new BlogInfoFormatter(this.controller);
        this.blogUnserializer = new BlogUnserializer();

        this.recordManager = RecordManager.getInstance(this.controller);
    }

    public void loadBlogs() {
        // get user preferences
        this.settings = this.controller.getSettings();

        // init browser settings
        if (this.blogOrder == -1) {
            this.blogOrder = this.settings.getBlogOrder();
        }

        this.refreshScreen();
        // load a new blog set at startup and when blog order changes
        if (this.browseableBlogSet == null || this.blogOrder != this.settings.getBlogOrder()) {
            //#debug
//#             System.out.println("brow. server: loading...");
            // init browser items
            this.browseableBlogSet = null;
            this.browseableBlogSet = new Stack();
            this.blogOrder = settings.getBlogOrder();
            this.browserScreen.deleteAll();

            RMSBlogSelector selector = new RMSBlogSelector(RMSBlogSelector.LESSER_THEN, null, new Long(System.currentTimeMillis()));

            // sort blogs according their type
            if (this.blogOrder == settings.BLOG_ORDER_TYPE) {
                // get separate blog sets
                this.blogSetM = this.getBlogSet(this.BLOG_M, selector);
                this.blogSetPM = this.getBlogSet(this.BLOG_PM, selector);
                if (this.blogSetPM == null || this.blogSetM == null) {
                    return;
                }

                //#debug
//#                 System.out.println("posted blogs (type order): " + this.blogSetPM.size() + "+" + this.blogSetM.size());

                Stack[] sets = new Stack[]{this.blogSetM, this.blogSetPM};

                // merge type blog sets to a browseable blog set and create the corresponding item
                for (int i = 0; i < sets.length; i++) {
                    if (sets[i].size() > 0) {
                        Enumeration enumeration = sets[i].elements();
                        while (enumeration.hasMoreElements()) {
                            Object element = enumeration.nextElement();
                            this.browseableBlogSet.push(element);
                            Blog blog = (Blog) element;
                            // add list entry
                            this.createItem(blog, null);
                        }
                    }
                }
            } // sort blogs according their timestamp
            else if (this.blogOrder == settings.BLOG_ORDER_TIME) {
                // get single browsable blog set
                this.browseableBlogSet = this.getBlogSet(this.BLOG_ALL, selector);
                if (this.browseableBlogSet == null) {
                    return;
                }

                //#debug
//#                 System.out.println("posted blogs (time order): " + this.browseableBlogSet.size());

                // sort merged type blog sets before updating browseable blog set
                Stack sortedSet = super.sortBlogSet(this.browseableBlogSet);
                this.browseableBlogSet = sortedSet;
                
                Enumeration enumeration = sortedSet.elements();
                while (enumeration.hasMoreElements()) {
                    Object element = enumeration.nextElement();
                    Blog blog = (Blog) element;
                    this.createItem(blog, null);
                }
            }
            this.startIndex = this.browseableBlogSet.size() - 1;

        } else {
            // check if new blogs have been posted 
            int storedRecords = this.recordManager.countRecords(this.POSTED_BLOGS, this.BLOG_PM) +
                    this.recordManager.countRecords(this.POSTED_BLOGS, this.BLOG_M);
            this.startIndex = this.browseableBlogSet.size() - 1;
            if (storedRecords > this.browseableBlogSet.size()) {
                this.updateBlogs();
            }
        }
        this.lastLoading = System.currentTimeMillis();
    }

    // update browsable blog set
    private void updateBlogs() {
        Stack blogSet;
        Stack blogSetM;
        Stack blogSetPM;
        RMSBlogSelector selector = new RMSBlogSelector(RMSBlogSelector.GREATER_THEN, null, new Long(this.lastLoading));

        //#debug
//#         System.out.println("brow. server: updating...");
        // load new message records
        if (this.blogOrder == this.settings.BLOG_ORDER_TYPE &&
                this.recordManager.countRecords(this.POSTED_BLOGS, this.BLOG_M) > this.blogSetM.size()) {

            //#debug
//#             System.out.println("brow. server - order type - getting message update set...");
            blogSetM = this.getBlogSet(this.BLOG_M, selector);
            if (blogSetM == null) {
                return;
            }
            //#debug
//#             System.out.println("brow. server - messages to update: "+blogSetM.size());

            Enumeration enumeration = blogSetM.elements();
            while (enumeration.hasMoreElements()) {
                Object element = enumeration.nextElement();
                // update message blog set
                this.blogSetM.addElement(element);
                // update browseable blog set and create the corresponding item
                this.browseableBlogSet.insertElementAt(element, this.blogSetM.size() - 1);
                Blog blog = (Blog) element;
                this.createItem(blog, new Integer(this.blogSetM.size() - 1));
            }
            this.startIndex = this.blogSetM.size() - 1;
            //#debug
//#             System.out.println("server browser - M Blogs updated: " + blogSetM.size());
        }

        // load new multimedia records
        if (this.blogOrder == this.settings.BLOG_ORDER_TYPE &&
                this.recordManager.countRecords(this.POSTED_BLOGS, this.BLOG_PM) > this.blogSetPM.size()) {

            //#debug
//#             System.out.println("brow. server - order type - getting pm update set...");
            
            blogSetPM = this.getBlogSet(this.BLOG_PM, selector);
            if (blogSetPM == null) {
                return;
            }
            
            //#debug
//#             System.out.println("brow. server - pm to update: "+blogSetPM.size());

            Enumeration enumeration = blogSetPM.elements();
            while (enumeration.hasMoreElements()) {
                Object element = enumeration.nextElement();
                // update multimedia blog set
                this.blogSetPM.addElement(element);
                // update browseable blog set and create the corresponding item
                this.browseableBlogSet.insertElementAt(element, this.browseableBlogSet.size());
                Blog blog = (Blog) element;
                // notify browser if a journey must be managed
                this.createItem(blog, null);
            }
            this.startIndex = this.browseableBlogSet.size() - 1;
            //#debug
//#             System.out.println("server browser - PM Blogs updated: " + blogSetPM.size());
        }

        // load new records of multiple types
        if (this.blogOrder == this.settings.BLOG_ORDER_TIME) {

            blogSet = this.getBlogSet(this.BLOG_ALL, selector);
            // sort merged type blog sets before updating browseable blog set
            blogSet = super.sortBlogSet(blogSet);
            
             //#debug
//#             System.out.println("brow. server - order time - blogs to update: "+blogSet.size());

            Enumeration enumeration = blogSet.elements();
            while (enumeration.hasMoreElements()) {
                Object element = enumeration.nextElement();
                // update browseable blog set and create the corresponding item
                this.browseableBlogSet.insertElementAt(element, this.browseableBlogSet.size());
                Blog blog = (Blog) element;
                this.createItem(blog, null);
            }
            this.startIndex = this.browseableBlogSet.size() - 1;
        }
        //#debug
//#         System.out.println("server browser - blogSet size now: " + this.browseableBlogSet.size());
    }

    // load blogs from the rms and returns them as a sets of blogs of corresponding types or
    // as merged browseable set of multiple types
    private Stack getBlogSet(int blogType, RMSBlogSelector selector) {
        Stack blogSet = new Stack();
        Stack blogSetM;
        Stack blogSetPM;

        if (blogType == this.BLOG_M || blogType == this.BLOG_ALL) {
            Stack rmsResultSetM = this.recordManager.getBlog(this.POSTED_BLOGS, this.BLOG_M, this.QUALITY_BROWSE, selector);
            if (rmsResultSetM != null) {
                if (rmsResultSetM.size() > 0) {
                    blogSetM = this.blogUnserializer.unserialize(rmsResultSetM, this.BLOG_M);
                    if (blogType == this.BLOG_ALL) {
                        // add message blogs to final set
                        Enumeration enumeration = blogSetM.elements();
                        while (enumeration.hasMoreElements()) {
                            blogSet.push(enumeration.nextElement());
                        }
                    } else {
                        return blogSetM;
                    }
                }
            }
        }
        if (blogType == this.BLOG_PM || blogType == this.BLOG_ALL) {
            Stack rmsResultSetPM = this.recordManager.getBlog(this.POSTED_BLOGS, this.BLOG_PM, this.QUALITY_BROWSE, selector);
            if (rmsResultSetPM != null && rmsResultSetPM.size() > 0) {
                // unserialize thumb/preview records
                blogSetPM = this.blogUnserializer.unserialize(rmsResultSetPM, this.BLOG_PM);
                if (blogType == this.BLOG_ALL) {
                    // add blogs to final set
                    Enumeration enumeration = blogSetPM.elements();
                    while (enumeration.hasMoreElements()) {
                        blogSet.push(enumeration.nextElement());
                    }
                } else {
                    return blogSetPM;
                }
            }
        }
        return blogSet;
    }

    // creates a list item for the corresponding blog
    private void createItem(Blog blog, Integer listIndex) {
        Vector browserItemInfo = this.blogInfoFormatter.getBrowserItemInfo(this.POSTED_BLOGS, blog);
        String itemText = (String) browserItemInfo.firstElement();
        Image itemIcon = null;
        if (browserItemInfo.lastElement() != null) {
            itemIcon = (Image) browserItemInfo.lastElement();
        } else {
            itemIcon = null;
        }

        // add blog to browser list
        if (listIndex == null || listIndex.intValue() >= this.browseableBlogSet.size()) {
            //#style blogBrowserItem
            this.browserScreen.append(itemText, itemIcon);
        } else {
            //#style blogBrowserItem
            this.browserScreen.insert(listIndex.intValue(), itemText, itemIcon);
        }
    }

    public void openSelectedBlog() {
        // get blog info from blog info formatter
        Vector blogCategories = this.blogInfoFormatter.getCategories((Blog) this.browseableBlogSet.elementAt(this.getBlogIndex()));
        Vector blogCategoryNames = this.blogInfoFormatter.getCategoryNames();
        Vector blogCategoryIcons = this.blogInfoFormatter.getCategoryIcons();
        if (blogCategories.size() == 0) {
            return;
        }

        String[] tabNames = new String[blogCategoryNames.size()];
        Image[] tabIcons = new Image[blogCategoryIcons.size()];
        for (int i = 0; i < blogCategoryNames.size(); i++) {
            tabNames[i] = (String) blogCategoryNames.elementAt(i);
            tabIcons[i] = (Image) blogCategoryIcons.elementAt(i);
        }

        BlogInfoScreen blogInfoScreen = new BlogInfoScreen(tabNames, tabIcons);
        for (int i = 0; i < blogCategories.size(); i++) {
            boolean binaryContent = false;
            Vector info = (Vector) blogCategories.elementAt(i);
            if (tabNames[i].equals(this.blogInfoFormatter.BLOG_PHOTO)) {
                binaryContent = true;
            }
            blogInfoScreen.setBlogInfoItems(i, info, binaryContent);
        }

        blogInfoScreen.addCommand(new Command(Locale.get("cmd.close"), Command.BACK, 1));
        blogInfoScreen.setCommandListener(new CommandListener() {

            public void commandAction(Command cmd, Displayable d) {
                controller.show(browserScreen);
            }
        });
        this.controller.show(blogInfoScreen);
    }

    public void clear() {
        // set search filter
        RMSBlogSelector selector = new RMSBlogSelector(RMSBlogSelector.LESSER_THEN, null, new Long(System.currentTimeMillis()));
        int deletedBlogs = 0;

        // remove all posted blogs 
        deletedBlogs = this.recordManager.removeBlog(this.POSTED_BLOGS, this.BLOG_PM, selector);
        deletedBlogs += this.recordManager.removeBlog(this.POSTED_BLOGS, this.BLOG_M, selector);
        
        if (deletedBlogs == this.browseableBlogSet.size()) {
            if(this.blogSetM != null) {
                this.blogSetM.removeAllElements();
            }
            if(this.blogSetPM != null) {
                this.blogSetPM.removeAllElements();
            }
            this.browseableBlogSet.removeAllElements();
            this.browserScreen.deleteAll();
            
            this.refreshScreen();
        } else {
            //#debug error
            System.out.println("FATAL: record store error!");
        }
    }
    
    public int getBlogIndex() {
        return this.browserScreen.getSelectedIndex();
    }

    // update screen commands and items - called after an operation
    private void refreshScreen() {
        if (this.browseableBlogSet == null) {
            return;
        }
        if (this.browseableBlogSet.isEmpty()) {
            // remove commands and list items
            this.browserScreen.removeCommand(this.browserScreen.OPEN_CMD);
            this.browserScreen.removeCommand(this.browserScreen.CLEAR_CMD);
        }
    }

    // load screen commands - called on browser startup
    public void browseBlogs() {
        if (!this.browseableBlogSet.isEmpty()) {
            this.browserScreen.addCommand(this.browserScreen.OPEN_CMD);
            this.browserScreen.addCommand(this.browserScreen.CLEAR_CMD);
            // go to latest blog
            this.browserScreen.setSelectedIndex(this.startIndex, true);
        }
        // show browser screen
        this.controller.show(this.browserScreen);
    }
}
