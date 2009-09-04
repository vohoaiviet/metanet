/*
 * BlogInfoFormatter.java
 *
 * Created on 13. Dezember 2007, 16:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.metadon.utils;




import javax.microedition.lcdui.Image;

import org.metadon.beans.Blog;
import org.metadon.beans.BlogMessage;
import org.metadon.beans.BlogMultimedia;
import org.metadon.beans.Message;
import org.metadon.beans.Photo;
import org.metadon.beans.Settings;
import org.metadon.control.BlogManager;
import org.metadon.control.Controller;
import org.metadon.control.RecordManager;
import org.metadon.extern.location.GPSLocation;
import org.metadon.extern.location.TraceManager;
import org.metadon.extern.location.Waypoint;
import org.metadon.extern.location.WaypointToolkit;

import java.util.Vector;

import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class BlogInfoFormatter {

    private Controller controller;
    private RecordManager recordManager;
    private TraceManager traceManager;
    private Blog blog = null;
    private Photo photo = null;
    private Message message = null;
    private GPSLocation location = null;
    private Waypoint waypoint = null;
    private int blogType;
    private Vector blogInfo = null;
    private Vector browserItemInfo = null;
    private Vector blogCategoryNames = null;
    private Vector blogCategoryIcons = null;
    private final int STORED_BLOGS = Integer.valueOf(Locale.get("blog.stored")).intValue();
    private final int POSTED_BLOGS = Integer.valueOf(Locale.get("blog.posted")).intValue();
    private final int BLOG_PM = Integer.valueOf(Locale.get("blog.PM.id")).intValue();
    private final int BLOG_M = Integer.valueOf(Locale.get("blog.M.id")).intValue();
    public final String BLOG_PROPS = Locale.get("blog.tab.properties");
    public final String BLOG_WAYPOINT = Locale.get("blog.tab.waypoint");
    public final String BLOG_PHOTO = Locale.get("blog.tab.photo");
    public final String BLOG_MESSAGE = Locale.get("blog.tab.message");
    private final int QUALITY_POST = Integer.valueOf(Locale.get("blog.PM.id.quality.post")).intValue();
    private final int QUALITY_BROWSE = Integer.valueOf(Locale.get("blog.PM.id.quality.browse")).intValue();
    private final static int LOCATION_INFO_TYPE = 10;
    private final static int LOCATION_INFO_METHOD = 11;
    private final static int LOCATION_INFO_HYBRID = 12;

    /** Creates a new instance of BlogInfoFormatter */
    public BlogInfoFormatter(Controller c) {
        super();
        this.controller = c;
        this.recordManager = RecordManager.getInstance(this.controller);
        this.traceManager = TraceManager.getInstance(this.controller);
        this.browserItemInfo = new Vector(2);
        this.blogCategoryNames = new Vector();
        this.blogCategoryIcons = new Vector();
        this.blogInfo = new Vector();
    }

    // set properties of the current blog
    private void setBlog(Blog blog) {
        this.cleanUp();

        this.blog = blog;
        this.message = this.blog.getMessage();
        this.location = this.blog.getLocation();
        this.waypoint = this.traceManager.getWaypoint(this.blog.getTimestamp());

        if (this.blog instanceof BlogMultimedia) {
            this.blogType = this.BLOG_PM;
            this.photo = ((BlogMultimedia) this.blog).getPhoto();
        } else if (this.blog instanceof BlogMessage) {
            this.blogType = this.BLOG_M;
        }
    }

    // returns a blog info string and an icon for a specific browser item related to a blog
    public Vector getBrowserItemInfo(int storeType, Blog blog) {
        this.setBlog(blog);
        String blogInfoText = null;

        if (storeType == this.STORED_BLOGS) {
            String[] id = DateFormatter.getUserFriendlyDate(this.blog.getTimestamp());
            blogInfoText = id[0] + " " + id[1];
            // relation to the last waypoint (time interval, distance)
            blogInfoText += "\n" + this.getWaypointIndicator();
            // shows the content of the blog (J...Journey, M...Message, W...Waypoint)
            blogInfoText += "\n" + this.getContentIndicator();
        } else if (storeType == this.POSTED_BLOGS) {
            String[] id = DateFormatter.getUserFriendlyDate(this.blog.getTimestampPost());
            blogInfoText = id[0] + " " + id[1];
            // journey to which blog belongs
            blogInfoText += "\n" + this.blog.getJourneyName();
            // shows the content of the blog (J...Journey, M...Message, W...Waypoint)
            blogInfoText += "\n" + this.getContentIndicator();
        }
        this.browserItemInfo.addElement(blogInfoText);
        this.browserItemInfo.addElement(this.getBlogIcon());
        return this.browserItemInfo;
    }

    // returns a vector with the blog infos of the different categories (properties, location, message)
    public Vector getCategories(Blog blog) {
        this.setBlog(blog);

        Vector photo = this.getBlogPhotoInfo();
        if (photo != null && photo.size() > 0) {
            this.blogInfo.addElement(photo);
            this.blogCategoryNames.addElement(this.BLOG_PHOTO);
            this.blogCategoryIcons.addElement(this.controller.BLOG_PHOTO_ICON);
        }
        Vector message = this.getBlogMessageInfo();
        if (message != null && message.size() > 0) {
            this.blogInfo.addElement(message);
            this.blogCategoryNames.addElement(this.BLOG_MESSAGE);
            this.blogCategoryIcons.addElement(this.controller.BLOG_MESSAGE_ICON);
        }
        Vector props = this.getBlogPropInfo();
        if (props != null && props.size() > 0) {
            this.blogInfo.addElement(props);
            this.blogCategoryNames.addElement(this.BLOG_PROPS);
            this.blogCategoryIcons.addElement(this.controller.BLOG_PROPS_ICON);
        }
        Vector location = this.getBlogLocationInfo();
        if (location != null && location.size() > 0) {
            this.blogInfo.addElement(location);
            this.blogCategoryNames.addElement(this.BLOG_WAYPOINT);
            this.blogCategoryIcons.addElement(this.controller.BLOG_WAYPOINT_ICON);
        }

        return this.blogInfo;
    }

    public Vector getCategoryNames() {
        return this.blogCategoryNames;
    }

    public Vector getCategoryIcons() {
        return this.blogCategoryIcons;
    }

    private void cleanUp() {
        this.blog = null;
        this.photo = null;
        this.message = null;
        this.location = null;

        this.browserItemInfo.removeAllElements();
        this.blogCategoryNames.removeAllElements();
        this.blogCategoryIcons.removeAllElements();
        this.blogInfo.removeAllElements();
    }

    /***************************************************************************/
    // return a vector with (label, value) pairs of blog property info
    private Vector getBlogPropInfo() {
        Vector blogCategoryInfo = new Vector();

        // get date
        String[] id = DateFormatter.getUserFriendlyDate(this.blog.getTimestamp());
        blogCategoryInfo.addElement(
                new String[]{Locale.get("blog.info.label.date") + ": ", id[0]});
        // get time
        blogCategoryInfo.addElement(
                new String[]{Locale.get("blog.info.label.time") + ": ", id[1]});
        // get blog size
        blogCategoryInfo.addElement(
                new String[]{Locale.get("blog.info.label.blogSize") + ": ", this.getBlogSize()});
        return blogCategoryInfo;
    }

    /***************************************************************************/
    // return a vector with (label, value) pairs of blog location info
    private Vector getBlogLocationInfo() {
        if (this.location == null) {
            return null;
        }
        Vector blogCategoryInfo = new Vector();

        // get user preferences
        Settings settings = this.controller.getSettings();
        int unit = WaypointToolkit.KILOMETER;
        if (settings.getDistanceUnit() == settings.DISTANCE_ML) {
            unit = WaypointToolkit.MILE;
        }

        // check if according waypoint is existing and resolved
        if (this.waypoint == null || !this.waypoint.isResolved()) {
            // get longitude
            String longitudeString = Double.toString(this.location.getLongitude());
            if (longitudeString.length() > 6) {
                longitudeString = longitudeString.substring(0, 6);
            }
            blogCategoryInfo.addElement(
                    new String[]{Locale.get("blog.info.label.longitude") + ": ", longitudeString});
            // get latitude
            String latitudeString = Double.toString(this.location.getLatitude());
            if (latitudeString.length() > 6) {
                latitudeString = latitudeString.substring(0, 6);
            }
            blogCategoryInfo.addElement(
                    new String[]{Locale.get("blog.info.label.latitude") + ": ", latitudeString});

        } else if (this.waypoint.isResolved()) {
            // get waypoint elevation
            if (this.waypoint.containsElevation()) {
                String elevation = WaypointToolkit.getUserFriendlyDistance(
                        this.waypoint.getElevation(),
                        unit,
                        true);
                blogCategoryInfo.addElement(
                        new String[]{Locale.get("blog.info.label.elevation") + ": ", elevation});
            }
            // get nearby place information
            if (this.waypoint.containsNearbyPlace() && this.waypoint.containsState()) {
                // show nearby place only if it is more specific than the state name
                if (!this.waypoint.getNearbyPlace().equals(this.waypoint.getState())) {
                    String place = this.waypoint.getNearbyPlace();
                    blogCategoryInfo.addElement(
                            new String[]{Locale.get("blog.info.label.place") + ": ", place});
                }
            }
            // get state information
            if (this.waypoint.containsState()) {
                String state = this.waypoint.getState();
                blogCategoryInfo.addElement(
                        new String[]{Locale.get("blog.info.label.state") + ": ", state});
            }
            // get country information
            if (this.waypoint.containsCountry()) {
                String country = this.waypoint.getCountry();
                if (this.waypoint.containsCountryCode()) {
                    country += " (" + this.waypoint.getCountryCode() + ")";
                }
                blogCategoryInfo.addElement(
                        new String[]{Locale.get("blog.info.label.country") + ": ", country});
            }
        }
        // get location info - method
        String methodValue = this.getLIPropertyValue(this.location.getLocationMethod(), LOCATION_INFO_METHOD);
        blogCategoryInfo.addElement(
                new String[]{Locale.get("blog.info.label.methodValue") + ": ", methodValue});

        // get location info - hybrid approach
        String hybridValue = this.getLIPropertyValue(this.location.getLocationMethod(), LOCATION_INFO_HYBRID);
        blogCategoryInfo.addElement(
                new String[]{Locale.get("blog.info.label.hybridValue") + ": ", hybridValue});

        // get speed if available
//        if(this.location.containsSpeed()) {
//            String speed = WaypointToolkit.getUserFriendlySpeed(
//                    this.location.getSpeed(),
//                    unit
//            );
//            blogCategoryInfo.addElement(
//                new String[] {Locale.get("blog.info.label.speed")+": ", speed}
//            );
//        }
        // get gps service quality if available
//        if(this.location.containsServiceQuality()) {
//            String quality = new Short(this.location.getServiceQuality()).toString();
//            blogCategoryInfo.addElement(
//                    new String[] {Locale.get("blog.info.label.serviceQuality")+": ", quality}
//            );
//        }

        // get location info - type
//        String typeValue = this.getLIPropertyValue(this.location.getLocationMethod(), this.LOCATION_INFO_TYPE);
//        blogCategoryInfo.addElement(
//                new String[] {Locale.get("blog.info.label.typeValue")+": ", typeValue}
//        );


//        // get number of gps satellites used to determine the location, if available
//        if(this.location.containsSatellitesCount()) {
//            String satCount = new Short(this.location.getSatellitesCount()).toString();
//            blogCategoryInfo.addElement(
//                    new String[] {Locale.get("blog.info.label.satelliteCount")+": ", satCount}
//            );
//        }
        return blogCategoryInfo;
    }

    /***************************************************************************/
    private Vector getBlogPhotoInfo() {
        if (this.photo == null) {
            return null;
        }
        Vector blogCategoryInfo = new Vector();

        // get photo content
        blogCategoryInfo.addElement(
                new Object[]{Locale.get("blog.info.label.content") + ": ", this.photo.getPreview()});
        // get photo resolution
        blogCategoryInfo.addElement(
                new String[]{Locale.get("blog.info.label.photoResolution") + ": ", this.getPhotoResolution()});
        // get photo encoding
        blogCategoryInfo.addElement(
                new String[]{Locale.get("blog.info.label.photoEncoding") + ": ", Locale.get("photoManager.photo.encoding.captureFormat")});
        return blogCategoryInfo;
    }

    /***************************************************************************/
    // return a vector with (label, value) pairs of blog message info
    private Vector getBlogMessageInfo() {
        if (this.message == null) {
            return null;
        }
        Vector blogCategoryInfo = new Vector();

        // get message content
        blogCategoryInfo.addElement(
                new String[]{Locale.get("blog.info.label.content") + ": ", this.message.getText()});
        // get message length
        blogCategoryInfo.addElement(
                new String[]{Locale.get("blog.info.label.messageLength") + ": ", Integer.toString(this.message.getText().length())});
        return blogCategoryInfo;
    }

    /***************************************************************************/
    private Image getBlogIcon() {
        if (this.photo != null) {
            return this.photo.getThumb();
        } else {
            return this.controller.BROWSER_MESSAGE_ICON;
        }
    }

    private String getPhotoResolution() {
        int[] res = this.photo.getDimension();
        // get photo size id
        String sizeID = this.photo.getSizeID();
        return sizeID + " (" + res[0] + " x " + res[1] + ")";
    }

    private String getBlogSize() {
        String blogSize = "n/a";
        int blogSizeBytes = -1;
        
        if(!this.blog.containsSizeBytes()) {
            blogSizeBytes = BlogManager.getInstance(this.controller).getBlogSize(this.blog);
        } else {
            blogSizeBytes = this.blog.getSizeBytes();
        }
        if (blogSizeBytes < 1024) {
            blogSize = blogSizeBytes + " bytes";
        } else {
            blogSize = blogSizeBytes / 1024 + " kb";
        }
        return blogSize;
    }

    private String getContentIndicator() {
        if (this.blogType == this.BLOG_PM) {
            // journey indicator
            String journeyFlag = "(" + Locale.get("blogInfoFormatter.indicator.notAvailable") + "/";
            if (this.blog.isJourneyBlog()) {
                journeyFlag = "(" + Locale.get("blogInfoFormatter.indicator.journey") + this.blog.getNumber() + "/";
            }

            // message indicator
            String messageFlag = Locale.get("blogInfoFormatter.indicator.notAvailable") + "/";
            if (this.message != null) {
                messageFlag = Locale.get("blogInfoFormatter.indicator.message") + "/";
            }

            // location indicator
            String waypointFlag = Locale.get("blogInfoFormatter.indicator.notAvailable") + ")";
            if (this.location != null) {
                waypointFlag = Locale.get("blogInfoFormatter.indicator.waypoint") + ")";
            }

            return journeyFlag + messageFlag + waypointFlag;

        } else if (this.blogType == this.BLOG_M) {
            // journey indicator
            String journeyFlag = "(" + Locale.get("blogInfoFormatter.indicator.notAvailable") + "/";
            if (this.blog.isJourneyBlog()) {
                journeyFlag = "(" + Locale.get("blogInfoFormatter.indicator.journey") + this.blog.getNumber() + "/";
            }

            // location indicator
            String waypointFlag = Locale.get("blogInfoFormatter.indicator.notAvailable") + ")";
            if (this.location != null) {
                waypointFlag = Locale.get("blogInfoFormatter.indicator.waypoint") + ")";
            }

            return journeyFlag + waypointFlag;
        } else {
            return "n/a";
        }
    }

    private String getWaypointIndicator() {
        String[] info = this.traceManager.getWaypointInformation(this.blog.getTimestamp());

        String intervalFlag = "(" + Locale.get("blogInfoFormatter.indicator.notAvailable") + ")";
        if (info != null) {
            intervalFlag = "(" + info[1] + ")";
        }

        String distanceFlag = "(" + Locale.get("blogInfoFormatter.indicator.notAvailable") + ")";
        if (info != null) {
            distanceFlag = "(" + info[0] + ")";
        }

        return intervalFlag + "\n" + distanceFlag;
    }

    // parses and returns a specific value of a "location method info string"
    private String getLIPropertyValue(String locationInfo, int prop) {
        String startSequence = "";
        String delimiter = "$";
        switch (prop) {
            case BlogInfoFormatter.LOCATION_INFO_TYPE:
                startSequence = "T:";
                break;
            case BlogInfoFormatter.LOCATION_INFO_METHOD:
                startSequence = "M:";
                break;
            case BlogInfoFormatter.LOCATION_INFO_HYBRID:
                startSequence = "H:";
                break;
            default:
        }
        int startIndex = locationInfo.indexOf(startSequence) + startSequence.length();
        if (startIndex == -1) {
            return locationInfo;
        }

        int endIndex = locationInfo.indexOf(delimiter, startIndex);
        if (endIndex == -1) {
            endIndex = locationInfo.length() - 1;
        }

        return locationInfo.substring(startIndex, endIndex);
    }
}
