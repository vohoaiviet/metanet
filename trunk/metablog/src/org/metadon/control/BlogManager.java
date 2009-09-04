/*
 * BlogManager.java
 *
 * Created on 05. November 2007, 12:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.metadon.control;

import javax.microedition.lcdui.Displayable;

import org.metadon.beans.Blog;
import org.metadon.beans.BlogMessage;
import org.metadon.beans.BlogMultimedia;
import org.metadon.beans.Message;
import org.metadon.beans.Payload;
import org.metadon.beans.Photo;
import org.metadon.beans.Settings;
import org.metadon.extern.location.GPSLocation;
import org.metadon.extern.location.TraceManager;
import org.metadon.extern.location.Waypoint;
import org.metadon.utils.ActivityAlert;
import org.metadon.utils.BlogSerializer;
import org.metadon.utils.BlogUnserializer;
import org.metadon.utils.RMSBlogSelector;
import org.metadon.view.AppSplashScreen;
import org.metadon.view.BlogMessageScreen;

import java.util.Stack;
import java.util.TimerTask;
import java.util.Timer;
import java.util.Enumeration;
import java.io.IOException;




import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class BlogManager {

    private volatile static BlogManager uniqueInstance;
    private Controller controller;
    private Settings settings;
    private PhotoManager photoManager;
    private TraceManager traceManager;
    private BlogMessageScreen messageScreen;
    private RecordManager recordManager;
    private BlogBrowserLocal blogBrowserLocal;
    private BlogBrowserServer blogBrowserServer;
    private BlogSerializer blogSerializer;
    private GPSLocation location = null;
    private Photo photo = null;
    private Message message = null;
    private BlogMessage blogMessage = null;
    private BlogMultimedia blogMultimedia = null;

    private Blog blogPost = null;
    private Enumeration blogPostBatch = null;
    private int blogPostNumber = 1;
    private int blogPostBatchSize = 1;
   
    private boolean isJourneyBlog = false;
    private Timer storageTerminationNotifier;
    public int journeyBlogCounter = 0;
    public int waypointCounter = 0;
    
    private static final ActivityAlert activityScreen = new ActivityAlert(true);
    private boolean init;
    private long timestamp = -1;
    public int blogType = -1;
    private final int STORED_BLOGS = Integer.valueOf(Locale.get("blog.stored")).intValue();
    private final int POSTED_BLOGS = Integer.valueOf(Locale.get("blog.posted")).intValue();
    private final int BLOG_PM = Integer.valueOf(Locale.get("blog.PM.id")).intValue();
    private final int BLOG_M = Integer.valueOf(Locale.get("blog.M.id")).intValue();
    private final int QUALITY_POST = Integer.valueOf(Locale.get("blog.PM.id.quality.post")).intValue();
    private final int QUALITY_BROWSE = Integer.valueOf(Locale.get("blog.PM.id.quality.browse")).intValue();
    private static final int STORAGE_TIMEOUT = 30000;

    /**
     * Creates a new instance of BlogManager
     */
    private BlogManager(Controller c) {
        super();
        this.controller = c;
        this.init();
    }

    // singletone
    public static BlogManager getInstance(Controller c) {
        if (uniqueInstance == null) {
            //synchronize concurrent threads on creating a instance (performance decrease!)
            synchronized (BlogManager.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new BlogManager(c);
                }
            }
        }
        return uniqueInstance;
    }

    public void init() {
        this.photoManager = PhotoManager.getInstance(this.controller);
        this.photoManager.init();

        // init blog browser
        this.blogBrowserLocal = BlogBrowserLocal.getInstance();
        this.blogBrowserLocal.setController(this.controller);
        this.blogBrowserLocal.setBlogManager(this);
        this.blogBrowserLocal.init();

        // init blog browser server
        this.blogBrowserServer = BlogBrowserServer.getInstance();
        this.blogBrowserServer.setController(this.controller);
        this.blogBrowserServer.setBlogManager(this);
        this.blogBrowserServer.init();

        // load stored and posted blogs on application start-up
        AppSplashScreen sc = (AppSplashScreen) this.controller.getScreen(this.controller.SPLA_SCR);
        sc.setLoadingStatusText(Locale.get("appSplashScreen.loading.blogs"));
        this.blogBrowserLocal.loadBlogs();
        this.blogBrowserServer.loadBlogs();

        this.recordManager = RecordManager.getInstance(this.controller);
        this.traceManager = TraceManager.getInstance(this.controller);
        this.messageScreen = new BlogMessageScreen(this.controller, this);
        this.init = true;
    }

    public void createBlog(GPSLocation location, int type) {
        if (!this.init) {
            return;
        }
        this.settings = this.controller.getSettings();
        this.storageTerminationNotifier = new Timer();
        this.blogType = type;
        this.location = location;

        // photo and text blog
        if (this.blogType == this.BLOG_PM) {
            this.blogMultimedia = new BlogMultimedia();
            this.createPhoto();
        // text blog
        } else if (this.blogType == this.BLOG_M) {
            this.blogMessage = new BlogMessage();
            this.createMessage();
        }
    }

    public void browserType(int storeType) {
        if (!this.init) {
            return;
        }
        if (storeType == this.STORED_BLOGS) {
            blogBrowserLocal.loadBlogs();
            blogBrowserLocal.browseBlogs();
        } else if (storeType == this.POSTED_BLOGS) {
            blogBrowserServer.loadBlogs();
            blogBrowserServer.browseBlogs();
        }
    }

    /**************************************************************************/
    private void createPhoto() {
        // init photo capture process
        this.photoManager.mandatePhoto();
        // photo waiting thread
        Thread t = new Thread() {

            public void run() {
                BlogManager m = BlogManager.uniqueInstance;
                Stack pb = m.photoManager.photoBin; // lock
                m.photo = null;
                synchronized (pb) {
                    try {
                        pb.wait();
                    } catch (InterruptedException ie) {/*ignore*/
                    }
                    // look for a photo
                    if (!pb.isEmpty()) {
                        m.photo = (Photo) pb.pop();
                        m.createMessage();
                    }
                }
            }
        };
        t.start();
    }

    private void createMessage() {
        this.messageScreen.setString(null);
        this.controller.show(this.messageScreen);
    }

    /**************************************************************************/
    // initializes the blog storing process for the current blog
    public void storeBlog() {
        // notify user
        activityScreen.setString(Locale.get("blogManager.alert.storing"));
        controller.show(activityScreen);
        // schedule process
        this.storageTerminationNotifier.schedule(new BlogStoreTerminator(), STORAGE_TIMEOUT);
        // start process
        this.prepareBlog();
        this.serializeBlog();
    }

    /**************************************************************************/
    // prepare current blog
    private void prepareBlog() {

        // current timestamp
        this.timestamp = System.currentTimeMillis();

        // get user message input
        if (this.messageScreen.validMessage()) {
            this.message = new Message(this.messageScreen.getInput());
        }

        // get blog relation
        if (this.settings.getBlogRelation() == this.settings.BLOG_RELATION_JOURNEY) {
            this.isJourneyBlog = true;
        }

        // prepare blog
        if (this.blogType == this.BLOG_PM && this.photo != null) {
            ////#debug
            System.out.println("blog type: multimedia");
            this.blogMultimedia.setTimestamp(this.timestamp);
            // set journey blog number
            if (this.isJourneyBlog) {
                this.blogMultimedia.setNumber(this.getNextJourneyBlogNumber());
            }
            this.blogMultimedia.setPhoto(this.photo);
            if (this.message != null) {
                this.blogMultimedia.setMessage(this.message);
            }
            if (this.location != null) {
                this.blogMultimedia.setLocation(this.location);
            }

        } else if (this.blogType == this.BLOG_M && this.message != null) {
            ////#debug
            System.out.println("blog type: message");
            this.blogMessage.setTimestamp(this.timestamp);
            // set journey blog number
            if (this.isJourneyBlog) {
                this.blogMessage.setNumber(this.getNextJourneyBlogNumber());
            }
            this.blogMessage.setMessage(this.message);
            if (this.location != null) {
                this.blogMessage.setLocation(this.location);
            }
        }
    }

    /**************************************************************************/
    // serialize current blog
    private void serializeBlog() {

        // serialize blog in thread
        if (this.blogType == this.BLOG_PM) {
            ////#debug
            System.out.println("init serializer for multimedia blog");
            this.blogSerializer = new BlogSerializer(this.blogMultimedia);
        } else if (this.blogType == this.BLOG_M) {
            ///#debug
            System.out.println("init serializer for message blog");
            this.blogSerializer = new BlogSerializer(this.blogMessage);
        }

        this.blogSerializer.start();

        // wait until blog serialization is finished
        Thread t = new Thread() {

            public void run() {
                BlogManager m = BlogManager.uniqueInstance;
                try {
                    m.blogSerializer.join();
                } catch (InterruptedException ie) {
                }
                // get serialized data
                Stack recordBin = m.blogSerializer.getRecordBin();
                storeSerializedBlog(recordBin);
            }
        };
        t.start();
    }

    /**************************************************************************/
    // store current blog in RMS
    private void storeSerializedBlog(Stack recordBin) {
        Stack recordSet = new Stack();

        // stack order: (bottom)[thumb, full](top)
        while (!recordBin.isEmpty() && this.blogType == this.BLOG_PM) {
            recordSet.push(recordBin.pop());
        }
        if (this.blogType == this.BLOG_M) {
            recordSet.push(recordBin.pop());
        }

        // store serialized blog in rms
        if (this.recordManager.setBlog(this.STORED_BLOGS, this.blogType, recordSet)) {
            ////#debug
            System.out.println("blog stored!");

            if (this.isJourneyBlog) {
                this.journeyBlogCounter++;
                if (this.location != null) {
                    this.waypointCounter++;
                    // track blog as new journey waypoint
                    this.traceManager.trackWaypoint(this.timestamp, this.location);
                }
            } else if (this.location != null) {
                this.waypointCounter++;
            }
            this.cleanUp();
            this.controller.loadStoredBlogs();
//            Displayable screen = this.controller.getScreen(this.controller.MAIN_SCR);
//            this.controller.show(screen);
        } else {
            //#debug error
            System.out.println("FATAL: blog not stored!");
            this.cleanUp();
            Displayable next = controller.getScreen(controller.BLOG_SCR);
            controller.show(controller.ERROR_ASCR, Locale.get("blogManager.alert.storageFailed")+Locale.get("photoManager.alert.memoryProblem"), null, next);
        }
    }

    // full quality blog size in byte
    public int getBlogSize(Blog blog) {
        int blogSizeBytes = -1;
        int blogType = -1;
        RMSBlogSelector selector = new RMSBlogSelector(RMSBlogSelector.EQUAL_TO, new Long(blog.getTimestamp()), null);
        if(blog instanceof BlogMultimedia) {
            blogType = this.BLOG_PM;
        } else if(blog instanceof BlogMessage) {
            blogType = this.BLOG_M;
        }
        Stack result = this.recordManager.getBlog(this.STORED_BLOGS, blogType, this.QUALITY_POST, selector);
        if(result.size() == 1) {
            byte[] record = (byte[]) result.firstElement();
            blogSizeBytes = record.length;
        }
        return blogSizeBytes;
    }

    /**************************************************************************/
    // initializes the batch-based blog posting process
    public void postBlogs(Stack stackOfBlogs) {
        this.blogPostBatchSize = stackOfBlogs.size();
        this.blogPostBatch = stackOfBlogs.elements();
        
        if(this.blogPostBatch.hasMoreElements())
            this.postBlog((Blog)this.blogPostBatch.nextElement());
    }
    
    // initializes the blog posting process
    public void postBlog(Blog b) {
        this.blogPost = b;
        Stack serializedPhoto;
        BlogMultimedia blogPM;
        BlogMessage blogM;
        byte[] photoData = null;
        Payload payload = null;

        if (b instanceof BlogMultimedia) {
            blogPM = (BlogMultimedia) this.blogPost;
            RMSBlogSelector selector = new RMSBlogSelector(RMSBlogSelector.EQUAL_TO, new Long(blogPM.getTimestamp()), null);
            serializedPhoto = this.recordManager.getBlog(this.STORED_BLOGS, this.BLOG_PM, this.QUALITY_POST, selector);
            BlogUnserializer bu = new BlogUnserializer();
            try {
                photoData = bu.getPhotoData(serializedPhoto);
            } catch (IOException ioe) {
                //#debug error
                System.out.println("getPhotoData: " + ioe);
            }
            // set payload for data transfer
            if (blogPM != null && photoData != null) {
                // timestamp
                payload = new Payload(blogPM.getTimestamp());
                // photo
                payload.setPhoto(photoData);
                // message
                if (blogPM.containsMessage()) {
                    payload.setMessage(blogPM.getMessage().getText());
                }
                // location
                if (blogPM.containsLocation()) {
                    GPSLocation loc = blogPM.getLocation();
                    payload.setLocation(new double[]{loc.getLongitude(), loc.getLatitude()});
                }
                // elevation
                Waypoint wp = this.traceManager.getWaypoint(blogPM.getTimestamp());
                if (wp != null && wp.containsElevation()) {
                    payload.setElevation(wp.getElevation());
                }
                // journey name
                if (blogPM.isJourneyBlog()) {
                    String journeyName = this.controller.getStatistics().getJourneyName();
                    this.blogPost.setJourneyName(journeyName);
                    payload.setJourneyName(journeyName);
                }
            }
        } else if (b instanceof BlogMessage) {
            blogM = (BlogMessage) this.blogPost;
            // set payload for data transfer
            if (blogM != null) {
                // timestamp
                payload = new Payload(blogM.getTimestamp());
                // message
                payload.setMessage(blogM.getMessage().getText());
                // location
                if (blogM.containsLocation()) {
                    GPSLocation loc = blogM.getLocation();
                    payload.setLocation(new double[]{loc.getLongitude(), loc.getLatitude()});
                }
                // elevation
                Waypoint wp = this.traceManager.getWaypoint(blogM.getTimestamp());
                if (wp != null && wp.containsElevation()) {
                    payload.setElevation(wp.getElevation());
                }
                // journey name
                if (blogM.isJourneyBlog()) {
                    String journeyName = this.controller.getStatistics().getJourneyName();
                    this.blogPost.setJourneyName(journeyName);
                    payload.setJourneyName(journeyName);
                }
            }
        }
        payload.setComment(this.blogPostNumber+"/"+this.blogPostBatchSize);
        // perform post
        this.controller.postBlog(payload);
    }

    public void updatePostedBlogs() {
        boolean stored = false;
        this.settings = this.controller.getSettings();
        
        // set current timestamp as posted timestamp
        this.blogPost.setTimestampPost(System.currentTimeMillis());
        // set blog size
        int transferedPayload = this.getBlogSize(this.blogPost);
        this.blogPost.setSizeBytes(transferedPayload);
        this.settings.addTransferedPayload(transferedPayload);
        
        // serialize blog
        this.blogSerializer = new BlogSerializer(this.blogPost);
        this.blogSerializer.start();
        try {
            this.blogSerializer.join();
        } catch (InterruptedException ie) {
        }
        if (this.blogPost instanceof BlogMultimedia) {
            // store thumb version of posted blog in server rms store
            stored = this.recordManager.setBlog(this.POSTED_BLOGS, this.BLOG_PM, this.blogSerializer.getRecordBin());
            if(stored) {
                this.settings.updatePostedMultimediaBlogs();
            }
        } else if (this.blogPost instanceof BlogMessage) {
            // store thumb version of posted blog in server rms store
            stored = this.recordManager.setBlog(this.POSTED_BLOGS, this.BLOG_M, this.blogSerializer.getRecordBin());
            if(stored) {
                this.settings.updatePostedMessageBlogs();
            }
        }
        if(stored) {
            // remove posted blog from local browser and local rms store
            this.blogBrowserLocal.deleteBlog(this.blogPost);
            // store settings
            this.controller.storeSettings(this.settings);
        }
        
        if(this.blogPostBatch != null && this.blogPostBatch.hasMoreElements()) {
            this.blogPostNumber++;
            this.postBlog((Blog)this.blogPostBatch.nextElement());
        } else {
            this.cleanUpPost();
            this.controller.loadPostedBlogs();
        }
    }
    
    public void cleanUpPost() {
        this.blogPost = null;
        this.blogPostBatch = null;
        this.blogPostNumber = 1;
        this.blogPostBatchSize = 1;
    }

    /**************************************************************************/

    private int getNextJourneyBlogNumber() {
        RMSBlogSelector selector = new RMSBlogSelector(RMSBlogSelector.IS_JOURNEY_BLOG, new Long(System.currentTimeMillis()), null);

        Stack storedPMBlogs = (Stack) this.recordManager.getBlog(this.STORED_BLOGS, this.BLOG_PM, this.QUALITY_BROWSE, selector);
        Stack storedMBlogs = (Stack) this.recordManager.getBlog(this.STORED_BLOGS, this.BLOG_M, this.QUALITY_BROWSE, selector);
        int storedJourneyRecords = storedPMBlogs.size() + storedMBlogs.size();
        return this.settings.getDeletedJourneyBlogs() + storedJourneyRecords + 1;
    }

    public int getJourneyBlogCount() {
        return this.journeyBlogCounter;
    }

    public int getWaypointCount() {
        return this.waypointCounter;
    }

    public void cleanUp() {
        this.storageTerminationNotifier.cancel();
        this.storageTerminationNotifier = null;

        this.location = null;
        this.photo = null;
        this.message = null;

        this.blogMultimedia = null;
        this.blogMessage = null;

        this.blogSerializer = null;

        this.timestamp = -1;
        this.blogType = -1;
        this.isJourneyBlog = false;
    }

    public void cancel() {
        this.cleanUp();
        Displayable screen = this.controller.getScreen(this.controller.BLOG_SCR);
        this.controller.show(screen);
    }

    /***********************************************************************/
    // terminates storing process if system is frezzed in case of memory problems
    private class BlogStoreTerminator extends TimerTask {

        public void run() {
            cleanUp();
            Displayable next = controller.getScreen(controller.BLOG_SCR);
            controller.show(controller.ERROR_ASCR, Locale.get("blogManager.alert.storageFailed")+Locale.get("photoManager.alert.memoryProblem"), null, next);
        }
    }
}
