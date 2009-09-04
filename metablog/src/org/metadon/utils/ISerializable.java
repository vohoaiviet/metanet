/*
 * ISerializable.java
 *
 * Created on 05. November 2007, 13:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.utils;

import java.io.IOException;

/**
 *
 * @author Hannes
 */
public interface ISerializable {

    /**
     * This method should serialize the given file into a sequence of bytes so
     * that it can be easily stored in the RMS.
     */
    public abstract byte[] serialize() throws IOException;

    /**
     * This method should do the exact opposite of the
     * {@link ISerializable#serialize()} method. i.e. it should
     * re-construct the Object from the given byte array.
     */
    public abstract void unserialize(byte[] dataStream) throws IOException;

}