/*
 * PhotoManager.java
 *
 * Created on 05. November 2007, 12:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.control;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Displayable;

import java.util.Stack;
import java.io.IOException;
import javax.microedition.media.*;
import javax.microedition.media.control.VideoControl;

import org.metadon.beans.Photo;
import org.metadon.beans.Settings;
import org.metadon.info.DisplayInfoCollector;
import org.metadon.view.PhotoCaptureScreen;
import org.metadon.view.PhotoViewerScreen;


import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class PhotoManager {
    
    private volatile static PhotoManager uniqueInstance;
    
    private PhotoFormatter formatter = null;
    private PhotoCaptureScreen captureScreen;
    private PhotoViewerScreen viewerScreen;
    private Controller controller;
    
    private int[] grabbedDimension = new int[2];
    private byte[] rawData = null;
    private Photo formattedPhoto = null;
    protected Stack photoBin = null;
    
    private Player player;
    private VideoControl videoControl;
    private Displayable next;
    private boolean memoryProblem = false;
    
    private String encoding;
    private String photoSizeID = "n/a";
    private boolean init = false;
    private boolean active = false;
    private int[] canvasDimension = new int[2];
    
    
    private PhotoManager(Controller c) {
        super();
        this.controller = c;
        // create once on application startup
        this.captureScreen = new PhotoCaptureScreen(this);  
        this.viewerScreen = new PhotoViewerScreen(this);
        this.photoBin = new Stack();
        this.formatter = new PhotoFormatter();
    }
    
    // singletone
    public static synchronized PhotoManager getInstance(Controller c) {
        if(uniqueInstance == null) {
            //synchronize concurrent threads on creating a instance (performance decrease!)
            synchronized (PhotoManager.class) {
                if(uniqueInstance == null) {
                    uniqueInstance = new PhotoManager(c);
                }
            }
        }
        return uniqueInstance;
    }
    
    public void init() {
        //#if polish.hasCamera
//#             this.init = true;
        //#endif
        //#if polish.ScreenWidth:defined && polish.ScreenHeight:defined
//#             // get screen dimension from polish framework during preprocessing
            //#= this.canvasDimension[0] = ${polish.ScreenWidth};
            //#= this.canvasDimension[1] = ${polish.ScreenHeight};
        //#else
            // get screen dimension trough application intern helper class
            this.canvasDimension = DisplayInfoCollector.getCanvasDimension();
        //#endif
    }
    
    public void mandatePhoto() {
        if(!this.init) {
            this.controller.show(this.controller.CONF_ASCR, Locale.get("alert.notSupported"), null, null);
        } if(this.memoryProblem) {
            this.controller.show(this.controller.ERROR_ASCR, Locale.get("photoManager.alert.memoryProblem"), null, null);
        } else {
            // return screen in case of error or cancel
            this.next = this.controller.getScreen(this.controller.BLOG_SCR);
             try {
                this.encoding = this.initVideoSettings();
            } catch(NumberFormatException nfe) {
                //#debug error
                System.out.println("init photo manager: "+nfe);
            }
            ////#debug
            System.out.println("encoding: "+this.encoding);
            this.showVideo();
        }
    }
    
    public void showVideo() {
        try {
            if(this.player == null) {
                // create player for the camera
                this.player = Manager.createPlayer("capture://video");
                ////#debug
                System.out.println("player in state:"+this.player.getState());
                this.player.realize();
                ////#debug
                System.out.println("player in state:"+this.player.getState());
                // allocate necessary resources before start - reduces startup time
                this.player.prefetch();
                ////#debug
                System.out.println("player in state:"+this.player.getState());
            }
            if(this.videoControl == null) {
                // grab the videocontrol and set it to the current display.
                this.videoControl = (VideoControl)this.player.getControl("VideoControl");
                ////#debug
                System.out.println("videocontrol grabbed");
            }
            if(this.videoControl == null) {
                this.discardPlayer();              
            } else {
                // for rendering video directly on the canvas
                this.videoControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, this.captureScreen);
                this.startCamera();
            }
        } catch (IOException ioe) {
        	this.discardPlayer();
                //#debug error
                System.out.println("Init Capture Screen - IO Exception "+ioe);
        } catch (MediaException me) {
	    	this.discardPlayer();
                //#debug error
                System.out.println("Init Capture Screen - Media Exception "+me);
    	} catch (SecurityException se) {
    		this.discardPlayer();
                //#debug error
                System.out.println("Init Capture Screen - Security Exception "+se);
        } catch (IllegalStateException ise) {
    		this.discardPlayer();
                //#debug error
                System.out.println("Init Capture Screen - Illegal State: "+ise);
        } catch (IllegalArgumentException iae) {
                this.discardPlayer();
                //#debug error
                System.out.println("Init Capture Screen - Illegal Argument: "+iae);
        } catch (Exception e) {
    		this.discardPlayer();
                //#debug error
                System.out.println("Init Capture Screen: "+e);
        }
    }
    
      // start camera
    private synchronized void startCamera() {
        if (this.player != null && !this.active) {
            try{
                this.videoControl.setDisplayLocation(0, 30);
                this.videoControl.setVisible(true);
                this.player.start();
                // show capture screen
                this.controller.show(this.captureScreen);
            }catch(MediaException me) {
                //#debug error
                System.out.println("Start Camera - Media Exception "+me);
            }catch(SecurityException se) {
                //#debug error
                System.out.println("Start Camera - Security Exception "+se);
            }
            this.active = true;
        }
    }
    
    // stop camera
    public synchronized void stopCamera() {
        if (this.player != null && this.active) {
            try {
                this.videoControl.setVisible(false);
                this.player.stop();
                ////#debug
                System.out.println("player in state:"+this.player.getState());
                this.player.deallocate();
            } catch(MediaException me) {
                //#debug error
                System.out.println("Stop Camera - Media Exception "+me);
            }
            this.active = false;
        }
    }
    
     public void takeSnapshot() {
        this.captureScreen.setStatus(PhotoCaptureScreen.STATUS_INITIALIZING);
        // disable commands during capture phase
        this.captureScreen.removeCommand(this.captureScreen.CAPTURE_CMD);
        this.captureScreen.removeCommand(this.captureScreen.CANCEL_CMD);
        
        if(this.player != null) {
            try{
                // get raw jpeg photo data
                this.rawData = videoControl.getSnapshot(this.encoding);
                if(this.rawData != null) {
                    Image previewPhoto = null;
                    previewPhoto = this.formatter.getPreview(this.rawData);

                    // delete last preview
                    this.viewerScreen.deleteAll();
                    //#style photoViewerTitleItem
                    this.viewerScreen.append(Locale.get("photoManager.label.preview")+": ");
                    // set new preview
                    //#style photoViewerPhotoItem
                    this.viewerScreen.append(previewPhoto);
                    // set snapshot info
                    //#style photoViewerInfoItem
                    this.viewerScreen.append(Locale.get("photoManager.label.photo.size")+": "+this.photoSizeID+" ("+this.grabbedDimension[0]+" x "+this.grabbedDimension[1]+")");
                    
                    ////#debug
                    System.out.println("start preview");
                    // show preview screen
                    this.controller.show(this.viewerScreen);
                    
                    // enable commands
                    this.captureScreen.addCommand(this.captureScreen.CAPTURE_CMD);
                    this.captureScreen.addCommand(this.captureScreen.CANCEL_CMD);
                }
            } catch(MediaException me) {
                //#debug error
                System.out.println("Capture - Media Exception: "+me);
                this.memoryProblem = true;
                this.cleanUp();
                // symbian error -4: not enough memory
                // message for user
                this.controller.show(this.controller.ERROR_ASCR, Locale.get("photoManager.alert.captureFailed")+Locale.get("photoManager.alert.memoryProblem"), null, this.next);
            } catch(SecurityException se) {
                this.cleanUp();
                this.captureScreen.addCommand(this.captureScreen.CAPTURE_CMD);
                this.captureScreen.addCommand(this.captureScreen.CANCEL_CMD);
                this.controller.show(this.controller.getScreen(this.controller.BLOG_SCR));
            } catch(Exception e) {
                //#debug error
                System.out.println("maybe phone memory problem.");
                this.cleanUp();
                // message for user
                this.controller.show(this.controller.ERROR_ASCR, Locale.get("photoManager.alert.captureFailed")+Locale.get("photoManager.alert.memoryProblem"), null, this.next);
            }
            
        }
        this.captureScreen.setStatus(PhotoCaptureScreen.STATUS_READY);
    }
    
    // called in case of exception to make sure invalid players are closed
    private void discardPlayer() {
        if(this.player != null){
           this.player.close();
           ////#debug
           System.out.println("player in state:"+this.player.getState());
           this.player = null;
        }
        this.videoControl = null;
    }
    
    // performs final tasks on the captured photo
    public void finished() {
        // create formatted photo object
        this.formattedPhoto = this.formatter.prepare();
        
        // put formatted photo into photo bin        
        this.photoBin.push(this.formattedPhoto);
        
        // blog manager notifier thread
        Thread t = new Thread(){
        public void run(){
            Stack pb = PhotoManager.uniqueInstance.photoBin;
            synchronized(pb) {
                ////#debug
                System.out.println("notifying manager");
                pb.notify();
            }
          }
        };
        t.start();  
        this.cleanUp();
    }
    
    // get photo encoding and viewable area dimension
    private String initVideoSettings() throws NumberFormatException {
        String encoding = null;
 
        Settings settings = this.controller.getSettings();
        if(settings.getPhotoSizeID() == settings.PHOTO_SMALL) {
            this.grabbedDimension[0] = Integer.valueOf(Locale.get("photoManager.photo.encoding.small.width")).intValue();
            this.grabbedDimension[1] = Integer.valueOf(Locale.get("photoManager.photo.encoding.small.height")).intValue();
            this.photoSizeID = Locale.get("photoManager.photo.size.small");
        } else if(settings.getPhotoSizeID() == settings.PHOTO_LARGE) {
            this.grabbedDimension[0] = Integer.valueOf(Locale.get("photoManager.photo.encoding.large.width")).intValue();
            this.grabbedDimension[1] = Integer.valueOf(Locale.get("photoManager.photo.encoding.large.height")).intValue();
            this.photoSizeID = Locale.get("photoManager.photo.size.large");
        }
        // encoding string (png more supported)
        // encoding=image/jpeg&quality=50
        encoding = "encoding=image/"+Locale.get("photoManager.photo.encoding.captureFormat")+"&width="+this.grabbedDimension[0]+"&height="+this.grabbedDimension[1];
        return encoding;
    }
    
    // restarts the capture procedure
    public void restart() {
        this.cleanUp();
        this.showVideo();
    }
    
    public void cleanUp() {
        this.stopCamera();
        this.discardPlayer();
        this.rawData = null;
        this.active = false;
        this.formatter.cleanUp();
    }
    
    // cancels blog creation
    public void cancel() {
        this.cleanUp();
        this.controller.show(this.next);
    }
    
    
    
    /*************************** INNER CLASS - FORMATTER *****************************/
    
    class PhotoFormatter {

        private byte[] rawData = null;
        private Image image = null;
        private Image preview = null;
        private Photo fPhoto = null;
        
        private int[] fullDimension = new int[2];
        private int[] previewDimension = new int[2];
        private int[] thumbnailDimension = new int[2];

        /** Creates a new instance of PhotoFormatter */
        public PhotoFormatter() {
            super();
        }

        // set photo props
        public Photo prepare() {
            // create thumbnail from cached preview photo to improve performance
            Image thumbnail = this.getThumbnail(this.preview);
            
            // store data and props in photo object
            this.fPhoto = new Photo();
            
            this.fPhoto.set(this.rawData);
            this.fullDimension[0] = this.image.getWidth();
            this.fullDimension[1] = this.image.getHeight();
            this.fPhoto.setDimension(this.fullDimension);
            
            this.fPhoto.setThumb(thumbnail);
            this.fPhoto.setPreview(this.preview);
            this.fPhoto.setSizeID(photoSizeID);
            return this.fPhoto;
        }

        // creates preview photo
        public Image getPreview(byte[] rawData) {
            this.rawData = rawData;
            // convert raw image data into image object
            this.image = Image.createImage(this.rawData, 0, this.rawData.length);
            
            // resize photo for preview if photo dimension is larger then viewable area
            if(this.image.getWidth() > canvasDimension[0]) {
                int xOffset = 9;
            
                ////#debug
                System.out.println("canvas size: "+canvasDimension[0]+" x "+canvasDimension[1]);
                this.previewDimension[0] = canvasDimension[0] - xOffset;
                float scale = (float)this.previewDimension[0]/image.getWidth();
                this.previewDimension[1] = (int)(scale*image.getHeight());
                Image resizedImage = this.resize(this.image, Image.createImage(previewDimension[0], previewDimension[1]));

                ////#debug
                System.out.println("resized to: "+resizedImage.getWidth()+" x "+resizedImage.getHeight());
                this.preview = resizedImage;
                return this.preview;
            } else {
                this.preview = this.image;
                return this.preview;
            }
        }

        // create a thumbnail for the blog browser
        private Image getThumbnail(Image image) {
            Image source = image;
            // use system suggested list image dimension
            this.thumbnailDimension[0] = controller.getDisplay().getBestImageWidth(controller.getDisplay().LIST_ELEMENT);
            this.thumbnailDimension[1] = controller.getDisplay().getBestImageHeight(controller.getDisplay().LIST_ELEMENT);
            Image t = this.resize(source, Image.createImage(thumbnailDimension[0], thumbnailDimension[1]));
            ////#debug
            System.out.println("thumbnail dimension: "+this.thumbnailDimension[0]+" x "+this.thumbnailDimension[1]);
            // create immutable thumbnail
            Image thumbnail = Image.createImage(t);
            return thumbnail;
        }
        
        // resize photo
        private Image resize(Image s, Image t) {
            Image source = s;
            Image target = t;
            
            Graphics g = target.getGraphics();
            for(int y = 0; y < target.getHeight(); y++){
                for(int x = 0; x < target.getWidth(); x++){
                    g.setClip(x, y, 1, 1);
                    int dx = x * source.getWidth() / target.getWidth();
                    int dy = y * source.getHeight() / target.getHeight();
                    g.drawImage(source, x-dx, y-dy, Graphics.LEFT | Graphics.TOP );
                }
            }
            return target;
        }
        
        public void cleanUp() {
            this.rawData = null;
            this.image = null;
        }

    } // end formatter

} // end photo manager



