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
public class RMSWaypointSelector implements RecordFilter {

    private long referenceValue;
    private int searchCriteria;
    public static final int EQUAL_TO = 0;
    public static final int GREATER_THEN = 1;
    public static final int LESSER_THEN = 2;
    public static final int IS_RESOLVED = 3;

    public RMSWaypointSelector(int criteria, long timestamp) {
        this.searchCriteria = criteria;
        this.referenceValue = timestamp;
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
                return IS_RESOLVED;
            default:
                return -1;
        }
    }

    public boolean matches(byte[] record) {
        try {
            // get timestamp from stored record stream
            ByteArrayInputStream bais = new ByteArrayInputStream(record);
            DataInputStream dis = new DataInputStream(bais);

            long timestamp = dis.readLong();
            boolean isResolved = dis.readBoolean();
            dis.close(); // closes bais as well

            switch (this.searchCriteria) {
                case 0:
                    if (timestamp == this.referenceValue) {
                        return true;
                    } else {
                        return false;
                    }
                case 1:
                    if (timestamp > this.referenceValue) {
                        return true;
                    } else {
                        return false;
                    }
                case 2:
                    if (timestamp < this.referenceValue) {
                        return true;
                    } else {
                        return false;
                    }
                case 3:
                    if (isResolved && timestamp < this.referenceValue) {
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

