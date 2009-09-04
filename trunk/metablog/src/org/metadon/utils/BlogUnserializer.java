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

import org.metadon.beans.BlogMessage;
import org.metadon.beans.BlogMultimedia;
import org.metadon.beans.Message;
import org.metadon.beans.Photo;
import org.metadon.extern.location.GPSLocation;

import java.util.Stack;
import java.util.Enumeration;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;


import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class BlogUnserializer {

    private Stack blogBin = null;
    private int blogType;
    private final int BLOG_PM = Integer.valueOf(Locale.get("blog.PM.id")).intValue();
    private final int BLOG_M = Integer.valueOf(Locale.get("blog.M.id")).intValue();

    /**
     * Creates a new instance of BlogSerializer
     */
    public BlogUnserializer() {
        super();
    }

    public Stack unserialize(Stack records, int blogType) {
        this.blogType = blogType;
        this.blogBin = new Stack();

        try {
            if (this.blogType == this.BLOG_PM) {
                return this.unserializeBlogPMThumb(records);
            } else if (this.blogType == this.BLOG_M) {
                return this.unserializeBlogM(records);
            }
        } catch (IOException ioe) {
            //#debug error
            System.out.println("Unserializing Blog: " + ioe);
            return null;
        }
        return null;
    }

    // unserialize photo object as thumbnail blog browsing purposes
    private Stack unserializeBlogPMThumb(Stack records) throws IOException {
        if (records == null) {
            return null;
        }

        Enumeration enumeration = records.elements();
        while (enumeration.hasMoreElements()) {

            BlogMultimedia blogMultimedia = new BlogMultimedia();
            Photo photo = new Photo();
            Message message = null;
            GPSLocation gpsLocation = null;

            ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) enumeration.nextElement());
            DataInputStream dis = new DataInputStream(bais);

            // set by blog manager
            blogMultimedia.setTimestamp(dis.readLong());

            blogMultimedia.setTimestampPost(dis.readLong());

            // unserialize flags
            blogMultimedia.containsLocation = dis.readBoolean();
            blogMultimedia.isJourneyBlog = dis.readBoolean();
            blogMultimedia.containsMessage = dis.readBoolean();
            blogMultimedia.containsSizeBytes = dis.readBoolean();

            if (blogMultimedia.containsSizeBytes()) {
                blogMultimedia.setSizeBytes(dis.readInt());
            }

            if (blogMultimedia.containsLocation()) {
                gpsLocation = this.unserializeLocation(dis);
            }

            if (blogMultimedia.isJourneyBlog()) {
                blogMultimedia.setNumber(dis.readInt());
                blogMultimedia.setJourneyName(dis.readUTF());
            }

            if (blogMultimedia.containsMessage()) {
                message = new Message(dis.readUTF());
            }

            // set by photo formatter
            photo.setSizeID(dis.readUTF());
            ////#debug
            System.out.println("thumb: size ID: " + photo.getSizeID());

            // unserialize thumb dimension values
            int widthT = dis.readInt();
            int heightT = dis.readInt();
            int[] rawDataT = new int[widthT * heightT];
            ////#debug
            System.out.println("thumb: unserialized thumb dimension: " + widthT + " x " + heightT);

            // unserialize preview dimension values
            int widthP = dis.readInt();
            int heightP = dis.readInt();
            int[] rawDataP = new int[widthP * heightP];
            ////#debug
            System.out.println("thumb: unserialized preview dimension: " + widthP + " x " + heightP);

            // set by photo oject
            // unserialize original image dimension values
            int[] dimOrig = new int[2];
            dimOrig[0] = dis.readInt();
            dimOrig[1] = dis.readInt();
            photo.setDimension(dimOrig);
            ////#debug
            System.out.println("thumb: unserialized orig. dimension: " + dimOrig[0] + " x " + dimOrig[1]);

            // unserialize thumb raw data length value
            int lengthT = dis.readInt();

            // unserialize the image raw data
            for (int i = 0; i < lengthT; i++) {
                rawDataT[i] = dis.readInt();
            }
            photo.setThumb(Image.createRGBImage(rawDataT, widthT, heightT, false));
            ////#debug
            System.out.println("thumb: unserialized thumb data: " + photo.getThumb().getWidth() + " x " + photo.getThumb().getHeight());

            // unserialize preview raw data length value
            int lengthP = dis.readInt();

            // unserialize the image raw data
            for (int i = 0; i < lengthP; i++) {
                rawDataP[i] = dis.readInt();
            }
            photo.setPreview(Image.createRGBImage(rawDataP, widthP, heightP, false));
            ////#debug
            System.out.println("thumb: unserialized preview data: " + photo.getPreview().getWidth() + " x " + photo.getPreview().getHeight());

            dis.close(); // closes bais as well

            blogMultimedia.setPhoto(photo);
            blogMultimedia.setMessage(message);
            blogMultimedia.setLocation(gpsLocation);
            this.blogBin.push(blogMultimedia);
        }
        return this.blogBin;
    }

    // serialize message object for storage
    private Stack unserializeBlogM(Stack records) throws IOException {
        if (records == null) {
            return null;
        }

        Enumeration enumeration = records.elements();
        while (enumeration.hasMoreElements()) {

            BlogMessage blogMessage = new BlogMessage();
            Message message = null;
            GPSLocation gpsLocation = null;

            ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) enumeration.nextElement());
            DataInputStream dis = new DataInputStream(bais);

            blogMessage.setTimestamp(dis.readLong());
            blogMessage.setTimestampPost(dis.readLong());
            blogMessage.containsLocation = dis.readBoolean();
            blogMessage.isJourneyBlog = dis.readBoolean();
            blogMessage.containsSizeBytes = dis.readBoolean();

            if (blogMessage.containsSizeBytes()) {
                blogMessage.setSizeBytes(dis.readInt());
            }

            if (blogMessage.containsLocation()) {
                gpsLocation = this.unserializeLocation(dis);
            }

            if (blogMessage.isJourneyBlog()) {
                blogMessage.setNumber(dis.readInt());
                blogMessage.setJourneyName(dis.readUTF());
            }

            message = new Message(dis.readUTF());

            // unserialize location
            dis.close(); // closes bais as well

            blogMessage.setMessage(message);
            blogMessage.setLocation(gpsLocation);
            this.blogBin.push(blogMessage);
        }
        return this.blogBin;
    }

    public byte[] getPhotoData(Stack serializedPhoto) throws IOException {
        //#debug
//#         System.out.println("serPhotolength: "+serializedPhoto.size());
        ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) serializedPhoto.firstElement());
        DataInputStream dis = new DataInputStream(bais);

        // ignore RMS flags
        dis.readLong();
        dis.readLong();
        dis.readBoolean();
        dis.readBoolean();

        int length = dis.readInt();
        byte[] data = new byte[length];
        dis.readFully(data, 0, length);

        dis.close(); // closes bais as well
        return data;
    }

    private GPSLocation unserializeLocation(DataInputStream dis) throws IOException {
        //#debug
//#         System.out.println("start unser. location...");
        // unserialize fundamental properties
        double longitude = dis.readDouble();
        double latitude = dis.readDouble();
        GPSLocation gpsLocation = new GPSLocation(longitude, latitude);

        // location method
        gpsLocation.setLocationMethod(dis.readUTF());

//        // satellite count
//        gpsLocation.containsSatellitesCount = dis.readBoolean();
//        if(gpsLocation.containsSatellitesCount())
//            gpsLocation.setSatellitesCount(dis.readShort());

//        gpsLocation.containsSpeed = dis.readBoolean();
//        if(gpsLocation.containsSpeed())
//            gpsLocation.setSpeed(dis.readDouble());

//        gpsLocation.containsServiceQuality = dis.readBoolean();
//        if(gpsLocation.containsServiceQuality())
//            gpsLocation.setServiceQuality(dis.readShort());

        return gpsLocation;
    }
}
