/*
 * RecordManagement.java
 *
 * Created on 02. November 2007, 23:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.metadon.control;

import javax.microedition.rms.*;

import org.metadon.beans.Credentials;
import org.metadon.beans.Settings;
import org.metadon.extern.location.Waypoint;
import org.metadon.utils.RMSBlogSelector;
import org.metadon.utils.RMSWaypointSelector;

import java.util.Stack;
import java.util.Vector;
import java.io.IOException;
import java.util.Enumeration;


import de.enough.polish.util.Locale;

/**
 *
 * @author Hannes
 */
public class RecordManager {

    private volatile static RecordManager uniqueInstance;
    private Controller controller;
    private RecordStore loginRS;
    private RecordStore settingsRS;
    private RecordStore traceRS;
    private RecordStore blogPMRS;
    private RecordStore blogPMThumbRS;
    //private RecordStore blogPMServerThumbRS;
    private RecordStore blogMRS;
    //private RecordStore blogMServerRS;
    
    private final String LOGIN_STORE = "LoginRS";
    private final String SETTINGS_STORE = "SettingsRS";
    private final String TRACE_STORE = Locale.get("recordManager.store.trace");
    
    private final String BLOG_PM_STORE = Locale.get("recordManager.store.PM");
    private final String BLOG_PM_THUMB_STORE = Locale.get("recordManager.store.PMThumb");
    private final String BLOG_PM_SERVER_THUMB_STORE = Locale.get("recordManager.store.PMServerThumb");
    private final String BLOG_M_STORE = Locale.get("recordManager.store.M");
    private final String BLOG_M_SERVER_STORE = Locale.get("recordManager.store.MServer");
    
    private final int STORED_BLOGS = Integer.valueOf(Locale.get("blog.stored")).intValue();
    private final int POSTED_BLOGS = Integer.valueOf(Locale.get("blog.posted")).intValue();
    private final int BLOG_PM = Integer.valueOf(Locale.get("blog.PM.id")).intValue();
    private final int BLOG_M = Integer.valueOf(Locale.get("blog.M.id")).intValue();
    private final int QUALITY_POST = Integer.valueOf(Locale.get("blog.PM.id.quality.post")).intValue();
    private final int QUALITY_BROWSE = Integer.valueOf(Locale.get("blog.PM.id.quality.browse")).intValue();

    /**
     * Creates a new instance of RecordManagement
     */
    private RecordManager(Controller c) {
        super();
        this.controller = c;
    }

    // singletone
    public static RecordManager getInstance(Controller c) {
        if (uniqueInstance == null) {
            //synchronize concurrent threads on creating a instance (performance decrease!)
            synchronized (RecordManager.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new RecordManager(c);
                }
            }
        }
        return uniqueInstance;
    }

    /***************************************************************************************/
    // opens or creates a new record store if not existing yet
    public RecordStore openRecordStore(String storeName) {
        try {
            return RecordStore.openRecordStore(storeName, true, RecordStore.AUTHMODE_PRIVATE, true);
        } catch (RecordStoreNotFoundException rsnfe) {
            //#debug error
            System.out.println("Open Store - Record Store not found: " + storeName + " " + rsnfe);
            return null;
        } catch (RecordStoreException rse) {
            //#debug error
            System.out.println("Open Store: " + storeName + " " + rse);
            return null;
        } catch (IllegalArgumentException iae) {
            //#debug error
            System.out.println("Open Store - Invalid Record Store name: " + storeName + " " + iae);
            return null;
        } catch (SecurityException se) {
            //#debug error
            System.out.println("Open Store - Access denied: " + storeName + " " + se);
            return null;
        }
    }

    // adds a record to a specific record store
    private boolean addRecord(RecordStore store, byte[] record) {
        String storeName = null;
        int storageTolerance = 20000;
        try {
            storeName = store.getName();
            // check free storage
            if (record.length < store.getSizeAvailable() - storageTolerance) {
                store.addRecord(record, 0, record.length);
                return true;
            } else {
                // to less storage
                // this.controller.show(this.controller.INFO_ASCR, "", null, this.controller.getScreen(this.controller.MAIN_SCR));
                return false;
            }
        } catch (RecordStoreNotOpenException rsnoe) {
            //#debug error
            System.out.println("Add Record - Record Store closed: " + storeName + " " + rsnoe);
            return false;
        } catch (RecordStoreFullException rsfe) {
            //#debug error
            System.out.println("Add Record - Record Store full: " + storeName + " " + rsfe);
            return false;
        } catch (RecordStoreException rse) {
            //#debug error
            System.out.println("Add Record: " + storeName + " " + rse);
            return false;
        } catch (Exception e) {
            //#debug error
            System.out.println("Generic Exception: " + e);
            this.controller.show(this.controller.ERROR_ASCR, "Store failed: Memory?", null, this.controller.getScreen(this.controller.MAIN_SCR));
            return false;
        }
    }

    // returns a filtered and sorted record set of a specified store
    private Stack getRecords(RecordStore store, RecordFilter filter, RecordComparator comparator) {
        String storeName = null;
        Stack resultSet = new Stack();
        try {
            storeName = store.getName();
            RecordEnumeration enumeration = store.enumerateRecords(filter, comparator, false);
            ////#debug
            System.out.println("resultSet size: " + enumeration.numRecords());
            while (enumeration.hasNextElement()) {
                resultSet.push(store.getRecord(enumeration.nextRecordId()));
            }
            enumeration.destroy();
            return resultSet;
        } catch (RecordStoreNotOpenException rsnoe) {
            //#debug error
            System.out.println("Read Records - Record Store closed: " + storeName + " " + rsnoe);
            return null;
        } catch (InvalidRecordIDException irie) {
            //#debug error
            System.out.println("Read Records - Invalid Record ID: " + storeName + " " + irie);
            return null;
        } catch (RecordStoreException rse) {
            //#debug error
            System.out.println("Read Records: " + storeName + " " + rse);
            return null;
        }
    }

    // updates existing record specified by the given filter
    private boolean updateRecord(RecordStore store, byte[] record, RecordFilter filter) {
        String storeName = null;
        try {
            storeName = store.getName();
            RecordEnumeration enumeration = store.enumerateRecords(filter, null, false);
            if (enumeration.hasNextElement()) {
                int id = enumeration.nextRecordId();
                store.setRecord(id, record, 0, record.length);
                return true;
            }
            enumeration.destroy();
            return false;
        } catch (RecordStoreNotOpenException rsnoe) {
            //#debug error
            System.out.println("Update Record - Record Store closed: " + storeName + " " + rsnoe);
            return false;
        } catch (InvalidRecordIDException irie) {
            //#debug error
            System.out.println("Update Record - Invalid Record ID: " + storeName + " " + irie);
            return false;
        } catch (RecordStoreFullException rsfe) {
            //#debug error
            System.out.println("Update Record - Record Store full: " + storeName + " " + rsfe);
            return false;
        } catch (RecordStoreException rse) {
            //#debug error
            System.out.println("Read Record: " + storeName + " " + rse);
            return false;
        }
    }

    public int countRecords(int storeType, int blogType) {
        int count = 0;
        if (blogType == this.BLOG_PM) {
            if(storeType == this.STORED_BLOGS)
                this.blogPMThumbRS = this.openRecordStore(this.BLOG_PM_THUMB_STORE);
            else if(storeType == this.POSTED_BLOGS)
                this.blogPMThumbRS = this.openRecordStore(this.BLOG_PM_SERVER_THUMB_STORE);
                
            if (this.blogPMThumbRS == null) {
                return -1;
            }
            count = this.countRecords(this.blogPMThumbRS);
            this.closeRecordStore(this.blogPMThumbRS);

        } else if (blogType == this.BLOG_M) {
            if(storeType == this.STORED_BLOGS)
                this.blogMRS = this.openRecordStore(this.BLOG_M_STORE);
            else if(storeType == this.POSTED_BLOGS)
                this.blogMRS = this.openRecordStore(this.BLOG_M_SERVER_STORE);
            
            if (this.blogMRS == null) {
                return -1;
            }
            count = this.countRecords(this.blogMRS);
            this.closeRecordStore(this.blogMRS);
        }
        return count;
    }

    // returns the number of stored records within a specific store
    private int countRecords(RecordStore store) {
        try {
            int count = store.getNumRecords();
            return count;
        } catch (RecordStoreNotOpenException rsnoe) {
            // do nothing
            return -1;
        } catch (RecordStoreException rse) {
            // do nothing
            return -1;
        }
    }

    // removes a filtered record set from a specified store
    private int removeRecords(RecordStore store, RecordFilter filter) {
        String storeName = null;
        int deleteCounter = 0;

        try {
            storeName = store.getName();
            RecordEnumeration enumeration = store.enumerateRecords(filter, null, false);
            while (enumeration.hasNextElement()) {
                store.deleteRecord(enumeration.nextRecordId());
                deleteCounter++;
            }
            enumeration.destroy();
            return deleteCounter;
        } catch (RecordStoreNotOpenException rsnoe) {
            //#debug error
            System.out.println("Delete Record - Record Store closed: " + storeName + " " + rsnoe);
            return deleteCounter;
        } catch (InvalidRecordIDException irie) {
            //#debug error
            System.out.println("Delete Record - Invalid Record ID: " + storeName + " " + irie);
            return deleteCounter;
        } catch (RecordStoreException rse) {
            //#debug error
            System.out.println("Read Record: " + storeName + " " + rse);
            return deleteCounter;
        }
    }

    // closes the specified record store
    public boolean closeRecordStore(RecordStore store) {
        String storeName = null;
        try {
            storeName = store.getName();
            store.closeRecordStore();
            return true;
        } catch (RecordStoreNotOpenException rsnoe) {
            //#debug
//#             System.out.println("Close Record Store - Record Store not open: " + storeName + " " + rsnoe);
            return false;
        } catch (RecordStoreException rse) {
            //#debug error
            System.out.println("Close Record Store: " + storeName + " " + rse);
            return false;
        }
    }

    /***************************************************************************************/
    // store login object as record
    public boolean setCredentials(Credentials credentials) {

        this.loginRS = this.openRecordStore(this.LOGIN_STORE);
        if (this.loginRS != null) {
            byte[] record = null;
            int count = this.countRecords(this.loginRS);
            if (count == 0 || count == 1) {
                // serialize login object
                try {
                    record = credentials.serialize();
                } catch (IOException ioe) {
                    //#debug error
                    System.out.println("Set Login Data - Serialize: " + ioe);
                }
            }
            if (count == 1 && record != null) {
                // update stored login data
                boolean success = this.updateRecord(this.loginRS, record, null);
                this.closeRecordStore(this.loginRS);
                return success;
            } else if (count == 0 && record != null) {
                // store login data
                boolean success = this.addRecord(this.loginRS, record);
                this.closeRecordStore(this.loginRS);
                return success;
            } else {
                // record store closed or to many records
                //#debug error
                System.out.println("Set Login Data - FATAL: Login Record Failure.");
            }
        }
        this.closeRecordStore(this.loginRS);
        return false;
    }

    // return login object from record store
    public Credentials getCredentials() {
        this.loginRS = this.openRecordStore(this.LOGIN_STORE);
        if (this.loginRS != null) {
            int count = this.countRecords(this.loginRS);
            // get login data
            if (count == 1) {
                byte[] record = (byte[]) this.getRecords(this.loginRS, null, null).firstElement();
                this.closeRecordStore(this.loginRS);
                if (record != null) {
                    Credentials credentials = new Credentials();
                    try {
                        // reconstuct login object
                        credentials.unserialize(record);
                        return credentials;
                    } catch (IOException ioe) {
                        //#debug error
                        System.out.println("Get Login Data - Unserialize: " + ioe);
                        return null;
                    }
                }
            } else if (count != 0) {
                // record store closed or to many records
                //#debug error
                System.out.println("Get Login Record - FATAL: Login Record Failure.");
            }
        }
        // nothing stored
        this.closeRecordStore(this.loginRS);
        return null;
    }

    // remove login object from record store 
    public boolean removeCredentials() {
        this.loginRS = this.openRecordStore(this.LOGIN_STORE);
        if (this.loginRS != null) {
            int count = this.countRecords(this.loginRS);
            // remove login data if existing
            if ((count == 0) || (count == 1 && this.removeRecords(this.loginRS, null) == 1)) {
                this.closeRecordStore(this.loginRS);
                return true;
            } else if (count == 1) {
                //#debug error
                System.out.println("Remove Login Data - FATAL: Not removed.");
            } else {
                // record store closed or to many records
                //#debug error
                System.out.println("Remove Login Data - FATAL: Login Record Failure.");
            }
        }
        this.closeRecordStore(this.loginRS);
        return false;
    }

    /***************************************************************************************/
    // store settings object as record
    public boolean setSettings(Settings settings) {

        this.settingsRS = this.openRecordStore(this.SETTINGS_STORE);
        if (this.settingsRS != null) {
            byte[] record = null;
            int count = this.countRecords(this.settingsRS);
            if (count == 0 || count == 1) {
                // serialize settings object
                try {
                    record = settings.serialize();
                } catch (IOException ioe) {
                    //#debug error
                    System.out.println("Set Settings - Serialize: " + ioe);
                }
            }
            if (count == 1 && record != null) {
                // update stored settings
                boolean success = this.updateRecord(this.settingsRS, record, null);
                this.closeRecordStore(this.settingsRS);
                return success;
            } else if (count == 0 && record != null) {
                // store settings the first time
                boolean success = this.addRecord(this.settingsRS, record);
                this.closeRecordStore(this.settingsRS);
                return success;
            } else {
                // record store closed or to many records
                //#debug error
                System.out.println("Set Settings - FATAL: Settings Record Failure.");
            }
        }
        this.closeRecordStore(this.settingsRS);
        return false;
    }

    // return settings object from record store
    public Settings getSettings() {
        this.settingsRS = this.openRecordStore(this.SETTINGS_STORE);
        if (this.settingsRS != null) {
            int count = this.countRecords(this.settingsRS);
            // default settings object
            if (count == 0) {
                this.closeRecordStore(this.settingsRS);
                Settings settings = new Settings();
                settings.setDefaults();
                return settings;
            }
            // reconstuct settings object from RMS
            if (count == 1) {
                byte[] record = (byte[]) this.getRecords(this.settingsRS, null, null).firstElement();
                this.closeRecordStore(this.settingsRS);
                if (record != null) {
                    Settings settings = new Settings();
                    try {
                        settings.unserialize(record);
                        return settings;
                    } catch (IOException ioe) {
                        //#debug error
                        System.out.println("Get Settings - Unserialize: " + ioe);
                        return null;
                    }
                }
            } else {
                // record store closed or to many records
                //#debug error
                System.out.println("Get Settings - FATAL: Settings Record Failure.");
            }
        }
        this.closeRecordStore(this.settingsRS);
        return null;
    }

    /***************************************************************************************/
    // store blog
    public boolean setBlog(int storeType, int blogType, Stack recordSet) {
        if (recordSet == null) {
            return false;
        }

        byte[] fullRecord = null;
        byte[] thumbRecord = null;
        byte[] messageRecord = null;

        // check data of stack - order: (bottom)[thumb, full](top)

        // get serialized records
        if (blogType == this.BLOG_PM) {
            if(storeType == this.STORED_BLOGS) {
                if (recordSet.peek() instanceof byte[]) {
                    fullRecord = (byte[]) recordSet.pop();
                } else {
                    return false;
                }
            }
            if (recordSet.peek() instanceof byte[]) {
                thumbRecord = (byte[]) recordSet.pop();
            } else {
                return false;
            }
        } else if (blogType == this.BLOG_M && recordSet.peek() instanceof byte[]) {
            messageRecord = (byte[]) recordSet.pop();
        } else {
            return false;
        }

        // store multimedia blog
        if (blogType == this.BLOG_PM) {
            // open required record stores
            if (storeType == this.STORED_BLOGS) {
                this.blogPMThumbRS = this.openRecordStore(this.BLOG_PM_THUMB_STORE);
            } else if (storeType == this.POSTED_BLOGS) {
                this.blogPMThumbRS = this.openRecordStore(this.BLOG_PM_SERVER_THUMB_STORE);
            }

            if (this.blogPMThumbRS == null) {
                return false;
            }

            if (storeType == this.STORED_BLOGS) {
                this.blogPMRS = this.openRecordStore(this.BLOG_PM_STORE);
                if (this.blogPMRS == null) {
                    return false;
                }

                // store full record
                if (this.addRecord(this.blogPMRS, fullRecord)) {
                    ////#debug
                    System.out.println("Full record stored!");
                } else {
                    //#debug error
                    System.out.println("Set Blog - FATAL: Full Record Failure.");
                    this.closeRecordStore(this.blogPMRS);
                    return false;
                }
                this.closeRecordStore(this.blogPMRS);
            }

            // store thumb record
            if (this.addRecord(this.blogPMThumbRS, thumbRecord)) {
                ////#debug
                System.out.println("Thumb record stored!");
            } else {
                //#debug error
                System.out.println("Set Blog - FATAL: Thumb Record Failure.");
                this.closeRecordStore(this.blogPMThumbRS);
                return false;
            }
            this.closeRecordStore(this.blogPMThumbRS);
            return true;

        } else if (blogType == this.BLOG_M) {
            if (storeType == this.STORED_BLOGS) {
                this.blogMRS = this.openRecordStore(this.BLOG_M_STORE);
            } else if (storeType == this.POSTED_BLOGS) {
                this.blogMRS = this.openRecordStore(this.BLOG_M_SERVER_STORE);
            }

            if (this.blogMRS == null) {
                return false;
            }

            if (this.addRecord(this.blogMRS, messageRecord)) {
            } else {
                //#debug error
                System.out.println("Store Blog - FATAL: Message Record Failure.");
                this.closeRecordStore(this.blogMRS);
                return false;
            }
            this.closeRecordStore(this.blogMRS);
            return true;

        } else {
            return false;
        }
    }

    // return all blog records specified by the record selector
    public Stack getBlog(int storeType, int blogType, int quality, RMSBlogSelector filter) {
        Stack resultSet = null;

        // stores for multimedia blog
        if (blogType == this.BLOG_PM) {
            if (quality == this.QUALITY_POST && storeType == this.STORED_BLOGS) {
                this.blogPMRS = this.openRecordStore(this.BLOG_PM_STORE);
                if (this.blogPMRS == null) {
                    return null;
                }
                resultSet = this.getRecords(this.blogPMRS, filter, null);
                this.closeRecordStore(this.blogPMRS);

            } else if (quality == this.QUALITY_BROWSE) {
                if (storeType == this.STORED_BLOGS) {
                    this.blogPMThumbRS = this.openRecordStore(this.BLOG_PM_THUMB_STORE);
                } else if (storeType == this.POSTED_BLOGS) {
                    this.blogPMThumbRS = this.openRecordStore(this.BLOG_PM_SERVER_THUMB_STORE);
                }

                if (this.blogPMThumbRS == null) {
                    return null;
                }
                resultSet = this.getRecords(this.blogPMThumbRS, filter, null);
                this.closeRecordStore(this.blogPMThumbRS);
            }
            return resultSet;

        // store for text messages
        } else if (blogType == this.BLOG_M) {
            if (storeType == this.STORED_BLOGS) {
                this.blogMRS = this.openRecordStore(this.BLOG_M_STORE);
            } else if (storeType == this.POSTED_BLOGS) {
                this.blogMRS = this.openRecordStore(this.BLOG_M_SERVER_STORE);
            }

            if (this.blogMRS == null) {
                return null;
            }
            resultSet = this.getRecords(this.blogMRS, filter, null);
            this.closeRecordStore(this.blogMRS);
            return resultSet;
        } else {
            return new Stack();
        }
    }


    // removes a specified blog
    public int removeBlog(int storeType, int blogType, RMSBlogSelector filter) {
        int removedFullPhotoRecords = 0;
        int removedThumbPhotoRecords = 0;
        int removedMessageRecords = 0;

        //  0 not found error
        // -1 init error 
        // -2 consistency error

        if (blogType == this.BLOG_PM) {
            // stores for multimedia blog
            if (storeType == this.STORED_BLOGS) {
                this.blogPMThumbRS = this.openRecordStore(this.BLOG_PM_THUMB_STORE);
            } else if (storeType == this.POSTED_BLOGS) {
                this.blogPMThumbRS = this.openRecordStore(this.BLOG_PM_SERVER_THUMB_STORE);
            }

            if (this.blogPMThumbRS == null) {
                return -1;
            }

            this.blogPMRS = this.openRecordStore(this.BLOG_PM_STORE);
            if (this.blogPMRS == null) {
                return -1;
            }

            removedThumbPhotoRecords = this.removeRecords(this.blogPMThumbRS, filter);
            if (storeType == this.STORED_BLOGS) {
                removedFullPhotoRecords = this.removeRecords(this.blogPMRS, filter);
            }


            //#debug
//#             System.out.println("filter criteria: "+filter.getCriteria());

            if (storeType == this.STORED_BLOGS && removedFullPhotoRecords == removedThumbPhotoRecords) {
                // delete record stores if empty (deallocates storage)
                //#debug
//#                 System.out.println("store consistent.");

                //#debug
//#                 System.out.println("remvoved records: "+removedFullPhotoRecords);
                //#debug
//#                 System.out.println("now stored records: "+this.countRecords(this.blogPMRS));
                try {
                    if (this.countRecords(this.blogPMRS) == 0 && this.countRecords(blogPMThumbRS) == 0) {
                        if (this.closeRecordStore(this.blogPMRS) &&
                                this.closeRecordStore(this.blogPMThumbRS)) {
                        //#debug
//#                             System.out.println("Multimedia stores closed.");
                        }
                        RecordStore.deleteRecordStore(this.BLOG_PM_STORE);
                        RecordStore.deleteRecordStore(this.BLOG_PM_THUMB_STORE);
                    //#debug
//#                         System.out.println("Multimedia stores deleted!");
                    }
                } catch (Exception e) {
                    /* BUG? Double close necessary in case where single delete last blog */
                    this.closeRecordStore(this.blogPMRS);
                    this.closeRecordStore(this.blogPMThumbRS);
                    this.blogPMRS = null;
                    this.blogPMThumbRS = null;
                    // second attempt
                    try {
                        RecordStore.deleteRecordStore(this.BLOG_PM_STORE);
                        RecordStore.deleteRecordStore(this.BLOG_PM_THUMB_STORE);
                    //#debug
//#                         System.out.println("Multimedia stores deleted on second attempt!");
                    } catch (Exception ex) { /* ignore */ }
                }
                return removedFullPhotoRecords;
                
            } else if (storeType == this.POSTED_BLOGS) {
                try {
                    if (this.countRecords(blogPMThumbRS) == 0) {
                        if (this.closeRecordStore(this.blogPMThumbRS)) {
                        //#debug
//#                             System.out.println("Server Multimedia store closed.");
                        }
                        RecordStore.deleteRecordStore(this.BLOG_PM_SERVER_THUMB_STORE);
                    //#debug
//#                         System.out.println("Server Multimedia store deleted!");
                    }
                } catch (Exception e) {
                    /* BUG? Double close necessary in case where single delete last blog */
                    this.closeRecordStore(this.blogPMThumbRS);
                    this.blogPMThumbRS = null;
                    // second attempt
                    try {
                        RecordStore.deleteRecordStore(this.BLOG_PM_SERVER_THUMB_STORE);
                    //#debug
//#                         System.out.println("Server Multimedia store deleted on second attempt!");
                    } catch (Exception ex) { /* ignore */ }
                }
                return removedThumbPhotoRecords;
            } else {
                //#debug error
                System.out.println("FATAL: delete - store inconsistency!");
                this.closeRecordStore(this.blogPMRS);
                this.closeRecordStore(this.blogPMThumbRS);
                return -2;
            }
            
        } else if (blogType == this.BLOG_M) {
            // store for text messages
            if (storeType == this.STORED_BLOGS)
                this.blogMRS = this.openRecordStore(this.BLOG_M_STORE);
            else if (storeType == this.POSTED_BLOGS)
                this.blogMRS = this.openRecordStore(this.BLOG_M_SERVER_STORE);
            
            if (this.blogMRS == null) {
                return -1;
            }

            removedMessageRecords = this.removeRecords(this.blogMRS, filter);

            // delete record store if empty (deallocates storage)
            try {
                if (this.blogMRS.getNumRecords() == 0) {
                    if (this.closeRecordStore(this.blogMRS)) {
                    //#debug
//#                         System.out.println("Server Message store closed.");
                    }
                    if (storeType == this.STORED_BLOGS)
                        RecordStore.deleteRecordStore(this.BLOG_M_STORE);
                    else if (storeType == this.POSTED_BLOGS)
                        RecordStore.deleteRecordStore(this.BLOG_M_SERVER_STORE);
                    
                //#debug
//#                     System.out.println("Server Message store deleted!");
                }
            } catch (Exception e) {
                /* BUG? Double close necessary in case where single delete last blog */
                this.closeRecordStore(this.blogMRS);
                this.blogMRS = null;
                // second attempt
                try {
                    if (storeType == this.STORED_BLOGS)
                        RecordStore.deleteRecordStore(this.BLOG_M_STORE);
                    else if (storeType == this.POSTED_BLOGS)
                        RecordStore.deleteRecordStore(this.BLOG_M_SERVER_STORE);
                //#debug
//#                     System.out.println("Server Message store deleted on second attempt!");
                } catch (Exception ex) { /* ignore */ }
            }
            return removedMessageRecords;
        } else {
            return -1;
        }
    }

    /***************************************************************************************/
    // store specific waypoint either as new one or as updated one (of filter is specified)
    public boolean setWaypoint(Waypoint waypoint, RMSWaypointSelector filter) {
        this.traceRS = this.openRecordStore(this.TRACE_STORE);
        boolean success = false;
        if (this.traceRS != null) {
            byte[] record = null;
            // serialize waypoint
            try {
                record = waypoint.serialize();
            } catch (IOException ioe) {
                //#debug error
                System.out.println("Set Waypoint - Serialize: " + ioe);
            }
            if (filter == null) {
                success = this.addRecord(this.traceRS, record);
            } else {
                success = this.updateRecord(traceRS, record, filter);
            }
            this.closeRecordStore(this.traceRS);
            return success;
        }
        this.closeRecordStore(this.traceRS);
        return false;
    }

    // return requested waypoint from record store
    public Vector getWaypoint(RMSWaypointSelector filter) {
        this.traceRS = this.openRecordStore(this.TRACE_STORE);
        if (this.traceRS != null) {
            // perform request
            Stack response = this.getRecords(this.traceRS, filter, null);
            this.closeRecordStore(this.traceRS);
            if (response != null) {
                // create result vector
                Vector result = new Vector();
                Enumeration enumeration = response.elements();
                while (enumeration.hasMoreElements()) {
                    Waypoint wp = new Waypoint(0, null);
                    try {
                        wp.unserialize((byte[]) enumeration.nextElement());
                    } catch (IOException ioe) {
                        //#debug error
                        System.out.println("Get waypoint - Unserialize: " + ioe);
                        return null;
                    }
                    result.addElement(wp);
                }
                //#debug
//#                 System.out.println("unserialized waypoints: "+result.size());
                return result;
            }
        }
        this.closeRecordStore(this.traceRS);
        return null;
    }

    // removes a specified waypoint
    public int removeWaypoint(RMSWaypointSelector filter) {
        this.traceRS = this.openRecordStore(this.TRACE_STORE);
        if (this.traceRS == null) {
            return -1;
        }

        int removedWaypointRecords = this.removeRecords(this.traceRS, filter);
        try {
            this.closeRecordStore(this.traceRS);
            if (this.countRecords(this.traceRS) == 0) {
                RecordStore.deleteRecordStore(this.TRACE_STORE);
            //#debug
//#                     System.out.println("Trace store deleted!");
            }
        } catch (Exception e) {
            /* BUG? */
            this.closeRecordStore(this.traceRS);
            this.traceRS = null;
            // second attempt
            try {
                RecordStore.deleteRecordStore(this.TRACE_STORE);
            //#debug
//#                     System.out.println("Trace store deleted on second attempt!");
            } catch (Exception ex) { /* ignore */ }
        }
        return removedWaypointRecords;
    }
}
