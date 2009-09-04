/*
 * PlatformInfo.java
 *
 * Created on 31. Oktober 2007, 12:47
 *
 */

package org.metadon.beans;

/**
 *
 * @author Hannes
 */
public class PlatformInfo {

	public final String name;
	public final String value;

	/**
	 * Creates a new info.
	 * 
	 * @param name the name of the setting
	 * @param value the value of the setting
	 */
	public PlatformInfo( String name, String value ) {
            super();
            this.name = name;
            this.value = value;
	}
}

