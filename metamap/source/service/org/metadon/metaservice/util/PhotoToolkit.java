package org.metadon.metaservice.util;

import java.io.*;

import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.ImageIcon;

import javax.imageio.ImageIO;
import com.sun.image.codec.jpeg.*;

public class PhotoToolkit {
	
	private static final int[] thumbnailDimension = new int[] {160, 120};

    public PhotoToolkit() {
    }

    public static BufferedImage decodeAsJPEG(byte[] data) {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try {
            System.out.println("get jpeg decoder...");
            JPEGImageDecoder dec = JPEGCodec.createJPEGDecoder(bis);
            System.out.println("creating buffered image...");
            BufferedImage bi = dec.decodeAsBufferedImage();

            int width = bi.getWidth();
            System.out.println("found photo width: " + width);

            int height = bi.getHeight();
            System.out.println("found photo height: " + height);

            bis.close();
            return bi;

        } catch (IOException e) {
            System.out.println("IO Exception decoding jpeg photo: " + e.getMessage());
            return null;
        }
    }

    public static boolean storeAsPhoto(String path, BufferedImage bi) {
        System.out.println("storing photo as file: " + path);
        try {
            ImageIO.write(bi, "jpg", new File(path));
            System.out.println("photo stored.");
        } catch (IOException e) {
            System.out.println("IO Exception storing photo: " + e.getMessage());
            return false;
        }
        return true;
    }
    
    // create a thumbnail for google maps
    public static BufferedImage getThumbnail(BufferedImage s) {
    	Image source = (Image)s;
    	
    	// scale image
    	Image tempScaled = source.getScaledInstance(thumbnailDimension[0], thumbnailDimension[1], Image.SCALE_DEFAULT);
    	
    	// ensure that all pixels in the image are loaded.
    	Image temp = new ImageIcon(tempScaled).getImage();
    	
    	// create new buffered image with scaled dimensions
    	BufferedImage thumbnail = new BufferedImage(temp.getWidth(null), temp.getHeight(null), BufferedImage.TYPE_INT_RGB);
    	
    	// copy image to buffered image
    	Graphics g = thumbnail.createGraphics();
    	// clear background and paint the image.
    	g.setColor(Color.white);
    	g.fillRect(0, 0, temp.getWidth(null), temp.getHeight(null));
    	g.drawImage(temp, 0, 0, null);
    	g.dispose();
    	
    	return thumbnail;
    }
}
