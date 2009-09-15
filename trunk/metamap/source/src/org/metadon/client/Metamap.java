package org.metadon.client;

import org.metadon.client.openlayers.raster.BasicWMS;
import org.metadon.client.widget.ContentPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Module entry point - defines <code>onModuleLoad()</code>.
 * 
 * @author Hannes Weingartner
 */
public class Metamap implements EntryPoint
{
	private DockPanel mainPanel = new DockPanel();
	private ContentPanel contentPanel = new ContentPanel();

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{
		// add a specific map type here
		contentPanel.setTitle("Meta-D.O.N");
		BasicWMS basicWms = new BasicWMS();
		contentPanel.setMapHandler(basicWms.getMapHandler());

		// TODO add login and status panel
		
		mainPanel.add(contentPanel, DockPanel.CENTER);
		RootPanel.get().add(mainPanel);
	}
}
