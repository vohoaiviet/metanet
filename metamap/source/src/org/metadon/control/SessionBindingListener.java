/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.metadon.control;

import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionBindingEvent;

import org.metadon.beans.Blog;
import org.metadon.beans.Photo;
import org.metadon.beans.Waypoint;
import org.metadon.utils.DateFormatter;
import org.metadon.utils.UserRepositoryManager;


import java.sql.*;
import java.util.Date;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;


/**
 *
 * @author Hannes
 */
public class SessionBindingListener implements HttpSessionBindingListener {

    private String username;
    private javax.sql.DataSource dataSource = null;
    private java.sql.Connection connection = null;
    private Hashtable blogTable;
    private Vector<Waypoint> waypointList;
    private long latestBlog = 0;
    private int coordinatesLength = 10;
    
    private final String thumbnailPrefix = "thumb_";

    public SessionBindingListener(javax.sql.DataSource ds, String username) {
        this.dataSource = ds;
        this.username = username;
    }

    public void valueBound(HttpSessionBindingEvent ev) {
        System.out.println("[" + new Date() + "] bound at session: " + ev.getSession().getId());
        System.out.println("connecting database and loading blogs...");
        try {
            this.connection = this.dataSource.getConnection();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        this.blogTable = new Hashtable();
        this.waypointList = new Vector();
    }

    public void valueUnbound(HttpSessionBindingEvent ev) {
        System.out.println("[" + new Date() + "] unbound from session: " + ev.getSession().getId());
        System.out.println("close database connection...");
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.blogTable = null;
            this.waypointList = null;
        }
    }

    // returns a list of all initially available waypoints
    public Vector<Waypoint> getWaypointList() {
        this.latestBlog = 0;
        this.updateBlogList();
        return this.waypointList;
    }

    // returns a list of all waypoints which have been added during the web session
    // through the mobile client
    public Vector<Waypoint> getWaypointUpdateList() {
        this.scheduleUpdater();
        return this.waypointList;
    }

    // returns a list of all blogs which have a small content like text messages
    // (all other blogs must be loaded on demand with getBlog(id))
    public Vector<Blog> getBlogList() {
        Vector<Blog> blogList = new Vector();
        Enumeration<Blog> enumeration = this.blogTable.elements();
        while (enumeration.hasMoreElements()) {
            Blog blog = enumeration.nextElement();
            if (blog.getOnDemand() == 0) {
                blogList.add(blog);
            }
        }
        System.out.println("returning blog list...");
        return blogList;
    }

    // returns a specific blog
    public Blog getBlog(int id) {
        System.out.println("returning on demand blog: " + id);
        return (Blog) this.blogTable.get(id);
    }

    /*************************************************************************/
    private void updateBlogList() {
        if (this.connection == null) {
            return;
        }
        try {
            if (this.latestBlog == 0) {
                System.out.println("loading whole blog list...");
                this.loadBlogList();
            } else {
                System.out.println("update blog list...");
                // db interaction
                Statement stmt = connection.createStatement();
                ResultSet rsUpdatedBlogs = stmt.executeQuery("SELECT id, timestamp, longitude, latitude, elevation, journeyname, message, photo_id " +
                        "FROM blog WHERE username='" + this.username + "'" +
                        "GROUP BY id HAVING (longitude >'-1.0'" + "AND latitude >'-1.0'" + "AND id > " + this.latestBlog + ") ORDER BY id");
                this.processData(rsUpdatedBlogs);
            }
        } catch (SQLException sqle) {
            System.out.println("sql error: " + sqle.getMessage());
        }
    }

    private void loadBlogList() throws SQLException {
        // db interaction
        Statement stmt = connection.createStatement();
        ResultSet rsBlogs = stmt.executeQuery("SELECT id, timestamp, longitude, latitude, elevation, journeyname, message, photo_id " +
                "FROM blog WHERE username='" + this.username + "'" +
                "GROUP BY id HAVING (longitude >'-1.0'" + "AND latitude >'-1.0') ORDER BY id");
        this.processData(rsBlogs);
    }

    private void processData(ResultSet rs) throws SQLException {
        while (rs.next()) {
            int id = rs.getInt(1);
            long timestamp = Long.valueOf(rs.getString(2)).longValue();
            double longitude = rs.getDouble(3);
            double latitude = rs.getDouble(4);
            
            String longitudeString = Double.toString(longitude);
            String latitudeString = Double.toString(latitude);
            
            if(longitudeString.length() > this.coordinatesLength) {
                longitude = Double.valueOf(longitudeString.substring(0, this.coordinatesLength));
            }
            if(latitudeString.length() > this.coordinatesLength) {
                latitude = Double.valueOf(latitudeString.substring(0, this.coordinatesLength));
            }
            
            double elevation = rs.getDouble(5);
            String journeyName = rs.getString(6);
            String message = rs.getString(7);
            int photoId = rs.getInt(8);

            Blog blog = new Blog();
            blog.setID(id);
            blog.setTimestamp(timestamp);
            blog.setDate(DateFormatter.getUserFriendlyDate(timestamp)[0] +"\u0020"+ DateFormatter.getUserFriendlyDate(timestamp)[1]);
            if (journeyName == null) {
                blog.setJourneyName("NULL");
            } else {
                blog.setJourneyName(journeyName);
            }
            if (message == null) {
                blog.setMessage("NULL");
            } else {
                blog.setMessage(message);
            }
            if (photoId == 0) {
                blog.setPhotoPath("NULL");
                blog.setPhotoPathThumb("NULL");
                blog.setPhotoWidth(-1);
                blog.setPhotoHeight(-1);
                System.out.println("id: " + id + " - message blog.");
            } else {
                Photo photo = this.getPhoto(photoId);
                if(photo != null) {
                    blog.setPhotoPath(UserRepositoryManager.getPhotoRepositoryPath(this.username) + photo.getName());
                    blog.setPhotoPathThumb(UserRepositoryManager.getPhotoRepositoryPath(this.username) + this.thumbnailPrefix + photo.getName());

                    blog.setPhotoWidth(photo.getWidth());
                    blog.setPhotoHeight(photo.getHeight());

                    System.out.println("id: " + id + " - photo blog.");
                } else {
                    System.out.println("no photo found in db.");
                }
            }
            blog.setOnDemand(0);
            Waypoint waypoint = new Waypoint(id, longitude, latitude, elevation);
            // update blog table
            this.blogTable.put(blog.getID(), blog);
            // update waypoint list
            this.waypointList.addElement(waypoint);
            this.latestBlog = blog.getID();
        }
        rs.close();
        System.out.println("listener: blogs found for user " + this.username + ": " + blogTable.size());
        System.out.println("listener: latest blog id: " + this.latestBlog);
        System.out.println("listener: waypoints update: " + this.waypointList.size());
    }
    
    private Photo getPhoto(int photoId) throws SQLException {
        Photo photo = null;
        // db interaction
        Statement stmt = connection.createStatement();
        ResultSet rsPhoto = stmt.executeQuery("SELECT name, size, width, height " +
                "FROM photo WHERE id='" + photoId + "'");
        if(rsPhoto.next()) {
            photo = new Photo();
            photo.setID(photoId);
            photo.setName(rsPhoto.getString(1));
            photo.setSize(rsPhoto.getInt(2));
            photo.setWidth(rsPhoto.getInt(3));
            photo.setHeight(rsPhoto.getInt(4));
        }
        stmt.close();
        return photo;
    }

    /*************************************************************************/
    public void scheduleUpdater() {
        if (this.connection == null) {
            return;
        }
        // remove old waypoints
        this.waypointList.removeAllElements();
        // remove old blogs
        this.blogTable.clear();
        try {
            // db interaction
            Statement stmt = connection.createStatement();
            ResultSet rsUserMobile = stmt.executeQuery("SELECT * FROM trackeduser " +
                    "WHERE username='" + this.username + "'");
            if (rsUserMobile.next()) {
                updateBlogList();
            }
            rsUserMobile.close();
        } catch (SQLException sqle) {
            System.out.println("sql error: " + sqle.getMessage());
        }
    }
}
