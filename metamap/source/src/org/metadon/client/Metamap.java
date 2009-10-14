package org.metadon.client;

import org.metadon.client.admin.view.AdminMainView;

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
	private String	               metamapIconCls	= "map-icon";

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{
		// TODO create async login service
		
		
		
		// create the root panel
		Panel rootPanel = new Panel();
		rootPanel.setLayout(new BorderLayout());


		// create application tab panel
		TabPanel applicationTabPanel = new TabPanel();
		applicationTabPanel.setBodyBorder(false);
		applicationTabPanel.setEnableTabScroll(true);
		applicationTabPanel.setAutoScroll(true);
		applicationTabPanel.setAutoDestroy(false);
		applicationTabPanel.setActiveTab(0);

		Panel applicationTabPanelWrapper = new Panel();
		applicationTabPanelWrapper.setLayout(new FitLayout());
		applicationTabPanelWrapper.setBorder(false);
		applicationTabPanelWrapper.setBodyBorder(false);

		// create application tab: metamap
		final Panel metamapTab = new Panel();
		metamapTab.setTitle("Metamap");
		metamapTab.setPaddings(0);
		metamapTab.setLayout(new FitLayout());
		metamapTab.setIconCls(metamapIconCls);
		
		final AdminMainView metamapAdminView = new AdminMainView();
		metamapTab.add(metamapAdminView); // add tab content

		// append
		BorderLayoutData centerLayoutData = new BorderLayoutData(RegionPosition.CENTER);
		centerLayoutData.setMargins(new Margins(5, 0, 5, 5));
		
		applicationTabPanel.add(metamapTab, centerLayoutData); // add tab to tabPanel
		applicationTabPanelWrapper.add(applicationTabPanel);
		
		
		rootPanel.add(applicationTabPanelWrapper, centerLayoutData);
		new Viewport(rootPanel);
		
		Log.debug("Module loaded.");
	}


}
