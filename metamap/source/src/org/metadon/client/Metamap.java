package org.metadon.client;

import java.util.HashMap;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.gwtext.client.core.Margins;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Viewport;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;

/**
 * Module entry point - defines <code>onModuleLoad()</code>.
 * 
 * @author Hannes Weingartner
 */
public class Metamap implements EntryPoint//, HistoryListener
{

	public static HashMap<String, Panel>	uiRegistry	= new HashMap<String, Panel>();

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{
		//create the main panel and assign it a BorderLayout
		Panel mainPanel = new Panel();
		mainPanel.setLayout(new BorderLayout());

		BorderLayoutData northLayoutData = new BorderLayoutData(RegionPosition.NORTH);
		northLayoutData.setSplit(false);

		BorderLayoutData centerLayoutData = new BorderLayoutData(RegionPosition.CENTER);
		centerLayoutData.setMargins(new Margins(5, 0, 5, 5));

		// define application tab panel
		TabPanel applicationPanel = new TabPanel();
		applicationPanel.setBodyBorder(false);
		applicationPanel.setEnableTabScroll(true);
		applicationPanel.setAutoScroll(true);
		applicationPanel.setAutoDestroy(false);
		applicationPanel.setActiveTab(0);

		Panel applicationPanelWrapper = new Panel();
		applicationPanelWrapper.setLayout(new FitLayout());
		applicationPanelWrapper.setBorder(false);
		applicationPanelWrapper.setBodyBorder(false);

		// define tab
		final Panel introPanel = new Panel();
		introPanel.setTitle("Metamap");
		introPanel.setPaddings(0);
		introPanel.setLayout(new FitLayout());
		introPanel.setIconCls("map-icon");
		final MetamapAdminView metamapAdminView = new MetamapAdminView();
		introPanel.add(metamapAdminView); // add tab content

		applicationPanel.add(introPanel, centerLayoutData); // add tab to tabPanel
		applicationPanelWrapper.add(applicationPanel);
		mainPanel.add(applicationPanelWrapper, centerLayoutData);

		Viewport v = new Viewport(mainPanel);
		
		Log.debug("Module loaded.");
	}


}
