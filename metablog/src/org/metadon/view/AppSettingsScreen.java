/*
 * AppSettingsScreen.java
 *
 * Created on 03. November 2007, 23:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.view;


import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import org.metadon.beans.Settings;
import org.metadon.control.Controller;

import de.enough.polish.ui.TabbedForm;
import de.enough.polish.ui.Screen;
import de.enough.polish.ui.ScreenStateListener;
import de.enough.polish.util.Locale;


/**
 *
 * @author Hannes
 */
public class AppSettingsScreen extends TabbedForm implements ScreenStateListener, CommandListener {
    
    private Settings settings;
    
    private final Image[] loginIcons = new Image[2];
    private final Image[] secureChannelIcons = new Image[2];
    private final Image[] photoIcons = new Image[2];
    private final Image[] blogRelationIcons = new Image[2];
    private final Image[] waypointIcons = new Image[2];
    private final Image[] gpsDeviceTypeIcons = new Image[2];
    private final Image[] gpsDeviceIcons = new Image[2];
    private final Image[] networkIcons = new Image[2];
    
    // init login choice group options
    private final String[] loginOptions = new String[2];
    // init secure channel choice group options
    private final String[] secureChannelOptions = new String[2];
    // init photo choice group options
    private final String[] photoOptions = new String[2];
    // init blog relation choice group options
    private final String[] blogRelationOptions = new String[2];
    // init blog order choice group options
    private final String[] blogOrderOptions = new String[2];
    // init waypoint choice group options
    private final String[] waypointOptions = new String[2];
    // init distance unit choice group options
    private final String[] distanceUnitOptions = new String[2];
    // init gps device type choice group options
    private final String[] gpsDeviceTypeOptions = new String[2];
    // init gps device choice group options
    private final String[] gpsDeviceOptions = new String[2];
    // init network choice group options
    private final String[] networkOptions = new String[2];
    

    private Controller controller;
    private ChoiceGroup cgLogin;
    private ChoiceGroup cgSecureChannel;
    private ChoiceGroup cgPhoto;
    private ChoiceGroup cgBlogRelation;
    private ChoiceGroup cgBlogOrder;
    private ChoiceGroup cgWaypoint;
    private ChoiceGroup cgDistanceUnit;
    private ChoiceGroup cgGPSDeviceType;
    private ChoiceGroup cgGPSDevice;
    private ChoiceGroup cgNetwork;
    
    private String[] tabNames;
    
    private final Command CLOSE_CMD = new Command(Locale.get("cmd.close"), Command.BACK, 1);
    
    /**
     * Creates a new instance of AppSettingsScreen
     */
    public AppSettingsScreen(Controller controller) {
        super(null,
              null,
              new Image[] { controller.SETTINGS_SECURITY_ICON ,
                            controller.SETTINGS_CONTENT_ICON,
                            controller.SETTINGS_LOCATION_ICON,
                            controller.SETTINGS_CONNECTIVITY_ICON
                          }
        );
        this.controller = controller;
        // load tab names
        this.tabNames = new String[] {
            Locale.get("settings.tab.security"),
            Locale.get("settings.tab.content"),
            Locale.get("settings.tab.location"),
            Locale.get("settings.tab.connectivity")
        };
        this.settings = this.controller.getSettings();
        
        // init login choice group parameters
        this.loginIcons[0] = this.controller.LOGIN_KEY_NO_ICON;
        this.loginIcons[1] = this.controller.LOGIN_KEY_ICON;
        this.loginOptions[0] = Locale.get("settings.no");
        this.loginOptions[1] = Locale.get("settings.yes");
        
        // init secure channel choice group parameters
        this.secureChannelIcons[0] = this.controller.LOGIN_SECURE_CHAN_NO_ICON;
        this.secureChannelIcons[1] = this.controller.LOGIN_SECURE_CHAN_ICON;
        this.secureChannelOptions[0] = Locale.get("settings.no");
        this.secureChannelOptions[1] = Locale.get("settings.yes");
        
        // init photo choice group parameters
        this.photoIcons[0] = this.controller.BLOG_PHOTO_SMALL_ICON;
        this.photoIcons[1] = this.controller.BLOG_PHOTO_LARGE_ICON;
        this.photoOptions[0] = Locale.get("settings.photo.small");
        this.photoOptions[1] = Locale.get("settings.photo.large");
        
        // init blog relation choice group parameters
        this.blogRelationIcons[0] = this.controller.BLOG_RELATION_JOURNEY_ICON;
        this.blogRelationIcons[1] = this.controller.BLOG_RELATION_STANDALONE_ICON;
        this.blogRelationOptions[0] = Locale.get("settings.relation.journey");
        this.blogRelationOptions[1] = Locale.get("settings.relation.standalone");
        
         // init blog order choice group parameters
        this.blogOrderOptions[0] = Locale.get("settings.order.time");
        this.blogOrderOptions[1] = Locale.get("settings.order.type");
        
        // init waypoint choice group parameters
        this.waypointIcons[0] = this.controller.BLOG_WAYPOINT_NO_ICON;
        this.waypointIcons[1] = this.controller.BLOG_WAYPOINT_ICON;
        this.waypointOptions[0] = Locale.get("settings.waypoint.disabled");
        this.waypointOptions[1] = Locale.get("settings.waypoint.enabled");
        
        // init distance unit choice group parameters
        this.distanceUnitOptions[0] = Locale.get("settings.distanceUnit.kilometers");
        this.distanceUnitOptions[1] = Locale.get("settings.distanceUnit.miles");
        
        // init gps device type choice group parameters
        this.gpsDeviceTypeIcons[0] = this.controller.GPS_DEVICE_INTERN_ICON;
        this.gpsDeviceTypeIcons[1] = this.controller.GPS_DEVICE_EXTERN_ICON;
        this.gpsDeviceTypeOptions[0] = Locale.get("settings.gpsDeviceType.intern");
        this.gpsDeviceTypeOptions[1] = Locale.get("settings.gpsDeviceType.extern");
        
        // init gps device choice group parameters
        this.gpsDeviceIcons[0] = this.controller.GPS_DEVICE_NO_ICON;
        this.gpsDeviceIcons[1] = this.controller.GPS_DEVICE_ICON;
        
        // init network choice group parameters
        this.networkIcons[0] = this.controller.NETWORK_PROVIDER_ICON;
        this.networkIcons[1] = this.controller.NETWORK_BLUETOOTH_ICON;
        this.networkOptions[0] = Locale.get("settings.network.provider");
        this.networkOptions[1] = Locale.get("settings.network.bluetooth");
            
        // security tab
        // set login choice group
        //#style exclusiveChoiceGroup
        this.cgLogin = new ChoiceGroup(Locale.get("settings.label.login")+": ", Choice.EXCLUSIVE, this.loginOptions, this.loginIcons);
        this.append(0, this.cgLogin);
        
        // set secure channel choice group
        //#style exclusiveChoiceGroup
        this.cgSecureChannel = new ChoiceGroup(Locale.get("settings.label.secureChannel")+": ", Choice.EXCLUSIVE, this.secureChannelOptions, this.secureChannelIcons);
        this.append(0, this.cgSecureChannel);
        
        // content tab
        // set blog relation choice group
        //#style exclusiveChoiceGroup
        this.cgBlogRelation = new ChoiceGroup(Locale.get("settings.label.relation")+": ", Choice.EXCLUSIVE, this.blogRelationOptions, this.blogRelationIcons);
        this.append(1, this.cgBlogRelation);
        
        // set blog order choice group
        //#style exclusiveChoiceGroup
        this.cgBlogOrder = new ChoiceGroup(Locale.get("settings.label.order")+": ", Choice.EXCLUSIVE, this.blogOrderOptions, null);
        this.append(1, this.cgBlogOrder);
        
        // set photo quality choice group
        //#style exclusiveChoiceGroup
        this.cgPhoto = new ChoiceGroup(Locale.get("settings.label.photo")+": ", Choice.EXCLUSIVE, this.photoOptions, this.photoIcons);
        this.append(1, this.cgPhoto);
        
        // location tab
        // set waypoint choice group
        //#style exclusiveChoiceGroup
        this.cgWaypoint = new ChoiceGroup(Locale.get("settings.label.waypoint")+": ", Choice.EXCLUSIVE, this.waypointOptions, this.waypointIcons);
        this.append(2, this.cgWaypoint);
        // set gps device type choice group
        //#style exclusiveChoiceGroup
        this.cgGPSDeviceType = new ChoiceGroup(Locale.get("settings.label.gpsDeviceType")+": ", Choice.EXCLUSIVE, this.gpsDeviceTypeOptions, this.gpsDeviceTypeIcons);
        this.append(2, this.cgGPSDeviceType);
        // set gps device choice group
        //#style exclusiveChoiceGroup
        this.cgGPSDevice = new ChoiceGroup(Locale.get("settings.label.gpsDevice")+": ", Choice.EXCLUSIVE);
        // set gps device default entry
        //#style choiceGroupEntry
        this.cgGPSDevice.append(this.settings.GPS_DEVICE_EMPTY_ALIAS, this.gpsDeviceIcons[0]);
        this.append(2, this.cgGPSDevice);
        // set distance unit choice group
        //#style exclusiveChoiceGroup
        this.cgDistanceUnit = new ChoiceGroup(Locale.get("settings.label.distanceUnit")+": ", Choice.EXCLUSIVE, this.distanceUnitOptions, null);
        this.append(2, this.cgDistanceUnit);
        
        // connectivity tab
        // set network choice group
        //#style exclusiveChoiceGroup
        this.cgNetwork = new ChoiceGroup(Locale.get("settings.label.network")+": ", Choice.EXCLUSIVE, this.networkOptions, this.networkIcons);
        this.append(3, this.cgNetwork);
        
        this.addCommand(this.CLOSE_CMD);
        this.setCommandListener(this);
        this.setScreenStateListener(this);
        
        this.setTitle(this.tabNames[0]);
    }
    
    
    public void updateItems() {
        // get cached settings
        this.settings = this.controller.getSettings();
       
        // update login choice group
        if(this.settings.getForceLogin())
             this.cgLogin.setSelectedIndex(1, true);
        else
             this.cgLogin.setSelectedIndex(0, true);
        
        // update secure channel choice group
        if(this.settings.getForceSecureChannel())
             this.cgSecureChannel.setSelectedIndex(1, true);
        else
             this.cgSecureChannel.setSelectedIndex(0, true);
        
        // update photo size choice group
        if(this.settings.getPhotoSizeID() == this.settings.PHOTO_SMALL)
             this.cgPhoto.setSelectedIndex(0, true);
        else if (this.settings.getPhotoSizeID() == this.settings.PHOTO_LARGE)
             this.cgPhoto.setSelectedIndex(1, true);
        
        // update blog relation choice group
        if(this.settings.getBlogRelation() == this.settings.BLOG_RELATION_JOURNEY)
             this.cgBlogRelation.setSelectedIndex(0, true);
        else if(this.settings.getBlogRelation() == this.settings.BLOG_RELATION_STANDALONE)
             this.cgBlogRelation.setSelectedIndex(1, true);
        
        // update blog order choice group
        if(this.settings.getBlogOrder() == this.settings.BLOG_ORDER_TIME)
             this.cgBlogOrder.setSelectedIndex(0, true);
        else if(this.settings.getBlogOrder() == this.settings.BLOG_ORDER_TYPE)
             this.cgBlogOrder.setSelectedIndex(1, true);
        
        // update distance unit choice group
        if(this.settings.getDistanceUnit() == this.settings.DISTANCE_ML)
             this.cgDistanceUnit.setSelectedIndex(1, true);
        else if(this.settings.getDistanceUnit() == this.settings.DISTANCE_KM)
             this.cgDistanceUnit.setSelectedIndex(0, true);
        
        // update waypoint choice group
        if(this.settings.getForceLocation())
             this.cgWaypoint.setSelectedIndex(1, true);
        else
             this.cgWaypoint.setSelectedIndex(0, true);
        
        // update gps device type choice group
        if(this.settings.getGPSDeviceType() == this.settings.GPS_TYPE_EXTERN)
             this.cgGPSDeviceType.setSelectedIndex(1, true);
        else if(this.settings.getGPSDeviceType() == this.settings.GPS_TYPE_INTERN)
             this.cgGPSDeviceType.setSelectedIndex(0, true);
        
        // update gps device choice group
        String alias = this.settings.getGPSDevice().getAlias();
        if(!alias.equals(this.settings.GPS_DEVICE_EMPTY_ALIAS)) {
            if(this.cgGPSDevice.size() == 2)
                this.cgGPSDevice.delete(1);
            //#style choiceGroupEntry
            this.cgGPSDevice.append(alias, this.gpsDeviceIcons[1]);
            this.cgGPSDevice.setSelectedIndex(1, true);
        } else {
            if(this.cgGPSDevice.size() == 2)
                this.cgGPSDevice.delete(1);
            this.cgGPSDevice.setSelectedIndex(0, true);
        }
        
        // update network choice group
        if(this.settings.getForceBluetooth())
             this.cgNetwork.setSelectedIndex(1, true);
        else
             this.cgNetwork.setSelectedIndex(0, true);
    }
    
    // called when tab changes
    public void screenStateChanged(Screen tabbedScreen) {
        this.setTitle(this.tabNames[this.getSelectedTab()]);        
    }
    
    public void commandAction(Command cmd, Displayable disp)
    {
	if (cmd == this.CLOSE_CMD && disp == this) {
            int index;
            
            // determine login settings
            index = this.cgLogin.getSelectedIndex();
            if(index == 0) {
                this.settings.setForceLogin(false);
            } else {
                this.settings.setForceLogin(true);
            }
            
            // determine secure channel settings
            index = this.cgSecureChannel.getSelectedIndex();
            if(index == 0) {
                this.settings.setForceSecureChannel(false);
            } else {
                this.settings.setForceSecureChannel(true);
            }
            
            // determine photo size settings
            index = this.cgPhoto.getSelectedIndex();
            if(index == 0) {
                this.settings.setPhotoSizeID(this.settings.PHOTO_SMALL);
            } else if (index == 1){
                this.settings.setPhotoSizeID(this.settings.PHOTO_LARGE);
            }
            
            // determine blog relation settings
            index = this.cgBlogRelation.getSelectedIndex();
            if(index == 0) {
                this.settings.setBlogRelation(this.settings.BLOG_RELATION_JOURNEY);
            } else {
                this.settings.setBlogRelation(this.settings.BLOG_RELATION_STANDALONE);
            }
            
            // determine blog order settings
            index = this.cgBlogOrder.getSelectedIndex();
            if(index == 0) {
                this.settings.setBlogOrder(this.settings.BLOG_ORDER_TIME);
            } else {
                this.settings.setBlogOrder(this.settings.BLOG_ORDER_TYPE);
            }
            
            // determine distance unit settings
            index = this.cgDistanceUnit.getSelectedIndex();
            if(index == 0) {
                this.settings.setDistanceUnit(this.settings.DISTANCE_KM);
            } else {
                this.settings.setDistanceUnit(this.settings.DISTANCE_ML);
            }
            
            // determine waypoint settings
            index = this.cgWaypoint.getSelectedIndex();
            if(index == 0) {
                this.settings.setForceLocation(false);
            } else {
                this.settings.setForceLocation(true);
            }
            
            // determine gps device type settings
            index = this.cgGPSDeviceType.getSelectedIndex();
            if(index == 0) {
                this.settings.setGPSDeviceType(this.settings.GPS_TYPE_INTERN);
            } else {
                this.settings.setGPSDeviceType(this.settings.GPS_TYPE_EXTERN);
            }
            
            // determine gps device settings
            index = this.cgGPSDevice.getSelectedIndex();
            if(index == 0) {
                this.settings.setGPSDevice(this.settings.GPS_DEVICE_EMPTY);
            } else {
                this.settings.setGPSDevice(this.settings.getGPSDevice());
            }
            
            // determine network settings
            index = this.cgNetwork.getSelectedIndex();
            if(index == 0) {
                this.settings.setForceBluetooth(false);
            } else {
                this.settings.setForceBluetooth(true);
            }
            
            // store settings in RMS
            if(this.controller.storeSettings(this.settings)) {
                this.controller.show(this.controller.getScreen(this.controller.MAIN_SCR));
            }
        }
    }
    
}
