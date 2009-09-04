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
import org.metadon.beans.BlogMessage;
import org.metadon.beans.BlogMultimedia;
import org.metadon.beans.Settings;
import org.metadon.extern.location.TraceManager;
import org.metadon.utils.ActivityAlert;
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
public class BlogBrowserLocal extends BlogBrowser {

    private volatile static BlogBrowserLocal uniqueInstance;
    private Controller controller;
    private BlogUnserializer blogUnserializer;
    private BlogManager blogManager;
    private BlogBrowserScreen browserScreen;
    private TraceManager traceManager;
    private RecordManager recordManager;
    private BlogInfoFormatter blogInfoFormatter;
    private Settings settings;
    private long lastLoading;
    private int startIndex;
    private int blogOrder = -1;
    private int distanceUnit = -1;
    private boolean waypointRemoved = false;
    private Stack browseableBlogSet = null;
    private Stack blogSetPM = null;
    private Stack blogSetM = null;
    private final int STORED_BLOGS = Integer.valueOf(Locale.get("blog.stored")).intValue();
    private final int BLOG_ALL = Integer.valueOf(Locale.get("blog.ALL.id")).intValue();
    private final int BLOG_PM = Integer.valueOf(Locale.get("blog.PM.id")).intValue();
    private final int BLOG_M = Integer.valueOf(Locale.get("blog.M.id")).intValue();
    private final int QUALITY_BROWSE = Integer.valueOf(Locale.get("blog.PM.id.quality.browse")).intValue();

    /** Creates a new instance of BlogBrowser */
    public BlogBrowserLocal() {
        super();
    }

    public static synchronized BlogBrowserLocal getInstance() {
        if (uniqueInstance == null) {
            //synchronize concurrent threads on creating a instance (performance decrease!)
            synchronized (BlogBrowserLocal.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new BlogBrowserLocal();
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
        this.browserScreen = new BlogBrowserScreen(this.controller, this, Locale.get("title.browserLocal"));
        this.blogInfoFormatter = new BlogInfoFormatter(this.controller);
        this.blogUnserializer = new BlogUnserializer();

        this.recordManager = RecordManager.getInstance(this.controller);
        this.traceManager = TraceManager.getInstance(this.controller);
    }

    public void loadBlogs() {
        // get user preferences
        this.settings = this.controller.getSettings();

        // init browser settings
        if (this.blogOrder == -1) {
            this.blogOrder = this.settings.getBlogOrder();
        }
        if (this.distanceUnit == -1) {
            this.distanceUnit = this.settings.getDistanceUnit();
        }

        this.refreshScreen();
        // load a new blog set at startup and when blog order changes
        if (this.browseableBlogSet == null || this.blogOrder != this.settings.getBlogOrder()) {
            // init browser items
            this.browseableBlogSet = new Stack();
            this.blogOrder = settings.getBlogOrder();
            this.blogManager.journeyBlogCounter = 0;
            this.blogManager.waypointCounter = 0;
            this.browserScreen.deleteAll();

            RMSBlogSelector selector = new RMSBlogSelector(RMSBlogSelector.LESSER_THEN, new Long(System.currentTimeMillis()), null);

            // sort blogs according their type
            if (this.blogOrder == settings.BLOG_ORDER_TYPE) {
                // get separate blog sets
                this.blogSetM = this.getBlogSet(this.BLOG_M, selector);
                this.blogSetPM = this.getBlogSet(this.BLOG_PM, selector);
                if (this.blogSetPM == null || this.blogSetM == null) {
                    return;
                }

                //#debug
//#                 System.out.println("stored blogs (type order): "+this.blogSetPM.size()+"+"+this.blogSetM.size());

                Stack[] sets = new Stack[]{this.blogSetM, this.blogSetPM};

                // merge type blog sets to a browseable blog set and create the corresponding item
                for (int i = 0; i < sets.length; i++) {
                    if (sets[i].size() > 0) {
                        Enumeration enumeration = sets[i].elements();
                        while (enumeration.hasMoreElements()) {
                            Object element = enumeration.nextElement();
                            this.browseableBlogSet.push(element);
                            Blog blog = (Blog) element;
                            // init/update journey blog counter
                            if (blog.isJourneyBlog()) {
                                this.blogManager.journeyBlogCounter++;
                            }
                            if (blog.containsLocation()) {
                                this.blogManager.waypointCounter++;
                            }
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

                ////#debug
                System.out.println("stored blogs (time order): " + this.browseableBlogSet.size());

                // sort merged type blog sets before updating browseable blog set
                this.browseableBlogSet = super.sortBlogSet(this.browseableBlogSet);

                Enumeration enumeration = this.browseableBlogSet.elements();
                while (enumeration.hasMoreElements()) {
                    Object element = enumeration.nextElement();
                    Blog blog = (Blog) element;
                    // init/update journey blog counter
                    if (blog.isJourneyBlog()) {
                        this.blogManager.journeyBlogCounter++;
                    }
                    if (blog.containsLocation()) {
                        this.blogManager.waypointCounter++;
                    }
                    this.createItem(blog, null);
                }
            }
            this.startIndex = this.browseableBlogSet.size() - 1;

        } else {
            // check if new blogs have been created 
            int storedRecords = this.recordManager.countRecords(this.STORED_BLOGS, this.BLOG_PM) +
                    this.recordManager.countRecords(this.STORED_BLOGS, this.BLOG_M);
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
        RMSBlogSelector selector = new RMSBlogSelector(RMSBlogSelector.GREATER_THEN, new Long(this.lastLoading), null);

        // load new message records
        if (this.blogOrder == this.settings.BLOG_ORDER_TYPE &&
                this.recordManager.countRecords(this.STORED_BLOGS, this.BLOG_M) > this.blogSetM.size()) {

            blogSetM = this.getBlogSet(this.BLOG_M, selector);
            if (blogSetM == null) {
                return;
            }

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
//#            System.out.println("browser - M Blogs updated: "+blogSetM.size());
        }

        // load new multimedia records
        if (this.blogOrder == this.settings.BLOG_ORDER_TYPE &&
                this.recordManager.countRecords(this.STORED_BLOGS, this.BLOG_PM) > this.blogSetPM.size()) {

            blogSetPM = this.getBlogSet(this.BLOG_PM, selector);
            if (blogSetPM == null) {
                return;
            }

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
//#            System.out.println("browser - PM Blogs updated: "+blogSetPM.size());
        }

        // load new records of multiple types
        if (this.blogOrder == this.settings.BLOG_ORDER_TIME) {

            blogSet = this.getBlogSet(this.BLOG_ALL, selector);
            // sort merged type blog sets before updating browseable blog set
            blogSet = super.sortBlogSet(blogSet);

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
//#        System.out.println("browser - blogSet size now: "+this.browseableBlogSet.size());
    }

    // load blogs from the rms and returns them as a sets of blogs of corresponding types or
    // as merged browseable set of multiple types
    private Stack getBlogSet(int blogType, RMSBlogSelector selector) {
        Stack blogSet = new Stack();
        Stack blogSetM;
        Stack blogSetPM;

        if (blogType == this.BLOG_M || blogType == this.BLOG_ALL) {
            Stack rmsResultSetM = this.recordManager.getBlog(this.STORED_BLOGS, this.BLOG_M, this.QUALITY_BROWSE, selector);
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
            Stack rmsResultSetPM = this.recordManager.getBlog(this.STORED_BLOGS, this.BLOG_PM, this.QUALITY_BROWSE, selector);
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
        Vector browserItemInfo = this.blogInfoFormatter.getBrowserItemInfo(this.STORED_BLOGS, blog);
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

    public void deleteSelectedBlog() {
        Object element = this.browseableBlogSet.elementAt(this.getBlogIndex());
        if (element != null) {
            this.deleteBlog((Blog) element);
        }
    }

    public void deleteBlog(Blog blog) {
        int deletedBlogs = 0;
        int type = -1;
        if (blog instanceof BlogMultimedia) {
            type = this.BLOG_PM;
        } else if (blog instanceof BlogMessage) {
            type = this.BLOG_M;
        }

        RMSBlogSelector selector = new RMSBlogSelector(RMSBlogSelector.EQUAL_TO, new Long(blog.getTimestamp()), null);
        deletedBlogs = this.recordManager.removeBlog(this.STORED_BLOGS, type, selector);

        if (deletedBlogs > 0) {
            // remove tracked journey waypoint if existing
            this.waypointRemoved = this.traceManager.removeWaypoint(blog.getTimestamp());

            // update blog sets
            if (this.blogOrder == this.settings.BLOG_ORDER_TYPE) {
                // remove blog from blog set
                if (type == this.BLOG_PM) {
                    for (int i = 0; i < this.blogSetPM.size(); i++) {
                        Blog blogStack = (Blog) this.blogSetPM.elementAt(i);
                        if (blogStack.getTimestamp() == blog.getTimestamp()) {
                            this.blogSetPM.removeElementAt(i);
                            break;
                        }
                    }
                    //#debug
//#                     System.out.println("blogSetPM size now: " + this.blogSetPM.size());
                } else if (type == this.BLOG_M) {
                    for (int i = 0; i < this.blogSetM.size(); i++) {
                        Blog blogStack = (Blog) this.blogSetM.elementAt(i);
                        if (blogStack.getTimestamp() == blog.getTimestamp()) {
                            this.blogSetM.removeElementAt(i);
                            break;
                        }
                    }
                    //#debug
//#                     System.out.println("blogSetM size now: " + this.blogSetM.size());
                }
            }
            // update browseable blog set
            // remove blog from browseable blog set
            for (int i = 0; i < this.browseableBlogSet.size(); i++) {
                Blog blogStack = (Blog) this.browseableBlogSet.elementAt(i);
                if (blogStack.getTimestamp() == blog.getTimestamp()) {
                    this.browseableBlogSet.removeElementAt(i);
                    // update screen
                    if(this.browserScreen.size() > 1 && this.browserScreen.size() - 1 == i) {
                        this.browserScreen.setSelectedIndex(this.browserScreen.size() - 2, true);
                    }
                    this.browserScreen.delete(i);
                    break;
                }
            }
            if (blog.isJourneyBlog()) {
                this.blogManager.journeyBlogCounter--;
                if (this.blogManager.journeyBlogCounter == 0) {
                    // reset delete counter
                    this.settings.setDeletedJourneyBlogs(0);
                    this.settings.setJourneyName(Locale.get("statistics.defaultValue.journeyName"));
                } else {
                    // track as deleted blog
                    this.settings.setDeletedJourneyBlogs(this.settings.getDeletedJourneyBlogs() + 1);
                }
                //#debug
//#                 System.out.println("journey blog counter: " + this.blogManager.journeyBlogCounter);
                // update settings
                this.controller.storeSettings(this.settings);
            }
            if (blog.containsLocation()) {
                this.blogManager.waypointCounter--;
            }

            this.refreshScreen();
            //#debug
//#             System.out.println("Blog deleted! - blogSet size now: "+this.browseableBlogSet.size());
        } else {
            //#debug error
            System.out.println("FATAL: record store error!");
        }
    }

    public void deleteJourney() {
        // set search filter
        RMSBlogSelector selector = new RMSBlogSelector(RMSBlogSelector.IS_JOURNEY_BLOG, new Long(System.currentTimeMillis()), null);
        int deletedJourneyBlogs = 0;

        // search all journey blogs 
        deletedJourneyBlogs =
                this.recordManager.removeBlog(this.STORED_BLOGS, this.BLOG_PM, selector);
        deletedJourneyBlogs +=
                this.recordManager.removeBlog(this.STORED_BLOGS, this.BLOG_M, selector);

        if (deletedJourneyBlogs >= 0) {
            // remove journey blogs from blog sets
            if (this.blogOrder == this.settings.BLOG_ORDER_TYPE) {
                for (int i = 0; i < this.blogSetPM.size(); i++) {
                    Blog blog = (Blog) this.blogSetPM.elementAt(i);
                    if (blog.isJourneyBlog()) {
                        this.blogSetPM.removeElementAt(i);
                        i--;
                    }
                }
                for (int i = 0; i < this.blogSetM.size(); i++) {
                    Blog blog = (Blog) this.blogSetM.elementAt(i);
                    if (blog.isJourneyBlog()) {
                        this.blogSetM.removeElementAt(i);
                        i--;
                    }
                }
            }
            // remove journey blogs from browseable blog set
            for (int i = 0; i < this.browseableBlogSet.size(); i++) {
                Blog blog = (Blog) this.browseableBlogSet.elementAt(i);
                if (blog.isJourneyBlog()) {
                    this.browseableBlogSet.removeElementAt(i);
                    this.browserScreen.delete(i);
                    i--;
                }
            }
            //#debug
//#             System.out.println("browsableBlogSet: "+this.browseableBlogSet.size());

            // update waypoint counter
            this.blogManager.waypointCounter -= this.traceManager.getJourneyWaypointCount();
            // remove all tracked waypoints for this journey
            this.traceManager.removeTrace();
            // update settings
            this.settings.setDeletedJourneyBlogs(0);
            this.settings.setJourneyName(Locale.get("statistics.defaultValue.journeyName"));
            this.controller.storeSettings(this.settings);

            this.blogManager.journeyBlogCounter = 0;
            this.refreshScreen();
        //#debug
//#             System.out.println("deleted journey blogs: "+deletedJourneyBlogs);
        } else {
            //#debug error
            System.out.println("FATAL: record store error!");
        }

    }

    // update screen commands and items - called after an operation
    private void refreshScreen() {
        if (this.browseableBlogSet == null) {
            return;
        }

        if (this.browseableBlogSet.isEmpty()) {
            // remove commands and list items
            this.browserScreen.removeCommand(this.browserScreen.OPEN_CMD);
            this.browserScreen.removeCommand(this.browserScreen.POST_CMD);
            this.browserScreen.removeCommand(this.browserScreen.DELETE_CMD);
        }

        if (this.blogManager.journeyBlogCounter == 0) {
            this.browserScreen.removeCommand(this.browserScreen.RESOLVE_CMD);
            this.browserScreen.removeCommand(this.browserScreen.POST_JOURNEY_CMD);
            this.browserScreen.removeCommand(this.browserScreen.DELETE_JOURNEY_CMD);
        } else if (this.traceManager.isResolvedTrace()) {
            this.browserScreen.removeCommand(this.browserScreen.RESOLVE_CMD);
        }
            // refresh item info if the distance unit has been changed
        if (this.distanceUnit != this.settings.getDistanceUnit()) {
            this.distanceUnit = this.settings.getDistanceUnit();
            this.refreshItemInfo();
        }
            // refresh item info if a waypoint has been deleted
        if (this.waypointRemoved) {
            this.waypointRemoved = false;
            this.refreshItemInfo();
        }

    }

    private void refreshItemInfo() {
        for (int i = 0; i <
                this.browseableBlogSet.size(); i++) {
            Blog blog = (Blog) this.browseableBlogSet.elementAt(i);
            Vector browserItemInfo = this.blogInfoFormatter.getBrowserItemInfo(this.STORED_BLOGS, blog);
            String itemText = (String) browserItemInfo.firstElement();
            Image itemIcon = (Image) browserItemInfo.lastElement();
            this.browserScreen.set(i, itemText, itemIcon);
        }

    }

    public int getBlogIndex() {
        return this.browserScreen.getSelectedIndex();
    }

// load screen commands - called on browser startup
    public void browseBlogs() {
        if (!this.browseableBlogSet.isEmpty()) {
            this.browserScreen.addCommand(this.browserScreen.OPEN_CMD);
            if (this.controller.isLoggedOn()) {
                this.browserScreen.addCommand(this.browserScreen.POST_CMD);
            }

            this.browserScreen.addCommand(this.browserScreen.DELETE_CMD);
            if (this.blogManager.getJourneyBlogCount() > 0) {
                if (this.controller.isLoggedOn()) {
                    this.browserScreen.addCommand(this.browserScreen.POST_JOURNEY_CMD);
                }

                this.browserScreen.addCommand(this.browserScreen.DELETE_JOURNEY_CMD);
                if (!this.traceManager.isResolvedTrace()) {
                    this.browserScreen.addCommand(this.browserScreen.RESOLVE_CMD);
                }

            }
            // go to latest blog
            this.browserScreen.setSelectedIndex(this.startIndex, true);
        }
        this.controller.show(this.browserScreen);
    }

    /*************************************************************************/
    public void postSelectedBlog() {
        ActivityAlert activityScreen = this.controller.getMonitor().getActivityScreen();
        activityScreen.setString(Locale.get("blogBrowserLocal.initPost"));
        this.controller.show(activityScreen);
        Blog blog = (Blog) this.browseableBlogSet.elementAt(this.getBlogIndex());
        if (blog != null) {
            this.blogManager.postBlog(blog);
        }
    }

    public void resolveJourney() {
        this.traceManager.resolveTrace();
    }

    public void postJourney() {
        ActivityAlert activityScreen = this.controller.getMonitor().getActivityScreen();
        activityScreen.setString(Locale.get("blogBrowserLocal.initPost"));
        this.controller.show(activityScreen);
        
        RMSBlogSelector selector = new RMSBlogSelector(RMSBlogSelector.IS_JOURNEY_BLOG, new Long(System.currentTimeMillis()), null);
        Stack blogPostStack = this.getBlogSet(this.BLOG_ALL, selector);
        // sort journey blogs according their timestamp before posting
        this.blogManager.postBlogs(super.sortBlogSet(blogPostStack));
    }

    public void updateBrowserScreen() {
        this.refreshScreen();
    }

    public Displayable getBrowserScreen() {
        return this.browserScreen;
    }
}
