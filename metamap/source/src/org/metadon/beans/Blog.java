/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.metadon.beans;

public class Blog {

    int id;
    long timestamp;
    String date;
    String journeyName;
    String message;
    String photoPath;
    String photoPathThumb;
    int photoWidth;
    int photoHeight;
    int onDemand;
    

    // id
    public void setID(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }
    
     // timestamp
    public void setTimestamp(long ts) {
        this.timestamp = ts;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
    
    // date
    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return this.date;
    }

     // journey name
    public void setJourneyName(String jname) {
        this.journeyName = jname;
    }

    public String getJourneyName() {
        return this.journeyName;
    }
    
    // message
    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    // photo path
    public void setPhotoPath(String path) {
        this.photoPath = path;
    }

    public String getPhotoPath() {
        return this.photoPath;
    }
    
    // photo path for thumbnail
    public void setPhotoPathThumb(String path) {
        this.photoPathThumb = path;
    }

    public String getPhotoPathThumb() {
        return this.photoPathThumb;
    }
    
    // photo dimensions
    public void setPhotoWidth(int width) {
        this.photoWidth = width;
    }

    public int getPhotoWidth() {
        return this.photoWidth;
    }
    
    public void setPhotoHeight(int height) {
        this.photoHeight = height;
    }

    public int getPhotoHeight() {
        return this.photoHeight;
    }
    
    // defines wheather the blog content will be delivered on demand or immediately
    public void setOnDemand(int onDemand) {
        this.onDemand = onDemand;
    }

    public int getOnDemand() {
        return this.onDemand;
    }
}
