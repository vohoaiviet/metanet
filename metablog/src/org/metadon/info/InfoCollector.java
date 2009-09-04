/*
 * InfoController.java
 *
 * Created on 31. Oktober 2007, 12:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.metadon.info;

import de.enough.polish.util.ArrayList;

import javax.microedition.lcdui.Display;

import org.metadon.beans.PlatformInfo;
import org.metadon.control.Controller;
import org.metadon.view.PlatformInfoScreen;


/**
 *
 * @author Hannes
 */

public abstract class InfoCollector {
	
	private final ArrayList settings;
	protected PlatformInfoScreen view;

	/**
	 * Creates a new collector
	 */
	public InfoCollector() {
		super();
		this.settings = new ArrayList();
	}
	
	/**
	 * Collects information.
	 * 
	 * @param midlet the parent MIDlet
	 * @param display the display
	 */
	public abstract void collectInfos( Controller controller, Display display );
	
	
	public void addInfo( String name, String value ) {
            // update list
            this.settings.add( new PlatformInfo( name, value ));
	}
	
	public PlatformInfo[] getInfos() {
            return (PlatformInfo[]) this.settings.toArray( new PlatformInfo[ this.settings.size() ] );
	}
	
	public void show( Display display, PlatformInfoScreen infoScreen ) {
            this.view = infoScreen;
            display.setCurrent( infoScreen );
	}


}
