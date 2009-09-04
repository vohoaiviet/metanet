/*
 * Controller.java
 *
 * Created on 17. September 2007, 04:06
 */
package org.metadon.control;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;

import org.metadon.beans.Credentials;
import org.metadon.beans.Payload;
import org.metadon.beans.Settings;
import org.metadon.extern.bluetooth.BluetoothDevice;
import org.metadon.extern.bluetooth.BluetoothManager;
import org.metadon.extern.location.GPSLocation;
import org.metadon.extern.location.LocationManager;
import org.metadon.extern.location.TraceManager;
import org.metadon.extern.web.metaservice.GatewayWSC;
import org.metadon.info.InfoCollector;
import org.metadon.info.Monitor;
import org.metadon.info.Statistics;
import org.metadon.view.AppMainScreen;
import org.metadon.view.AppSettingsScreen;
import org.metadon.view.AppSplashScreen;
import org.metadon.view.BlogMainScreen;
import org.metadon.view.LoginForm;
import org.metadon.view.PlatformInfoScreen;
import org.metadon.view.PlatformMainScreen;

import java.io.IOException;





import de.enough.polish.util.DeviceControl;
import de.enough.polish.util.Locale;
import de.enough.polish.java5.*;

/**
 *
 * @author  Hannes Weingartner
 * @version
 */
public class Controller extends MIDlet implements CommandListener {

    private AppSplashScreen splashScreen;
    private LoginForm loginScreen;
    private AppMainScreen mainScreen;
    private PlatformMainScreen systemScreen;
    private PlatformInfoScreen infoScreen;
    private AppSettingsScreen settingsScreen;
    private BlogMainScreen blogScreen;
    private GatewayWSC gatewayService;
    private static Display display;
    private Displayable next;
    private BluetoothManager bluetoothManager;
    private LocationManager locationManager;
    private TraceManager waypointManager;
    private GPSLocation gpsLocation;
    private RecordManager recordManager;
    private BlogManager blogManager;
    private Statistics statistics;
    private Monitor monitor;
    private Credentials credentials;
    private Settings settings;
    private boolean isLoggedIn;
    private boolean isPosted;
    // commands
    public final Command YES_CMD = new Command(Locale.get("cmd.yes"), Command.OK, 1);
    public final Command NO_CMD = new Command(Locale.get("cmd.no"), Command.CANCEL, 1);
    // screen types
    public final int SPLA_SCR = 0;
    public final int MAIN_SCR = 1;
    public final int SYST_SCR = 2;
    public final int SETT_SCR = 3;
    public final int INFO_SCR = 4;
    public final int BLOG_SCR = 5;
    public final int LOGIN_SCR = 6;
    // alert screen types
    public static final int OK_ASCR = 10;
    public static final int ERROR_ASCR = 11;
    public static final int INFO_ASCR = 12;
    public static final int CONF_ASCR = 13;
    // icons
    public Image OK_ICON = null;
    public Image LOGIN_USER_ICON = null;
    public Image LOGIN_KEY_ICON = null;
    public Image LOGIN_KEY_NO_ICON = null;
    public Image LOGIN_SECURE_CHAN_ICON = null;
    public Image LOGIN_SECURE_CHAN_NO_ICON = null;
    public Image SETTINGS_SECURITY_ICON = null;
    public Image SETTINGS_CONTENT_ICON = null;
    public Image SETTINGS_LOCATION_ICON = null;
    public Image SETTINGS_CONNECTIVITY_ICON = null;
    public Image NETWORK_BLUETOOTH_ICON = null;
    public Image NETWORK_PROVIDER_ICON = null;
    public Image GPS_DEVICE_ICON = null;
    public Image GPS_DEVICE_NO_ICON = null;
    public Image GPS_DEVICE_EXTERN_ICON = null;
    public Image GPS_DEVICE_INTERN_ICON = null;
    public Image BLOG_PROPS_ICON = null;
    public Image BLOG_WAYPOINT_ICON = null;
    public Image BLOG_WAYPOINT_NO_ICON = null;
    public Image BLOG_RELATION_STANDALONE_ICON = null;
    public Image BLOG_RELATION_JOURNEY_ICON = null;
    public Image BLOG_PHOTO_ICON = null;
    public Image BLOG_PHOTO_SMALL_ICON = null;
    public Image BLOG_PHOTO_LARGE_ICON = null;
    public Image BLOG_MESSAGE_ICON = null;
    public Image BLOG_MESSAGE_COM_ICON = null;
    public Image BROWSER_MESSAGE_ICON = null;
    public Image BROWSER_JOURNEY_ICON = null;
    
    private final int STORED_BLOGS = Integer.valueOf(Locale.get("blog.stored")).intValue();
    private final int POSTED_BLOGS = Integer.valueOf(Locale.get("blog.posted")).intValue();

    /***************************************************************************************/
    public Controller() {
        super();
        //System.out.println("starting midlet initialisation.");

        // load resources
        this.loadResources();
    }

    public void startApp() throws MIDletStateChangeException {
        // application start
        if (display == null) {

            // get application display
            display = Display.getDisplay(this);

            // show splash screen
            this.splashScreen = new AppSplashScreen(this);

            // create unique record manager
            this.recordManager = RecordManager.getInstance(this);

            // put stored or default settings into application cache for faster access
            this.settings = this.retrieveSettings();

            // start only if settings are available
            if (this.settings == null) {
                this.notifyDestroyed();
            }

            this.splashScreen.setLoadingStatusText(Locale.get("appSplashScreen.loading.controllers"));
            // create unique bluetooth manager
            this.bluetoothManager = BluetoothManager.getInstance(this);
            // create unique location manager
            this.locationManager = LocationManager.getInstance(this);
            // create unique waypoint manager
            this.waypointManager = TraceManager.getInstance(this);
            // create unique blog manager
            this.blogManager = BlogManager.getInstance(this);
            this.statistics = new Statistics(this);
            this.monitor = new Monitor(this);

            // load application screens
            this.splashScreen.setLoadingStatusText(Locale.get("appSplashScreen.loading.screens"));
            this.systemScreen = new PlatformMainScreen(this);
            this.settingsScreen = new AppSettingsScreen(this);
            this.settingsScreen.updateItems();
            this.blogScreen = new BlogMainScreen(this);

            this.mainScreen = new AppMainScreen(this);
            this.loginScreen = new LoginForm(this);

            // retrieve stored credentials
            this.credentials = this.retrieveCredentials();

            // prevent standalone access on first startup
            if (this.credentials == null) {
                this.loginScreen.removeCommand(this.loginScreen.STANDALONE_CMD);
            }

            // check whether user prefers setting the credentials fields automatically
            boolean autoFill = false;
            if (this.credentials != null && !this.settings.getForceLogin()) {
                autoFill = true;
            }
            // set login data
            this.loginScreen.setCredentials(this.credentials, autoFill);
            splashScreen.setNextScreen(this.loginScreen);
            
            // this web service is used as a gateway to the tmblog server for
            // * user authentication
            // * posting
            this.gatewayService = new GatewayWSC(this);
            this.setBackgroundLight(true);
        }
    }

    public void pauseApp() {
    // not implemented on nokia phones
    // TODO: implement workaround
    }

    public void destroyApp(boolean unconditional) throws MIDletStateChangeException {

    }

    /***************************************************************************************/
    // stores Credentials object in RMS
    public boolean storeCredentials(Credentials credentials) {
       return this.recordManager.setCredentials(credentials);
    }
    
    // gets Credentials object from RMS
    public Credentials retrieveCredentials() {
        return this.recordManager.getCredentials();
    }
    
    public Credentials getCredentials() {
        return this.credentials;
    }
    
//    // removes Credentials object from RMS
//    public boolean removeCredentials() {
//        return this.recordManager.removeCredentials();
//    }
    /***************************************************************************************/
    // stores settings object in RMS
    public boolean storeSettings(Settings settings) {
        if (this.recordManager.setSettings(settings)) {
            // update cache
            this.settings = settings;
            return true;
        }
        return false;
    }

    // gets settings object from RMS
    private Settings retrieveSettings() {
        return this.recordManager.getSettings();
    }

    // return applications settings
    public Settings getSettings() {
        return this.settings;
    }

    /***************************************************************************************/
    
    public void storeAsDefaultBTD(int context, BluetoothDevice device) {
        if (device == null) {
            return;
        }
        //#debug
//#         System.out.println("selected bt device: "+device.getAlias());

        // create local copy of settings to update rms and cache within one operation
        Settings settings = this.settings;
        if (context == this.bluetoothManager.GPS_DEVICE) {
            // store selected device as default GPS receiver
            settings.setGPSDevice(device);
        } else if (context == this.bluetoothManager.GATEWAY_DEVICE) {

        }
        // update RMS
        if (this.storeSettings(settings)) {
        ////#debug
        System.out.println("stored as default gps device: "+device.getAlias());
        }
    }

    /***************************************************************************************/
    public void createBlog(int type) {
        this.blogManager.createBlog(this.getCurrentGPSLocation(), type);
    }

    public void loadStoredBlogs() {
        this.blogManager.browserType(this.STORED_BLOGS);
    }
    
    public void loadPostedBlogs() {
        this.blogManager.browserType(this.POSTED_BLOGS);
    }
    
    /***************************************************************************************/

    public void authenticateUser(Credentials credentials) {
        if (credentials == null || this.gatewayService == null) {
            return;
        }
        this.credentials = credentials;
        this.isLoggedIn = false;
        this.gatewayService.logon();
    }
    
    public void setLoggedIn(boolean logged) {
        this.isLoggedIn = logged;
        if(this.isLoggedIn) {
            // store valid credentials on device
            if(this.storeCredentials(this.credentials)) {
                this.show(this.getScreen(this.MAIN_SCR));
            }
        } else {
            this.credentials = null;
        }
    }
    
    public boolean isLoggedOn() {
        return this.isLoggedIn;
    }
    
    /***************************************************************************************/
  
    public void postBlog(Payload payload) {
        if (payload == null) {
            return;
        }
        this.isPosted = false;
        // get prefered network
        if(this.settings.getForceBluetooth()) {
            //this.show(this.INFO_ASCR, "Service not Available.", null, null);
        } else {
            this.gatewayService.post(payload);
        }
    }
    
    public void setPosted(boolean posted) {
        this.isPosted = posted;
        if(this.isPosted) {
            // declare blog as posted
            //#debug
//#             System.out.println("calling isPosted()...");
            this.blogManager.updatePostedBlogs();
        } else {
            this.blogManager.cleanUpPost();
        }
    }
    
    /***************************************************************************************/
    
    public void retrieveLocation() {
        this.gpsLocation = null;
        this.locationManager.retrieveLocation();
    }

    public void setCurrentGPSLocation(GPSLocation location) {
        this.gpsLocation = location;
    }

    public GPSLocation getCurrentGPSLocation() {
        if (this.settings.getForceLocation()) {
            return this.gpsLocation;
        } else {
            return null;
        }
    }

    /***************************************************************************************/
    public Statistics getStatistics() {
        return this.statistics;
    }

    public void loadStatistics() {
        this.statistics.show();
    }

    /***************************************************************************************/
    
    public Monitor getMonitor() {
        return this.monitor;
    }

    public void loadMonitor() {
        this.monitor.show();
    }

    /***************************************************************************************/
    
    // return a displayable srceen
    public Displayable getScreen(int scrID) {

        if (scrID == this.SPLA_SCR) {
            return this.splashScreen;
        } else if (scrID == this.MAIN_SCR) {
            return this.mainScreen;
        } else if (scrID == this.SYST_SCR) {
            return this.systemScreen;
        } else if (scrID == this.SETT_SCR) {
            return this.settingsScreen;
        } else if (scrID == this.INFO_SCR) {
            return this.infoScreen;
        } else if (scrID == this.BLOG_SCR) {
            return this.blogScreen;
        } else if (scrID == this.LOGIN_SCR) {
            return this.loginScreen;
        }
        return null;
    }

    // return application display
    static public Display getDisplay() {
        return display;
    }

    /***************************************************************************************/
    //public BlogTypes BlogType() { return this.blogType; }
    private void loadResources() {

        try {
//            this.OK_ICON = Image.createImage("/ok.png");

            // large icons for browser
            this.BROWSER_MESSAGE_ICON = Image.createImage("/browser_message.png");
            this.BROWSER_JOURNEY_ICON = Image.createImage("/browser_journey.png");

//            this.LOGIN_USER_ICON = Image.createImage("/user.png");
//            this.LOGIN_KEY_ICON = Image.createImage("/login_key.png");
//            this.LOGIN_KEY_NO_ICON = Image.createImage("/login_key_no.png");
            this.LOGIN_SECURE_CHAN_ICON = Image.createImage("/secure_channel.png");
            this.LOGIN_SECURE_CHAN_NO_ICON = Image.createImage("/secure_channel_no.png");

            this.SETTINGS_SECURITY_ICON = Image.createImage("/settings_security.png");
            this.SETTINGS_CONTENT_ICON = Image.createImage("/settings_blog_content.png");
            this.SETTINGS_LOCATION_ICON = Image.createImage("/settings_location.png");
            this.SETTINGS_CONNECTIVITY_ICON = Image.createImage("/settings_connectivity.png");

            this.NETWORK_BLUETOOTH_ICON = Image.createImage("/network_bluetooth.png");
            this.NETWORK_PROVIDER_ICON = Image.createImage("/network_provider.png");

            this.GPS_DEVICE_ICON = Image.createImage("/gps_device.png");
            this.GPS_DEVICE_NO_ICON = Image.createImage("/gps_device_no.png");
            this.GPS_DEVICE_EXTERN_ICON = Image.createImage("/gps_device_extern.png");
            this.GPS_DEVICE_INTERN_ICON = Image.createImage("/gps_device_intern.png");

            this.BLOG_PROPS_ICON = Image.createImage("/blog_properties.png");
            this.BLOG_WAYPOINT_ICON = Image.createImage("/blog_waypoint.png");
            this.BLOG_WAYPOINT_NO_ICON = Image.createImage("/blog_waypoint_no.png");
            this.BLOG_RELATION_STANDALONE_ICON = Image.createImage("/blog_relation_standalone.png");
            this.BLOG_RELATION_JOURNEY_ICON = Image.createImage("/blog_relation_journey.png");
            this.BLOG_PHOTO_ICON = Image.createImage("/blog_photo.png");
            this.BLOG_PHOTO_SMALL_ICON = Image.createImage("/photo_small.png");
            this.BLOG_PHOTO_LARGE_ICON = Image.createImage("/photo_large.png");
            this.BLOG_MESSAGE_ICON = Image.createImage("/blog_message.png");
            this.BLOG_MESSAGE_COM_ICON = Image.createImage("/blog_message_community.png");



        } catch (IOException e) {
            //#debug error
            System.out.println("Unable to load application icon" + e.getMessage());
        }
    }

    /***************************************************************************************/
    // display generic screens
    public void show(Displayable disp) {
        display.setCurrent(disp);
    }

    // display info collection screens
    public void show(String title, InfoCollector collector) {
        collector.collectInfos(this, this.getDisplay());
        this.infoScreen = new PlatformInfoScreen(this, title, collector);
        collector.show(this.getDisplay(), this.infoScreen);
    }

    public void show(int type, String text, CommandListener controlScreen, Displayable nextScreen) {
        Alert alertScreen = null;
        if (type == this.OK_ASCR) {
            //#style alertOKScreen
            alertScreen = new Alert(Locale.get("title.alert.ok"), text, null, AlertType.INFO);
        }
        if (type == this.ERROR_ASCR) {
            //#style alertErrorScreen
            alertScreen = new Alert(Locale.get("title.alert.error"), text, null, AlertType.ERROR);
        }
        if (type == this.INFO_ASCR) {
            //#style alertInfoScreen
            alertScreen = new Alert(Locale.get("title.alert.info"), text, null, AlertType.INFO);
        }
        if (type == this.CONF_ASCR) {
            //#style alertConfirmScreen
            alertScreen = new Alert(Locale.get("title.alert.confirmation"), text, null, AlertType.CONFIRMATION);
            alertScreen.addCommand(this.YES_CMD);
            alertScreen.addCommand(this.NO_CMD);
            alertScreen.setCommandListener(controlScreen);
        }
        // haptic feedback on error
		if (type == ERROR_ASCR)
			this.vibrate(250);
        alertScreen.setTimeout(Alert.FOREVER);
        display.setCurrent(alertScreen, nextScreen);
    }
    
    /**
	 * Turns on/off background light of handset.
	 * 
	 * @param status <code>true</code> if the backlight should turned on. <code>false</code> otherwise.
	 */
	public void setBackgroundLight(boolean status)
	{
		if (!DeviceControl.isLightSupported())
			return;
		System.out.println("Controller: BG Light support.");
		if (status)
			DeviceControl.lightOn();
		else
			DeviceControl.lightOff();
	}
	
	/**
	 * Activates the vibra call for a specified duration.
	 * 
	 * @param duration the vibra duration in milliseconds.
	 */
	public void vibrate(int duration)
	{
		if (!DeviceControl.isVibrateSupported())
			return;
		System.out.println("Controller: Vibra support.");
		DeviceControl.vibrate(duration);
	}

    /***************************************************************************************/
    public void confirmExit(Displayable next) {
        this.next = next;
        this.show(this.CONF_ASCR, Locale.get("controller.alert.exit"), this, null);
    }
    
    public void exit() {
        this.notifyDestroyed();
    }

    public void commandAction(Command cmd, Displayable disp) {
        if (cmd == this.YES_CMD && disp instanceof Alert) {
            // logout
            if (this.isLoggedOn()) {
                this.gatewayService.logout();
            } else {
                this.exit();
            }
            
        } else if (cmd == this.NO_CMD && disp instanceof Alert) {
            this.show(this.next);
        }
    }
}
