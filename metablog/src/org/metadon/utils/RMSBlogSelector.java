/*
 * RMSBlogSelector.java
 *
 * Created on 16. November 2007, 15:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.metadon.utils;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import javax.microedition.rms.RecordFilter;

/**
 *
 * @author Hannes
 * a record filter which can by used to search a record store for a record with the given timestamp
 */
public class RMSBlogSelector implements RecordFilter {

    private long referenceValue = -1;
    private long timestamp = -1;
    private int searchCriteria;
    private int searchType;
    
    public static final int EQUAL_TO = 0;
    public static final int GREATER_THEN = 1;
    public static final int LESSER_THEN = 2;
    public static final int CONTAINS_LOCATION = 3;
    public static final int IS_JOURNEY_BLOG = 4;
    
    private final int SEARCH_TYPE_STORED = 5;
    private final int SEARCH_TYPE_POSTED = 6;

    public RMSBlogSelector(int criteria, Long timestampStored, Long timestampPosted) {
        this.searchCriteria = criteria;
        if (timestampStored != null) {
            this.referenceValue = timestampStored.longValue();
            this.searchType = this.SEARCH_TYPE_STORED;
        } else if (timestampPosted != null) {
            this.referenceValue = timestampPosted.longValue();
            this.searchType = this.SEARCH_TYPE_POSTED;
        }
    }

    public int getCriteria() {
        switch (this.searchCriteria) {
            case 0:
                return EQUAL_TO;
            case 1:
                return GREATER_THEN;
            case 2:
                return LESSER_THEN;
            case 3:
                return CONTAINS_LOCATION;
            case 4:
                return IS_JOURNEY_BLOG;
            default:
                return -1;
        }
    }

    public boolean matches(byte[] record) {
        try {
            // get timestamp from stored record stream
            ByteArrayInputStream bais = new ByteArrayInputStream(record);
            DataInputStream dis = new DataInputStream(bais);

            long timestampStored = dis.readLong();
            long timestampPosted = dis.readLong();
            boolean containsLocation = dis.readBoolean();
            boolean isJourneyBlog = dis.readBoolean();

            dis.close(); // closes bais as well
            
            if(this.searchType == this.SEARCH_TYPE_STORED)
                this.timestamp = timestampStored;
            else if(this.searchType == this.SEARCH_TYPE_POSTED)
                this.timestamp = timestampPosted;

            switch (this.searchCriteria) {
                case 0:
                    if (this.timestamp == this.referenceValue) {
                        return true;
                    } else {
                        return false;
                    }
                case 1:
                    if (this.timestamp > this.referenceValue) {
                        return true;
                    } else {
                        return false;
                    }
                case 2:
                    if (this.timestamp < this.referenceValue) {
                        return true;
                    } else {
                        return false;
                    }
                case 3:
                    if (containsLocation && this.timestamp < this.referenceValue) {
                        return true;
                    } else {
                        return false;
                    }
                case 4:
                    if (isJourneyBlog && this.timestamp < this.referenceValue) {
                        return true;
                    } else {
                        return false;
                    }
            }
        } catch (IOException ioe) {
            //#debug error
            System.out.println("Record Filter: " + ioe);
        }
        return false;
    }
}

