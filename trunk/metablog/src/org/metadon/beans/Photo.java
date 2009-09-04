/*
 * Photo.java
 *
 * Created on 08. November 2007, 23:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.beans;

import javax.microedition.lcdui.Image;

/**
 *
 * @author Hannes
 */
public class Photo {
    
    private String sizeID = "N/A";
    private byte[] data = null;
    private Image thumb = null;
    private Image preview = null;
    private int[] dimension = new int[2];
    
    public final String PHOTO_FULL = "FULL";
    public final String PHOTO_THUMB = "THUMB";
    
    /** Creates a new instance of Photo */
    public Photo() {
        super();
    }
    
    public void set(byte[] i) {
        this.data = i;
    }
    
    public byte[] get() {
        return this.data;
    }
    
    public void setThumb(Image t) {
        this.thumb = t;
    }
    
    public Image getThumb() {
        return this.thumb;
    }
    
    public void setPreview(Image p) {
        this.preview = p;
    }
    
    public Image getPreview() {
        return this.preview;
    }
    
    public void setDimension(int[] dim) {
        this.dimension = dim;
    }
    
    public int[] getDimension() {
        return this.dimension;
    }
    
    // sets photo size id
    public void setSizeID(String sid) {
        this.sizeID = sid;
    }
    
    // gets photo size id
    public String getSizeID() {
        return this.sizeID;
    }
    
}

