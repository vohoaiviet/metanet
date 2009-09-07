/*
 * BlogSerializer.java
 *
 * Created on 14. November 2007, 22:02
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
import org.metadon.location.GPSLocation;

import java.util.Stack;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;


import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class BlogSerializer extends Thread {

    private BlogMultimedia blogMultimedia = null;
    private BlogMessage blogMessage = null;
    private GPSLocation gpsLocation = null;
    private Photo photo = null;
    private Message message = null;
    private byte[] rawData = null;
    private Image imageThumb = null;
    private Image imagePreview = null;
    private Stack recordBin = null;
    private int blogType;
    private final int BLOG_PM = Integer.valueOf(Locale.get("blog.PM.id")).intValue();
    private final int BLOG_M = Integer.valueOf(Locale.get("blog.M.id")).intValue();

    /**
     * Creates a new instance of BlogSerializer
     */
    public BlogSerializer(Blog blog) {
        super();
        this.recordBin = new Stack();

        if (blog instanceof BlogMultimedia) {
            this.blogType = this.BLOG_PM;
            this.blogMultimedia = (BlogMultimedia) blog;
            if (this.blogMultimedia != null) {
                this.gpsLocation = this.blogMultimedia.getLocation();
                this.photo = this.blogMultimedia.getPhoto();
                if (this.photo != null) {
                    this.rawData = this.photo.get();
                    this.imageThumb = this.photo.getThumb();
                    this.imagePreview = this.photo.getPreview();
                }
            }
        } else if (blog instanceof BlogMessage) {
            this.blogType = this.BLOG_M;
            this.blogMessage = (BlogMessage) blog;
            if (this.blogMessage != null) {
                this.message = this.blogMessage.getMessage();
                this.gpsLocation = this.blogMessage.getLocation();
            }
        }
    }

    // starts serializing process
    public void run() {
        try {
            if (this.blogType == this.BLOG_PM) {
                this.serializePhoto();
                this.serializeBlogPMThumb();
            } else if (this.blogType == this.BLOG_M) {
                this.serializeBlogM();
            }
        } catch (IOException ioe) {
            //#debug error
            System.out.println("Serializing Blog: " + ioe);
        }
    }

    // serialize photo object as thumbnail for storage and blog browsing purposes
    private void serializeBlogPMThumb() throws IOException {
        if (this.photo == null) {
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // thumbnail dimension values
        int widthT = this.imageThumb.getWidth();
        int heightT = this.imageThumb.getHeight();
        int[] rgbDataT = new int[widthT * heightT];

        // preview dimension values
        int widthP = this.imagePreview.getWidth();
        int heightP = this.imagePreview.getHeight();
        int[] rgbDataP = new int[widthP * heightP];

        // !!! FIRST VALUE MUST BE TIMESTAMP - USED WITH RMS !!!
        dos.writeLong(this.blogMultimedia.getTimestamp());

        // !!! SECOND VALUE MUST BE TIMESTAMP POSTED - USED WITH RMS !!!
        dos.writeLong(this.blogMultimedia.getTimestampPost());

        // !!! THIRD VALUE MUST BE LOCATION FLAG - USED WITH RMS !!!
        dos.writeBoolean(this.blogMultimedia.containsLocation());

        // !!! FOURTH VALUE MUST BE JOURNEY FLAG - USED WITH RMS !!!
        dos.writeBoolean(this.blogMultimedia.isJourneyBlog());

        dos.writeBoolean(this.blogMultimedia.containsMessage());
        dos.writeBoolean(this.blogMultimedia.containsSizeBytes());

        // serialize size
        if (this.blogMultimedia.containsSizeBytes()) {
            dos.writeInt(this.blogMultimedia.getSizeBytes());
        }

        // serialize location
        if (this.blogMultimedia.containsLocation()) {
            this.serializeLocation(dos);
        }

        // serialize journey info
        if (this.blogMultimedia.isJourneyBlog()) {
            dos.writeInt(this.blogMultimedia.getNumber());
            dos.writeUTF(this.blogMultimedia.getJourneyName());
        }

        // serialize message
        if (this.blogMultimedia.containsMessage()) {
            dos.writeUTF(this.blogMultimedia.getMessage().getText());
        }

        dos.writeUTF(this.photo.getSizeID());

        // get thumbnail image raw data
        this.imageThumb.getRGB(rgbDataT, 0, widthT, 0, 0, widthT, heightT);
        dos.writeInt(widthT);
        dos.writeInt(heightT);

        // get preview image raw data
        this.imagePreview.getRGB(rgbDataP, 0, widthP, 0, 0, widthP, heightP);
        dos.writeInt(widthP);
        dos.writeInt(heightP);

        // additionally serialize original dimension values
        dos.writeInt(this.photo.getDimension()[0]);
        dos.writeInt(this.photo.getDimension()[1]);

        // serialize thumb raw data length value for thumb reconstruction
        dos.writeInt(rgbDataT.length);

        // serialize thumbnail raw data
        for (int i = 0; i < rgbDataT.length; i++) {
            dos.writeInt(rgbDataT[i]);
        }

        // serialize preview raw data length value for thumb reconstruction
        dos.writeInt(rgbDataP.length);

        // serialize preview raw data
        for (int i = 0; i < rgbDataP.length; i++) {
            dos.writeInt(rgbDataP[i]);
        }

        dos.flush();
        dos.close(); // closes baos as well

        // put serialized record into record bin
        this.recordBin.push(baos.toByteArray());
    //#debug
//#     System.out.println("thumb record in bin!");
    }

    // serialize message object for storage
    private void serializeBlogM() throws IOException {
        if (this.message == null) {
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // !!! FIRST VALUE MUST BE TIMESTAMP - USED WITH RMS !!!
        dos.writeLong(this.blogMessage.getTimestamp());

        // !!! SECOND VALUE MUST BE TIMESTAMP POSTED - USED WITH RMS !!!
        dos.writeLong(this.blogMessage.getTimestampPost());

        // !!! THIRD VALUE MUST BE LOCATION FLAG - USED WITH RMS !!!
        // serialize location storage flag
        dos.writeBoolean(this.blogMessage.containsLocation());

        // !!! FOURTH VALUE MUST BE JOURNEY FLAG - USED WITH RMS !!!
        dos.writeBoolean(this.blogMessage.isJourneyBlog());

        dos.writeBoolean(this.blogMessage.containsSizeBytes());

        // serialize size
        if (this.blogMessage.containsSizeBytes()) {
            dos.writeInt(this.blogMessage.getSizeBytes());
        }

        // serialize location
        if (this.blogMessage.containsLocation()) {
            this.serializeLocation(dos);
        }

        // serialize journey info
        if (this.blogMessage.isJourneyBlog()) {
            dos.writeInt(this.blogMessage.getNumber());
            dos.writeUTF(this.blogMessage.getJourneyName());
        }

        dos.writeUTF(this.message.getText());

        this.recordBin.push(baos.toByteArray());
    //#debug
//#         System.out.println("message record in bin!");
    }

    // return the filled bin
    public Stack getRecordBin() {
        return this.recordBin;
    }

    // serialize photo object for storage
    private void serializePhoto() throws IOException {
        if (this.photo.get() == null) {
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        // !!! FIRST VALUE MUST BE TIMESTAMP - USED WITH RMS !!!
        dos.writeLong(this.blogMultimedia.getTimestamp());

        // !!! SECOND VALUE MUST BE TIMESTAMP POSTED - USED WITH RMS !!!
        dos.writeLong(this.blogMultimedia.getTimestampPost());

        // !!! THIRD VALUE MUST BE LOCATION FLAG - USED WITH RMS !!!
        dos.writeBoolean(this.blogMultimedia.containsLocation());

        // !!! FOURTH VALUE MUST BE JOURNEY FLAG - USED WITH RMS !!!
        dos.writeBoolean(this.blogMultimedia.isJourneyBlog());

        // serialize raw data length value for image reconstruction
        dos.writeInt(rawData.length);

        // write raw data
        dos.write(this.rawData, 0, this.rawData.length);

        dos.flush();
        dos.close(); // closes baos as well

        // put serialized record into record bin
        this.recordBin.push(baos.toByteArray());
    //#debug
//#         System.out.println("full record in bin!");
    }

    private void serializeLocation(DataOutputStream dos) throws IOException {
        //#debug
//#         System.out.println("serialize location");
        // fundamental properties
        dos.writeDouble(this.gpsLocation.getLongitude());
        dos.writeDouble(this.gpsLocation.getLatitude());

        // location method
        dos.writeUTF(this.gpsLocation.getLocationMethod());

    // satellites count
//        dos.writeBoolean(this.gpsLocation.containsSatellitesCount());
//        if(this.gpsLocation.containsSatellitesCount())
//            dos.writeShort(this.gpsLocation.getSatellitesCount());

    // speed
//        dos.writeBoolean(this.gpsLocation.containsSpeed());
//        if(this.gpsLocation.containsSpeed())
//            dos.writeDouble(this.gpsLocation.getSpeed());

    // service quality
//        dos.writeBoolean(this.gpsLocation.containsServiceQuality());
//        if(this.gpsLocation.containsServiceQuality())
//            dos.writeShort(this.gpsLocation.getServiceQuality());

    // course
//        dos.writeInt(this.gpsLocation.getCourse());

    }
}
